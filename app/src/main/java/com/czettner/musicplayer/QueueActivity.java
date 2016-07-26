package com.czettner.musicplayer;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;

public class QueueActivity extends AppCompatActivity {

    private RecyclerView rv;
    private List<Music> musicList;
    private PlayerService mPlayerService;
    private boolean mBound;

    private Button backtoNowPlaying;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_queue);

        backtoNowPlaying = (Button) findViewById(R.id.backto_nowplaying);

        backtoNowPlaying.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(QueueActivity.this, NowplayingActivity.class);
                startActivity(i);
            }
        });
    }

    private void initializeData() {
        musicList = new ArrayList<>();
        musicList = mPlayerService.filesQueue;

        rv = (RecyclerView) findViewById(R.id.rv);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        rv.setLayoutManager(llm);
        MusicRVAdapter adapter = new MusicRVAdapter(musicList);
        rv.setAdapter(adapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Bind to PlayerService
        Intent i = new Intent(this, PlayerService.class);
        bindService(i, mConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        // Unbind from the service
        if (mBound) {
            unbindService(mConnection);
            mBound = false;
        }
    }

    /** Defines callbacks for service binding, passed to bindService() */
    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            // We've bound to PlayerService, cast the IBinder and get PlayerService instance
            PlayerService.PlayerBinder binder = (PlayerService.PlayerBinder) service;
            mPlayerService = binder.getService();
            mBound = true;

            // Load data into RecyclerView
            QueueActivity.this.initializeData();
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };

}
