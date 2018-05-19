package com.example.asus.travisor.Model;

import java.util.List;

/**
 * Created by ASUS on 1/8/2018.
 */

public class Request {
    private String name,address,phone,total,status;
    private List<Order> foods;

    public Request() {
    }

    public Request(String name, String address, String phone, String total, List<Order> foods) {

        this.name = name;
        this.address = address;
        this.phone = phone;
        this.total = total;
        this.foods = foods;
        status="0"; //0 wait, 1: shipping, 2:shipped
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

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<Order> getFoods() {
        return foods;
    }

    public void setFoods(List<Order> foods) {
        this.foods = foods;
    }
}
