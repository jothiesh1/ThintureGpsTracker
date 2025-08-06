package com.GpsTracker.Thinture.controller;

import com.GpsTracker.Thinture.dto.DeviceLogDTO;
import com.GpsTracker.Thinture.service.DeviceLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import org.springframework.ui.Model;

@Controller
public class DeviceLogController {

    @Autowired
    private DeviceLogService logService;

    // 👇 Load the HTML page
    @GetMapping("/device_logs")
    public String showDeviceLogPage() {
        return "device_logs";  // This looks for device_logs.html in /resources/templates
    }

    @ResponseBody
    @GetMapping("/device-logs-data")
    public List<DeviceLogDTO> getLogsByDate(
            @RequestParam String deviceID,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return logService.fetchLogsByDate(deviceID, date);
    }

}
