package cirrbagent.com.cirrbagentapp.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import cirrbagent.com.cirrbagentapp.R;

/**
 * Created by yuva on 22/6/17.
 */

public class OrderDetailsFragment extends Fragment {

    View rootView;
    Context context;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_order_details, container, false);
        context = getActivity();
        return rootView;
    }
}
