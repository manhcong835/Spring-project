package com.spring.project.entity;

import java.time.LocalDateTime;

public class Tour_image {
    private int id;
    private int tourId;
    private String imageUrl;
    private String altText;
    private boolean isThumbnail;
    private int sortOrder;
    private LocalDateTime createdAt;

    public Tour_image() {
    }

    public Tour_image(int id, int tourId, String imageUrl, String altText, boolean isThumbnail, int sortOrder,
            LocalDateTime createdAt) {
        this.id = id;
        this.tourId = tourId;
        this.imageUrl = imageUrl;
        this.altText = altText;
        this.isThumbnail = isThumbnail;
        this.sortOrder = sortOrder;
        this.createdAt = createdAt;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getTourId() {
        return tourId;
    }

    public void setTourId(int tourId) {
        this.tourId = tourId;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getAltText() {
        return altText;
    }

    public void setAltText(String altText) {
        this.altText = altText;
    }

    public boolean isThumbnail() {
        return isThumbnail;
    }

    public void setThumbnail(boolean isThumbnail) {
        this.isThumbnail = isThumbnail;
    }

    public int getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(int sortOrder) {
        this.sortOrder = sortOrder;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

}
