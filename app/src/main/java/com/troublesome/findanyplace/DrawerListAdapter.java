package com.troublesome.findanyplace;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by troublesome on 8/15/15.
 */
public class DrawerListAdapter extends ArrayAdapter<DrawerListItem> {

    private Context context;
    private List<DrawerListItem> arrayList= Collections.emptyList();

    public DrawerListAdapter(Context context, ArrayList<DrawerListItem> arrayList){
        super(context, R.layout.drawer_list_row, arrayList);
        this.context = context;
        this.arrayList = arrayList;
    }

    @Override
    public int getCount() {
        return arrayList.size();
    }

    @Override
    public DrawerListItem getItem(int position) {
        return arrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        if (view == null) {
            LayoutInflater mInflater = (LayoutInflater)
                    context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            view = mInflater.inflate(R.layout.drawer_list_row, null);
        }

        ImageView imgIcon = (ImageView) view.findViewById(R.id.drawer_list_icon);
        TextView txtTitle = (TextView) view.findViewById(R.id.drawer_list_title);

        imgIcon.setImageResource(arrayList.get(position).getIcon());
        txtTitle.setText(arrayList.get(position).getTitle());

        return view;
    }
}
