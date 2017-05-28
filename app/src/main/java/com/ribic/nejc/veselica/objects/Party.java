package com.ribic.nejc.veselica.objects;

public class Party {
    private String date;
    private String place;
    private String href;
    private String id;

    public Party(){}

    public Party(String date, String place, String href, String id){
        setDate(date);
        setPlace(place);
        setHref(href);
        setId(id);
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return String.format("%s@%s@%s@%s", this.date, this.place, this.href, this.id);
    }
}
