package com.example.disk1.services.impl;

import com.example.disk1.services.DateHandlerService;
import org.springframework.stereotype.Service;

import javax.xml.bind.DatatypeConverter;
import java.text.SimpleDateFormat;
import java.util.Date;
@Service
public class DateHandlerServiceImpl implements DateHandlerService {

    @Override
    public Date handle(String date) {
        Date importDate;
        try {
            SimpleDateFormat s = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            importDate = s.parse(date);
            return importDate;
        } catch (Exception ignored) {
        }
        try {
                SimpleDateFormat s = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
                importDate = s.parse(date);
                return importDate;
        } catch (Exception e) {
            return null;
        }
    }
}
