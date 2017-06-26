package cirrbagent.com.cirrbagentapp.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Filterable;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import cirrbagent.com.cirrbagentapp.MainActivity;
import cirrbagent.com.cirrbagentapp.R;
import cirrbagent.com.cirrbagentapp.adapter.OrderHistoryAdapter;
import cirrbagent.com.cirrbagentapp.constant.Constant;
import cirrbagent.com.cirrbagentapp.util.CircleImageView;
import cirrbagent.com.cirrbagentapp.util.ConnectionDetector;
import cirrbagent.com.cirrbagentapp.util.Directory;
import cirrbagent.com.cirrbagentapp.util.ImageLoading;
import cirrbagent.com.cirrbagentapp.util.PreferenceClass;
import cirrbagent.com.cirrbagentapp.util.ProgressHUD;

import static android.app.Activity.RESULT_CANCELED;

/**
 * Created by yuva on 13/6/17.
 */

public class ProfileFragment extends Fragment implements AdapterView.OnItemSelectedListener, View.OnClickListener {


    View rootView;
    Spinner spinnerLang;
    Context context;
    private final CharSequence[] options = {"Take Photo", "Choose from Gallery", "Cancel"};
    private final int REQUEST_CAMERA = 0, SELECT_FILE = 1, CROP_PIC = 2;
    ProgressHUD progressHUD;
    private File imageFile;
    Bitmap bitmap;
    File file;
    private ImageLoader imageLoader = ImageLoader.getInstance();
    private DisplayImageOptions optionsCircle;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_profile, container, false);
        context = getActivity();
        setHasOptionsMenu(true);
        init();
        return rootView;
    }


    private void init() {

        optionsCircle = ImageLoading.getDisplayImageOption(1000, R.drawable.profile);
        if (!imageLoader.isInited()) {
            imageLoader = ImageLoading.initImageLoader(context, imageLoader);
        }
        ((CircleImageView) rootView.findViewById(R.id.circleView)).setOnClickListener(this);
        String userLang = PreferenceClass.getStringPreferences(context, Constant.USER_LANG);

        if (!userLang.equals("")) {
            setLocaleLanguage(context, userLang);
        }
        setSpinner();
        setView();
    }

    private void setSpinner() {
        spinnerLang = (Spinner) rootView.findViewById(R.id.langSpinner);
        spinnerLang.setOnItemSelectedListener(this);

        ArrayList<String> arrayList = new ArrayList<String>();
        arrayList.add(0, "English");
        arrayList.add(1, "Arabic");
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>
                (context, android.R.layout.simple_spinner_item, arrayList);

        dataAdapter.setDropDownViewResource
                (android.R.layout.simple_spinner_dropdown_item);

        spinnerLang.setAdapter(dataAdapter);
        if (PreferenceClass.getIntegerPreferences(context, "LANGUAGE") == 0 || PreferenceClass.getIntegerPreferences(context, "LANGUAGE") == 1) {
            int lanPos = PreferenceClass.getIntegerPreferences(context, "LANGUAGE");
            spinnerLang.setSelection(lanPos);
        }
    }

    private void setView() {

        String name = PreferenceClass.getStringPreferences(context, Constant.LOGINNAME) + " " + PreferenceClass.getStringPreferences(context, Constant.LOGINLASTNAME);
        ((TextView) rootView.findViewById(R.id.tv_usr_name)).setText(name);

        ((EditText) rootView.findViewById(R.id.user_fname)).setText(PreferenceClass.getStringPreferences(context, Constant.LOGINNAME));
        ((EditText) rootView.findViewById(R.id.usr_lname)).setText(PreferenceClass.getStringPreferences(context, Constant.LOGINLASTNAME));

        ConnectionDetector cd = new ConnectionDetector(context);
        if (cd.isConnectingToInternet()) {
            int userId = PreferenceClass.getIntegerPreferences(context, Constant.LOGINEMPID);
            getDetails(userId);
        } else {
            Toast.makeText(context, R.string.txt_Exception_Message, Toast.LENGTH_SHORT).show();
            ((TextView) rootView.findViewById(R.id.txt_inhand_value)).setText(PreferenceClass.getIntegerPreferences(context, "INHAND") + "");
            ((TextView) rootView.findViewById(R.id.txt_lstrcvd_value)).setText(PreferenceClass.getIntegerPreferences(context, "LASTORDER") + "");
            ((TextView) rootView.findViewById(R.id.txt_totalorder_value)).setText(PreferenceClass.getIntegerPreferences(context, "ORDERCOUNT") + "");
            ((TextView) rootView.findViewById(R.id.txt_limit_value)).setText(PreferenceClass.getIntegerPreferences(context, "LIMIT") + "");
        }
    }

    private void getDetails(int userId) {

        progressHUD = ProgressHUD.show(context, "Please wait...", true, false, new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                // TODO Auto-generated method stub
            }
        });
        AQuery aQuery = new AQuery(context);

        String url = Constant.BASE_URL + "customerDetails?user_id=" + userId;

        aQuery.ajax(url, JSONObject.class, new AjaxCallback<JSONObject>() {
            @Override
            public void callback(String url, JSONObject object, AjaxStatus status) {

                if (progressHUD != null && progressHUD.isShowing()) {
                    progressHUD.dismiss();
                }
                if (status.getCode() == 200) {

                    try {
                        JSONObject jsonObject = object.getJSONObject("details");
                        int inHand = jsonObject.getInt("cash_in_hand");
                        int limit = jsonObject.getInt("order_limit");
                        int last_order = jsonObject.getInt("last_order");
                        int order_count = jsonObject.getInt("order_count");

                        PreferenceClass.setIntegerPreference(context, "INHAND", inHand);
                        PreferenceClass.setIntegerPreference(context, "LIMIT", limit);
                        PreferenceClass.setIntegerPreference(context, "LASTORDER", last_order);
                        PreferenceClass.setIntegerPreference(context, "ORDERCOUNT", order_count);

                        ((TextView) rootView.findViewById(R.id.txt_inhand_value)).setText(inHand + "");
                        ((TextView) rootView.findViewById(R.id.txt_lstrcvd_value)).setText(last_order + "");
                        ((TextView) rootView.findViewById(R.id.txt_totalorder_value)).setText(order_count + "");
                        ((TextView) rootView.findViewById(R.id.txt_limit_value)).setText(limit + "");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.method(AQuery.METHOD_GET).header("Content-Type", "application/json"));
    }

    private void updateUser(int userId, String displayName, String fName, String lName, String phoneNo, File image) {

        progressHUD = ProgressHUD.show(context, "Please wait...", true, false, new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                // TODO Auto-generated method stub
            }
        });
        AQuery aQuery = new AQuery(context);
        String url = Constant.BASE_URL + Constant.UPDATE_CUSTOMER;

        Map<String, Object> param = new HashMap<>();
        param.put("user_id", userId);
        param.put("display_name", displayName);
        param.put("first_name", fName);
        param.put("last_name", lName);
        param.put("phone", phoneNo);
        param.put("image", image);

        JSONObject jsonObject = new JSONObject(param);

        Log.e("JsnoObject is", jsonObject + "");

        aQuery.post(url, jsonObject, JSONObject.class, new AjaxCallback<JSONObject>() {
            @Override
            public void callback(String url, JSONObject object, AjaxStatus status) {

                if (progressHUD != null && progressHUD.isShowing()) {
                    progressHUD.dismiss();
                }

                Log.e("Status is", status.getCode() + "");
                if (status.getCode() == 200) {
                    imageLoader.displayImage("file://" + imageFile, ((CircleImageView) rootView.findViewById(R.id.circleView)), optionsCircle);
                    Snackbar.make(((CircleImageView) rootView.findViewById(R.id.circleView)), "Successfully_uploaded", Snackbar.LENGTH_LONG)
                            .setAction("Action", null)
                            .show();
                  /*  Picasso.with(context)
                            .load(String.valueOf(bitmap))
                            .into(((CircleImageView) rootView.findViewById(R.id.circleView)));*/

                }
            }
        }.method(AQuery.METHOD_POST).header("Content-Type", "application/json").header("Authorization", PreferenceClass.getStringPreferences(context, Constant.LOGINTOKEN)));
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.circleView:
                selectImage();
                break;
        }

    }

    private void selectImage() {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Add Photo!");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (options[item].equals("Take Photo")) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
                        startActivityForResult(intent, REQUEST_CAMERA);
                    }
                } else if (options[item].equals("Choose from Gallery")) {
                    Intent intent1 = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    intent1.setType("image/*");
                    startActivityForResult(Intent.createChooser(intent1, "Select File"), SELECT_FILE);

                } else if (options[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode != RESULT_CANCELED) {
            if (requestCode == REQUEST_CAMERA && data.getData() != null) {
                Uri picUri = data.getData();
                performCrop(picUri);
            }
        }
        try {
            if (requestCode == SELECT_FILE && data.getData() != null) {
                Uri picUri = data.getData();
                performCrop(picUri);

            } else if (requestCode == CROP_PIC && data.getExtras() != null) {
                bitmap = data.getExtras().getParcelable("data");
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
                byte[] byteArray = outputStream.toByteArray();

                /*File rootPathFile = new File(PreferenceClass.getStringPreferences(context, Directory.StoragePaths.PrefStrings.CREATED_DIR_PATH)
                        , Directory.StoragePaths.IMAGES_PATH);*/

               /* if (!rootPathFile.exists())
                    rootPathFile.mkdirs();*/

                // ((CircleImageView) rootView.findViewById(R.id.circleView)).setImageBitmap(bitmap);
                File path = Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_PICTURES);
                Log.e("Path anem is", path + "");
                File file = new File(path, System.currentTimeMillis() + ".png");
                File file1 = new File(System.currentTimeMillis() + ".png");
                Log.e("File anem is", path + "");
                Log.e("File1 anem is", file1 + "");

                try {
                    path.mkdirs();
                    file.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                ((CircleImageView) rootView.findViewById(R.id.circleView)).setImageBitmap(bitmap);
                /*    File finalFile = new File(rootPathFile, System.currentTimeMillis() + ".png");
                    FileOutputStream fo = new FileOutputStream(finalFile);
                    fo.write(byteArray);
                    fo.flush();
                    fo.close();*/

                imageFile = file1;

                int userId = PreferenceClass.getIntegerPreferences(context, Constant.LOGINEMPID);
                String displayname = PreferenceClass.getStringPreferences(context, Constant.LOGINDISPLAYNAME);
                String fNmae = ((EditText) rootView.findViewById(R.id.user_fname)).getText().toString();
                String lname = ((EditText) rootView.findViewById(R.id.usr_lname)).getText().toString();
                String phnNo = ((EditText) rootView.findViewById(R.id.edt_phn)).getText().toString();

                updateUser(userId, displayname, fNmae, lname, phnNo, imageFile);


            }
        } catch (NullPointerException e) {
        }


        /*else if(data.getData()==null){
            finish();
        }*/
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void performCrop(Uri picUri) {
        int aspectX = 400;
        int aspectY = 400;
        try {
            Intent cropIntent = new Intent("com.android.camera.action.CROP");
            cropIntent.setDataAndType(picUri, "image/*");
            cropIntent.putExtra("crop", "true");
            cropIntent.putExtra("aspectX", aspectX);
            cropIntent.putExtra("aspectY", aspectY);
            cropIntent.putExtra("outputX", aspectX);
            cropIntent.putExtra("outputY", aspectY);
            cropIntent.putExtra("return-data", true);
            startActivityForResult(cropIntent, CROP_PIC);
        } catch (ActivityNotFoundException anfe) {
            Toast.makeText(context, "This device doesn't support the crop action!", Toast.LENGTH_SHORT).show();
        }
    }

    private void setUIForLanguage() {

        ((TextView) rootView.findViewById(R.id.tv_inhand)).setText(context.getResources().getString(R.string.txtinhand));
        ((TextView) rootView.findViewById(R.id.tv_lstRec)).setText(context.getResources().getString(R.string.txtlstrcvd));
        ((TextView) rootView.findViewById(R.id.tv_totlaorder)).setText(context.getResources().getString(R.string.txttotlaordr));
        ((TextView) rootView.findViewById(R.id.tv_limit)).setText(context.getResources().getString(R.string.txtlimit));
        ((TextView) rootView.findViewById(R.id.txt_prfl)).setText(context.getResources().getString(R.string.txtProfile));

    }

    public void setLocaleLanguage(Context context, String lang) {
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        context.getApplicationContext().getResources().updateConfiguration(config, null);
        setUIForLanguage();
        PreferenceClass.setStringPreference(context, Constant.USER_LANG, lang);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        if (position == 0) {
            setLocaleLanguage(context, "en");
            PreferenceClass.setIntegerPreference(context, "LANGUAGE", parent.getSelectedItemPosition());

        } else {
            setLocaleLanguage(context, "ar");
            PreferenceClass.setIntegerPreference(context, "LANGUAGE", parent.getSelectedItemPosition());
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu, menu);
        /*menu.add(Menu.NONE,
                0,
                Menu.NONE,
                "Save")
                .setIcon(R.drawable.profile)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);*/
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_addItem) {

            int userId = PreferenceClass.getIntegerPreferences(context, Constant.LOGINEMPID);
            String displayname = PreferenceClass.getStringPreferences(context, Constant.LOGINDISPLAYNAME);
            String fNmae = ((EditText) rootView.findViewById(R.id.user_fname)).getText().toString();
            String lname = ((EditText) rootView.findViewById(R.id.usr_lname)).getText().toString();
            String phnNo = ((EditText) rootView.findViewById(R.id.edt_phn)).getText().toString();
            updateUser(userId, displayname, fNmae, lname, phnNo, imageFile);
        } else {

        }
        return super.onOptionsItemSelected(item);
    }


  /*  @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        Toolbar topToolBar = (Toolbar) activity.findViewById(R.id.toolbar);
        topToolBar.setVisibility(View.GONE);

    }*/
}