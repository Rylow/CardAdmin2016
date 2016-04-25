package com.rylow.cardadmin2016.service;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import java.util.Comparator;


/**
 * Created by s.bakhti on 25.4.2016.
 */
public class Staff implements Comparable<Staff> {

    private String name;
    private String email;
    private String role;
    private String photo;
    private String photoFileName;
    private Boolean external;
    private Boolean active;
    private int terminal;
    private int id;
    private int securityGroup;
    private Boolean ontemporarycard;
    private long timein;

    public Staff(String name, String email, String role, String photo, String photoFileName, Boolean external, Boolean active, int terminal, int id, int securityGroup, Boolean ontemporarycard, long timein) {
        this.name = name;
        this.email = email;
        this.role = role;
        this.photo = photo;
        this.photoFileName = photoFileName;
        this.external = external;
        this.active = active;
        this.terminal = terminal;
        this.id = id;
        this.securityGroup = securityGroup;
        this.ontemporarycard = ontemporarycard;
        this.timein = timein;
    }

    public byte[] getPhotoAsByteArray(){
        return Base64.decode(photo, Base64.URL_SAFE);
    }

    public Bitmap getPicture (){

        byte[] img = Base64.decode(photo, Base64.URL_SAFE);

        return BitmapFactory.decodeByteArray(img, 0, img.length);

    }



    @Override
    public int compareTo(Staff o) {
        return this.name.compareTo(o.getName());
    }


    public static Comparator<Staff> CompareByArrivalTime
            = new Comparator<Staff>() {

        public int compare(Staff staff1, Staff staff2) {

            Long staffTime1 = staff1.getTimein();
            Long staffTime2 = staff2.getTimein();

            //ascending order
            return staffTime1.compareTo(staffTime2);

            //descending order
            //return fruitName2.compareTo(fruitName1);
        }

    };


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getPhotoFileName() {
        return photoFileName;
    }

    public void setPhotoFileName(String photoFileName) {
        this.photoFileName = photoFileName;
    }

    public Boolean getExternal() {
        return external;
    }

    public void setExternal(Boolean external) {
        this.external = external;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public int getTerminal() {
        return terminal;
    }

    public void setTerminal(int terminal) {
        this.terminal = terminal;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSecurityGroup() {
        return securityGroup;
    }

    public void setSecurityGroup(int securityGroup) {
        this.securityGroup = securityGroup;
    }

    public Boolean getOntemporarycard() {
        return ontemporarycard;
    }

    public void setOntemporarycard(Boolean ontemporarycard) {
        this.ontemporarycard = ontemporarycard;
    }

    public long getTimein() {
        return timein;
    }

    public void setTimein(long timein) {
        this.timein = timein;
    }
}
