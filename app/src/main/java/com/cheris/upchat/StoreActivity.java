package com.cheris.upchat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class StoreActivity extends AppCompatActivity {

    CardView btnPurchase,btnSubscribe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store);
        init();

    }
    private void init() {
        btnPurchase = (CardView) findViewById(R.id.card_purchase_item);
        btnSubscribe = (CardView) findViewById(R.id.card_subscribe_item);

        //Event
        btnPurchase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(StoreActivity.this,PurchaseItemActivity.class));
            }
        });
        btnSubscribe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(StoreActivity.this,SubscribeActivity.class));
            }
        });

    }
}