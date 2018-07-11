package com.android.lab07;

import android.content.res.Configuration;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import layout.MyFragment;

public class MainActivity extends AppCompatActivity {

    public static String[] field_name = {
            "Start Game", "Help","About"};
    protected List<String> list_name;
    protected ListView list_nav;
    protected boolean mainPage;
    protected boolean landscape;
    ArrayAdapter<String> list_name_adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mainPage = true;
        landscape = false;
        list_name = new ArrayList<>(Arrays.asList(field_name));
        list_name_adapter = new ArrayAdapter<>(
                this, R.layout.list_item,
                R.id.list_item_textView,
                list_name);
        list_nav = (ListView) findViewById(R.id.list_nav);
        list_nav.setAdapter(list_name_adapter);
        list_nav.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String ItemName = list_name_adapter.getItem(position);
                Fragment fragment = null;
                if (ItemName == "Start Game") {
                    fragment = MyFragment.newInstance(R.layout.fragment_game);
                }else if (ItemName == "Help") {
                    fragment = MyFragment.newInstance(R.layout.fragment_help);
                } else if (ItemName == "About") {
                    fragment = MyFragment.newInstance(R.layout.fragment_about);
                }
                FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.main_content, fragment).commit();
                if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                    list_nav.setVisibility(View.GONE);
                }
                else {
                    list_nav.setVisibility(View.VISIBLE);
                }

                mainPage = false;
            }
        });
        list_nav.setVisibility(View.VISIBLE);
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.main_content, MyFragment.newInstance(R.layout.fragment_empty)).commit();
    }

    @Override
    public void onBackPressed() {
        if (!mainPage) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.main_content, MyFragment.newInstance(R.layout.fragment_empty)).commit();
            list_nav.setVisibility(View.VISIBLE);
            mainPage = true;
        }
        else {
            super.onBackPressed();
        }
    }

}
