package com.example.api_rest;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.ComponentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends ComponentActivity implements TrackAdapter.OnTrackClickListener {
    private TracksServiceAPI tracksServiceAPI;
    private RecyclerView recyclerView;
    private TrackAdapter adapter; // Asegúrate de haber creado TrackAdapter
    private EditText editTextTitle, editTextSinger; // Campos de entrada para agregar track

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Configura el RecyclerView
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new TrackAdapter(new ArrayList<>(), this); // Pasar this como listener
        recyclerView.setAdapter(adapter);

        // Inicializa Retrofit
        tracksServiceAPI = ApiClient.getClient().create(TracksServiceAPI.class);

        // Inicializa los campos de entrada y botón
        editTextTitle = findViewById(R.id.editTextTitle);
        editTextSinger = findViewById(R.id.editTextSinger);
        Button buttonAddTrack = findViewById(R.id.buttonAddTrack);

        // Configura el botón para agregar un track
        buttonAddTrack.setOnClickListener(v -> addTrack());

        // Realiza la llamada a la API para obtener los tracks
        getTracks();
    }
    public void onTrackClick(Track track) {
        showEditTrackDialog(track); // Mostrar el diálogo de edición
    }
    public void onDeleteTrackClick(Track track) {
        // Confirmación antes de eliminar
        new AlertDialog.Builder(this)
                .setTitle("Eliminar Track")
                .setMessage("¿Estás seguro de que deseas eliminar este track?")
                .setPositiveButton("Sí", (dialog, which) -> {
                    deleteTrack(track);
                })
                .setNegativeButton("No", null)
                .show();
    }

    private void deleteTrack(Track track) {
        Call<Void> call = tracksServiceAPI.deleteTrack(track.getId());
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(MainActivity.this, "Track eliminado", Toast.LENGTH_SHORT).show();
                    // Remover el track de la lista y actualizar el RecyclerView
                    adapter.removeTrack(track);
                } else {
                    Toast.makeText(MainActivity.this, "Error al eliminar track", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void getTracks() {
        Call<List<Track>> call = tracksServiceAPI.getTracks();
        call.enqueue(new Callback<List<Track>>() {
            @Override
            public void onResponse(Call<List<Track>> call, Response<List<Track>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Track> tracks = response.body();
                    Log.d("API", "Tracks received: " + tracks.size()); // Verifica la cantidad de tracks

                    for (Track track : tracks) {
                        Log.d("API", "Track ID: " + track.getId() + ", Title: " + track.getTitle());
                    }

                    adapter.updateTracks(tracks); // Asegúrate de que updateTracks esté implementado
                } else {
                    Log.e("API", "Error: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<Track>> call, Throwable t) {
                Log.e("API", "Failure: " + t.getMessage());
            }
        });
    }

    private void addTrack() {
        String title = editTextTitle.getText().toString();
        String singer = editTextSinger.getText().toString();

        if (title.isEmpty() || singer.isEmpty()) {
            Toast.makeText(this, "Por favor, completa ambos campos", Toast.LENGTH_SHORT).show();
            return;
        }

        // Crear un nuevo track
        Track newTrack = new Track(null, title, singer); // Asumiendo que el ID se generará en el servidor

        // Llamar a la API para agregar el nuevo track usando createTrack
        Call<Track> call = tracksServiceAPI.createTrack(newTrack); // Cambiado de addTrack a createTrack

        call.enqueue(new Callback<Track>() {
            @Override
            public void onResponse(Call<Track> call, Response<Track> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Agregar el nuevo track a la lista y actualizar el RecyclerView
                    adapter.addTrack(response.body()); // Asegúrate de implementar addTrack en el adaptador
                    editTextTitle.setText("");
                    editTextSinger.setText("");
                } else {
                    Log.e("API", "Error: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Track> call, Throwable t) {
                Log.e("API", "Failure: " + t.getMessage());
            }
        });
    }
    private void showEditTrackDialog(Track track) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Edit Track");

        // Inflate el layout para el diálogo
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_edit_track, null);
        builder.setView(dialogView);

        EditText editTextTitle = dialogView.findViewById(R.id.editTextTitle);
        EditText editTextSinger = dialogView.findViewById(R.id.editTextSinger);
        Button buttonSave = dialogView.findViewById(R.id.buttonSave);

        // Rellenar los campos con los datos actuales del track
        editTextTitle.setText(track.getTitle());
        editTextSinger.setText(track.getSinger());

        // Configurar el botón para guardar los cambios
        buttonSave.setOnClickListener(v -> {
            String title = editTextTitle.getText().toString();
            String singer = editTextSinger.getText().toString();

            // Validar los campos
            if (title.isEmpty() || singer.isEmpty()) {
                Toast.makeText(this, "Por favor, completa ambos campos", Toast.LENGTH_SHORT).show();
                return;
            }

            if (track.getId() == null) {
                Toast.makeText(this, "ID del track no válido", Toast.LENGTH_SHORT).show();
                return;
            }

            // Actualizar los campos del track
            track.setTitle(title);
            track.setSinger(singer);

            // Llamar a la API para actualizar el track
            updateTrack(track);
        });

        // Mostrar el diálogo
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
        builder.show();
    }
    private void updateTrack(Track track) {
        Call<Void> call = tracksServiceAPI.updateTrack(track.getId(), track);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(MainActivity.this, "Track actualizado", Toast.LENGTH_SHORT).show();
                    getTracks(); // Recargar la lista de tracks
                } else {
                    Log.e("API", "Error al actualizar: " + response.code() + " - " + response.message());
                    Toast.makeText(MainActivity.this, "Error al actualizar", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}
