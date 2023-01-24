package com.example.osbikeshop;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegisterFragment extends Fragment {
    EditText username, password, firstName, lastName;
    Button button_register;
    private static final String TAG = "EmailPassword";
    private FirebaseAuth mAuth;
    FirebaseFirestore db;

    public RegisterFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_register, container, false);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        username = rootView.findViewById(R.id.editTextTextUsername);
        password = rootView.findViewById(R.id.editTextTextPassword);
        firstName = rootView.findViewById(R.id.editTextTextFirstName);
        lastName = rootView.findViewById(R.id.editTextTextLastName);
        button_register = rootView.findViewById(R.id.buttonRegister);
        button_register.setOnClickListener(v -> {
            mAuth.signOut();
            String emailVal = null;
            String passVal = null;
            String firstVal = null;
            String lastVal = null;
            String val = username.getText().toString();
            String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
            if (val.isEmpty()) {
                username.setError("Field cannot be empty");
            } else if (!val.matches(emailPattern)) {
                username.setError("Invalid email address");
            } else {
                username.setError(null);
                emailVal = val;
            }
            String val2 = password.getText().toString();
            if (val2.isEmpty()) {
                password.setError("Field cannot be empty");
            } else if (val2.length() < 6) {
                password.setError("Password needs to be at least 6 characters");
            } else {
                password.setError(null);;
                passVal = val2;
            }
            String val3 = firstName.getText().toString();
            if (val3.isEmpty()) {
                firstName.setError("Field cannot be empty");
            } else if (val3.length() < 3) {
                firstName.setError("First name needs to be at least 3 characters");
            } else {
                firstName.setError(null);;
                firstVal = val3;
            }
            String val4 = lastName.getText().toString();
            if (val4.isEmpty()) {
                lastName.setError("Field cannot be empty");
            } else if (val4.length() < 3) {
                lastName.setError("Last name needs to be at least 3 characters");
            } else {
                lastName.setError(null);;
                lastVal = val4;
            }
            if(passVal != null && emailVal != null && firstVal != null && lastVal != null) {
                register(username.getText().toString(), password.getText().toString(),firstName.getText().toString(),
                        lastName.getText().toString());
            }
        });
        return rootView;
    }
    private void register(String username, String password, String firstName, String lastName) {
        mAuth.createUserWithEmailAndPassword(username, password).addOnCompleteListener(getActivity(), task -> {

            if (task.isSuccessful()) {
                Log.d(TAG, "signInWithEmail:success");
                FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

                Map<String, Object> user = new HashMap<>();
                user.put("first", firstName);
                user.put("last", lastName);

                db.collection("users").document(currentUser.getUid())
                        .set(user)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d(TAG, "DocumentSnapshot successfully written!");
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w(TAG, "Error writing document", e);
                            }
                        });
                Intent intent = new Intent(getActivity(), ShopActivity.class);
                startActivity(intent);

            } else {
                Log.w(TAG, "signInWithEmail:failure", task.getException());
                Toast.makeText(getActivity(), "Incorrect username and password",
                        Toast.LENGTH_SHORT).show();

            }

        });
    }
}