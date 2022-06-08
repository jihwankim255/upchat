package com.cheris.upchat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.billingclient.api.AcknowledgePurchaseParams;
import com.android.billingclient.api.AcknowledgePurchaseResponseListener;
import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.ConsumeParams;
import com.android.billingclient.api.ConsumeResponseListener;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesResponseListener;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsParams;
import com.android.billingclient.api.SkuDetailsResponseListener;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.cheris.upchat.Model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class StoreActivity extends AppCompatActivity {

    BillingClient billingclinet;
    FirebaseAuth auth;
    FirebaseDatabase database;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store);

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        billingclinet = BillingClient.newBuilder(this)
                .enablePendingPurchases()
                .setListener(new PurchasesUpdatedListener() {
                    @Override
                    public void onPurchasesUpdated(@NonNull BillingResult billingResult, @Nullable List<Purchase> list) {
                        if(billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK && list != null) {
                            for(Purchase purchase: list) {
                                if (purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED &&
                                !purchase.isAcknowledged()) {
//                                    verify the purchase using a backend server
                                    Log.d("getSkus!", String.valueOf(purchase.getSkus()));
                                    Log.d("getSkus!", String.valueOf(String.valueOf(purchase.getSkus()).equals("[300point]")));

                                    verifyPurchase(purchase);

                                }
                            }
                        }
                    }
                }).build();
        connectToGooglePlayBilling();
    }

    @Override
    protected void onResume() {
        super.onResume();
        billingclinet.queryPurchasesAsync(
                BillingClient.SkuType.INAPP,
                new PurchasesResponseListener() {
                    @Override
                    public void onQueryPurchasesResponse(@NonNull BillingResult billingResult, @NonNull List<Purchase> list) {
                        if(billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                            for(Purchase purchase: list) {
                                if(purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED &&
                                !purchase.isAcknowledged()) {
//                                    verifyPurchase(purchase);
                                    Log.d("resumed","resumed");
                                }
                            }
                        }
                    }
                }
        );
    }

    private void connectToGooglePlayBilling() {
        billingclinet.startConnection(
                new BillingClientStateListener() {
                    @Override
                    public void onBillingServiceDisconnected() {
                        connectToGooglePlayBilling();
                    }

                    @Override
                    public void onBillingSetupFinished(@NonNull BillingResult billingResult) {
                        if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                            getProductDetails();
                        }
                    }
                }
        );
    }

    private void verifyPurchase(Purchase purchase) {
        String requestUrl ="https://us-central1-upchat-a0789.cloudfunctions.net/verifyPurchases?" +
                "purchaseToken=" + purchase.getPurchaseToken() + "&" +
                "purchaseTime=" + purchase.getPurchaseTime() + "&" +
                "orderId=" + purchase.getOrderId();
        Activity activity = this;
        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                requestUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
//                        여러번 구매하게 하기위해
                        try {
                            JSONObject purchaseInfoFromServer = new JSONObject(response);
                            Log.d("FromServer", String.valueOf(purchaseInfoFromServer));
                            Log.d("FromServergetBoolean", String.valueOf(purchaseInfoFromServer.getBoolean("isValid")));
                            Log.d("FromServerget", String.valueOf(purchaseInfoFromServer.get("isValid")));
                            Log.d("FromServergetString", purchaseInfoFromServer.getString("isValid"));
                            Log.d("FromServergetStringId", purchaseInfoFromServer.getString("orderId"));

                            if(purchaseInfoFromServer.getBoolean("isValid")) {
                                ConsumeParams consumeParams = ConsumeParams.newBuilder().setPurchaseToken(purchase.getPurchaseToken()).build();
                                billingclinet.consumeAsync(
                                        consumeParams,
                                        new ConsumeResponseListener() {
                                            @Override
                                            public void onConsumeResponse(@NonNull BillingResult billingResult, @NonNull String s) {
                                                if(billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
//                                                    Toast.makeText(activity, "Consumed!", Toast.LENGTH_LONG).show();
                                                    // 이부분에 데이터입력
                                                    database.getReference().child("Users")
                                                            .child(auth.getUid())
                                                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                            try {
                                                                if (snapshot.exists()){
                                                                    User user = snapshot.getValue(User.class);
//                                                                Log.d("snapshot", String.valueOf(user.getPoints()));
                                                                    Log.d("plusGetSkus", String.valueOf(purchase.getSkus().get(0)));
                                                                    String sku = purchase.getSkus().get(0);
                                                                    switch (sku) {
                                                                        case "100point":
                                                                            database.getReference().child("Users")
                                                                                    .child(auth.getUid())
                                                                                    .child("points").setValue(user.getPoints()+ 100);
                                                                            break;

                                                                        case "300point":
                                                                            database.getReference().child("Users")
                                                                                    .child(auth.getUid())
                                                                                    .child("points").setValue(user.getPoints()+ 300);
                                                                            break;
                                                                        case "500point":
                                                                            database.getReference().child("Users")
                                                                                    .child(auth.getUid())
                                                                                    .child("points").setValue(user.getPoints()+ 500);
                                                                            break;
                                                                        case "1000point":
                                                                            database.getReference().child("Users")
                                                                                    .child(auth.getUid())
                                                                                    .child("points").setValue(user.getPoints()+ 1000);
                                                                            break;
                                                                        case "1200point":
                                                                            database.getReference().child("Users")
                                                                                    .child(auth.getUid())
                                                                                    .child("points").setValue(user.getPoints()+ 1200);
                                                                            break;
                                                                        case "30000point":
                                                                            database.getReference().child("Users")
                                                                                    .child(auth.getUid())
                                                                                    .child("points").setValue(user.getPoints()+ 30000);
                                                                            break;


                                                                    }

                                                                }
                                                            } catch (Exception err) {
                                                                Toast.makeText(StoreActivity.this, ""+err, Toast.LENGTH_SHORT).show();
                                                                database.getReference().child("Users").child(auth.getUid()).child("errorMessage").setValue(""+err);
                                                            }





                                                        }

                                                        @Override
                                                        public void onCancelled(@NonNull DatabaseError error) {

                                                        }
                                                    });

                                                }
                                            }
                                        }
                                );

