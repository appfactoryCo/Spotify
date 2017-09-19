package appfactory.spotify.interfaces;

import appfactory.spotify.pojo.TrackData;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface TrackInterface {
    @GET("/v1/artists/{id}/top-tracks?country=US")
    Call<TrackData> getTracks(@Path("id") String id);
}

