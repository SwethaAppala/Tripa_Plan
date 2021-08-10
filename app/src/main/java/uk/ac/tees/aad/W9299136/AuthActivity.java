package uk.ac.tees.aad.W9299136;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;
import android.view.WindowManager;

import java.util.logging.Logger;

public class AuthActivity extends AppCompatActivity {

    public  static  FragmentManager fm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_auth);
        fm=getSupportFragmentManager();

        AddDefaultFragment(savedInstanceState);


    }

    private void AddDefaultFragment(Bundle savedInstanceState) {
        if (findViewById(R.id.container)!=null)
        {
            if (savedInstanceState!=null)
            {
                return;
            }
            fm.beginTransaction().add(R.id.container,new LoginFragment(),"login").commit();
        }
    }
}