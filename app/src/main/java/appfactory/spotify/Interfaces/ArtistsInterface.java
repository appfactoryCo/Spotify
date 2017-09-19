package appfactory.spotify.interfaces;


import appfactory.spotify.pojo.ArtistsData;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ArtistsInterface {
    @GET("/v1/search")
    Call<ArtistsData> getArtists(@Query("q") String query, @Query("type") String type);
}

