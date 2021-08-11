package uk.ac.tees.aad.W9299136;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import uk.ac.tees.aad.W9299136.Utills.Common;
import uk.ac.tees.aad.W9299136.Utills.CustomDialog;


public class LoginFragment extends Fragment {
    EditText edEmail, edPassword;
    Button btnLogin;
    TextView forgotPassword, createNewAccount;
    FirebaseAuth mAuth;
    FirebaseUser mUser;

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
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PerformLogin();
            }
        });

        return view;
    }

    private void PerformLogin() {
        String email = edEmail.getText().toString();
        String password = edPassword.getText().toString();

        if (email.isEmpty() || !email.matches(Common.emailPattern)) {
            edEmail.setError("Enter Correct Email");
            edEmail.requestFocus();
        } else if (password.isEmpty()) {
            edPassword.setError("Enter Correct Password");
            edPassword.requestFocus();
        } else {
            AttemptLogin(email,password);
        }
    }

    private void AttemptLogin(String email, String password) {
        CustomDialog customDialog = new CustomDialog(getContext());
        customDialog.ShowDialog("Logging");

        mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull @org.jetbrains.annotations.NotNull Task<AuthResult> task) {
                if (task.isSuccessful())
                {
                    customDialog.DismissDialog();
                    SendUserToMainActivity();
                    Toast.makeText(getContext(), "Registration Completed!", Toast.LENGTH_SHORT).show();
                }else
                {
                    customDialog.DismissDialog();
                    Toast.makeText(getContext(), ""+task.getException(), Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
    private void SendUserToMainActivity() {
        Intent intent=new Intent(getContext(),MainActivity.class);
        startActivity(intent);
        getActivity().finish();
    }

    private void InitVaribale(View view) {
        edEmail = view.findViewById(R.id.edEmail);
        edPassword = view.findViewById(R.id.edPassword);
        btnLogin = view.findViewById(R.id.btnLogin);
        createNewAccount = view.findViewById(R.id.createAccount);
        forgotPassword = view.findViewById(R.id.forgotPassword);
        mAuth=FirebaseAuth.getInstance();
        mUser=mAuth.getCurrentUser();


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