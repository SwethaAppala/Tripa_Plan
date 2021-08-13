package uk.ac.tees.aad.W9299136;

import android.content.Intent;
import android.content.SharedPreferences;

import android.os.Bundle;


import androidx.annotation.NonNull;

import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.constraintlayout.widget.ConstraintLayout;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;


import android.provider.Settings;
import android.util.Log;
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


import java.util.concurrent.Executor;

import uk.ac.tees.aad.W9299136.Utills.Common;
import uk.ac.tees.aad.W9299136.Utills.CustomDialog;

import static android.content.Context.MODE_PRIVATE;
import static androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG;
import static androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL;

public class LoginFragment extends Fragment {
    EditText edEmail, edPassword;
    Button btnLogin;
    TextView forgotPassword, createNewAccount;
    FirebaseAuth mAuth;
    FirebaseUser mUser;
    ConstraintLayout fingerPrint;


    //from Dcos
    Executor executor;
    BiometricPrompt biometricPrompt;
    BiometricPrompt.PromptInfo promptInfo;


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
        LoadSharePref();
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = edEmail.getText().toString();
                String password = edPassword.getText().toString();
                PerformLogin(email, password);
            }
        });

        fingerPrint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FingerPrintAuthentication();
            }
        });

        return view;
    }

    private void FingerPrintAuthentication() {
        SharedPreferences prefs = getActivity().getApplicationContext().getSharedPreferences("User", MODE_PRIVATE);
        String email = prefs.getString("email", "");
        String password = prefs.getString("password", "");


        BiometricManager biometricManager = BiometricManager.from(getContext());
        switch (biometricManager.canAuthenticate(BIOMETRIC_STRONG | DEVICE_CREDENTIAL)) {
            case BiometricManager.BIOMETRIC_SUCCESS:
                Log.d("MY_APP_TAG", "App can authenticate using biometrics.");
                break;
            case BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE:
                Toast.makeText(getContext(), "Fingerprint sensor Not exist", Toast.LENGTH_SHORT).show();
                break;
            case BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE:
                Toast.makeText(getContext(), "Sensor not avail or busy", Toast.LENGTH_SHORT).show();
                break;
            case BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED:
                // Prompts the user to create credentials that your app accepts.
                final Intent enrollIntent = new Intent(Settings.ACTION_BIOMETRIC_ENROLL);
                enrollIntent.putExtra(Settings.EXTRA_BIOMETRIC_AUTHENTICATORS_ALLOWED,
                        BIOMETRIC_STRONG | DEVICE_CREDENTIAL);
                startActivityForResult(enrollIntent, 101);
                break;
        }

        executor = ContextCompat.getMainExecutor(getContext());
        biometricPrompt = new BiometricPrompt(getActivity(),
                executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode,
                                              @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                Toast.makeText(getContext(),
                        "Authentication error: " + errString, Toast.LENGTH_SHORT)
                        .show();
            }

            @Override
            public void onAuthenticationSucceeded(
                    @NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);

                if (email != null && password != null) {
                    PerformLogin(email, password);
                }
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                Toast.makeText(getContext(), "Authentication failed",
                        Toast.LENGTH_SHORT)
                        .show();
            }
        });

        promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Biometric login Trip Plan")
                .setSubtitle("Place  finger on sensor SignIn")
                .setNegativeButtonText("Use account password")
                .build();
        biometricPrompt.authenticate(promptInfo);


    }

    private void LoadSharePref() {
        SharedPreferences prefs = getActivity().getApplicationContext().getSharedPreferences("User", MODE_PRIVATE);

        String email = prefs.getString("email", "");
        String password = prefs.getString("password", "");
        if (!email.isEmpty() && !password.isEmpty()) {
            fingerPrint.setVisibility(View.VISIBLE);
        } else {
            fingerPrint.setVisibility(View.GONE);
        }
    }

    private void PerformLogin(String email, String password) {


        if (email.isEmpty() || !email.matches(Common.emailPattern)) {
            edEmail.setError("Enter Correct Email");
            edEmail.requestFocus();
        } else if (password.isEmpty()) {
            edPassword.setError("Enter Correct Password");
            edPassword.requestFocus();
        } else {
            AttemptLogin(email, password);
        }
    }

    private void AttemptLogin(String email, String password) {
        CustomDialog customDialog = new CustomDialog(getContext());
        customDialog.ShowDialog("Logging");

        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull @org.jetbrains.annotations.NotNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    SaveSharePref(email, password);
                    customDialog.DismissDialog();
                    SendUserToMainActivity();
                    Toast.makeText(getContext(), "Registration Completed!", Toast.LENGTH_SHORT).show();
                } else {
                    customDialog.DismissDialog();
                    Toast.makeText(getContext(), "" + task.getException(), Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void SendUserToMainActivity() {
        Intent intent = new Intent(getContext(), MainActivity.class);
        startActivity(intent);
        getActivity().finish();
    }

    private void InitVaribale(View view) {
        edEmail = view.findViewById(R.id.edEmail);
        edPassword = view.findViewById(R.id.edPassword);
        btnLogin = view.findViewById(R.id.btnLogin);
        createNewAccount = view.findViewById(R.id.createAccount);
        forgotPassword = view.findViewById(R.id.forgotPassword);
        fingerPrint = view.findViewById(R.id.fingerPrint);
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

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

    private void SaveSharePref(String email, String password) {
        SharedPreferences.Editor editor = getActivity().getApplicationContext().getSharedPreferences("User", MODE_PRIVATE).edit();
        editor.putString("email", email);
        editor.putString("password", password);
        editor.apply();
    }
}