package uk.ac.tees.aad.W9299136;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.NotNull;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import uk.ac.tees.aad.W9299136.Receiver.BroadcastReceiver;
import uk.ac.tees.aad.W9299136.Utills.Common;
import uk.ac.tees.aad.W9299136.Utills.NearByPlace;
import uk.ac.tees.aad.W9299136.Utills.RecyclerViewPlacesAdapter;
import uk.ac.tees.aad.W9299136.Utills.User;

public class MainActivity extends AppCompatActivity {
    RecyclerView recyclerViewNearByPlaces;
    CardView connectWithPeople;
    CardView mapCard, compassCard,Direction;
    CircleImageView profileImage;
    public static TextView timer;
    FirebaseAuth mAuth;
    FirebaseUser mUser;
    DatabaseReference mUserRef;
    User user;
    TextView Username;




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

        Direction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this, GoogleMapActivity.class);
                intent.putExtra("key","mainActivity");
                startActivity(intent);
            }
        });
        LoadMyProfile();

    }
    private void LoadMyProfile() {
       if (Common.user==null)
       {
           mUserRef.child(mUser.getUid()).addValueEventListener(new ValueEventListener() {
               @Override
               public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                   if (snapshot.exists()) {
                       user = snapshot.getValue(User.class);
                       Common.user = user;
                       Username.setText(user.getUsername());
                       Picasso.get().load(user.getImage()).placeholder(R.drawable.user).into(profileImage);
                   }
               }

               @Override
               public void onCancelled(@NonNull @NotNull DatabaseError error) {

               }
           });
       }else
       {
           Username.setText(Common.user.getUsername());
           Picasso.get().load(Common.user.getImage()).placeholder(R.drawable.user).into(profileImage);

       }
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
        Direction = findViewById(R.id.Directions);
        timer = findViewById(R.id.timer);
        Username = findViewById(R.id.username);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        mUserRef = FirebaseDatabase.getInstance().getReference().child("Users");

    }
}