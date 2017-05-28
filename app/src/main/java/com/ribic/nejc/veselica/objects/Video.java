package com.ribic.nejc.veselica.objects;

public class Video {
    private String title;
    private String videoUrl;
    private String imgUrl;

    public Video(String title, String imgUrl, String videoUrl) {
        this.title = title;
        this.imgUrl = imgUrl;
        this.videoUrl = videoUrl;
    }

    public String getTitle() {
        return title;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public String getImgUrl() {
        return imgUrl;
    }
}
