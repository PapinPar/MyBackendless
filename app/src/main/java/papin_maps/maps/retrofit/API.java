package papin_maps.maps.retrofit;

import papin_maps.maps.model.map.MainAddress;
import retrofit2.Call;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Created by Papin on 21.10.2016.
 */

public interface API {

    @POST("json")
    Call<MainAddress> getAddress(@Query("sensor") String sensor, @Query("latlng") String latlng, @Query("language") String language);
}
