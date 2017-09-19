package appfactory.spotify.activity;

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
import appfactory.spotify.adapter.TrackAdapter;
import appfactory.spotify.utils.Constants;
import appfactory.spotify.interfaces.TrackInterface;
import appfactory.spotify.pojo.TrackData;
import appfactory.spotify.R;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class TrackActivity extends AppCompatActivity {

    ListView listView;
    private ArrayList<TrackData.Track> data = new ArrayList<>();
    private TrackAdapter adapter;
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
        adapter = new TrackAdapter(this, data);
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

        final TrackInterface apiRequest = retrofit.create(TrackInterface.class);

        Call<TrackData> call = apiRequest.getTracks(id); // pass id parameter to the endpoint
        call.enqueue(new Callback<TrackData>() {
            @Override
            public void onResponse(Call<TrackData> call, Response<TrackData> response) {
                if (response.isSuccessful()) {

                    if (response.body().getTracks() != null) { // check data
                        final List<TrackData.Track> tracks = response.body().getTracks();
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
            public void onFailure(Call<TrackData> call, Throwable t) {
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
