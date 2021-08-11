package uk.ac.tees.aad.W9299136.Receiver;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.Calendar;

import uk.ac.tees.aad.W9299136.MainActivity;

public class BroadcastReceiver extends android.content.BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        Calendar now = Calendar.getInstance();
        if(now.get(Calendar.AM_PM) == Calendar.AM){
            MainActivity.timer.setText(now.get(Calendar.HOUR)+":"+now.get(Calendar.MINUTE)+"AM");
        }else{
            MainActivity.timer.setText(now.get(Calendar.HOUR)+":"+now.get(Calendar.MINUTE)+"PM");
        }

    }
}
