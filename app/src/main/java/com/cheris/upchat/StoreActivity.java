package com.cheris.upchat;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.ConsumeParams;
import com.android.billingclient.api.ConsumeResponseListener;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsParams;
import com.android.billingclient.api.SkuDetailsResponseListener;
import com.cheris.upchat.Utils.BillingClientSetup;

import java.util.Arrays;
import java.util.List;

public class StoreActivity extends AppCompatActivity implements PurchasesUpdatedListener {

    BillingClient billingClient;
    ConsumeResponseListener consumeResponseListener;

    Button abtn;
    TextView a;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store);

        setupBillingClient();
        init();

    }
    private  void init() {
        a = (TextView) findViewById(R.id.a);
        abtn = (Button) findViewById(R.id.a_btn);

        abtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (billingClient.isReady()) {
                    SkuDetailsParams params = SkuDetailsParams.newBuilder()
                            .setSkusList(Arrays.asList("포인트"))
                            .setType(BillingClient.SkuType.INAPP)
                            .build();
                    billingClient.querySkuDetailsAsync(params, new SkuDetailsResponseListener() {
                        @Override
                        public void onSkuDetailsResponse(@NonNull BillingResult billingResult, @Nullable List<SkuDetails> list) {
                            if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                                loadProduct(list);
                            } else {
                                Toast.makeText(StoreActivity.this, "Error code: " +billingResult.getResponseCode(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });

    }

    private void loadProduct(List<SkuDetails> list) {

    }

    private void setupBillingClient() {
        consumeResponseListener = new ConsumeResponseListener() {
            @Override
            public void onConsumeResponse(@NonNull BillingResult billingResult, @NonNull String s) {
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                    Toast.makeText(StoreActivity.this, "Consume OK", Toast.LENGTH_SHORT).show();
                }
            }
        };
        billingClient = BillingClientSetup.getInstance(this, this);
        billingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingServiceDisconnected() {
                Toast.makeText(StoreActivity.this, "You are disconnected from Billing service", Toast.LENGTH_SHORT).show();
                
            }

            @Override
            public void onBillingSetupFinished(@NonNull BillingResult billingResult) {
                if(billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                    Toast.makeText(StoreActivity.this, "Success to connect billing", Toast.LENGTH_SHORT).show();
                    //Query
                    List<Purchase> purchases = billingClient.queryPurchases(BillingClient.SkuType.INAPP)
                            .getPurchasesList();
                    handleItemAlreadyPurchased(purchases);
                } else {
                    Toast.makeText(StoreActivity.this, "Error code: "+
                            billingResult.getResponseCode(), Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

    private void handleItemAlreadyPurchased(List<Purchase> purchases) {
        StringBuilder purchasedItem = new StringBuilder(a.getText());
        for(Purchase purchase : purchases) {
            if (purchase.getSkus().equals("point")) {
                ConsumeParams consumeParams = ConsumeParams.newBuilder()
                        .setPurchaseToken(purchase.getPurchaseToken())
                        .build();
                billingClient.consumeAsync(consumeParams,consumeResponseListener);
            }
            purchasedItem.append("\n"+purchase.getSkus())
                    .append("\n");
        }
        a.setText(purchasedItem.toString());
        //a.setVisibility(View.VISIBLE);
    }

    @Override
    public void onPurchasesUpdated(@NonNull BillingResult billingResult, @Nullable List<Purchase> list) {

    }
}