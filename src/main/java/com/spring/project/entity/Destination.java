package com.spring.project.entity;

public class Destination {
    private int id;
    private String name;
    private String province;
    private String country;
    private String description;
    private String imageUrl;

    public Destination() {
    }

    public Destination(int id, String name, String province, String country, String description, String imageUrl) {
        this.id = id;
        this.name = name;
        this.province = province;
        this.country = country;
        this.description = description;
        this.imageUrl = imageUrl;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

}
