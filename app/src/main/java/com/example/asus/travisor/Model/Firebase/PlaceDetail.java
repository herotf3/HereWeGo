package com.example.asus.travisor.Model.Firebase;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by ASUS on 5/14/2018.
 */

public class PlaceDetail {
    String description;
    HashMap<String,String> images;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Map<String, String> getImages() {
        return images;
    }

    public void setImages(HashMap<String, String> images) {
        this.images = images;
    }

    public PlaceDetail() {

    }

    public PlaceDetail(String description, HashMap<String, String> images) {

        this.description = description;
        this.images = images;
    }
}
