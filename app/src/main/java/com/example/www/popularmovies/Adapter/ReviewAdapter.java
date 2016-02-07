package com.example.www.popularmovies.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.www.popularmovies.Model.Review;
import com.example.www.popularmovies.R;

import java.util.ArrayList;

/**
 * Created by shmundada on 05-02-2016.
 */
public class ReviewAdapter extends BaseAdapter {
    private Context mContext;
    private ArrayList<Review> mReviewList;

    public ReviewAdapter(Context c, ArrayList<Review> reviewList) {
        mContext = c;
        mReviewList = reviewList;
    }

    public int getCount() {
        return mReviewList.size();
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
            convertView = LayoutInflater.from(mContext).inflate(
                    R.layout.list_review_item, parent, false);
        TextView reviewAuthor = (TextView) convertView.findViewById(R.id.reviewAuthor);
        TextView reviewContent = (TextView) convertView.findViewById(R.id.reviewContent);
        reviewAuthor.setText(mReviewList.get(position).getAuthor());
        reviewContent.setText(mReviewList.get(position).getContent());
        return convertView;
    }

}
