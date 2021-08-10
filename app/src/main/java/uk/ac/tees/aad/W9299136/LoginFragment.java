package uk.ac.tees.aad.W9299136;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


public class LoginFragment extends Fragment {

    EditText edEmail, edPassword;
    Button btnLogin;
    TextView forgotPassword, createNewAccount;

    public LoginFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_login, container, false);

        InitVaribale(view);

        return view;
    }

    private void InitVaribale(View view) {
        edEmail = view.findViewById(R.id.edEmail);
        edPassword = view.findViewById(R.id.edPassword);
        btnLogin = view.findViewById(R.id.btnLogin);
        createNewAccount = view.findViewById(R.id.createAccount);
        forgotPassword = view.findViewById(R.id.forgotPassword);

        createNewAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AuthActivity.fm.beginTransaction().replace(R.id.container, new RegisterFragment(), "register").commit();
            }
        });

        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "Forgot Password", Toast.LENGTH_SHORT).show();
            }
        });
    }
}