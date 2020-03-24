package org.andresoviedo.app.model3D;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface vehicledetailsapi {
    @GET("manufacturers/{hsn}/vehicles/{tsn}")
    Call<vehicledetails> getUserData(@Path("hsn") String hsn, @Path("tsn") String tsn);
}
