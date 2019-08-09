package com.example.bikeaid.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.bikeaid.CosineSimilarityMap;
import com.example.bikeaid.Model.Locations;
import com.example.bikeaid.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class CosineRecyclerAdapter extends RecyclerView.Adapter<CosineRecyclerAdapter.ViewHolder> {
    Context context;
    List<Locations> filteredList;
    List<Double> filteredCosineValueList;
    boolean isService;

    public CosineRecyclerAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public CosineRecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.similarity_list, viewGroup, false);
        return new CosineRecyclerAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CosineRecyclerAdapter.ViewHolder viewHolder, int i) {
        Picasso.get().load(filteredList.get(viewHolder.getAdapterPosition()).getImage()).into(viewHolder.imageView);
        viewHolder.title.setText(filteredList.get(viewHolder.getAdapterPosition()).getTitle());
        if (filteredCosineValueList.get(viewHolder.getAdapterPosition()) != null) {
            viewHolder.cosineValue.setText(String.valueOf(filteredCosineValueList.get(viewHolder.getAdapterPosition())));
        }
        viewHolder.constraintLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, CosineSimilarityMap.class);
                intent.putExtra("id", filteredList.get(viewHolder.getAdapterPosition()).getName());
                intent.putExtra("isService", isService);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        if (filteredList == null)
            return 0;
        else
            return filteredList.size();
    }

    public void setList(List<Locations> filteredList) {
        this.filteredList = filteredList;
        notifyDataSetChanged();
    }


    public void setCosineValues(List<Double> filteredCosineValueList) {
        this.filteredCosineValueList = filteredCosineValueList;
        notifyDataSetChanged();
    }

    public void setBooleanValue(boolean isService) {
        this.isService = isService;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView title, cosineValue;

        ConstraintLayout constraintLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image);
            title = itemView.findViewById(R.id.title);
            cosineValue = itemView.findViewById(R.id.cosineValue);
            constraintLayout = itemView.findViewById(R.id.constraintLayout);
        }
    }
}
