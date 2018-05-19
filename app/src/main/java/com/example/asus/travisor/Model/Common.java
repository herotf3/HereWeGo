package com.example.asus.travisor.Model;

/**
 * Created by ASUS on 1/13/2018.
 */

public class Common {
    public static RestaurantAdmin currentUser;
    public static String userKey;
    public static String convertCodeToStatus(String status) {
        if (status.equals("0"))
            return "Đang chế biến";
        if (status.equals("1"))
            return "Shipping...";
        if (status.equals("2"))
            return "Đã giao!";
        return "Không xác định";
    }
}
