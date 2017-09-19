package appfactory.spotify.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import appfactory.spotify.adapter.ArtistsAdapter;
import appfactory.spotify.utils.Constants;
import appfactory.spotify.interfaces.ArtistsInterface;
import appfactory.spotify.pojo.ArtistsData;
import appfactory.spotify.R;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ArtistsActivity extends AppCompatActivity {

    private ListView listView;
    private EditText srchField;
    private ArrayList<ArtistsData.Item> data = new ArrayList<>();
    private ArtistsAdapter adapter;
    public static Context ctx;
    private TextView message;
    private ProgressBar spinner;
    private static String SEARCH_TERM = "";




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_artists);

        ctx = this;

        initViews();
        sendAPIRequest();

    }// onCreate




    // Initialize views
    private void initViews() {

        spinner = (ProgressBar) findViewById(R.id.progressBar);

        if (getSupportActionBar() != null) {
            ActionBar ab = getSupportActionBar();
            ab.setTitle(Html.fromHtml("<b>Search for Artist</b>"));
        }

        message = (TextView) findViewById(R.id.message);
        listView = (ListView) findViewById(R.id.srch_listview);
        adapter = new ArtistsAdapter(this, data);
        listView.setAdapter(adapter);
        srchField = (EditText) findViewById(R.id.srchfield);
        setSrchFieldListeners();

    }// End initViews


    // Set listeners to the search field
    private void setSrchFieldListeners() {

        // When done button clicked
        srchField.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    showSpinner();
                    searchArtist();
                }
                return false;
            }
        });

        // Detect typing activities
        srchField.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                message.setText("");
            }
        });

    }// End srchFieldListeners


    // To show progress
    private void showSpinner() {
        spinner.setVisibility(View.VISIBLE);
    }

    private void hideSpinner() {
        spinner.setVisibility(View.GONE);
    }


    // When done key pressed
    public void searchArtist() {

        // Empty the listview
        data = new ArrayList<>();
        adapter = new ArtistsAdapter(this, data);
        listView.setAdapter(adapter);

        // Get the text entered and pass it to a constant
        final String srchTerm = srchField.getText().toString();
        if (srchTerm.equals("")) {
            hideSpinner();
            showMessage("Please Enter an Artist");
        } else {
            SEARCH_TERM = srchTerm; // pass the text to a constant
            sendAPIRequest();
        }
    }


    // Use Retrofit2 to send a GET request to Spotify API
    private void sendAPIRequest() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        final ArtistsInterface apiRequest = retrofit.create(ArtistsInterface.class);

        Call<ArtistsData> call = apiRequest.getArtists(SEARCH_TERM, "artist"); // pass query to the endpoint
        call.enqueue(new Callback<ArtistsData>() {
            @Override
            public void onResponse(Call<ArtistsData> call, Response<ArtistsData> response) {

                if (response.isSuccessful()) {

                    if (response.body().getArtists().getItems() != null) { // check data
                        final List<ArtistsData.Item> items = response.body().getArtists().getItems();
                        hideSpinner();
                        if (items.size() != 0) {
                            for (int i = 0; i < items.size(); i++) {
                                adapter.addArtist(items.get(i));
                            }
                            adapter.notifyDataSetChanged();
                        } else {
                            showMessage("No Artists Found!");
                        }
                    } else {
                        showMessage("No Artists Found!");
                    }
                }
            }// end onResponse

            @Override
            public void onFailure(Call<ArtistsData> call, Throwable t) {
                System.out.println("onFAIL::: " + t);
                hideSpinner();
                showMessage("Failed To Get Artists");
            }
        });

        srchField.setText(SEARCH_TERM);

    }// loadJSON


    // Show a message if something goes wrong
    public void showMessage(String msg) {
        final TextView message = (TextView) findViewById(R.id.message);
        if (message != null) {
            message.setText(msg);
        }
    }


}// End Artists_Activity









