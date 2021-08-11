package uk.ac.tees.aad.W9299136;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import uk.ac.tees.aad.W9299136.Receiver.BroadcastReceiver;
import uk.ac.tees.aad.W9299136.Utills.NearByPlace;
import uk.ac.tees.aad.W9299136.Utills.RecyclerViewPlacesAdapter;

public class MainActivity extends AppCompatActivity {
    RecyclerView recyclerViewNearByPlaces;
    CardView connectWithPeople;
    CardView mapCard, compassCard;
    CircleImageView profileImage;
    public static TextView timer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        InitVaribale();
        InitRecyclerviewPlaces();
        ShowTime();
        connectWithPeople.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, DiscussionActivity.class));
            }
        });

        mapCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, GoogleMapActivity.class));
            }
        });

        compassCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, CompassActivity.class));
            }
        });

        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, ProfileActivity.class));
            }
        });

    }

    private void ShowTime() {

        Calendar now = Calendar.getInstance();
        if(now.get(Calendar.AM_PM) == Calendar.AM){
            timer.setText(now.get(Calendar.HOUR)+":"+now.get(Calendar.MINUTE)+"AM");
        }else{
            timer.setText(now.get(Calendar.HOUR)+":"+now.get(Calendar.MINUTE)+"PM");
        }

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_TIME_CHANGED);
        intentFilter.addAction(Intent.ACTION_TIME_TICK);
        intentFilter.addAction(Intent.ACTION_TIMEZONE_CHANGED);

        BroadcastReceiver myReciever = new BroadcastReceiver();
        registerReceiver(myReciever,intentFilter);
    }

    private void InitRecyclerviewPlaces() {
        recyclerViewNearByPlaces.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, true));
        List<NearByPlace> list = new ArrayList<>();
        list.add(new NearByPlace("Gas", R.drawable.ic_baseline_email_24));
        list.add(new NearByPlace("Bank", R.drawable.ic_baseline_email_24));
        list.add(new NearByPlace("Restaurant", R.drawable.compass_icon));
        list.add(new NearByPlace("ATM", R.drawable.ic_baseline_security_24));
        list.add(new NearByPlace("School", R.drawable.ic_baseline_dehaze_24));
        list.add(new NearByPlace("Gas", R.drawable.ic_baseline_fmd_good_24));
        RecyclerViewPlacesAdapter adapter = new RecyclerViewPlacesAdapter(list, MainActivity.this);
        recyclerViewNearByPlaces.setAdapter(adapter);
    }

    private void InitVaribale() {
        recyclerViewNearByPlaces = findViewById(R.id.recyclerView);
        connectWithPeople = findViewById(R.id.connectWithPeople);
        mapCard = findViewById(R.id.map);
        compassCard = findViewById(R.id.compass);
        profileImage = findViewById(R.id.profileImage);
        timer = findViewById(R.id.timer);
    }
}