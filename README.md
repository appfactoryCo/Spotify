# Spotify
The app queries the Spotify web API to fetch artist and track data and populate list views. You can search for artists on Spotify, display their top ten tracks, and play the tracks. It uses Retrofit2 to handle HTTP requests and deserializing the JSON response. It uses Picsso library to handle loading and caching images. 

##Searching for Artists

We'll need to modify the build.gradle file and add Retrofit2 and Picasso dependencies. These modifications will happen in the build.gradle file, not the project root directory.
```javascript
dependencies { 
    // squareup libraries
    compile 'com.squareup.picasso:picasso:2.5.2' 
    compile 'com.squareup.retrofit2:retrofit:2.0.2'
    compile 'com.squareup.retrofit2:converter-gson:2.0.2' 
}
```

Now, Let's say you want to search for Beyonce as an artist, and got the following JSON response:
https://api.spotify.com/v1/search?q=Beyonce&type=artist


**Step 1:**

Create a Pojo class to deserialize the items in the response. 
Deserialization basically means that in this JSON response, we have "artists" array and inside it there are "href" string and "items" 
list, and we want to convert them into java objects that can be it in our app to access the data. We can use http://www.jsonschema2pojo.org to generate 
the pojo classes that will deserialize these items for us. Artist_Data and Track_Data are our pojo classes.


**Step 2:** 

The url is divided into 2 parts, a base and an endpoint. The base url is https://api.spotify.com and the end point
is /v1/search?q=Beyonce&type=artist. "q" represents our query and "type" represents they type of our query. 
We need to set up Retrofit2 interface class to accept a query and a type:
```javascript
public interface Artists_Interface {
    @GET("/v1/search")
    Call<Artists_Data> getArtists(@Query("q") String query, @Query("type") String type);
}
```


**Step 3:** 

• In Artists_Activity, when you type a search term and press the done button on the keyboard, the text gets passed to loadJSON() 
method. 
In this method, first you create a Retrofit2 object and pass it
the base url, which is https://api.spotify.com and add converter factory for serialization and deserialization of objects.
```javascript
 Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
```
• Then, create an interface object that implements the API endpoints defined by the interface using retrofit.create(...) method
```javascript
final Artists_Interface apiRequest = retrofit.create(Artists_Interface.class);
```
• Create Retrofit2 call object that sends a request to a the webserver and returns a response. We call getArtists() method in the 
interface and pass it our query and type and assign it to the Retrofit2 call object.
our query and type and assign it to the call.
```javascript
Call<Artists_Data> call = apiRequest.getArtists(SEARCH_TERM, "artist"); // pass query to the endpoint
```
• Asynchronously send the request using enqueue(Callback<T> callback) method and notify of its response or if an error
occurred when talking to the server, creating the request, or processing the response.
```javascript
call.enqueue(new Callback<Artists_Data>() {
            @Override
            public void onResponse(Call<Artists_Data> call, Response<Artists_Data> response) {

                if (response.isSuccessful()) {

                    if (response.body().getArtists().getItems() != null) { // check data
                        final List<Artists_Data.Item> items = response.body().getArtists().getItems();
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
            public void onFailure(Call<Artists_Data> call, Throwable t) {
                System.out.println("onFAIL::: " + t);
                hideSpinner();
                showMessage("Failed To Get Artists");
            }
        });
```





##Get Top 10 Tracks for An Artist
**Step 1:** 

We can check and see the JSON response for artist's top tracks from Spotify developer website:
https://developer.spotify.com/web-api/console/get-artist-top-tracks/

As before, we can pass that JSON response to http://www.jsonschema2pojo.org tool to generate the pojo classes that will 
deserialize the JSON items into serialized java objects.


**Step 2:** 

We create a Retrofit2 interface class that accepts the artist's id as a parameter.
```javascript
public interface Track_Interface {
    @GET("/v1/artists/{id}/top-tracks?country=US")
    Call<Track_Data> getTracks(@Path("id") String id);
}
```
The getTracks(...) method is in the Track_Data class that returns a list of tracks each of which has its own serialized data
elements like name, album, available markets, and more.

We get the artist's id as an extra from Artists_Data.Item class when we click the artist in the search list
```javascript
// Impleent item click listener
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, Track_Activity.class);
                i.putExtra("id", items.get(position).getId());
                i.putExtra("name", items.get(position).getName());
                context.startActivity(i);
            }
        });
```
and the "items" object is declared in the Artist_Adapter
```javascript
List<Artists_Data.Item> items;
```
It is a list of items each of which has its own serialized data like name, id, images, etc..


**Step 3:** 

In the Track_Activity, we call loadJSON(String id) method to populate the list with top ten tracks of the selected artist.
• We create Retrofit2 object as before
```javascript
 Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
```
 • Create an interface object that implements the API endpoints defined by the interface using retrofit.create(...) method
```javascript
final Track_Interface apiRequest = retrofit.create(Track_Interface.class);
```
• Create Retrofit2 call object that sends a request to a the webserver and returns a response. We call getTracks(id) method in the 
interface and pass it track id we got as an extra and assign it to the Retrofit2 call object.
```javascript
Call<Track_Data> call = apiRequest.getTracks(id); // pass id parameter to the endpoint
```
• Asynchronously send the request using enqueue(Callback<T> callback) method and notify of its response or if an error
occurred when talking to the server, creating the request, or processing the response.
```javascript
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
```



##Loading Images
We use Picasso to fetch images and load them into views. Picasso is a powerful library that will handle image
loading and caching
In the Artists_Adapter, we have
```javascript
String imgUrl = items.get(position).getImages().get(0).getUrl();
            Picasso.with(context).load(imgUrl).into(viewHolder.imageView);
```
We get the image url from items object mentioned before.

In the Track_Adapter, we have
```javascript
String imgUrl = tracks.get(position).getAlbum().getImages().get(0).getUrl();
            Picasso.with(context).load(imgUrl).into(viewHolder.imageView);
```
The "tracks" object is declared earlier in the adapter 
```javascript
List<Track_Data.Track> tracks;
```
It is a list of tracks each of which contains serialized objects like track's name, id, album, etc... and we take the image
from that album and pass it to Picasso to load it into our imageView.
