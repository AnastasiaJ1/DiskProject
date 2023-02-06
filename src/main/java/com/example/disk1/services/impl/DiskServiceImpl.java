package com.example.disk1.services.impl;

import com.example.disk1.models.SystemItem;
import com.example.disk1.models.SystemItemHistory;
import com.example.disk1.models.SystemItemImport;
import com.example.disk1.models.SystemItemImportRequest;
import com.example.disk1.repositories.SystemItemHistoryRepository;
import com.example.disk1.repositories.SystemItemRepository;
import com.example.disk1.services.DiskService;
import org.json.simple.JSONObject;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class DiskServiceImpl implements DiskService {
    private final SystemItemRepository repository;

    private final SystemItemHistoryRepository repository_history;

    public DiskServiceImpl(SystemItemRepository repository, SystemItemHistoryRepository repository_history) {
        this.repository = repository;
        this.repository_history = repository_history;
    }

    @Override
    public int deleteNode(String id){
        int res = 0;
        try {
            for (SystemItem ch : repository.findAllByParentId(id)) {
                if (ch.getId() != null) {
                    res += deleteNode(ch.getId());
                } else {
                    res += 1;
                }
            }
            if (repository.findById(id) != null) {
                repository.deleteById(id);
                for(SystemItemHistory it: repository_history.findAllByRealId(id)) {
                    repository_history.deleteById(it.getId());
                }
            } else {
                res += 1;
            }
        } catch (Exception e){
            return 1;
        }
        return res;


    }

    @Override
    public JSONObject getNode(String id){
        try {
            JSONObject node = new JSONObject();
            SystemItem item = repository.findById(id).orElseGet(null);
            node.put("type", item.getType());
            node.put("id", item.getId());
            node.put("size", item.getSize());
            node.put("url", item.getUrl());
            node.put("parentId", item.getParentId());
            node.put("date", item.getDate());
            ArrayList<JSONObject> children = new ArrayList<>();
            for (SystemItem ch : repository.findAllByParentId(id)) {
                children.add(getNode(ch.getId()));
            }
            if (children.size() == 0) {
                if (item.getType() == "FOLDER") {
                    node.put("children", children);
                } else {
                    node.put("children", null);
                }
            } else {
                node.put("children", children);
            }

            return node;
        } catch (Exception e){
            return null;
        }


    }

    @Override
    public JSONObject getNodeHistory(SystemItemHistory item){
        try {
            JSONObject node = new JSONObject();
            node.put("type", item.getType());
            node.put("id", item.getRealId());
            node.put("size", item.getSize());
            node.put("url", item.getUrl());
            node.put("parentId", item.getParentId());
            node.put("date", item.getDate());

            return node;
        } catch (Exception e){
            return null;
        }


    }

    @Override
    public void updateParent(String childId, String date, String id, Long size, Date d){
        if(id != null) {
            SystemItem item = repository.findById(id).orElseGet(null);
            SystemItemHistory itemHistory = new SystemItemHistory();
            SystemItem childItem = repository.findById(childId).orElseGet(null);
            long b = item.getSize() != null ? item.getSize() : (long) 0;
            Long res = (Long) size + b;
            item.setSize(res);
            item.setDate(date);
            item.setCreatedAt(d);
            itemHistory.setRealId(item.getId());
            itemHistory.setType(item.getType());
            itemHistory.setUrl(item.getUrl());
            itemHistory.setParentId(item.getParentId());
            itemHistory.setSize(item.getSize());
            itemHistory.setDate(item.getDate());
            itemHistory.setCreatedAt(d);
            repository.save(item);
            repository_history.save(itemHistory);
            if (item.getParentId() != null) {
                updateParent(id, date, item.getParentId(), size, d);
            }
        }


    }

    @Override
    public void updateParentDel(String childId, String date, String id, Long size, Date d){
        if(id != null) {
            SystemItem item = repository.findById(id).orElseGet(null);
            SystemItemHistory itemHistory = new SystemItemHistory();
            long b = item.getSize() != null ? item.getSize() : (long) 0;
            Long res = - (Long) size + b;
            item.setSize(res);
            item.setDate(date);
            item.setCreatedAt(d);
            itemHistory.setRealId(item.getId());
            itemHistory.setType(item.getType());
            itemHistory.setUrl(item.getUrl());
            itemHistory.setParentId(item.getParentId());
            itemHistory.setSize(item.getSize());
            itemHistory.setDate(item.getDate());
            itemHistory.setCreatedAt(item.getCreatedAt());
            repository.save(item);
            repository_history.save(itemHistory);
            if (item.getParentId() != null) {
                updateParentDel(id, date, item.getParentId(), size, d);
            }
        }


    }

    @Override
    public Optional<SystemItem> findByIdSystemItem(String id) {
        return repository.findById(id);
    }

    @Override
    public List<SystemItem> findAllByParentIdSystemItem(String parentId) {
        return repository.findAllByParentId(parentId);
    }

    @Override
    public List<SystemItem> findByCreatedAtBetweenSystemItem(Date start, Date end) {
        return repository.findByCreatedAtBetween(start, end);
    }

    @Override
    public List<SystemItemHistory> findAllByRealIdSystemItemHistory(String realId) {
        return repository_history.findAllByRealId(realId);
    }

    @Override
    public long deleteAllByRealIdSystemItemHistory(String realId) {
        return repository_history.deleteAllByRealId(realId);
    }

    @Override
    public List<SystemItemHistory> findByCreatedAtBetweenSystemItemHistory(Date start, Date end) {
        return repository_history.findByCreatedAtBetween(start, end);
    }

    @Override
    public Optional<SystemItemHistory> findByIdSystemItemHistory(Long id) {
        return repository_history.findById(id);
    }
    @Override
    public void saveSystemItem(SystemItem sysItem) {
        repository.save(sysItem);
    }
    @Override
    public void saveSystemItemHistory(SystemItemHistory sysItemHistory) {
        repository_history.save(sysItemHistory);
    }

    @Override
    public Boolean checkAndFillSystemItemImportRequest(SystemItemImportRequest request, Date importDate) {
        Map<String,String> list_id1 = new HashMap<>();
        List<SystemItem> systemItemList = new ArrayList<>();
        List<SystemItemHistory> systemItemHistories = new ArrayList<>();
        List<List<Object>> result = new ArrayList<>();
        for (SystemItemImport itemCurrent : request.getItems()) {
            SystemItem sysItem = new SystemItem();
            SystemItemHistory sysItemHistory = new SystemItemHistory();
            if (itemCurrent.getId() != null){
                sysItem.setId(itemCurrent.getId());
                sysItemHistory.setRealId(itemCurrent.getId());
            }
            if (itemCurrent.getType() != null){
                sysItem.setType(itemCurrent.getType());
                sysItemHistory.setType(itemCurrent.getType());
            }

            if (itemCurrent.getUrl() != null){
                sysItem.setUrl(itemCurrent.getUrl());
                sysItemHistory.setUrl(itemCurrent.getUrl());
            }

            if (importDate != null){
                sysItem.setDate(request.getUpdateDate());
                sysItemHistory.setDate(request.getUpdateDate());
            }
            if (itemCurrent.getParentId() != null){
                sysItem.setParentId(itemCurrent.getParentId());
                sysItemHistory.setParentId(itemCurrent.getParentId());
            }
            if (itemCurrent.getSize() != null){
                sysItem.setSize(itemCurrent.getSize());
                sysItemHistory.setSize(itemCurrent.getSize());
            } else {
                sysItem.setSize((long) 0);
                sysItemHistory.setSize((long) 0);
            }
            sysItem.setCreatedAt(importDate);
            sysItemHistory.setCreatedAt(importDate);

            if (sysItem.getId() == null || sysItem.getType() == null) {
                System.out.println("sysItem.getId() == null || sysItem.getType() == null");
                return false;
            }

            //размер поля url при импорте файла всегда должен быть меньше либо равным 255
            if(sysItem.getUrl()!= null && sysItem.getUrl().length()>255){
                System.out.println("sysItem.getUrl()!= null && sysItem.getUrl().length()>255");
                return false;
            }

            //id каждого элемента является уникальным среди остальных элементов
            if (list_id1.containsKey(itemCurrent.getId())){
                System.out.println("list_id1.containsKey(itemCurrent.getId())");
                return false;
            }
            list_id1.put(itemCurrent.getId(),itemCurrent.getType());

              //  изменение типа елем не допускается
            if(findByIdSystemItem(sysItem.getId()).orElse(null) != null){
                SystemItem prevItem = findByIdSystemItem(sysItem.getId()).orElseGet(null);
                if(!sysItem.getType().equals(prevItem.getType())){
                    System.out.println("!sysItem.getType().equals(prevItem.getType())");
                    return false;
                }
            }

            //поле url при импорте папки всегда должно быть равно null
            if(sysItem.getType().equals((String) "FOLDER") && sysItem.getUrl() != null){
                System.out.println("sysItem.getType().equals((String) \"FOLDER\") && sysItem.getUrl() != null");
                return false;
            }

                //поле size при импорте папки всегда должно быть равно null
            if(sysItem.getType().equals((String) "FOLDER")  && itemCurrent.getSize() != null){
                System.out.println("sysItem.getType().equals((String) \"FOLDER\")  && itemCurrent.getSize() != null");
                return false;
            }
            //поле size для файлов всегда должно быть больше 0
            if(sysItem.getType().equals((String) "FILE") && (itemCurrent.getSize() == null || sysItem.getSize() <= 0)){
                System.out.println("sysItem.getType().equals((String) \"FILE\") && (itemCurrent.getSize() == null || sysItem.getSize() <= 0)");
                return false;
            }

            //тип FILE или FOLDER
            if (!sysItem.getType().equals((String) "FILE")  && !sysItem.getType().equals((String) "FOLDER")){
                System.out.println("!sysItem.getType().equals((String) \"FILE\")  && !sysItem.getType().equals((String) \"FOLDER\")");
                return false;
            }
                //родителем мб только папка

            if (sysItem.getParentId() != null) {
                if (list_id1.get(sysItem.getParentId()) != null) {
                    if (!(list_id1.get(sysItem.getParentId())).equals("FOLDER")) {
                        System.out.println("!(list_id1.get(sysItem.getParentId())).equals(\"FOLDER\")");
                        return false;
                    }
                } else {
                    SystemItem pItem = findByIdSystemItem(sysItem.getParentId()).orElseGet(null);
                    if (!Objects.equals(pItem.getType(), "FOLDER")) {
                        System.out.println("!Objects.equals(pItem.getType(), \"FOLDER\")");
                        return false;
                    }
                }
            }

            systemItemList.add(sysItem);
            systemItemHistories.add(sysItemHistory);
        }
        for(int i = 0; i < systemItemList.size(); i++){
            SystemItem systemItem = systemItemList.get(i);
            SystemItemHistory systemItemHistory = systemItemHistories.get(i);
            saveSystemItem(systemItem);
            saveSystemItemHistory(systemItemHistory);
            updateParent(systemItem.getId(), request.getUpdateDate(), systemItemHistory.getParentId(), systemItem.getSize(), systemItem.getCreatedAt());
        }
        return true;
        }
}
