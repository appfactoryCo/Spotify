package appfactory.spotify.Interfaces;

import appfactory.spotify.Pojo.Track_Data;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface Track_Interface {
    @GET("/v1/artists/{id}/top-tracks?country=US")
    Call<Track_Data> getTracks(@Path("id") String id);
}

