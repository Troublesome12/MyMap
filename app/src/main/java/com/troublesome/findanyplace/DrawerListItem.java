package com.troublesome.findanyplace;

/**
 * Created by troublesome on 8/14/15.
 */
public class DrawerListItem {

    private int icon;
    private String title;

    public DrawerListItem(int icon, String title){
        this.icon=icon;
        this.title=title;
    }

    public int getIcon() {return icon;}

    public String getTitle() {return title;}
}