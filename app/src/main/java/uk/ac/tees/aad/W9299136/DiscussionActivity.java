package uk.ac.tees.aad.W9299136;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import org.jetbrains.annotations.NotNull;

import java.sql.Ref;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import uk.ac.tees.aad.W9299136.Utills.Common;
import uk.ac.tees.aad.W9299136.Utills.Message;
import uk.ac.tees.aad.W9299136.Utills.RecyclerViewDiscussionAdapter;
import uk.ac.tees.aad.W9299136.Utills.User;

public class DiscussionActivity extends AppCompatActivity {

    FirebaseAuth mAuth;
    FirebaseUser mUSer;
    RecyclerView recyclerView;
    EditText edSms;
    ImageView imageViewSend;
    DatabaseReference mRef;

    FirebaseUser mUser;
    DatabaseReference mUserRef, notification;
    CircleImageView profileImage;
    User user;
    List<Message> messageList;
    RecyclerViewDiscussionAdapter adapter;
    ImageView back;
    List<User> userList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discussion);

        InitVariable();

        imageViewSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        LoadMessages();
        LoadMyProfile();
        LoadAllUserForNotification();
    }


    private void InitVariable() {
        mAuth = FirebaseAuth.getInstance();
        mUSer = mAuth.getCurrentUser();
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        messageList = new ArrayList<>();
        edSms = findViewById(R.id.edSms);
        back = findViewById(R.id.imageView);
        imageViewSend = findViewById(R.id.ImageViewSend);
        mRef = FirebaseDatabase.getInstance().getReference().child("Discussion");
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        mUserRef = FirebaseDatabase.getInstance().getReference().child("Users");
        notification = FirebaseDatabase.getInstance().getReference().child("Notification");
        userList = new ArrayList<>();
    }

    private void LoadMyProfile() {
        mUserRef.child(mUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    user = snapshot.getValue(User.class);
                    Common.user = user;
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }


    private void LoadAllUserForNotification() {
        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                userList = new ArrayList<>();
                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                    User u = snapshot1.getValue(User.class);
                    userList.add(u);
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }

    private void LoadMessages() {

        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                messageList = new ArrayList<>();
                if (snapshot.exists()) {
                    for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                        Message message = snapshot1.getValue(Message.class);
                        messageList.add(message);
                    }
                    adapter = new RecyclerViewDiscussionAdapter(messageList, DiscussionActivity.this);
                    recyclerView.setAdapter(adapter);
                }


            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });

    }

    private void sendMessage() {
        String message = edSms.getText().toString();
        if (message.isEmpty()) {
            Toast.makeText(this, "Select Message", Toast.LENGTH_SHORT).show();
        } else {
            if (user != null) {
                Date date = Calendar.getInstance().getTime();
                DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
                String today = formatter.format(date);


                String key = mRef.push().getKey().toString();
                HashMap hashMap = new HashMap();
                hashMap.put("message", message);
                hashMap.put("key", key);
                hashMap.put("userID", mUSer.getUid());
                hashMap.put("date", today);
                hashMap.put("username", user.getUsername());

                mRef.child(key).updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task task) {
                        if (task.isSuccessful()) {
                            edSms.setText(null);
                            sendNotification(key, message);
                            Toast.makeText(DiscussionActivity.this, "Sent", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(DiscussionActivity.this, "" + task.getException(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });


            } else {
                Toast.makeText(this, "Update Profile", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void sendNotification(String key, String message) {

        for (int i = 0; i < userList.size(); i++) {
            if (!userList.get(i).getUserID().equals(mUSer.getUid())) {
                HashMap hashMap = new HashMap();
                hashMap.put("key", key);
                hashMap.put("message", message);
                hashMap.put("status", "unseen");
                hashMap.put("userID", userList.get(i).getUserID());

                notification.child(userList.get(i).getUserID()).child(key).updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task task) {
                    }
                });
            }
        }
    }
}