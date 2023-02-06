package com.example.disk1.repositories;

import com.example.disk1.models.SystemItem;
import org.springframework.data.repository.CrudRepository;

import java.util.Date;
import java.util.List;

public interface SystemItemRepository
        extends CrudRepository<SystemItem, String> {
    List<SystemItem> findAllByParentId(String parentId);
    List<SystemItem> findByCreatedAtBetween(Date start, Date end);
}