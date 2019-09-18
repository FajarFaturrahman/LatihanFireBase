package com.example.latihanfirebase;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.List;

public class ArtistList extends ArrayAdapter<Artist> {

    private Activity context;
    private List<Artist> artistList;

    public ArtistList(Activity context, List<Artist> artistList){
        super(context, R.layout.layout_artist_list, artistList);
        this.context = context;
        this.artistList = artistList;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();

        View listViewItem = inflater.inflate(R.layout.layout_artist_list, null, true);

        TextView textViewName = listViewItem.findViewById(R.id.textViewName);
        TextView textViewGenre = listViewItem.findViewById(R.id.textViewGenre);

        Artist artist = artistList.get(position);

        textViewName.setText(artist.getArtistName());
        textViewGenre.setText(artist.getArtistGenre());

        return  listViewItem;
    }
}
