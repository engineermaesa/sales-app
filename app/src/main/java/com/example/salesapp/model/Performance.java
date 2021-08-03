package com.example.salesapp.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Performance {
    @SerializedName("achieved")
    @Expose
    private Integer achieved;
    @SerializedName("target_low")
    @Expose
    private Integer targetLow;
    @SerializedName("target_middle")
    @Expose
    private Integer targetMidle;
    @SerializedName("target_hight")
    @Expose
    private Integer targetHigh;

    public Integer getAchieved() {
        return achieved;
    }

    public void setAchieved(Integer achieved) {
        this.achieved = achieved;
    }

    public Integer getTargetLow() {
        return targetLow;
    }

    public void setTargetLow(Integer targetLow) {
        this.targetLow = targetLow;
    }

    public Integer getTargetMidle() {
        return targetMidle;
    }

    public void setTargetMidle(Integer targetMidle) {
        this.targetMidle = targetMidle;
    }

    public Integer getTargetHigh() {
        return targetHigh;
    }

    public void setTargetHigh(Integer targetHigh) {
        this.targetHigh = targetHigh;
    }
}
