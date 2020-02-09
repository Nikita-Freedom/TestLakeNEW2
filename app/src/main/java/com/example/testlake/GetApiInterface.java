package com.example.testlake;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface GetApiInterface {
    @GET("/data/htdocs/test/")
    Call<List<Files>> getfiles();
}
