package com.example.api_rest;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;

public class TrackAdapter extends RecyclerView.Adapter<TrackAdapter.TrackViewHolder> {
    private List<Track> tracks;
    private OnTrackClickListener listener;

    public TrackAdapter(List<Track> tracks, OnTrackClickListener listener) {
        this.tracks = tracks;
        this.listener = listener;
    }

    @NonNull
    @Override
    public TrackViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.track_item, parent, false);
        return new TrackViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TrackViewHolder holder, int position) {
        Track track = tracks.get(position);
        holder.title.setText(track.getTitle());
        holder.singer.setText(track.getSinger());

        // Añadir un listener para manejar clics en el item
        holder.itemView.setOnClickListener(v -> listener.onTrackClick(track));
        holder.buttonDelete.setOnClickListener(v -> {
            listener.onDeleteTrackClick(track); // Avisar a la actividad que se quiere eliminar
        });
    }

    @Override
    public int getItemCount() {
        return tracks.size();
    }

    public void removeTrack(Track track) {
        int position = tracks.indexOf(track); // Cambia trackList a tracks
        if (position >= 0) {
            tracks.remove(position); // Cambia trackList a tracks
            notifyItemRemoved(position);
        }
    }

    public void updateTracks(List<Track> newTracks) {
        this.tracks.clear();
        this.tracks.addAll(newTracks);
        notifyDataSetChanged(); // Notificar al adaptador que los datos han cambiado
    }

    public void addTrack(Track track) {
        this.tracks.add(track);
        notifyItemInserted(tracks.size() - 1);
    }

    static class TrackViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        TextView singer;
        Button buttonDelete; // Añadir referencia al botón de eliminar

        TrackViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.text1);
            singer = itemView.findViewById(R.id.text2);
            buttonDelete = itemView.findViewById(R.id.buttonDelete); // Inicializar el botón de eliminar
        }
    }

    public interface OnTrackClickListener {
        void onTrackClick(Track track);
        void onDeleteTrackClick(Track track); // Asegúrate de que esto esté aquí
    }
}