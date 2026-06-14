package com.spring.project.dto;

import java.util.List;

public class RevenueReportView {

    private final String fromText;
    private final String toText;
    private final String fromValue;
    private final String toValue;
    private final String groupBy;
    private final String totalText;
    private final long transactionCount;
    private final List<RevenuePoint> timeline;
    private final List<RevenueByTour> topTours;
    private final List<RevenueByMethod> byMethod;

    public RevenueReportView(String fromText, String toText, String fromValue, String toValue,
                             String groupBy, String totalText, long transactionCount,
                             List<RevenuePoint> timeline, List<RevenueByTour> topTours,
                             List<RevenueByMethod> byMethod) {
        this.fromText = fromText;
        this.toText = toText;
        this.fromValue = fromValue;
        this.toValue = toValue;
        this.groupBy = groupBy;
        this.totalText = totalText;
        this.transactionCount = transactionCount;
        this.timeline = timeline;
        this.topTours = topTours;
        this.byMethod = byMethod;
    }

    public String getFromText() { return fromText; }
    public String getToText() { return toText; }
    public String getFromValue() { return fromValue; }
    public String getToValue() { return toValue; }
    public String getGroupBy() { return groupBy; }
    public String getTotalText() { return totalText; }
    public long getTransactionCount() { return transactionCount; }
    public List<RevenuePoint> getTimeline() { return timeline; }
    public List<RevenueByTour> getTopTours() { return topTours; }
    public List<RevenueByMethod> getByMethod() { return byMethod; }
}
