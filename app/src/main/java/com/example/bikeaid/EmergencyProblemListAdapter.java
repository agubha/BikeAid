package com.example.bikeaid;

import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bikeaid.Model.ProblemModel;
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
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, final int i) {
        String price = "Rs. " + problemModels.get(i).getPrice();
        viewHolder.price.setText(price);
        viewHolder.title.setText(problemModels.get(i).getName());
        Picasso.get().load(problemModels.get(i).getImg()).into(viewHolder.icon);
        viewHolder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, MapsActivity.class);
                intent.putExtra("id", problemModels.get(i).getId());
                intent.putExtra("img", problemModels.get(i).getImg());
                intent.putExtra("price", problemModels.get(i).getPrice());
                intent.putExtra("name", problemModels.get(i).getName());
                context.startActivity(intent);
                Toast.makeText(context, "Selected Problem:" + problemModels.get(i).getName(), Toast.LENGTH_SHORT).show();
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
            title = itemView.findViewById(R.id.title);
            price = itemView.findViewById(R.id.price);
            icon = itemView.findViewById(R.id.imageView2);
            cardView = itemView.findViewById(R.id.cardView);
        }
    }
}
