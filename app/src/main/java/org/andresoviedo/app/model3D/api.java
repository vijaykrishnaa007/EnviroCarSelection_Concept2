package org.andresoviedo.app.model3D;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface api {
    @GET("manufacturers")
    Call<List<manufacturer>> getUserData();
}

