package com.example.api_rest;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Body;
import retrofit2.http.DELETE;

import java.util.List;

public interface TracksServiceAPI {
    @GET("tracks") // Cambia esto según tu endpoint real
    Call<List<Track>> getTracks();


    @POST("tracks") // EndPoint para crear una nueva pista
    Call<Track> createTrack(@Body Track track);

    @GET("tracks/{id}") // EndPoint para obtener una pista específica
    Call<Track> getTrack(@Path("id") String id);

    @PUT("tracks/{id}")
    Call<Void> updateTrack(@Path("id") String id, @Body Track track);

    @DELETE("tracks/{id}") // EndPoint para eliminar una pista
    Call<Void> deleteTrack(@Path("id") String id);
}
