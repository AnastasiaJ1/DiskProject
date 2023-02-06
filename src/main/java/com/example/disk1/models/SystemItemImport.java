package com.example.disk1.models;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.Column;
import javax.persistence.Entity;

public class SystemItemImport {
    private String id;
    private String url;
    private String parentId;

    private String type;
    private Long size;

    public SystemItemImport(String id, String url, String parentId, String type, Long size) {
        this.id = id;
        this.url = url;
        this.parentId = parentId;
        this.type = type;
        this.size = size;
    }


    public void setId(String id) {
        this.id = id;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public void setSize(Long size) {
        this.size = size;
    }

    public String getId() {
        return id;
    }

    public String getUrl() {
        return url;
    }

    public String getParentId() {
        return parentId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Long getSize() {
        return size;
    }
}
