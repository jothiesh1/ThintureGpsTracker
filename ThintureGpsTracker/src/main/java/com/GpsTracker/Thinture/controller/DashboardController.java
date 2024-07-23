package com.GpsTracker.Thinture.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        String mapScript = "<script>\n" +
                "    var map = L.map('map').setView([51.505, -0.09], 13);\n" +
                "    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {\n" +
                "        attribution: '&copy; <a href=\"https://www.openstreetmap.org/copyright\">OpenStreetMap</a> contributors'\n" +
                "    }).addTo(map);\n" +
                "    var marker = L.marker([51.5, -0.09]).addTo(map)\n" +
                "        .bindPopup('A pretty CSS3 popup.<br> Easily customizable.')\n" +
                "        .openPopup();\n" +
                "    var socket = new SockJS('/gs-guide-websocket');\n" +
                "    var stompClient = Stomp.over(socket);\n" +
                "    stompClient.connect({}, function (frame) {\n" +
                "        stompClient.subscribe('/topic/location-updates', function (locationUpdate) {\n" +
                "            var data = JSON.parse(locationUpdate.body);\n" +
                "            var lat = data.latitude;\n" +
                "            var lon = data.longitude;\n" +
                "            marker.setLatLng([lat, lon])\n" +
                "                .bindPopup('Device ID: ' + data.deviceID + '<br>Timestamp: ' + data.timestamp)\n" +
                "                .openPopup();\n" +
                "            map.setView([lat, lon], 13);\n" +
                "        });\n" +
                "    });\n" +
                "</script>";

        model.addAttribute("mapScript", mapScript);
        return "dashboard";
    }
}