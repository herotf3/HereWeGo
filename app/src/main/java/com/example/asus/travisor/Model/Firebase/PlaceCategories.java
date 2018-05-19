package com.example.asus.travisor.Model.Firebase;

/**
 * Created by ASUS on 5/14/2018.
 */

public class PlaceCategories {
    String name, image;

    public PlaceCategories(){}

    public PlaceCategories(String name, String image) {
        this.name = name;
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
