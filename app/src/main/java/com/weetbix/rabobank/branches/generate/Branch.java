package com.weetbix.rabobank.branches.generate;

/**
 * Created by varun on 13/10/2017.
 */

public class Branch {
    public String name;
    public String country;
    public String address;
    public double[] latLng;

    public Branch(String name, String country, String address, double[] latLng) {
        this.name = name;
        this.country = country;
        this.address = address;
        this.latLng = latLng;
    }
}
