package com.weetbix.rabobank.branches.generate;


public class Branch {
    public String name;
    public String country;
    public String address;
    public double[] latLng;
    public int ID;

    public Branch(String name, String country, String address, double[] latLng, int id) {
        this.name = name;
        this.country = country;
        this.address = address;
        this.latLng = latLng;
        this.ID = id;
    }
}
