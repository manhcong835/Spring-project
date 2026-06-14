package com.spring.project.dto;

public class RevenueByTour {

    private final String tourName;
    private final long transactionCount;
    private final String amountText;

    public RevenueByTour(String tourName, long transactionCount, String amountText) {
        this.tourName = tourName;
        this.transactionCount = transactionCount;
        this.amountText = amountText;
    }

    public String getTourName() { return tourName; }
    public long getTransactionCount() { return transactionCount; }
    public String getAmountText() { return amountText; }
}
