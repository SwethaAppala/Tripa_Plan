package uk.ac.tees.aad.W9299136;

import android.app.AlertDialog;
import android.content.DialogInterface;
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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.Executor;

import uk.ac.tees.aad.W9299136.SqlitePersistence.Sqlite;
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
    Sqlite sqlite;

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
                    SaveHistory(email);
                    Toast.makeText(getContext(), "Login Completed!", Toast.LENGTH_SHORT).show();
                } else {
                    customDialog.DismissDialog();
                    Toast.makeText(getContext(), "" + task.getException(), Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void SaveHistory(String email) {
        Date date = Calendar.getInstance().getTime();
        DateFormat formatter = new SimpleDateFormat("dd-M-yyyy hh:mm:ss");
        String today = formatter.format(date);
        sqlite.insertData(today, email);
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
        sqlite = new Sqlite(getContext());

        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SowDialog();
            }
        });

        createNewAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AuthActivity.fm.beginTransaction().replace(R.id.container, new RegisterFragment(), "register").commit();
            }
        });


    }

    private void SowDialog() {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());
        alertDialog.setMessage("Enter Email Address");

        final EditText input = new EditText(getContext());
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);
        alertDialog.setView(input);
        alertDialog.setIcon(R.drawable.ic_baseline_add_24);

        alertDialog.setPositiveButton("Reset",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String email = input.getText().toString();
                        if (email.matches(Common.emailPattern)) {
                           ResetPassword(email);
                        } else {
                            Toast.makeText(getContext(), "Please Enter Email", Toast.LENGTH_SHORT).show();
                        }

                    }
                });

        alertDialog.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        alertDialog.show();
    }

    private void ResetPassword(String email) {

        CustomDialog customDialog=new CustomDialog(getContext());
        customDialog.ShowDialog("Wait");

        mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            customDialog.DismissDialog();
                            Toast.makeText(getContext(), "Please Check Your email", Toast.LENGTH_SHORT).show();

                        }else
                        {
                            Toast.makeText(getContext(), ""+task.getException(), Toast.LENGTH_SHORT).show();
                        }
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