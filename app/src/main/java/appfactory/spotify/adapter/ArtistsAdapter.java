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

import appfactory.spotify.pojo.ArtistsData;
import appfactory.spotify.R;
import appfactory.spotify.activity.TrackActivity;

public class ArtistsAdapter extends ArrayAdapter {

    List<ArtistsData.Item> items;
    private Context context;

    public ArtistsAdapter(Context context, List<ArtistsData.Item> items) {
        super(context, 0, items);
        this.items = items;
        this.context = context;
    }


    @Override
    public View getView(final int position, View view, ViewGroup parent) {

        ViewHolder viewHolder;

        if (view == null) {
            view = View.inflate(getContext(), R.layout.row_artists, null);
            viewHolder = new ViewHolder();
            viewHolder.name = (TextView) view.findViewById(R.id.name);
            viewHolder.imageView = (ImageView) view.findViewById(R.id.image);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        viewHolder.name.setText(items.get(position).getName());

        if (items.get(position).getImages().size() != 0) {
            String imgUrl = items.get(position).getImages().get(0).getUrl();
            Picasso.with(context).load(imgUrl).into(viewHolder.imageView);
        } else {
            viewHolder.imageView.setImageResource(R.mipmap.ic_launcher);
        }


// Impleent item click listener
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, TrackActivity.class);
                i.putExtra("id", items.get(position).getId());
                i.putExtra("name", items.get(position).getName());
                context.startActivity(i);
            }
        });

        return view;

    }// End getView


    // Add items to the list, called from Retrofit2 onResponse() method
    public void addArtist(ArtistsData.Item item) {
        items.add(item);
    }


    // For performance
    public class ViewHolder {
        private TextView name;
        private ImageView imageView;
    }


}// end Artists
