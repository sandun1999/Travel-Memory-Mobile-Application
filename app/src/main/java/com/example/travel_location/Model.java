package com.example.travel_location;

public class Model {
    private String street;
    private String city;
    private String country;
    private String imageUrl;

    private String key; // Firebase unique key

    private double Latitude;

    // Required default constructor for Firebase
    public Model() {
    }

    public Model(String street, String city, String country, String imageUrl) {
        this.street = street;
        this.city = city;
        this.country = country;
        this.imageUrl = imageUrl;
    }

    // Add a getter and setter for 'key'
    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }


    public String getStreet() {
        return street;
    }

    public String getCity() {
        return city;
    }

    public String getCountry() {
        return country;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public double getLatitude() {
        return 0;
    }

    public double getLongitude() {
        return 0;
    }

    public String getAddress() {
        return null;
    }
}
