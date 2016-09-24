package com.bussquad.sluglife;

/**
 * Created by Jose on 8/30/2016.
 */
public class MapMenuItem {


    private int menuID;
    private int menuIcon;
    private String title;
    private boolean hasMenuIcon = false;


    public MapMenuItem(String title, int menuID){
        this.title = title;
        this.menuID = menuID;
    }



    MapMenuItem(String title, int menuID, int menuIcon){

        this.title = title;
        this.menuID = menuID;
        this.menuIcon = menuIcon;
        this.hasMenuIcon = true;


    }


    public int getMenuID() {
        return menuID;
    }




    public void setMenuID(int menuID) {
        this.menuID = menuID;
    }




    public int getMenuIcon() {
        return menuIcon;
    }




    public void setMenuIcon(int menuIcon) {
        this.menuIcon = menuIcon;
    }




    public String getTitle() {
        return title;
    }




    public void setTitle(String title) {
        this.title = title;
    }




    public boolean isHasMenuIcon() {
        return hasMenuIcon;
    }




    public void setHasMenuIcon(boolean hasMenuIcon) {
        this.hasMenuIcon = hasMenuIcon;
    }
}
