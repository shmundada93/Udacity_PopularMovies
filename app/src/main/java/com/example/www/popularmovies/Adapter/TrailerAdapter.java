package com.example.www.popularmovies.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.www.popularmovies.Model.Trailer;
import com.example.www.popularmovies.R;

import java.util.ArrayList;

/**
 * Created by shmundada on 06-02-2016.
 */
public class TrailerAdapter extends BaseAdapter {
    private Context mContext;
    private ArrayList<Trailer> mTrailerList;

    public TrailerAdapter(Context c, ArrayList<Trailer> trailerList) {
        mContext = c;
        mTrailerList = trailerList;
    }

    public int getCount() {
        return mTrailerList.size();
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = LayoutInflater.from(mContext).inflate(
                R.layout.list_trailer_item, parent, false);
        TextView trailerText = (TextView) convertView.findViewById(R.id.trailerText);
        trailerText.setText(mTrailerList.get(position).getName());
        return convertView;
    }

}
