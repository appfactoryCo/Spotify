package appfactory.spotify.Activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;
import appfactory.spotify.Adapters.Track_Adapter;
import appfactory.spotify.Utilities.Constants;
import appfactory.spotify.Interfaces.Track_Interface;
import appfactory.spotify.Pojo.Track_Data;
import appfactory.spotify.R;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class Track_Activity extends AppCompatActivity {

    ListView listView;
    private ArrayList<Track_Data.Track> data = new ArrayList<>();
    private Track_Adapter adapter;
    static Context ctx;
    private ProgressBar spinner;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track);

        ctx = this;

        Intent intent = getIntent();
        String id = intent.getStringExtra("id");

        initViews(intent);
        showSpinner();
        loadJSON(id);

    }// onCreate


    // Initialize views
    private void initViews(Intent intent) {

        spinner = (ProgressBar) findViewById(R.id.progressBar);

        if (getSupportActionBar() != null) {
            ActionBar ab = getSupportActionBar();
            ab.setTitle(Html.fromHtml("<b>Top 10 Tracks</b>"));
            ab.setSubtitle(intent.getStringExtra("name"));
        }
        listView = (ListView) findViewById(R.id.track_listview);
        adapter = new Track_Adapter(this, data);
        listView.setAdapter(adapter);
    }


    // To show progress
    private void showSpinner() {
        spinner.setVisibility(View.VISIBLE);
    }

    private void hideSpinner() {
        spinner.setVisibility(View.GONE);
    }


    // To send Retrofit2 GET request to Spotify API
    private void loadJSON(String id) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        final Track_Interface apiRequest = retrofit.create(Track_Interface.class);

        Call<Track_Data> call = apiRequest.getTracks(id); // pass id parameter to the endpoint
        call.enqueue(new Callback<Track_Data>() {
            @Override
            public void onResponse(Call<Track_Data> call, Response<Track_Data> response) {
                if (response.isSuccessful()) {

                    if (response.body().getTracks() != null) { // check data
                        final List<Track_Data.Track> tracks = response.body().getTracks();
                        hideSpinner();
                        if (tracks.size() != 0) {
                            for (int i = 0; i < tracks.size(); i++) {
                                adapter.addTrack(tracks.get(i));
                            }
                            adapter.notifyDataSetChanged();
                        } else {
                            showMessage("No Tracks Found!");
                        }
                    } else {
                        showMessage("No Tracks Found!");
                    }
                }
            }// onResponse

            @Override
            public void onFailure(Call<Track_Data> call, Throwable t) {
                System.out.println("onFAIL::: " + t);
                hideSpinner();
                showMessage("Failed To Get Tracks");
            }
        });

    }// End loadJSON


    // Show a message if something goes wrong
    public void showMessage(String msg) {

        final TextView message = (TextView) findViewById(R.id.message);
        if (message != null) {
            message.setText(msg);
        }
    }

}// End of class
