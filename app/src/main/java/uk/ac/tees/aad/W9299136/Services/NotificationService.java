package uk.ac.tees.aad.W9299136.Services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.RequiresApi;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.NotNull;

import java.util.HashMap;

import uk.ac.tees.aad.W9299136.R;

public class NotificationService extends Service {

    DatabaseReference notificationRef;
    FirebaseAuth mAuth;
    FirebaseUser mUser;

    public NotificationService() {
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void LauchNotification(DataSnapshot snapshot) {
        int notifyID = 1;
        String CHANNEL_ID = "my_channel_01";
        CharSequence name = "CHANNEL";
        int importance = NotificationManager.IMPORTANCE_HIGH;
        NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name, importance);

        Notification notification = new Notification.Builder(getBaseContext())
                .setContentTitle("Someone Sent Message!")
                .setContentText("Visit Discussion Page .")
                .setSmallIcon(R.drawable.ic_baseline_format_color_fill_24)
                .setChannelId(CHANNEL_ID)
                .build();


        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.createNotificationChannel(mChannel);
        mNotificationManager.notify(notifyID, notification);

        HashMap hashMap = new HashMap();
        hashMap.put("status", "seen");
        notificationRef.child(mUser.getUid()).child(snapshot.getRef().getKey().toString())
                .updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull @NotNull Task task) {
                if (!task.isSuccessful()) {
                    Toast.makeText(NotificationService.this, "" + task.getException(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();

        InitVaribale();
    }

    private void InitVaribale() {
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        notificationRef = FirebaseDatabase.getInstance().getReference().child("Notification");

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (mUser != null) {

            notificationRef.child(mUser.getUid()).addValueEventListener(new ValueEventListener() {
                @RequiresApi(api = Build.VERSION_CODES.O)
                @Override
                public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                            if (snapshot1.child("status").getValue().toString().equals("unseen")) {
                                LauchNotification(snapshot1);
                            }

                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull @NotNull DatabaseError error) {

                }
            });
        }
        return super.onStartCommand(intent, flags, startId);
    }
}