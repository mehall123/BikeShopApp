package com.example.osbikeshop;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ShopActivity extends AppCompatActivity implements cardViewRecyclerViewAdapter.ItemClickListener {

    private FirebaseAuth mAuth;
    FirebaseFirestore db;
    private int itemPosition, counter = 0;
    cardViewRecyclerViewAdapter adapter;
    Button checkout_button;
    TextView basket_count;
    ArrayList<Integer> itemImages = new ArrayList<Integer>();
    ArrayList<Integer> itemPrices = new ArrayList<>();
    ArrayList<String> itemNames = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop);
        db = FirebaseFirestore.getInstance();
        db.collection("bikes").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        itemNames.add(document.getString("Name"));
                        itemPrices.add(document.getLong("Price").intValue());
                        itemImages.add(getResources().getIdentifier(document.getString("image"), "drawable", getPackageName()));
                    }
                    createCards(itemImages, itemPrices, itemNames);
                    Log.d(TAG, itemNames.toString());
                } else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
            }
        });
    }

    public void createCards (ArrayList<Integer> itemImages, ArrayList<Integer> itemPrices, ArrayList<String> itemNames) {
        RecyclerView recyclerView = findViewById(R.id.rvStore);
        recyclerView.setLayoutManager((new LinearLayoutManager(this)));

        adapter = new cardViewRecyclerViewAdapter(this, itemNames, itemPrices, itemImages);
        adapter.setClickListener(this);
        recyclerView.setAdapter(adapter);

        basket_count = (TextView)findViewById(R.id.itemCount);
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        db.collection("users").document(currentUser.getUid()).collection("shoppingcart").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                int counter = 0;
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        counter = counter + 1;
                        basket_count.setText("Shopping Cart: " + Integer.toString(counter));
                    }
                    Log.d(TAG, itemNames.toString());
                } else {
                    basket_count.setText("Shopping Cart: " + Integer.toString(1));
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
            }
        });
        checkout_button = findViewById(R.id.rvCheckoutButton);
        checkout_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent checkoutPage = new Intent(ShopActivity.this, ShoppingCartActivity.class);
                startActivity(checkoutPage);
            }
        });
    }

    public void onItemClick (View view, int position) {
        itemPosition = position;
        PopupMenu popup = new PopupMenu(ShopActivity.this, view);
        popup.setOnMenuItemClickListener(ShopActivity.this::onMenuItemClick);
        popup.inflate(R.menu.shop_popup_menu);
        popup.show();
    }

    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.omAddToCart:
                addItemToShoppingCart();
                FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                db.collection("users").document(currentUser.getUid()).collection("shoppingcart").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        int counter = 1;
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                counter = counter + 1;
                            }
                            basket_count.setText("Shopping Cart: " + Integer.toString(counter));
                            Log.d(TAG, itemNames.toString());
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }
    }

    private void addItemToShoppingCart() {
        String itemName = itemNames.get(itemPosition);
        int price = itemPrices.get(itemPosition);
        DocumentReference documentReference = db.collection("bikes").document(itemName);
        documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists()) {
                    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                    Map<String, Object> bike = new HashMap<>();
                    bike.put("Name", itemName);
                    bike.put("Price", price);
                    bike.put("image", documentSnapshot.getString("image"));

                    db.collection("users").document(currentUser.getUid()).collection("shoppingcart").add(bike);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });

        Toast.makeText(this,  itemName + " has been added to the shopping cart", Toast.LENGTH_SHORT).show();
    }

}