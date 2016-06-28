package appfactory.spotify.Interfaces;


import appfactory.spotify.Pojo.Artists_Data;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface Artists_Interface {
    @GET("/v1/search")
    Call<Artists_Data> getArtists(@Query("q") String query, @Query("type") String type);
}

