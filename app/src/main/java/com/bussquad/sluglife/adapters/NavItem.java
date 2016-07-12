package com.bussquad.sluglife.adapters;

/**
 * Created by Jose on 6/14/2016.
 */
public class NavItem {
    String mTitle;
    String profileEmail;
    String profileName;
    boolean isSection = false;
    boolean isDivider = false;
    boolean isProfile = false;
    int mIcon;

    // basic constructor for the navigation item
    // @ title is the title of the navigation item
    // @ isSubheader indicates whether this is a section item indicating it cannot have an icon
    // @ icon indicates the Resource value of the image being used to represent the navigation item
    public NavItem(String title, boolean isSection, int icon) {

        this.mTitle = title;
        if(icon != -1){
            this.mIcon = icon;
        }
        this.isSection = isSection;
    }


    public NavItem(String profilename, String profileemail, int icon) {

        this.profileName = profilename;
        this.profileEmail = profileemail;
        this.mIcon = icon;
        this.isProfile = true;
    }

    public NavItem(boolean isDivider) {
        this.isDivider = isDivider;
    }





    public String getType(){

        return "title + " + mTitle + " is profile + " + isProfile + " is divider  " + isDivider;
    }
    // returns the title of the navigation item, if the navigation item is a divider then it cannot
    // have a title therefor it will throw a runtime exception.
    public String getTitle() {

        if(isDivider){
            throw new RuntimeException("Error: divider cannot have a title");
        }
        return mTitle;
    }



    // sets the title of the navigation item. If the navigation item is a divder then it cannot
    // have a title therefor it will throw a runtime exception
    public void setTitle(String mTitle) {
        if(isDivider){
            throw new RuntimeException("Error: divider cannot have a title");
        }
        this.mTitle = mTitle;
    }



    // returns true if the navigation is a section item false if its a regular navigation item or
    // divider
    public boolean isSection() {
        return this.isSection;
    }



    // returns true if the navigation item is a divider false otherwise
    public boolean isDivider(){
       return this.isDivider;
    }


    public int getIcon() {
        return mIcon;
    }




    public void setIcon(int mIcon) {
        this.mIcon = mIcon;
    }




    public String getProfileEmail() {
        return profileEmail;
    }


    public boolean isProfile(){
        return this.isProfile;
    }

    public void setProfileEmail(String profileEmail) {
        this.profileEmail = profileEmail;
    }




    public String getProfileName() {
        return profileName;
    }




    public void setProfileName(String profileName) {
        this.profileName = profileName;
    }
}