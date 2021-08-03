package com.example.salesapp.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class GetResponseHistoryTransactionsList {
    @SerializedName("transaction")
    @Expose
    private List<HistoryTransactions> historyTransactionsList;

    public List<HistoryTransactions> getHistoryTransactionsList() {
        return historyTransactionsList;
    }
}
