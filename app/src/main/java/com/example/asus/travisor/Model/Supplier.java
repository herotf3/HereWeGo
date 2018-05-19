package com.example.asus.travisor.Model;

/**
 * Created by ASUS on 12/8/2017.
 */

public class Supplier {
    private String name, address, image;
    private int rating, shipCost;

    public Supplier() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public int getShipCost() {
        return shipCost;
    }

    public void setShipCost(int shipCost) {
        this.shipCost = shipCost;
    }

    public Supplier(String name, String address, String image, int rating, int shipCost) {

        this.name = name;
        this.address = address;
        this.image = image;
        this.rating = rating;
        this.shipCost = shipCost;
    }
}
