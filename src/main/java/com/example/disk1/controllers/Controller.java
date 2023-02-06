package com.example.disk1.controllers;


import com.example.disk1.errors.Error;
import com.example.disk1.exceptions.InputFormatException;
import com.example.disk1.models.SystemItem;
import com.example.disk1.models.SystemItemHistory;
import com.example.disk1.models.SystemItemImportRequest;
import com.example.disk1.services.impl.DateHandlerServiceImpl;
import com.example.disk1.services.impl.DiskServiceImpl;
import org.joda.time.DateTime;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping()
public class Controller {
    @Autowired
    private DiskServiceImpl diskService;
    @Autowired
    private DateHandlerServiceImpl dateHandlerService;




    @PostMapping("/imports")
    public ResponseEntity<?> imports(@RequestBody SystemItemImportRequest request){
        try {
            Date importDate = dateHandlerService.handle(request.getUpdateDate());
            if (importDate == null){
                throw new InputFormatException("Validation Failed");
            }

            // check all items
            if (!diskService.checkAndFillSystemItemImportRequest(request, importDate)) {
                throw new InputFormatException("Validation Failed");
            }
        } catch (InputFormatException e) {
            return ResponseEntity.status(400).body(new Error(400, "Validation Failed"));
        }
        return ResponseEntity.status(200).body(new Error(200, "Successful import"));

    }




    @DeleteMapping("/delete/{id}")
    public  ResponseEntity<?>  deleteItem(@PathVariable(name = "id") String id, @RequestParam(value = "date", required=false) String date) {
        int res = 0;
        try {
            Date sDate = dateHandlerService.handle(date);
            if (sDate == null){
                return ResponseEntity.status(400).body(new Error(400, "Validation Failed"));
            }
            SystemItem sysItem;
            try {
                sysItem = diskService.findByIdSystemItem(id).orElseGet(null);
            } catch (Exception e){
                return ResponseEntity.status(404).body(new Error(404, "Item not found"));
            }
            if (sysItem != null) {
                res = diskService.deleteNode(id);
                if (res != 0){
                    return ResponseEntity.status(400).body(new Error(400, "Validation Failed3"));
                }
                diskService.updateParentDel(sysItem.getId(), date, sysItem.getParentId(), sysItem.getSize(),sDate);
            } else {
                return ResponseEntity.status(404).body(new Error(404, "Item not found"));
            }
        } catch (Exception e) {
            return ResponseEntity.status(400).body(new Error(400, "Validation Failed1"));
        }

        return ResponseEntity.status(200).body(new Error(200, "Successful delete"));
    }


    @GetMapping("/nodes/{id}")
    public ResponseEntity<?> getItem(@PathVariable(value = "id") String id){
        JSONObject node;
        try {
            node = diskService.getNode(id);
            if (node == null){
                return ResponseEntity.status(404).body(new Error(404, "Item not found"));
            }
        } catch (Exception e) {
            return ResponseEntity.status(400).body(new Error(400, "Validation Failed"));
        }
        return ResponseEntity.status(200).body(node);
    }



    @GetMapping("/updates")
    public ResponseEntity<?> updates(@RequestParam(value = "date", required=false) String date) {
        try {
            Date importDate = dateHandlerService.handle(date);
            if (importDate == null) {
                throw new InputFormatException("Validation Failed");
            }
            JSONObject node = new JSONObject();
            ArrayList<JSONObject> res= new ArrayList<>();

            for (SystemItem it: diskService.findByCreatedAtBetweenSystemItem(new DateTime(importDate).minusDays(1).toDate(), importDate)){
                JSONObject temp = new JSONObject();
                temp.put("type", it.getType());
                temp.put("id", it.getId());
                temp.put("size", it.getSize());
                temp.put("url", it.getUrl());
                temp.put("parentId", it.getParentId());
                temp.put("date", it.getDate());
                res.add(temp);
            }
            node.put("items", res);
            return ResponseEntity.status(200).body(node);

        } catch (InputFormatException e){
            return ResponseEntity.status(400).body(new Error(400, "Validation Failed"));
        }
    }

    @GetMapping("/node/{id}/history")
    public ResponseEntity<?>  historyItem(@PathVariable(name = "id") String id, @RequestParam(value = "dateStart", required=false) String dateStart, @RequestParam(value = "dateEnd", required=false) String dateEnd) {
        JSONObject node;
        ArrayList<JSONObject> nodes = new ArrayList<>();
        JSONObject res = new JSONObject();

        try {
            Date sDate = dateHandlerService.handle(dateStart);
            Date eDate = dateHandlerService.handle(dateEnd);
            if (sDate==null || eDate==null){
                throw new InputFormatException("Validation Failed");
            }

            for(SystemItemHistory item: diskService.findAllByRealIdSystemItemHistory(id)){
                if (!sDate.after(item.getCreatedAt()) && eDate.after(item.getCreatedAt())) {
                    node = diskService.getNodeHistory(item);
                    nodes.add(node);
                }
            }
            res.put("items", nodes);
            if (!(diskService.findAllByRealIdSystemItemHistory(id).size() > 0)){
                return ResponseEntity.status(404).body(new Error(404, "Item not found"));
            }
        } catch (InputFormatException e) {
            return ResponseEntity.status(400).body(new Error(400, "Validation Failed"));
        }
        return ResponseEntity.status(200).body(res);
    }


    }
