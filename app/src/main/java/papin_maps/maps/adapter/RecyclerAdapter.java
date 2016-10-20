package papin_maps.maps.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.util.ArrayList;

import papin_maps.maps.R;

/**
 * Created by Papin on 20.10.2016.
 */

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder>
{


    ArrayList<papin_maps.maps.model.Product> objects;
    Context context;


    public RecyclerAdapter(ArrayList<papin_maps.maps.model.Product> products) {
        objects = products;
    }


    public static class ViewHolder extends RecyclerView.ViewHolder
    {
        public ImageView mImageView;

        public ViewHolder(View v)
        {
            super(v);
            mImageView = (ImageView) v.findViewById(R.id.BBBASD);
        }
    }



    @Override
    public RecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                         int viewType)
    {

        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_imageview, parent, false);

        context = parent.getContext();

        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    public void onBindViewHolder(final ViewHolder holder, final int position)
    {
        ImageLoader imageLoader = ImageLoader.getInstance();

        imageLoader.init(ImageLoaderConfiguration.createDefault(context));
        imageLoader.displayImage(objects.get(position).url,holder.mImageView);

    }

    @Override
    public int getItemCount()
    {
        return objects.size();
    }

}