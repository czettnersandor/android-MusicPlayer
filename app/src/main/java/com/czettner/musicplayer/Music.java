package com.czettner.musicplayer;

public class Music {
    public String title;
    public String album;
    public String fileName;

    Music(String title, String album) {
        this.title = title;
        this.album = album;
    }

    Music(String fileName) {
        this.fileName = fileName;
        // TODO: fill in title, album from ID3
    }
}
