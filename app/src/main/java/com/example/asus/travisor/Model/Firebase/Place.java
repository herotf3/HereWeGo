package com.example.asus.travisor.Model.Firebase;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by ASUS on 5/14/2018.
 */

public class Place implements Serializable{
    private String name;
    private String tag;
    private String featureImage;
    private String address;
    private String openTime="0:0";
    private String closeTime="24:24";
    private long point=0;
    private double lat,lng;
    private String categoryId, detailId, commentsId;
    private int comment;


    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public Place(String name, String address, String fbPath, double lat, double lng) {
        this.name=name;
        this.address=address;
        featureImage=fbPath;
        this.lat=lat;this.lng=lng;
        tag="none";
    }
    public Place(String name, String tag, String featureImage, String address, String openTime, String closeTime, long point, double lat, double lng, String categoryId, String detailId, String commentsId, int comment, int luotVote) {
        this.name = name;
        this.tag = tag;
        this.featureImage = featureImage;
        this.address = address;
        this.openTime = openTime;
        this.closeTime = closeTime;
        this.point = point;
        this.lat = lat;
        this.lng = lng;
        this.categoryId = categoryId;
        this.detailId = detailId;
        this.commentsId = commentsId;
        this.comment = comment;
        this.luotVote = luotVote;
    }

    public int getLuotVote() {
        return luotVote;
    }

    public void setLuotVote(int luotVote) {
        this.luotVote = luotVote;
    }

    private int luotVote;

    public int getComment() {
        return comment;
    }

    public void setComment(int comment) {
        this.comment = comment;
    }



    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getOpenTime() {
        return openTime;
    }

    public void setOpenTime(String openTime) {
        this.openTime = openTime;
    }

    public String getCloseTime() {
        return closeTime;
    }

    public void setCloseTime(String closeTime) {
        this.closeTime = closeTime;
    }

    public Place() {
    }

    public Place(String name, long point, String featureImage, double lat, double lng, String CategoryId, String detailId, String CommentsId) {
        this.name = name;
        this.point = point;
        this.featureImage = featureImage;
        this.lat = lat;
        this.lng = lng;
        this.categoryId = CategoryId;
        this.detailId = detailId;
        CommentsId = CommentsId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getPoint() {
        return point;
    }

    public void setPoint(long point) {
        this.point = point;
    }

    public String getFeatureImage() {
        return featureImage;
    }

    public void setFeatureImage(String featureImage) {
        this.featureImage = featureImage;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public String getDetailId() {
        return detailId;
    }

    public void setDetailId(String detailId) {
        this.detailId = detailId;
    }

    public String getCommentsId() {
        return commentsId;
    }

    public void setCommentsId(String commentsId) {
        this.commentsId = commentsId;
    }
}
