package com.spring.project.dto;

public class RevenuePoint {

    private final String label;
    private final String amountText;
    private final long amountRaw;
    private final int barHeightPercent;

    public RevenuePoint(String label, String amountText, long amountRaw, int barHeightPercent) {
        this.label = label;
        this.amountText = amountText;
        this.amountRaw = amountRaw;
        this.barHeightPercent = barHeightPercent;
    }

    public String getLabel() { return label; }
    public String getAmountText() { return amountText; }
    public long getAmountRaw() { return amountRaw; }
    public int getBarHeightPercent() { return barHeightPercent; }
}
