package cirrbagent.com.cirrbagentapp;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.google.android.gms.maps.MapFragment;

import cirrbagent.com.cirrbagentapp.fragment.MapTabFragment;
import cirrbagent.com.cirrbagentapp.fragment.OrderDetailsFragment;

/**
 * Created by yuva on 22/6/17.
 */

public class AcceptOrderActiivty extends AppCompatActivity {

    private TabLayout tabLayout;
    private MapTabFragment fragmentMap;
    private OrderDetailsFragment fragmentOrderDetails;
    Toolbar mToolbar;
    ActionBar actionBar;
    Context context;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accept_order);
        context = this;
        setWidgetReference();
        setActionBar();
    }

    private void setActionBar() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
    }

    private void setWidgetReference() {
        tabLayout = (TabLayout) findViewById(R.id.tab_layout_order_accept);
        ((FrameLayout) findViewById(R.id.frame_layout)).setVisibility(View.VISIBLE);

        TabLayout.Tab tab = tabLayout.getTabAt(0);
        if (tab != null) {
            tab.select();
        }
        tabLayout.addTab(tabLayout.newTab().setText("MAP"));
        tabLayout.addTab(tabLayout.newTab().setText("Order Details"));
        replaceFragment(new MapTabFragment());

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener()

        {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == 0) {
                    replaceFragment(new MapTabFragment());
                } else if (tab.getPosition() == 1) {
                    replaceFragment(new OrderDetailsFragment());
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        android.support.v4.app.FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.frame_layout, fragment);
        transaction.commit();
    }

    /*private void setTab(int tab_position, boolean isFirsTime) {
        View viewTab1 = getLayoutInflater().from(context).inflate(R.layout.custom_map_tab, null);
        viewTab1.findViewById(R.id.imgvwTab).setBackgroundDrawable(null);

        if (tab_position == 0) {
            ((ImageView) viewTab1.findViewById(R.id.imgvwTab)).setSelected(true);
        } else {
            viewTab1.findViewById(R.id.imgvwTab).setSelected(false);
        }
        if (isFirsTime) {
            tabLayout.addTab(tabLayout.newTab().setCustomView(viewTab1));
        }

        View viewTab2 = getLayoutInflater().from(context).inflate(R.layout.custom_map_tab, null);
        viewTab2.findViewById(R.id.imgvwTab).setBackgroundDrawable(null);

        if (tab_position == 1) {
            ((ImageView) viewTab2.findViewById(R.id.imgvwTab)).setSelected(true);
        } else {
            viewTab2.findViewById(R.id.imgvwTab).setSelected(false);
        }
        if (isFirsTime) {
            tabLayout.addTab(tabLayout.newTab().setCustomView(viewTab2));
        }*/



           /* @Override
    public void onTabSelected(TabLayout.Tab tab) {

        int position = tab.getPosition();

        switch (position) {

            case 0:
                setTab(0, false);
                replaceFragment(fragmentMap);
                break;

            case 1:
                setTab(1, false);
                replaceFragment(fragmentOrderDetails);
                break;

            default:
                break;

        }
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {
        int position = tab.getPosition();


    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {
        int position = tab.getPosition();
    }

    public void replaceFragment(Fragment fragment) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.frame_layout_timesheet, fragment);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        ft.commit();
    }*/
}
