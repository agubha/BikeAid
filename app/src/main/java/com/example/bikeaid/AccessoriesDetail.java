package com.example.bikeaid;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.bikeaid.Model.ProductModel;
import com.example.bikeaid.Utils.Util;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.Objects;

public class AccessoriesDetail extends AppCompatActivity {
    private String id;
    private ImageView imageView;
    private TextView title, description, specification, price, salesprice, qty;
    private NestedScrollView scrollView2;
    private SeekBar seekBar;
    private int qtyT = 1, priceT = 0, salespriceT = 0;
    private int priceO, salesprice0;
    private Button btnPurchase;
    private int stock;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accessories_detail);
        getViews();
        getIntents();
        initSeek();
        accessDataBase();
        btnPurchase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verifyPurchase();
            }
        });

    }

    private void verifyPurchase() {
        new AlertDialog.Builder(this)
                .setTitle("Verify Purchse")
                .setMessage("Press ok to confirm purchase")

                // Specifying a listener allows you to take an action before dismissing the dialog.
                // The dialog is automatically dismissed when a dialog button is clicked.
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Continue with delete operation
                        if (performPurchase()) dialog.dismiss();
                    }
                })
                // A null listener allows the button to dismiss the dialog and take no further action.
                .setNegativeButton(android.R.string.no, null)
                .setIcon(R.drawable.ic_add_shopping_cart_black_24dp)
                .show();
    }

    private boolean performPurchase() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Accessories").child(String.valueOf(id));
        stock = stock - qtyT;
        if (stock < 0) {
            Util.toast("Out of Stock", this);
        } else
            ref.child("Stock").setValue(stock);
        return true;
    }

    private void initSeek() {
        seekBar.setMax(10);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                qtyT = seekBar.getProgress();
                Util.toast(qtyT + "", AccessoriesDetail.this);
                priceT = priceO * qtyT;
                salespriceT = salesprice0 * qtyT;
                setNewQty();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
    }

    @SuppressLint("SetTextI18n")
    private void setNewQty() {
        price.setText("Rs. " + priceT);
        salesprice.setText("Rs. " + salespriceT);
        qty.setText("x " + qtyT);
    }

    private void getViews() {
        imageView = findViewById(R.id.productImage);
        title = findViewById(R.id.title);
        description = findViewById(R.id.description);
        specification = findViewById(R.id.specification);
        price = findViewById(R.id.price);
        price.setPaintFlags(price.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        salesprice = findViewById(R.id.salesPrice);
        scrollView2 = findViewById(R.id.scrollView2);
        scrollView2.setNestedScrollingEnabled(false);
        seekBar = findViewById(R.id.seekBar);
        qty = findViewById(R.id.qty);
        btnPurchase = findViewById(R.id.btnPurchase);
    }


    private void accessDataBase() {
        Log.d("ID", "" + id);
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Accessories").child(String.valueOf(id));
        ref.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        ProductModel productModel = dataSnapshot.getValue(ProductModel.class);
                        if (productModel != null) {
                            Picasso.get().load(productModel.getImage()).into(imageView);
                            title.setText(productModel.getTitle());
                            description.setText(productModel.getDescription());
                            specification.setText(productModel.getSpecification());
                            Objects.requireNonNull(getSupportActionBar()).setTitle(productModel.getTitle());
                            priceO = productModel.getPrice();
                            salesprice0 = productModel.getSalesPrice();
                            salespriceT = salesprice0;
                            priceT = priceO;
                            stock = productModel.getStock();
                            setNewQty();
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Util.toast(databaseError.getDetails(), AccessoriesDetail.this);
                    }
                });
    }

    private void getIntents() {
        if (getIntent().hasExtra("id"))
            id = String.valueOf(getIntent().getIntExtra("id", 0));
    }
}
