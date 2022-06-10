package com.example.unimint.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.unimint.R;
import com.example.unimint.Constructors.BudgetPlanner;

import java.util.ArrayList;

public class BudgetPlannerAdapter extends ArrayAdapter<BudgetPlanner> {

    private Context mContext;
    private int mResource;

    public BudgetPlannerAdapter(@NonNull Context context, int resource, @NonNull ArrayList<BudgetPlanner> objects) {
        super(context, resource, objects);

        this.mContext = context;
        this.mResource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        convertView = layoutInflater.inflate(mResource, parent, false);

        TextView title = convertView.findViewById(R.id.title);
        TextView amount = convertView.findViewById(R.id.amount);

        title.setText(getItem(position).getTitle());
        amount.setText(getItem(position).getAmount());

        return convertView;
    }

}