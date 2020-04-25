package com.example.testlake.TLSResource;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;

public interface CryptoApi {
    @GET("default.htm")
    Call<ResponseBody> getData();
}