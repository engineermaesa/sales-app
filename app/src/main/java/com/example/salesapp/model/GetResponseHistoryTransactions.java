package com.example.salesapp.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class GetResponseHistoryTransactions {
    @SerializedName("transaction")
    @Expose
    private HistoryTransactions historyTransactions;

    public HistoryTransactions getHistoryTransactions() {
        return historyTransactions;
    }
}
