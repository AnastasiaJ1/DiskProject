package com.example.disk1.repositories;

import com.example.disk1.models.SystemItemHistory;
import org.springframework.data.repository.CrudRepository;

import java.util.Date;
import java.util.List;

public interface SystemItemHistoryRepository
        extends CrudRepository<SystemItemHistory, Long> {
    List<SystemItemHistory> findAllByRealId(String realId);
    long deleteAllByRealId(String realId);
    List<SystemItemHistory> findByCreatedAtBetween(Date start, Date end);
}
