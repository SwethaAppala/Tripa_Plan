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


public class RegisterFragment extends Fragment {
EditText edEmail;
EditText edPassword,edPassword2;
Button btnRegister;
TextView alreadyHaveAccount;

    public RegisterFragment() {
        // Required empty public constructor
    }




    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_register, container, false);

        InitVaribale(view);
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        return view;
    }

    private void InitVaribale(View view) {
        edEmail = view.findViewById(R.id.edEmail);
        edPassword = view.findViewById(R.id.edPassword);
        edPassword2 = view.findViewById(R.id.edPassword2);
        btnRegister = view.findViewById(R.id.btnRegister);
        alreadyHaveAccount = view.findViewById(R.id.alreadyHaveAccount);

        alreadyHaveAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AuthActivity.fm.beginTransaction().replace(R.id.container, new LoginFragment(), "register").commit();
            }
        });


    }
}