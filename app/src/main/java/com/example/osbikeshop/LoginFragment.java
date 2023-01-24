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

import androidx.annotation.Nullable;

import com.google.firebase.auth.FirebaseAuth;
import androidx.fragment.app.Fragment;

public class LoginFragment extends Fragment {
    EditText username, password;
    Button button_login;
    private static final String TAG = "EmailPassword";
    private FirebaseAuth mAuth;

    public LoginFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_login, container, false);
        mAuth = FirebaseAuth.getInstance();
        username = rootView.findViewById(R.id.editTextTextUsername);
        password = rootView.findViewById(R.id.editTextTextPassword);
        button_login = rootView.findViewById(R.id.buttonLogin);
        button_login.setOnClickListener(v -> {
            mAuth.signOut();
            String emailVal = null;
            String passVal = null;
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
            if(passVal != null && emailVal != null) {
                signIn(username.getText().toString(), password.getText().toString());
            }
        });
        return rootView;
    }

    private void signIn(String username, String password) {
        mAuth.signInWithEmailAndPassword(username, password).addOnCompleteListener(getActivity(), task -> {

            if (task.isSuccessful()) {
                Log.d(TAG, "signInWithEmail:success");
                Intent intent = new Intent(getActivity(), ShopActivity.class);
                startActivity(intent);

            } else {
                Log.w(TAG, "signInWithEmail:failure", task.getException());
                Toast.makeText(getActivity(), "Incorrect username and/or password",
                        Toast.LENGTH_SHORT).show();

            }

        });
    }
}