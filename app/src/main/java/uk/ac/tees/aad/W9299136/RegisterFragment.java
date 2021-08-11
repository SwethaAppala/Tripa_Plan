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


public class RegisterFragment extends Fragment {
    EditText edEmail;
    EditText edPassword, edPassword2;
    Button btnRegister;
    TextView alreadyHaveAccount;
    FirebaseAuth mAuth;
    FirebaseUser mUser;


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
        View view = inflater.inflate(R.layout.fragment_register, container, false);

        InitVaribale(view);
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PerformRegistration();
            }
        });
        return view;
    }

    private void PerformRegistration() {
        String email = edEmail.getText().toString();
        String password = edPassword.getText().toString();
        String password2 = edPassword2.getText().toString();

        if (email.isEmpty() || !email.matches(Common.emailPattern)) {
            edEmail.setError("Enter Correct Email");
            edEmail.requestFocus();
        } else if (password.isEmpty()) {
            edPassword.setError("Enter Correct Password");
            edPassword.requestFocus();
        } else if (!password.equals(password2)) {
            edPassword2.setError("Both Password are not Matched");
            edPassword2.requestFocus();
        } else {
            AttemptRegistration(email,password);
        }

    }

    private void AttemptRegistration(String email, String password) {
        CustomDialog customDialog = new CustomDialog(getContext());
        customDialog.ShowDialog("Registration");

        mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
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
        edPassword2 = view.findViewById(R.id.edPassword2);
        btnRegister = view.findViewById(R.id.btnRegister);
        alreadyHaveAccount = view.findViewById(R.id.alreadyHaveAccount);

        mAuth=FirebaseAuth.getInstance();
        mUser=mAuth.getCurrentUser();

        alreadyHaveAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AuthActivity.fm.beginTransaction().replace(R.id.container, new LoginFragment(), "register").commit();
            }
        });


    }
}