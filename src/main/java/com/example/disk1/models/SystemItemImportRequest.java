package com.example.disk1.models;

import com.example.disk1.models.SystemItemImport;

import java.util.ArrayList;
import java.util.List;

public class SystemItemImportRequest {
    private List<SystemItemImport> items;
    private String updateDate;

    public SystemItemImportRequest(ArrayList<SystemItemImport> items, String updateDate) {
        this.items = items;
        this.updateDate = updateDate;
    }

    public List<SystemItemImport> getItems(){
        return items;
    }

    public void setItems(List<SystemItemImport> items) {
        this.items = items;
    }

    public void setUpdateDate(String updateDate) {
        this.updateDate = updateDate;
    }

    public String getUpdateDate(){
        return updateDate;
    }
}
