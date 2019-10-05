package com.example.commutersafety;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.squareup.picasso.Picasso;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ZoneInfoAdapter extends FirebaseRecyclerAdapter<Zone, ZoneInfoAdapter.ZoneViewHolder> {

    public ZoneInfoAdapter(@NonNull FirebaseRecyclerOptions<Zone> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull ZoneViewHolder holder, int position, @NonNull Zone model) {
        System.out.println("zone id is ="+model.getZoneID());
        holder.title.setText(model.zoneTitle);
        holder.description.setText(model.zoneData);
        holder.solution.setText(model.zoneSolution);
        Picasso.get().load(model.zoneImage).into(holder.imageview);
    }

    @NonNull
    @Override
    public ZoneViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.zone_info_row, parent, false);

        return new ZoneViewHolder(view);
    }

    class ZoneViewHolder extends RecyclerView.ViewHolder{
    TextView title,description,solution;
    ImageView imageview;
        public ZoneViewHolder(@NonNull View itemView) {
            super(itemView);
            title=itemView.findViewById(R.id.title);
            description=itemView.findViewById(R.id.description);
            solution=itemView.findViewById(R.id.solution);
            imageview = itemView.findViewById(R.id.imageView);
        }
    }
}
