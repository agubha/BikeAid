package com.example.bikeaid.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bikeaid.CosineSimilarityListing;
import com.example.bikeaid.MapsActivity;
import com.example.bikeaid.Model.ProblemModel;
import com.example.bikeaid.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class EmergencyProblemListAdapter extends RecyclerView.Adapter<EmergencyProblemListAdapter.ViewHolder> {
    private Context context;
    private List<ProblemModel> problemModels;

    public EmergencyProblemListAdapter(Context emergencyBreakDown) {
        this.context = emergencyBreakDown;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.problem_list_layout, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        String price = "Rs. " + problemModels.get(viewHolder.getAdapterPosition()).getPrice();
        viewHolder.price.setText(price);
        viewHolder.title.setText(problemModels.get(viewHolder.getAdapterPosition()).getName());
        Picasso.get().load(problemModels.get(viewHolder.getAdapterPosition()).getImg()).into(viewHolder.icon);
        viewHolder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, CosineSimilarityListing.class);
                intent.putExtra("problem", problemModels.get(viewHolder.getAdapterPosition()).getName());
                intent.putExtra("isService",false);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return problemModels.size();
    }

    public void setList(List<ProblemModel> problemModels) {
        this.problemModels = problemModels;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView title, price;
        ImageView icon;
        CardView cardView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.productTitle);
            price = itemView.findViewById(R.id.productPrice);
            icon = itemView.findViewById(R.id.productImage);
            cardView = itemView.findViewById(R.id.cardView);
        }
    }
}
