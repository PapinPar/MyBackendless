package papin_maps.maps.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import papin_maps.maps.R;
import papin_maps.maps.model.Photo;

/**
 * Created by Papin on 21.10.2016.
 */

public class MyAdapter extends BaseAdapter {

    private LayoutInflater lInflater;
    private ArrayList<Photo> objects;

    public MyAdapter(Context context, ArrayList<Photo> photos) {
        lInflater = LayoutInflater.from(context);
        objects = photos;
    }

    @Override
    public int getCount() {
        return objects.size();
    }

    @Override
    public Object getItem(int position) {
        return objects.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;
        if (convertView == null) {
            convertView = lInflater.inflate(R.layout.item_imageview, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Picasso.with(parent.getContext())
                .load(objects.get(position).url).centerInside()
                .resize(320, 480)
                .into(viewHolder.ivPost);

        viewHolder.ivText.setText(objects.get(position).street);
        return convertView;
    }

    private static final class ViewHolder {
        private final ImageView ivPost;
        private final TextView ivText;

        public ViewHolder(View root) {
            ivPost = (ImageView) root.findViewById(R.id.BBBASD);
            ivText = (TextView) root.findViewById(R.id.streetName);
        }
    }
}
