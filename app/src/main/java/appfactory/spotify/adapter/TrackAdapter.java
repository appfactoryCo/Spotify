package appfactory.spotify.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import appfactory.spotify.activity.PlayActivity;
import appfactory.spotify.pojo.TrackData;
import appfactory.spotify.R;


public class TrackAdapter extends ArrayAdapter {

    List<TrackData.Track> tracks;
    private Context context;

    public TrackAdapter(Context context, List<TrackData.Track> tracks) {
        super(context, 0, tracks);
        this.tracks = tracks;
        this.context = context;
    }


    @Override
    public View getView(final int position, View view, ViewGroup parent) {

        ViewHolder viewHolder;

        if (view == null) {
            view = View.inflate(getContext(), R.layout.row_track, null);
            viewHolder = new ViewHolder();
            viewHolder.album = (TextView) view.findViewById(R.id.album);
            viewHolder.track = (TextView) view.findViewById(R.id.track);
            viewHolder.imageView = (ImageView) view.findViewById(R.id.thumbnail);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        viewHolder.album.setText(tracks.get(position).getAlbum().getName());
        viewHolder.track.setText(tracks.get(position).getName());

        if (tracks.get(position).getAlbum().getImages().size() != 0) {
            String imgUrl = tracks.get(position).getAlbum().getImages().get(0).getUrl();
            Picasso.with(context).load(imgUrl).into(viewHolder.imageView);
        } else {
            viewHolder.imageView.setImageResource(R.mipmap.ic_launcher);
        }


        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, PlayActivity.class);
                i.putExtra("id", tracks.get(position).getId());
                i.putExtra("track", tracks.get(position).getName());
                i.putExtra("album", tracks.get(position).getAlbum().getName());

                // Check if images exist
                if (tracks.get(position).getAlbum().getImages().size() != 0) {
                    i.putExtra("imgUrl", tracks.get(position).getAlbum().getImages().get(0).getUrl());
                }

                context.startActivity(i);
            }
        });

        return view;

    }// End getView


    // Add items to the list, called from Retrofit2 onResponse() method
    public void addTrack(TrackData.Track item) {
        tracks.add(item);
    }


    // For performance
    public class ViewHolder {
        private TextView album;
        private TextView track;
        private ImageView imageView;
    }


}// end Artists
