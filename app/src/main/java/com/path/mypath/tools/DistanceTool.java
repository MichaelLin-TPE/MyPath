package com.path.mypath.tools;

import java.util.ArrayList;

public class DistanceTool {


    private static DistanceTool instance = null;

    private DistanceTool(){

    }

    public static DistanceTool getInstance(){
        if (instance == null){
            instance = new DistanceTool();
        }
        return instance;
    }

    public int getZoomKmLevel(double distance){

        int zoomLevel = 0;

        if (distance > 1000){
            distance = distance / 1000;
            zoomLevel = getZoomKm(distance);
        }else {
            zoomLevel = getZoomMeter(distance);
        }
        return zoomLevel;
    }

    private int getZoomMeter(double distance) {
        int zoom = 0;
        if (distance >= 500 && distance < 1000){
            zoom = 14;
        }else if (distance >= 400 && distance < 500){
            zoom = 15;
        }else if (distance >= 200 && distance < 400){
            zoom = 16;
        }else if (distance >= 100 && distance < 200){
            zoom = 17;
        }else if (distance >= 50 && distance < 100){
            zoom = 18;
        }else if (distance >= 20 && distance < 50){
            zoom = 19;
        }else if (distance >= 10 && distance < 20){
            zoom = 20;
        }else if (distance >= 5 && distance < 10){
            zoom = 21;
        }else if (distance >= 2 && distance < 5){
            zoom = 22;
        }else if (distance >= 0 && distance < 2){
            zoom = 23;
        }
        return zoom;
    }

    private int getZoomKm(double distance){
        int zoom = 0;
        if (distance >= 1 && distance < 2){
            zoom = 13;
        }else if (distance >= 2 && distance < 5){
            zoom = 12;
        }else if (distance >= 5 && distance < 10){
            zoom = 11;
        }else if (distance >= 10 && distance < 20){
            zoom = 10;
        }else if (distance >= 20 && distance < 50){
            zoom = 9;
        }else if (distance >= 50 && distance < 100){
            zoom = 8;
        }else if (distance >= 100 && distance < 200){
            zoom = 7;
        }else if (distance >= 200 && distance < 400){
            zoom = 6;
        }else if (distance >= 400 && distance < 500){
            zoom = 5;
        }else if (distance >= 500 && distance < 1000){
            zoom = 4;
        }else if (distance >= 1000 && distance < 2000){
            zoom = 3;
        }else if (distance >= 2000 && distance < 5000){
            zoom = 2;
        }else if (distance >= 5000 && distance < 10000){
            zoom = 1;
        }else if (distance >= 10000){
            zoom = 0;
        }
        return zoom;
    }



}
