package com.example.asus.travisor.Model;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by ASUS on 1/4/2018.
 */

public class RestaurantAdmin {
    private String name;
    private String password;
    private String supplierId;
    private String isStaff;
    private Map<String,String>bookmarks=new HashMap<String,String>();


    public Map<String, String> getBookmarks() {
        return bookmarks;
    }

    public void setBookmarks(Map<String, String> bookmarks) {
        this.bookmarks = bookmarks;
    }



    public String getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(String supplierId) {
        this.supplierId = supplierId;
    }

    public RestaurantAdmin() {
    }

    public String getIsStaff() {
        return isStaff;
    }

    public void setIsStaff(String isStaff) {
        this.isStaff = isStaff;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public RestaurantAdmin(String name, String password) {
        this.name = name;

        this.password = password;
    }
}
