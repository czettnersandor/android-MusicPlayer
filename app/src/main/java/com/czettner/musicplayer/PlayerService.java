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

public class PlayerService extends Service implements MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener {

    public static final String ACTION_PLAY_TEST = "com.czettner.action.PLAY_TEST";
    public static final String ACTION_PAUSE = "com.czettner.action.PAUSE";
    public static final String ACTION_RESUME = "com.czettner.action.RESUME";
    public static final String ACTION_PLAY_DIRECTORY = "com.czettner.action.PLAY_DIRECTORY";
    public static final String ACTION_PREVIOUS = "com.czettner.action.PREVIOUS";
    public static final String ACTION_NEXT = "com.czettner.action.NEXT";
    public static final String FILE_PATTERN = "(.*)\\.(mp3|flac)$";

    private MediaPlayer mMediaPlayer = null;
    public ArrayList<Music> filesQueue = new ArrayList<Music>();
    public int currentlyPlaying = 0;

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
                if (filesQueue.size() != 0) {
                    playQueue();
                }
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
            case ACTION_PREVIOUS:
                if (mMediaPlayer != null) {
                    previous();
                }
                break;
            case ACTION_NEXT:
                if (mMediaPlayer != null) {
                    next();
                }
                break;
        }
        return START_NOT_STICKY;
    }

    private void playQueue() {
        release();
        File f = new File(filesQueue.get(currentlyPlaying).fileName);
        Uri musicUri = Uri.fromFile(f);
        mMediaPlayer = new MediaPlayer();
        try {
            mMediaPlayer.setDataSource(getApplicationContext(), musicUri);
        } catch (java.io.IOException e) {
            // TODO
        }
        mMediaPlayer.setOnPreparedListener(this);
        mMediaPlayer.setOnCompletionListener(this);
        mMediaPlayer.prepareAsync();
    }

    /**
     * Get all files from a directory recursively and add it to the queue
     * @param directoryName Directory name
     */
    public void queueDirectory(String directoryName) {
        currentlyPlaying = 0;
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
                    filesQueue.add(new Music(fileName));
                }
            } else if (file.isDirectory()) {
                queueDirectory(file.getAbsolutePath());
            }
        }
    }

    public int getCurrentPosition() {
        return mMediaPlayer.getCurrentPosition();
    }

    public int getDuration() {
        return mMediaPlayer.getDuration();
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
    public void onCompletion(MediaPlayer mp) {
        if (currentlyPlaying < filesQueue.size()) {
            currentlyPlaying++;
            playQueue();
        }
    }

    public void next() {
        if (currentlyPlaying < filesQueue.size()) {
            currentlyPlaying++;
            playQueue();
        }
    }

    public void previous() {
        if (currentlyPlaying > 0 && filesQueue.size() > 0) {
            currentlyPlaying--;
            playQueue();
        }
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
