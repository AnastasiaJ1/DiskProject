package com.example.disk1.services;

import com.example.disk1.models.SystemItem;
import com.example.disk1.models.SystemItemHistory;
import com.example.disk1.models.SystemItemImportRequest;
import org.json.simple.JSONObject;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface DiskService {
    public int deleteNode(String id);

    public JSONObject getNode(String id);

    public JSONObject getNodeHistory(SystemItemHistory item);

    public void updateParent(String childId, String date, String id, Long size, Date d);

    public void updateParentDel(String childId, String date, String id, Long size, Date d);

    public Optional<SystemItem> findByIdSystemItem(String id);

    public List<SystemItem> findAllByParentIdSystemItem(String parentId);

    public List<SystemItem> findByCreatedAtBetweenSystemItem(Date start, Date end);

    public List<SystemItemHistory> findAllByRealIdSystemItemHistory(String realId);

    long deleteAllByRealIdSystemItemHistory(String realId);

    public List<SystemItemHistory> findByCreatedAtBetweenSystemItemHistory(Date start, Date end);

    public Optional<SystemItemHistory> findByIdSystemItemHistory(Long id);

    public void saveSystemItem(SystemItem sysItem);

    public void saveSystemItemHistory(SystemItemHistory sysItemHistory);


   Boolean checkAndFillSystemItemImportRequest(SystemItemImportRequest request, Date importDate);
}
