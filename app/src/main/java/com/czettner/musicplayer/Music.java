package com.czettner.musicplayer;

import android.media.MediaMetadataRetriever;

public class Music {
    public String title;
    public String album;
    public String fileName;

    /**
     * Constructor using title and album
     * @param title
     * @param album
     */
    Music(String title, String album) {
        this.title = title;
        this.album = album;
    }

    /**
     * Constructor using filename
     * @param fileName file path and name
     */
    Music(String fileName) {
        this.fileName = fileName;
        // TODO: this seems to be running in the UI thread and slow with hundreds of files
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        mmr.setDataSource(fileName);
        title = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
        album = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM);
    }
}
