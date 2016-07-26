package com.czettner.musicplayer;


import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class MusicRVAdapter extends RecyclerView.Adapter<MusicRVAdapter.MusicViewHolder> {

    List<Music> musicList;

    MusicRVAdapter(List<Music> musicList){
        this.musicList = musicList;
    }

    @Override
    public int getItemCount() {
        return musicList.size();
    }

    @Override
    public MusicViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.queue_item, viewGroup, false);
        return new MusicViewHolder(v);
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public void onBindViewHolder(MusicViewHolder personViewHolder, int i) {
        personViewHolder.title.setText(musicList.get(i).title);
        personViewHolder.album.setText(musicList.get(i).album);
    }

    public class MusicViewHolder extends RecyclerView.ViewHolder {
        CardView cv;
        TextView title;
        TextView album;
        ImageView songImage;

        MusicViewHolder(View itemView) {
            super(itemView);
            cv = (CardView) itemView.findViewById(R.id.card_view);
            title = (TextView) itemView.findViewById(R.id.song_title);
            album = (TextView) itemView.findViewById(R.id.song_album);
        }
    }
}
