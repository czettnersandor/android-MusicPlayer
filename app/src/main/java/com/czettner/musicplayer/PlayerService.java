package com.czettner.musicplayer;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;

public class PlayerService extends Service implements MediaPlayer.OnPreparedListener {

    public static final String ACTION_PLAY_TEST = "com.czettner.action.PLAY_TEST";
    public static final String ACTION_PAUSE = "com.czettner.action.PAUSE";
    public static final String ACTION_RESUME = "com.czettner.action.RESUME";

    private MediaPlayer mMediaPlayer = null;

    // Binder given to clients
    private final IBinder mBinder = new PlayerBinder();

    public int onStartCommand(Intent intent, int flags, int startId) {
        switch (intent.getAction()) {
            case ACTION_PLAY_TEST:
                release();
                mMediaPlayer = MediaPlayer.create(this, R.raw.flowers_of_scotland);
                mMediaPlayer.setOnPreparedListener(this);
                break;
            case ACTION_PAUSE:
                mMediaPlayer.pause();
                break;
            case ACTION_RESUME:
                mMediaPlayer.start();
                break;
        }
        return START_NOT_STICKY;
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
