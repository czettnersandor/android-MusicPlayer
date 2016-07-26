package com.czettner.musicplayer;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class QueueActivity extends AppCompatActivity {

    private RecyclerView rv;
    private List<Music> musicList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_queue);

        initializeData();

        rv = (RecyclerView) findViewById(R.id.rv);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        rv.setLayoutManager(llm);
        MusicRVAdapter adapter = new MusicRVAdapter(musicList);
        rv.setAdapter(adapter);
    }

    private void initializeData() {
        musicList = new ArrayList<>();
        musicList.add(new Music("Test title 1", "Test Album 1"));
        musicList.add(new Music("Test title 2", "Test Album 2"));
        musicList.add(new Music("Test title 3", "Test Album 3"));
        musicList.add(new Music("Test title 4", "Test Album 4"));
    }
}
