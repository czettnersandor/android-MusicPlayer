package com.czettner.musicplayer;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;

import android.os.Handler;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import net.rdrei.android.dirchooser.DirectoryChooserActivity;
import net.rdrei.android.dirchooser.DirectoryChooserConfig;

public class NowplayingActivity extends AppCompatActivity {

    private final static int REQUEST_DIRECTORY = 1;

    private ProgressBar progressBar;
    private ImageButton previousButton;
    private ImageButton playButton;
    private ImageButton nextButton;
    private TextView currentFolderText;
    private PlayerService mPlayerService;
    private boolean mBound = false;
    private boolean mUpdateProgress = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nowplaying);

        progressBar = (ProgressBar) findViewById(R.id.progress);
        previousButton = (ImageButton) findViewById(R.id.previous);
        playButton = (ImageButton) findViewById(R.id.play);
        nextButton = (ImageButton) findViewById(R.id.next);
        currentFolderText = (TextView) findViewById(R.id.current_folder);

        playButton.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View v) {
                  if (mPlayerService.isStarted()) {
                      if (mPlayerService.isPlaying()) {
                          Intent i = new Intent(NowplayingActivity.this, PlayerService.class);
                          i.setAction(PlayerService.ACTION_PAUSE);
                          startService(i);
                          playButton.setImageResource(android.R.drawable.ic_media_play);
                          mUpdateProgress = false;
                      } else {
                          Intent i = new Intent(NowplayingActivity.this, PlayerService.class);
                          i.setAction(PlayerService.ACTION_RESUME);
                          startService(i);
                          playButton.setImageResource(android.R.drawable.ic_media_pause);
                          mUpdateProgress = true;
                          handler.post(mUpdateRunnable);
                      }
                  } else {
                      Intent i = new Intent(NowplayingActivity.this, PlayerService.class);
                      i.setAction(PlayerService.ACTION_PLAY_TEST);
                      startService(i);
                      playButton.setImageResource(android.R.drawable.ic_media_pause);
                      mUpdateProgress = true;
                      mUpdateRunnable.run();
                  }

              }
        });

        previousButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPlayerService.isStarted()) {
                    Intent i = new Intent(NowplayingActivity.this, PlayerService.class);
                    i.setAction(PlayerService.ACTION_PREVIOUS);
                    startService(i);
                }
            }
        });

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPlayerService.isStarted()) {
                    Intent i = new Intent(NowplayingActivity.this, PlayerService.class);
                    i.setAction(PlayerService.ACTION_NEXT);
                    startService(i);
                }
            }
        });
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

    /**
     * Choose folder to play
     * @param view View
     */
    public void chooseFolderClick(View view) {
        final Intent chooserIntent = new Intent(this, DirectoryChooserActivity.class);

        final DirectoryChooserConfig config = DirectoryChooserConfig.builder()
                .newDirectoryName("DirChooserSample")
                .allowReadOnlyDirectory(true)
                .allowNewDirectoryNameModification(true)
                .build();

        chooserIntent.putExtra(DirectoryChooserActivity.EXTRA_CONFIG, config);

        startActivityForResult(chooserIntent, REQUEST_DIRECTORY);
    }

    public void showQueueClick(View view) {
        Intent intent = new Intent(this, QueueActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_DIRECTORY) {
            if (resultCode == DirectoryChooserActivity.RESULT_CODE_DIR_SELECTED) {
                playDirectory(data.getStringExtra(DirectoryChooserActivity.RESULT_SELECTED_DIR));
            } else {
                // TODO: Nothing selected
            }
        }
    }

    /**
     * Queue files in directory to play
     * @param dir Directory
     */
    private void playDirectory(String dir) {
        currentFolderText.setText(dir);
        Intent i = new Intent(this, PlayerService.class);
        i.setAction(PlayerService.ACTION_PLAY_DIRECTORY);
        i.putExtra("directory", dir);
        startService(i);
        playButton.setImageResource(android.R.drawable.ic_media_pause);
        mUpdateProgress = true;
        handler.post(mUpdateRunnable);
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
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };

    // This code updates the progress bar
    final Handler handler = new Handler();
    private Runnable mUpdateRunnable = new Runnable() {
        @Override
        public void run() {
            if (mPlayerService != null && mPlayerService.isPlaying()) {
                progressBar.setMax(mPlayerService.getDuration());
                progressBar.setProgress(mPlayerService.getCurrentPosition());
            }
            if (mUpdateProgress) {
                handler.postDelayed(mUpdateRunnable, 500);
            }
        }
    };

}
