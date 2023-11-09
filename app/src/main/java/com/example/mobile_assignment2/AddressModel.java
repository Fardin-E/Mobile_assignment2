package com.example.mobile_assignment2;

public class AddressModel {
    private int id;
    private String Address;
    private double latitude;
    private double longitude;

    // constructors

    public AddressModel(int id, String address, double latitude, double longitude) {
        this.id = id;
        Address = address;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public AddressModel(String address, double latitude, double longitude) {
        Address = address;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public AddressModel(){

    }
    //toString is necessary for printing the contents of a class object


    @Override
    public String toString() {
        return "AddressModel{" +
                "id=" + id +
                ", Address='" + Address + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                '}';
    }

    // getters and setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
