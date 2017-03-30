package com.ribic.nejc.veselica.objects;

import java.util.ArrayList;

public class Date {
    private String date;
    private ArrayList<Party> places;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public ArrayList<Party> getPlaces() {
        return places;
    }

    public void setPlaces(ArrayList<Party> places) {
        this.places = places;
    }
}
