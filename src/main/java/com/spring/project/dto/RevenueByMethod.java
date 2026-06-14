package com.spring.project.dto;

public class RevenueByMethod {

    private final String methodCode;
    private final String methodLabel;
    private final long transactionCount;
    private final String amountText;
    private final int percentOfTotal;

    public RevenueByMethod(String methodCode, String methodLabel, long transactionCount,
                          String amountText, int percentOfTotal) {
        this.methodCode = methodCode;
        this.methodLabel = methodLabel;
        this.transactionCount = transactionCount;
        this.amountText = amountText;
        this.percentOfTotal = percentOfTotal;
    }

    public String getMethodCode() { return methodCode; }
    public String getMethodLabel() { return methodLabel; }
    public long getTransactionCount() { return transactionCount; }
    public String getAmountText() { return amountText; }
    public int getPercentOfTotal() { return percentOfTotal; }
}
