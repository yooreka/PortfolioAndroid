package com.example.ktravel;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class GridAdapter extends RecyclerView.Adapter<GridAdapter.MyViewHolder> {
    Context context;
    ArrayList<Item> list;

    public GridAdapter(Context context, ArrayList<Item> list) {
        super();
        this.context = context;
        this.list = list;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.image.setImageResource(list.get(position).image);
        holder.name.setText(list.get(position).name);

    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recyclerview_row, parent, false);
        return new MyViewHolder(view);

    }

    @Override
    public int getItemCount() {

        return list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        ImageView image;
        TextView name;

        public MyViewHolder(View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.recycle_row_image);
            name = itemView.findViewById(R.id.recycle_row_name);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int pos = getAdapterPosition() ;
                    if (pos != RecyclerView.NO_POSITION) {
                        // 데이터 리스트로부터 아이템 데이터 참조
                        Item item = list.get(pos);
                       //Log.e("위치:", item.getName());
                       switch (item.getName()){
                           case "산":
                               Intent intent = new Intent(context, MountainActivity.class);
                               context.startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                               break;
                           case "시장":
                                intent = new Intent(context, MarketActivity.class);
                               context.startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                               break;
                           case "축제":
                               intent = new Intent(context, FestivalActivity.class);
                               context.startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                               break;
                           case "캠핑":
                               intent = new Intent(context, CampingActivity.class);
                               context.startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                               break;
                           case "테마파크":
                               intent = new Intent(context, TemaParkActivity.class);
                               context.startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                               break;
                           case "해변":
                               intent = new Intent(context, CustomListViewActivity.class);
                               context.startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                               break;
                           case "박물관":
                               intent = new Intent(context, MuseumActivity.class);
                               context.startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                               break;
                           case "항구":
                               intent = new Intent(context, SeaportActivity.class);
                               context.startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                               break;

                       }

                    }
                }
            });
        }
    }

}
