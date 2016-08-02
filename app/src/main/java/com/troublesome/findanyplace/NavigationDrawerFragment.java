package com.troublesome.findanyplace;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.balysv.materialripple.MaterialRippleLayout;

import java.util.ArrayList;

public class NavigationDrawerFragment extends android.support.v4.app.Fragment {

    public static final String PREF_FILE_NAME = "testpref";
    public static final String KEY_USER_LEARNED_DRAWER = "user_learned_drawer";
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    private boolean mUserLearnedDrawer;
    private boolean mFromSavedInstanceState;
    private View containerView;
    private ArrayList<DrawerListItem> arrayList;
    private DrawerListAdapter adapter;
    private ListView mDrawerList;
    MaterialRippleLayout settingsLayout, aboutLayout;

    public NavigationDrawerFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.navigation_drawer_fragment, container, false);
        mDrawerList = (ListView) view.findViewById(R.id.drawer_list_view);

        LinearLayout footerLayout = (LinearLayout) getLayoutInflater(savedInstanceState).inflate(R.layout.drawer_footer,null);
        settingsLayout = (MaterialRippleLayout) footerLayout.findViewById(R.id.settings_layout);
        aboutLayout = (MaterialRippleLayout) footerLayout.findViewById(R.id.about_layout);

        settingsLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), SettingsActivity.class);
                startActivity(intent);
                mDrawerLayout.closeDrawer(containerView);
            }
        });

        aboutLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AboutActivity.class);
                startActivity(intent);
                mDrawerLayout.closeDrawer(containerView);
            }
        });

        populatingValue();
        adapter = new DrawerListAdapter(getActivity(), arrayList);
        mDrawerList.addFooterView(footerLayout);
        mDrawerList.setAdapter(adapter);
        mDrawerList.setOnItemClickListener(new SlideMenuClickListener());

        return view;
    }

    public void populatingValue() {
        arrayList = new ArrayList<>();
        arrayList.add(new DrawerListItem(R.drawable.ic_drawer_atm, "Atm"));
        arrayList.add(new DrawerListItem(R.drawable.ic_drawer_bank, "Bank"));
        arrayList.add(new DrawerListItem(R.drawable.ic_drawer_cafe, "Cafe"));
        arrayList.add(new DrawerListItem(R.drawable.ic_drawer_doctor, "Doctor"));
        arrayList.add(new DrawerListItem(R.drawable.ic_drawer_food, "Food"));
        arrayList.add(new DrawerListItem(R.drawable.ic_drawer_health, "Health"));
        arrayList.add(new DrawerListItem(R.drawable.ic_drawer_hospital, "Hospital"));
        arrayList.add(new DrawerListItem(R.drawable.ic_drawer_hindu, "Hindu Temple"));
        arrayList.add(new DrawerListItem(R.drawable.ic_drawer_mosque, "Mosque"));
        arrayList.add(new DrawerListItem(R.drawable.ic_drawer_pharmacy, "Pharmacy"));
        arrayList.add(new DrawerListItem(R.drawable.ic_drawer_police, "Police"));
        arrayList.add(new DrawerListItem(R.drawable.ic_drawer_post, "Post Office"));
        arrayList.add(new DrawerListItem(R.drawable.ic_drawer_restaurant, "Restaurant"));
        arrayList.add(new DrawerListItem(R.drawable.ic_drawer_school, "School"));
        arrayList.add(new DrawerListItem(R.drawable.ic_drawer_shopping, "Shopping Mall"));
        arrayList.add(new DrawerListItem(R.drawable.ic_drawer_university, "University"));
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mUserLearnedDrawer = Boolean.getBoolean(readFromPreference(getActivity(), KEY_USER_LEARNED_DRAWER, "false"));
        if (savedInstanceState != null) {
            mFromSavedInstanceState = true;
        }
    }

    public void setUp(int fragmentId, DrawerLayout drawerLayout, final Toolbar toolbar) {
        containerView = getActivity().findViewById(fragmentId);
        mDrawerLayout = drawerLayout;
        mDrawerToggle = new ActionBarDrawerToggle(getActivity(), drawerLayout, toolbar, R.string.DrawerOpen, R.string.DrawerClose) {

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);

                //closing keyboard if open
                getActivity().getWindow().setSoftInputMode(
                        WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
                );

                if (!mUserLearnedDrawer) {
                    mUserLearnedDrawer = true;

                    saveToPreference(getActivity(), KEY_USER_LEARNED_DRAWER, mUserLearnedDrawer + "");
                }
                getActivity().invalidateOptionsMenu();


                MapsActivity.autoCompleteTextView.setEnabled(false);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                getActivity().invalidateOptionsMenu();
                MapsActivity.autoCompleteTextView.setEnabled(true);
            }
        };

        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerLayout.post(new Runnable() {
            @Override
            public void run() {
                mDrawerToggle.syncState();
            }
        });
    }

    public static void saveToPreference(Context context, String preferenceName, String preferenceValue) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(preferenceName, preferenceValue);
        editor.apply();
    }

    public static String readFromPreference(Context context, String preferenceName, String defaultValue) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(preferenceName, defaultValue);
    }

    private class SlideMenuClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            // display view for selected nav drawer item
            ItemClicked(position);
        }
    }


    //Displaying fragment view for selected nav drawer list item
    private void ItemClicked(int position) {
        if (isNetworkAvailable()) {

            Intent intent = new Intent(getActivity(), ListViewActivity.class);
            //making the first char of the title lowerCase
            char a[] = arrayList.get(position).getTitle().toCharArray();

            for (int i = 0; i < a.length; i++) {
                if (a[i] >= 'A' && a[i] <= 'Z')
                    a[i] = Character.toLowerCase(a[i]);
                if (a[i] == ' ')
                    a[i] = '_';
            }
            intent.putExtra("type", new String(a));
            intent.putExtra("title", arrayList.get(position).getTitle());
            mDrawerLayout.closeDrawer(containerView);
            startActivity(intent);
        } else
            Toast.makeText(getActivity().getApplicationContext(), "No Internet Connection Available", Toast.LENGTH_LONG).show();
    }

    //check if internet access available or not
    private boolean isNetworkAvailable() {
        try {
            ConnectivityManager connectivityManager
                    = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            return activeNetworkInfo != null && activeNetworkInfo.isConnected();
        } catch (Exception ex) {
            return false;
        }
    }
}