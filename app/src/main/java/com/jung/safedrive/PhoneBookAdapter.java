package com.jung.safedrive;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class PhoneBookAdapter extends RecyclerView.Adapter<PhoneBookAdapter.PhoneViewHolder> {

    private ArrayList<PhoneBook> mList;
    private Activity mActivity;

    PhoneBookAdapter(ArrayList<PhoneBook> list,Activity activity){
        mList=list;
        mActivity=activity;
    }

    @NonNull
    @Override
    public PhoneViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = inflater.inflate(R.layout.item_phonebook, parent, false);
        PhoneBookAdapter.PhoneViewHolder vh = new PhoneBookAdapter.PhoneViewHolder(view);

        return vh ;
    }

    @Override
    public void onBindViewHolder(@NonNull final PhoneViewHolder holder, int position) {
        final PhoneBook item=mList.get(position);
        holder.nameView.setText(item.name);
        holder.nameView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra("result", item.number);
                mActivity.setResult(200, intent);
                mActivity.finish();
            }
        });
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    static class PhoneViewHolder extends RecyclerView.ViewHolder{
        TextView nameView;
        PhoneViewHolder(@NonNull View itemView) {
            super(itemView);
            nameView=itemView.findViewById(R.id.item_name);
        }
    }
}
