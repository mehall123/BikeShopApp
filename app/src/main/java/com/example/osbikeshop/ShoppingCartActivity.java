package com.example.osbikeshop;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.ArrayList;

public class ShoppingCartActivity extends AppCompatActivity {

    FirebaseFirestore db;
    ShoppingCartRecyclerViewAdapter adapter;
    TextView totalPrice;
    Button purchaseButton;
    private int sumPrice;
    ArrayList<Integer> itemImages = new ArrayList<Integer>();
    ArrayList<Integer> itemPrices = new ArrayList<>();
    ArrayList<String> itemNames = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shoppingcart);
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        db = FirebaseFirestore.getInstance();
        db.collection("users").document(currentUser.getUid()).collection("shoppingcart").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        itemNames.add(document.getString("Name"));
                        itemPrices.add(document.getLong("Price").intValue());
                        itemImages.add(getResources().getIdentifier(document.getString("image"), "drawable", getPackageName()));
                        sumPrice = sumPrice + document.getLong("Price").intValue();
                    }
                    createCards(itemImages, itemPrices, itemNames, sumPrice);
                    Log.d(TAG, itemNames.toString());
                } else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
            }
        });
    }

    public void createCards (ArrayList<Integer> itemImages, ArrayList<Integer> itemPrices, ArrayList<String> itemNames, int sumPrice) {
        RecyclerView recyclerView = findViewById(R.id.rvStore);
        recyclerView.setLayoutManager((new LinearLayoutManager(this)));

        adapter = new ShoppingCartRecyclerViewAdapter(this, itemNames, itemPrices, itemImages);
        //adapter.setClickListener(this);
        recyclerView.setAdapter(adapter);

        totalPrice = (TextView) findViewById(R.id.tvTotalPrice);
        totalPrice.setText("€" + Integer.toString(sumPrice));

        purchaseButton = findViewById(R.id.purchase_button);
        purchaseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent paymentPage = new Intent(ShoppingCartActivity.this, PaymentActivity.class);
                startActivity(paymentPage);
            }
        });
    }
}