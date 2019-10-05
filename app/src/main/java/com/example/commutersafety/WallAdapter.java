package com.example.commutersafety;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

public class WallAdapter extends RecyclerView.Adapter<WallAdapter.ViewHolder> {

    private String [] location1;
    private String [] location2;
    private String [] time;

    class ViewHolder extends RecyclerView.ViewHolder{

        private TextView textloc1;
        private TextView textloc2;
        private TextView texttime;
        private CardView wallcard;

        public ViewHolder(View itemView){
            super(itemView);

            wallcard = itemView.findViewById(R.id.wall_card);
            textloc1 = itemView.findViewById(R.id.actual_loc);
            textloc2 = itemView.findViewById(R.id.area);
            texttime = itemView.findViewById(R.id.time);
        }
    }

    public WallAdapter(String [] location1,String [] location2, String[] time)
    {
        this.location1 = location1;
        this.location2 = location2;
        this.time = time;
    }

    @NonNull
    @Override
    public WallAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.wall_cardview,parent,false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull WallAdapter.ViewHolder holder, int position) {
        holder.textloc1.setText(location1[position]);
        holder.textloc2.setText(location2[position]);
        holder.texttime.setText(time[position]);
    }

    @Override
    public int getItemCount() {
        return location1.length;
    }
}
