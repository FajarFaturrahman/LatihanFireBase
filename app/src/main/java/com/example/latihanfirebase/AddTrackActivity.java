package com.example.latihanfirebase;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AddTrackActivity extends AppCompatActivity {

    TextView textViewArtistName;
    EditText editTextTrackname;
    SeekBar seekBarRating;

    Button buttonAddTracks;

    ListView listViewTracks;

    DatabaseReference databaseTracks;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_track);

        textViewArtistName = findViewById(R.id.textViewArtistName);
        editTextTrackname = findViewById(R.id.editTextTrackName);
        seekBarRating = findViewById(R.id.seekBarRating);
        buttonAddTracks = findViewById(R.id.buttonAddTrack);

        listViewTracks = findViewById(R.id.listViewTracks);

        Intent intent = getIntent();

        String id = intent.getStringExtra(MainActivity.ARTIST_ID);
        String name = intent.getStringExtra(MainActivity.ARTIST_NAME);

        textViewArtistName.setText(name);


        databaseTracks = FirebaseDatabase.getInstance().getReference("tracks").child(id);

        buttonAddTracks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveTrack();
            }
        });
    }

    private void saveTrack(){
        String trackName = editTextTrackname.getText().toString().trim();
        int rating = seekBarRating.getProgress();

        if (!TextUtils.isEmpty(trackName)){
            String id = databaseTracks.push().getKey();

            Track track = new Track(id,trackName,rating);

            databaseTracks.child(id).setValue(track);

            Toast.makeText(this,"Track saved successfully", Toast.LENGTH_LONG).show();
        }else {
            Toast.makeText(this, "Track name should not empty", Toast.LENGTH_LONG).show();
        }
    }
}
