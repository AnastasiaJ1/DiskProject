package com.example.disk1.models;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
public class SystemItemHistory {
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO, generator="my_entity_seq_gen")
    @SequenceGenerator(name="my_entity_seq_gen", sequenceName="MY_ENTITY_SEQ")
    private long id;
    @Column(name="realId")
    private String realId;
    private String url;
    private String date;
    private String parentId;
    private String type;
    private Long size;
    @Column(name="createdAt")
    private Date createdAt;

}
