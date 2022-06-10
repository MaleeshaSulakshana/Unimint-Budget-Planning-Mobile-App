package com.example.unimint.Adapters;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.unimint.Constructors.BudgetPlanner;
import com.example.unimint.Constructors.Tutorial;
import com.example.unimint.R;

import java.util.ArrayList;

public class TutorialAdapter extends ArrayAdapter<Tutorial> {

    private Context mContext;
    private int mResource;

    public TutorialAdapter(@NonNull Context context, int resource, @NonNull ArrayList<Tutorial> objects) {
        super(context, resource, objects);

        this.mContext = context;
        this.mResource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        convertView = layoutInflater.inflate(mResource, parent, false);

//        VideoView videoView = convertView.findViewById(R.id.videoView);
        TextView title = convertView.findViewById(R.id.title);
        TextView date = convertView.findViewById(R.id.date);

//        videoView.setVideoPath(getItem(position).getTutorialUrl());
//        videoView.start();
        title.setText(getItem(position).getTitle());
        date.setText(getItem(position).getDate());

        return convertView;
    }

}