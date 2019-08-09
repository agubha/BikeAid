package com.example.bikeaid.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.bikeaid.AccessoriesDetail;
import com.example.bikeaid.Model.ProductModel;
import com.example.bikeaid.R;
import com.example.bikeaid.Utils.Util;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class AccessoriesRecyclerAdapter extends RecyclerView.Adapter<AccessoriesRecyclerAdapter.ViewHolder> {
    private Context context;
    private ArrayList<ProductModel> productModelArrayList;

    public AccessoriesRecyclerAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.accessories_grid_layout, viewGroup, false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewholder, int i) {
        viewholder.titleText.setText(productModelArrayList.get(viewholder.getAdapterPosition()).getTitle());
        Picasso.get().load(productModelArrayList.get(viewholder.getAdapterPosition()).getImage()).into(viewholder.imageView);
        viewholder.priceText.setText("Rs." + productModelArrayList.get(viewholder.getAdapterPosition()).getPrice());
        viewholder.priceText.setPaintFlags(viewholder.priceText.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        viewholder.percentageOffText.setText(Util.getdiscount
                (productModelArrayList.get(viewholder.getAdapterPosition()).getPrice(),
                        productModelArrayList.get(viewholder.getAdapterPosition()).getSalesPrice()) + " %off");
        viewholder.discountText.setText("Rs. " + productModelArrayList.get(viewholder.getAdapterPosition())
                .getSalesPrice());
        viewholder.constraintLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, AccessoriesDetail.class);
                intent.putExtra("id", productModelArrayList.get(viewholder.getAdapterPosition()).getId());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        if (productModelArrayList == null)
            return 0;
        else
            return productModelArrayList.size();
    }


    public void setProduct(ArrayList<ProductModel> productModelArrayList) {
        this.productModelArrayList = productModelArrayList;
        notifyDataSetChanged();
    }


    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView titleText, priceText, discountText, percentageOffText;
        private ImageView imageView;
        private ConstraintLayout constraintLayout;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            titleText = itemView.findViewById(R.id.productTitle);
            priceText = itemView.findViewById(R.id.productPrice);
            discountText = itemView.findViewById(R.id.productDiscountPrice);
            percentageOffText = itemView.findViewById(R.id.productDiscountRate);
            imageView = itemView.findViewById(R.id.productImage);
            constraintLayout = itemView.findViewById(R.id.viewMainConstraint);
        }
    }
}
