package com.example.disk1.models;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
@Data
@Entity
public class SystemItem {
    @Id
    private String id;
    private String url;
    private String date;
    private String parentId;
    private String type;
    private Long size;
    private String children;
    @Column(name="createdAt")
    private Date createdAt;

}
