package com.eeducationgo.tectik.features.registration.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class User implements Parcelable {
    private String Uid;
    private String Name;
    private String Image;
    private String Email;
    private String Phone;
    private String Type;
    private String station;
    private String destination;
    private String tripTime;
    private String tripDate;
    private double Late;
    private double Long;
    private String Token;


    public User() {
    }

    public User(String uid, String name, String image, String email, String phone, String type, String station, String destination, String tripDate, double aLong, double Late, String token , String tripTime) {
        Uid = uid;
        Name = name;
        Image = image;
        Email = email;
        Phone = phone;
        Type = type;
        this.station = station;
        this.destination = destination;
        this.tripDate = tripDate;
        Long = aLong;
        this.Late = Late;
        Token = token;
        this.tripTime = tripTime;
    }

    protected User(Parcel in) {
        Uid = in.readString();
        Name = in.readString();
        Image = in.readString();
        Email = in.readString();
        Phone = in.readString();
        Type = in.readString();
        station = in.readString();
        destination = in.readString();
        tripTime = in.readString();
        tripDate = in.readString();
        Late = in.readDouble();
        Long = in.readDouble();
        Token = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(Uid);
        dest.writeString(Name);
        dest.writeString(Image);
        dest.writeString(Email);
        dest.writeString(Phone);
        dest.writeString(Type);
        dest.writeString(station);
        dest.writeString(destination);
        dest.writeString(tripTime);
        dest.writeString(tripDate);
        dest.writeDouble(Late);
        dest.writeDouble(Long);
        dest.writeString(Token);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    public String getToken() {
        return Token;
    }

    public void setToken(String token) {
        Token = token;
    }

    public double getLate() {
        return Late;
    }

    public void setLate(double late) {
        Late = late;
    }

    public double getLong() {
        return Long;
    }

    public void setLong(double aLong) {
        Long = aLong;
    }

    public String getStation() {
        return station;
    }

    public void setStation(String station) {
        this.station = station;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getTripTime() {
        return tripTime;
    }

    public void setTripTime(String tripTime) {
        this.tripTime = tripTime;
    }

    public String getTripDate() {
        return tripDate;
    }

    public void setTripDate(String tripDate) {
        this.tripDate = tripDate;
    }

    public String getUid() {
        return Uid;
    }

    public void setUid(String uid) {
        Uid = uid;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        Image = image;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getPhone() {
        return Phone;
    }

    public void setPhone(String phone) {
        Phone = phone;
    }

    public String getType() {
        return Type;
    }

    public void setType(String type) {
        Type = type;
    }
}
