package com.example.latihanfirebase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.text.TextUtilsCompat;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static final String ARTIST_NAME = "artistnama";
    public static final String ARTIST_ID = "artistid";


    EditText editTextName;
    Button buttonAdd;
    Spinner spinnerGenres;

    DatabaseReference databaseArtist;

    ListView listViewArtist;

    List<Artist> artistsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        databaseArtist = FirebaseDatabase.getInstance().getReference("artist");

        editTextName = findViewById(R.id.editTextName);
        buttonAdd = findViewById(R.id.buttonAddArtist);
        spinnerGenres = findViewById(R.id.spinnerGenres);

        listViewArtist = findViewById(R.id.listViewArtist);

        artistsList = new ArrayList<>();

        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addArtist();
            }
        });

        listViewArtist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Artist artist = artistsList.get(i);

                Intent intent= new Intent(getApplicationContext(), AddTrackActivity.class);

                intent.putExtra(ARTIST_ID, artist.getArtistId());
                intent.putExtra(ARTIST_NAME, artist.getArtistName());

                startActivity(intent);


            }
        });

        listViewArtist.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {

                Artist artist = artistsList.get(i);

                showUpdateDialog(artist.getArtistId(), artist.getArtistName());
                return false;
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        databaseArtist.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                artistsList.clear();

                for (DataSnapshot artistSnapshot: dataSnapshot.getChildren()){
                    Artist artist = artistSnapshot.getValue(Artist.class);

                    artistsList.add(artist);
                }

                ArtistList artistAdapter = new ArtistList(MainActivity.this, artistsList);
                listViewArtist.setAdapter(artistAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void showUpdateDialog(final String artisId, String artistName){

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);

        LayoutInflater inflater = getLayoutInflater();

        final View dialogView = inflater.inflate(R.layout.update_dialog, null);

        dialogBuilder.setView(dialogView);


        final EditText editTextName = dialogView.findViewById(R.id.editTextName);
        final Button buttonUpdate = dialogView.findViewById(R.id.buttonUpdate);
        final Spinner spinnerGenres = dialogView.findViewById(R.id.spinnerGenres);
        final Button buttonDelete = dialogView.findViewById(R.id.buttonDelete);

        dialogBuilder.setTitle("Updating Artist"+artistName);

        final AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();

        buttonUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String name = editTextName.getText().toString().trim();
                String genre = spinnerGenres.getSelectedItem().toString();

                if (TextUtils.isEmpty(name)){
                    editTextName.setError("Name required");
                    return;
                }
                updateArtist(artisId,name,genre);

                alertDialog.dismiss();
            }
        });

        buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteArtist(artisId);

                alertDialog.dismiss();
            }
        });

    }

    private void deleteArtist(String artistId){
        DatabaseReference drArtist = FirebaseDatabase.getInstance().getReference("artist").child(artistId);

        DatabaseReference drTracks = FirebaseDatabase.getInstance().getReference("tracks").child(artistId);

        drArtist.removeValue();
        drTracks.removeValue();

        Toast.makeText(this, "Artist is deleted", Toast.LENGTH_LONG).show();
    }

    private boolean updateArtist(String id, String name, String genre){

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("artist").child(id);

        Artist artist = new Artist(id,name,genre);

        databaseReference.setValue(artist);

        Toast.makeText(this,"Artist Update Successfully", Toast.LENGTH_LONG).show();

        return true;
    }

    private void addArtist(){
        String name = editTextName.getText().toString().trim();
        String genre = spinnerGenres.getSelectedItem().toString();

        if (!TextUtils.isEmpty(name)){
            String id =  databaseArtist.push().getKey();

            Artist artist = new Artist(id, name, genre);

            databaseArtist.child(id).setValue(artist);

            editTextName.setText("");

            Toast.makeText(this, "Artist added", Toast.LENGTH_LONG).show();
        }else{
            Toast.makeText(this,"You should enter a name", Toast.LENGTH_LONG).show();
        }
    }
}
