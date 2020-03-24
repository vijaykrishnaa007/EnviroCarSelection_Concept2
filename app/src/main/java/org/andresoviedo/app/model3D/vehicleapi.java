package org.andresoviedo.app.model3D;


import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface vehicleapi {
    @GET("manufacturers/{hsn}/vehicles")
    Call<List<vehicles>> getUserData(@Path("hsn") String hsn);
}