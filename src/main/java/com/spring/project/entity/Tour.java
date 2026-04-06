package com.spring.project.entity;

import java.time.LocalDateTime;

public class Tour {
    private int id;
    private int categoryId;
    private int destinationId;
    private String code;
    private String name;
    private String slug;
    private String departureLocation;
    private String durationDay;
    private String durationNight;
    private String transport;
    private String hotelStandard;
    private String description;
    private String policy;
    private String includedServices;
    private String excludedServices;
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Tour() {
    }

    public Tour(int id, int categoryId, int destinationId, String code, String name, String slug,
            String departureLocation, String durationDay, String durationNight, String transport, String hotelStandard,
            String description, String policy, String includedServices, String excludedServices, String notes,
            LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.categoryId = categoryId;
        this.destinationId = destinationId;
        this.code = code;
        this.name = name;
        this.slug = slug;
        this.departureLocation = departureLocation;
        this.durationDay = durationDay;
        this.durationNight = durationNight;
        this.transport = transport;
        this.hotelStandard = hotelStandard;
        this.description = description;
        this.policy = policy;
        this.includedServices = includedServices;
        this.excludedServices = excludedServices;
        this.notes = notes;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public int getDestinationId() {
        return destinationId;
    }

    public void setDestinationId(int destinationId) {
        this.destinationId = destinationId;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getDepartureLocation() {
        return departureLocation;
    }

    public void setDepartureLocation(String departureLocation) {
        this.departureLocation = departureLocation;
    }

    public String getDurationDay() {
        return durationDay;
    }

    public void setDurationDay(String durationDay) {
        this.durationDay = durationDay;
    }

    public String getDurationNight() {
        return durationNight;
    }

    public void setDurationNight(String durationNight) {
        this.durationNight = durationNight;
    }

    public String getTransport() {
        return transport;
    }

    public void setTransport(String transport) {
        this.transport = transport;
    }

    public String getHotelStandard() {
        return hotelStandard;
    }

    public void setHotelStandard(String hotelStandard) {
        this.hotelStandard = hotelStandard;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPolicy() {
        return policy;
    }

    public void setPolicy(String policy) {
        this.policy = policy;
    }

    public String getIncludedServices() {
        return includedServices;
    }

    public void setIncludedServices(String includedServices) {
        this.includedServices = includedServices;
    }

    public String getExcludedServices() {
        return excludedServices;
    }

    public void setExcludedServices(String excludedServices) {
        this.excludedServices = excludedServices;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

}