//                                AcknowledgePurchaseParams acknowledgePurchaseParams
//                                        = AcknowledgePurchaseParams.newBuilder().setPurchaseToken(purchase.getPurchaseToken()).build();
//                                billingclinet.acknowledgePurchase(
//                                        acknowledgePurchaseParams,
//                                        new AcknowledgePurchaseResponseListener() {
//                                            @Override
//                                            public void onAcknowledgePurchaseResponse(@NonNull BillingResult billingResult) {
//                                                if(billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
//                                                    Toast.makeText(activity,"ACKNOWLEDGED!", Toast.LENGTH_LONG).show();
//                                                }
//                                            }
//                                        }
//                                );
                            }
//                            else if ( purchase.getPurchaseState() == Purchase.PurchaseState.PENDING) {
//                                Toast.makeText(StoreActivity.this, "Pending!", Toast.LENGTH_SHORT).show();
//                                Log.d("Pending!", "Pending!");
//                            } else if(purchase.getPurchaseState() == Purchase.PurchaseState.UNSPECIFIED_STATE){
//                                Toast.makeText(StoreActivity.this, "UNSPECIFIED_STATE", Toast.LENGTH_SHORT).show();
//                                Log.d("UNSPECIFIED_STATE!", "UNSPECIFIED_STATE!");
//                            } else {
//                                Log.d("inavlid!", "inavlid!");
//                            }

                            } catch (Exception err) {
//                            Toast.makeText(activity, "Not Consumed! " + err, Toast.LENGTH_LONG).show();
                            Log.d("Exception", "Exception: " + err);

                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("volleyError","volleyError");

                    }
                }
        );

        Volley.newRequestQueue(this).add(stringRequest);

    }

    private void getProductDetails() {
        List<String> productIds = new ArrayList<>();
        productIds.add(0,"100point");
        productIds.add(0,"300point");
        productIds.add(0,"500point");
        productIds.add(0,"1000point");
        productIds.add(0,"1200point");
        productIds.add(0,"30000point");
        Log.d("preproductId", String.valueOf(productIds));
        SkuDetailsParams getProductDetailsQuery = SkuDetailsParams
                .newBuilder()
                .setSkusList(productIds)
                .setType(BillingClient.SkuType.INAPP)
                .build();
        Activity activity = this;
        billingclinet.querySkuDetailsAsync(
                getProductDetailsQuery,
                new SkuDetailsResponseListener() {
                    @Override
                    public void onSkuDetailsResponse(@NonNull BillingResult billingResult, @Nullable List<SkuDetails> list) {
                        if(billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK &&
                        list != null) {

                            TextView tvPoint100 = findViewById(R.id.point100);
                            Button btnPoint100 = findViewById(R.id.buy100point);
                            TextView tvPoint300 = findViewById(R.id.point300);
                            Button btnPoint300 = findViewById(R.id.buy300point);
                            TextView tvPoint500 = findViewById(R.id.point500);
                            Button btnPoint500 = findViewById(R.id.buy500point);
                            TextView tvPoint1000 = findViewById(R.id.point1000);
                            Button btnPoint1000 = findViewById(R.id.buy1000point);
                            TextView tvPoint1200 = findViewById(R.id.point1200);
                            Button btnPoint1200 = findViewById(R.id.buy1200point);
                            TextView tvPoint30000 = findViewById(R.id.point30000);
                            Button btnPoint30000 = findViewById(R.id.buy30000point);
                            SkuDetails itemInfo0 = list.get(0);
                            SkuDetails itemInfo1 = list.get(1);
                            SkuDetails itemInfo2 = list.get(2);
                            SkuDetails itemInfo3 = list.get(3);
                            SkuDetails itemInfo4 = list.get(4);
                            SkuDetails itemInfo5 = list.get(5);
                            Log.d("listGetAll", String.valueOf(list));
                            Log.d("listGet0", String.valueOf(list.get(0)));
                            Log.d("listGet1", String.valueOf(list.get(1)));
                            Log.d("listGet2", String.valueOf(list.get(2)));
                            Log.d("listGet3", String.valueOf(list.get(3)));
                            Log.d("listGet4", String.valueOf(list.get(4)));
                            Log.d("listGet5", String.valueOf(list.get(5)));
//                            tvPoint100.setText(itemInfo.getTitle());
//                            btnPoint100.setText(itemInfo.getPrice());
                            btnPoint100.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    billingclinet.launchBillingFlow(
                                            activity,
                                            BillingFlowParams.newBuilder().setSkuDetails(itemInfo1).build()
                                    );
                                }
                            });
                            btnPoint300.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    billingclinet.launchBillingFlow(
                                            activity,
                                            BillingFlowParams.newBuilder().setSkuDetails(itemInfo4).build()
                                    );
                                }
                            });
                            btnPoint500.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    billingclinet.launchBillingFlow(
                                            activity,
                                            BillingFlowParams.newBuilder().setSkuDetails(itemInfo5).build()
                                    );
                                }
                            });
                            btnPoint1000.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    billingclinet.launchBillingFlow(
                                            activity,
                                            BillingFlowParams.newBuilder().setSkuDetails(itemInfo0).build()
                                    );
                                }
                            });
                            btnPoint1200.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    billingclinet.launchBillingFlow(
                                            activity,
                                            BillingFlowParams.newBuilder().setSkuDetails(itemInfo2).build()
                                    );
                                }
                            });
                            btnPoint30000.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    billingclinet.launchBillingFlow(
                                            activity,
                                            BillingFlowParams.newBuilder().setSkuDetails(itemInfo3).build()
                                    );
                                }
                            });
                        }
                    }
                }

        );
    }
}