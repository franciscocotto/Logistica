package com.example.logistica;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

public interface WS {
    @GET
    Call<String>obtenerRuta(@Url String url);
}
