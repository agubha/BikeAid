package com.example.bikeaid.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.bikeaid.Adapter.AccessoriesRecyclerAdapter;
import com.example.bikeaid.Model.ProductModel;
import com.example.bikeaid.R;
import com.example.bikeaid.Utils.Util;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class PurchaseFragment extends Fragment {
    AccessoriesRecyclerAdapter accessoriesRecyclerAdapter;
    ArrayList<ProductModel> productModelArrayList;
    private Context context;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        context = getContext();
        View view = inflater.inflate(R.layout.fragment_accessories, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.accessoriesRecycler);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        accessoriesRecyclerAdapter = new AccessoriesRecyclerAdapter(getContext());
        recyclerView.setAdapter(accessoriesRecyclerAdapter);
        //Make Network Call to Freebase
        productModelArrayList = new ArrayList<>();
        accessDataBase();
        return view;
    }

    private void accessDataBase() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Accessories");
        ref.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                            productModelArrayList.add(dataSnapshot1.getValue(ProductModel.class));
                        }
                        accessoriesRecyclerAdapter.setProduct(productModelArrayList);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Util.toast(databaseError.getDetails(), context);
                    }
                });
    }

    public static PurchaseFragment newInstance(int page, String title) {
        PurchaseFragment fragmentFirst = new PurchaseFragment();
        Bundle args = new Bundle();
        args.putInt("someInt", page);
        args.putString("someTitle", title);
        fragmentFirst.setArguments(args);
        return fragmentFirst;
    }
}
