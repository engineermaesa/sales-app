package com.example.salesapp.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class GetResponseVisit {
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("data")
    @Expose
    private Page data;
    @SerializedName("visit_performance")
    @Expose
    private Performance visit_performance;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Page getData() {
        return data;
    }

    public void setData(Page data) {
        this.data = data;
    }

    public void setVisit_performance(Performance visit_performance) {
        this.visit_performance = visit_performance;
    }

    public Performance getVisit_performance() {
        return visit_performance;
    }
}
