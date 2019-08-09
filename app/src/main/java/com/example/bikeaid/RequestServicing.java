package com.example.bikeaid;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.example.bikeaid.Model.ServiceModel;
import com.example.bikeaid.Utils.Util;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class RequestServicing extends AppCompatActivity {
    RecyclerView ServiceRecycler;
    ArrayList<ServiceModel> productModelArrayList;
    ServiceListAdapter serviceListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_servicing);
        ServiceRecycler = findViewById(R.id.ServiceRecycler);
        serviceListAdapter = new ServiceListAdapter(RequestServicing.this);
        ServiceRecycler.setAdapter(serviceListAdapter);
        ServiceRecycler.setLayoutManager(new GridLayoutManager(RequestServicing.this, 2));
        accessDataBase();
    }

    private void accessDataBase() {
        productModelArrayList = new ArrayList<>();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("ServiceList");
        ref.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                            productModelArrayList.add(dataSnapshot1.getValue(ServiceModel.class));
                        }
                        serviceListAdapter.setProduct(productModelArrayList);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Util.toast(databaseError.getDetails(), RequestServicing.this);
                    }
                });
    }

}

