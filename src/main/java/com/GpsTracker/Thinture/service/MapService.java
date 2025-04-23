package com.GpsTracker.Thinture.service;
import org.springframework.stereotype.Service;

import java.util.*;
/*
**********************************Developer Jothiesh **********************
*                                                                         /
*                                                                         /
***************************************************************************
*
*
*                                                                         /
*
***************************************************************************
*
*/
//
@Service
public class MapService {

    public String getMapInitializationScript() {
        return "var map = L.map('map').setView([51.505, -0.09], 13);\n" +
               "L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {\n" +
               "    attribution: '&copy; <a href=\"https://www.openstreetmap.org/copyright\">OpenStreetMap</a> contributors'\n" +
               "}).addTo(map);\n" +
               "L.marker([51.5, -0.09]).addTo(map)\n" +
               "    .bindPopup('A pretty CSS3 popup.<br> Easily customizable.')\n" +
               "    .openPopup();";
              //  " <a href=\"https://www.openstreetmap.org/copyright\">OpenStreetMap</a> contributors' \n "
    }
    
}