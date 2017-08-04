package com.example.fmonasterios.nav_view_expandable_list.Side_Menu;

/**
 * Created by fmonasterios on 5/5/2017.
 */

public class ExpandedMenuModel {

    private String iconName = "";
    private int iconImg = -1; // menu icon resource id

    String getIconName() {
        return iconName;
    }
    void setIconName(String iconName) {
        this.iconName = iconName;
    }
    int getIconImg() {
        return iconImg;
    }
    void setIconImg(int iconImg) {
        this.iconImg = iconImg;
    }
}
