package com.czettner.musicplayer;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PlayerService extends Service implements MediaPlayer.OnPreparedListener {

    public static final String ACTION_PLAY_TEST = "com.czettner.action.PLAY_TEST";
    public static final String ACTION_PAUSE = "com.czettner.action.PAUSE";
    public static final String ACTION_RESUME = "com.czettner.action.RESUME";
    public static final String ACTION_PLAY_DIRECTORY = "com.czettner.action.PLAY_DIRECTORY";
    public static final String FILE_PATTERN = "(.*)\\.(mp3|flac)$";

    private MediaPlayer mMediaPlayer = null;
    public ArrayList<String> filesQueue = new ArrayList<String>();

    // Binder given to clients
    private final IBinder mBinder = new PlayerBinder();

    public int onStartCommand(Intent intent, int flags, int startId) {
        switch (intent.getAction()) {
            case ACTION_PLAY_TEST:
                release();
                mMediaPlayer = MediaPlayer.create(this, R.raw.flowers_of_scotland);
                mMediaPlayer.setOnPreparedListener(this);
                break;
            case ACTION_PLAY_DIRECTORY:
                filesQueue.clear();
                queueDirectory(intent.getStringExtra("directory"));
                release();
                File f = new File(filesQueue.get(0));
                Uri musicUri = Uri.fromFile(f);
                mMediaPlayer = new MediaPlayer();
                // mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                try {
                    mMediaPlayer.setDataSource(getApplicationContext(), musicUri);
                } catch (java.io.IOException e) {
                    // TODO
                }
                mMediaPlayer.setOnPreparedListener(this);
                mMediaPlayer.prepareAsync();
                break;
            case ACTION_PAUSE:
                if (mMediaPlayer != null) {
                    mMediaPlayer.pause();
                }
                break;
            case ACTION_RESUME:
                if (mMediaPlayer != null) {
                    mMediaPlayer.start();
                }
                break;
        }
        return START_NOT_STICKY;
    }

    /**
     * Get all files from a directory recursively and add it to the queue
     * @param directoryName
     */
    public void queueDirectory(String directoryName) {
        File directory = new File(directoryName);
        String fileName;
        Pattern r = Pattern.compile(FILE_PATTERN);

        File[] fList = directory.listFiles();
        for (File file : fList) {
            if (file.isFile()) {
                fileName = file.toString();
                Matcher m = r.matcher(fileName);
                if (m.find()) {
                    Log.i("MediaPlayer", "Queue file " + fileName);
                    filesQueue.add(fileName);
                }
            } else if (file.isDirectory()) {
                queueDirectory(file.getAbsolutePath());
            }
        }
    }

    /**
     * This is used to query state from the @link NowplayingActivity
     */
    public class PlayerBinder extends Binder {
        PlayerService getService() {
            // Return this instance of LocalService so clients can call public methods
            return PlayerService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        mp.start();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        release();
    }

    public boolean isPlaying() {
        if (mMediaPlayer == null) {
            return false;
        } else {
            return mMediaPlayer.isPlaying();
        }
    }

    public boolean isStarted() {
        if (mMediaPlayer == null) {
            return false;
        } else {
            return true;
        }
    }

    private void release() {
        if (mMediaPlayer != null) mMediaPlayer.release();
    }
}
