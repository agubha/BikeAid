package com.example.bikeaid;

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

import com.example.bikeaid.Model.ServiceModel;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ServiceListAdapter extends RecyclerView.Adapter<ServiceListAdapter.Viewholder> {
    private ArrayList<ServiceModel> serviceModels;
    private Context context;

    public ServiceListAdapter(Context requestServicing) {
        this.context = requestServicing;
    }

    @NonNull
    @Override
    public Viewholder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.service_list_recycler_view, viewGroup, false);
        return new ServiceListAdapter.Viewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Viewholder viewholder, int i) {
        viewholder.title.setText(serviceModels.get(viewholder.getAdapterPosition()).getTitle());
        viewholder.price.setText(String.valueOf(serviceModels.get(viewholder.getAdapterPosition()).getPrice()));
        viewholder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, CosineSimilarityListing.class);
                intent.putExtra("problem", "" + serviceModels.get(i).getTitle());
                intent.putExtra("isService",true);

                context.startActivity(intent);
            }
        });
        Picasso.get().load(serviceModels.get(i).getImage()).into(viewholder.imageView);
    }

    @Override
    public int getItemCount() {
        if (serviceModels == null)
            return 0;
        else
            return serviceModels.size();
    }

    public void setProduct(ArrayList<ServiceModel> productModelArrayList) {
        this.serviceModels = productModelArrayList;
        notifyDataSetChanged();
    }

    public class Viewholder extends RecyclerView.ViewHolder {
        TextView title, price;
        ImageView imageView;
        CardView cardView;

        public Viewholder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title_service);
            price = itemView.findViewById(R.id.price_service);
            imageView = itemView.findViewById(R.id.image_service);
            cardView = itemView.findViewById(R.id.card_Service);
        }
    }
}
