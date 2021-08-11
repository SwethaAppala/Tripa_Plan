package uk.ac.tees.aad.W9299136;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

import uk.ac.tees.aad.W9299136.Utills.NearByPlace;
import uk.ac.tees.aad.W9299136.Utills.RecyclerViewPlacesAdapter;

public class MainActivity extends AppCompatActivity {

    RecyclerView recyclerViewNearByPlaces;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        InitVaribale();
        InitRecyclerviewPlaces();

    }

    private void InitRecyclerviewPlaces() {
        recyclerViewNearByPlaces.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, true));
        List<NearByPlace>list=new ArrayList<>();
        list.add(new NearByPlace("Gas",R.drawable.ic_baseline_email_24));
        list.add(new NearByPlace("Bank",R.drawable.ic_baseline_email_24));
        list.add(new NearByPlace("Restaurant",R.drawable.compass_icon));
        list.add(new NearByPlace("ATM",R.drawable.ic_baseline_security_24));
        list.add(new NearByPlace("School",R.drawable.ic_baseline_dehaze_24));
        list.add(new NearByPlace("Gas",R.drawable.ic_baseline_fmd_good_24));
        RecyclerViewPlacesAdapter adapter=new RecyclerViewPlacesAdapter(list,MainActivity.this);
        recyclerViewNearByPlaces.setAdapter(adapter);
    }

    private void InitVaribale() {
        recyclerViewNearByPlaces = findViewById(R.id.recyclerView);
    }
}