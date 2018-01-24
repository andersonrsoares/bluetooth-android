package br.com.andersonsoares.mibandnotification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;

import org.greenrobot.eventbus.EventBus;

public class PhoneCallReceiver extends BroadcastReceiver {
    public PhoneCallReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        EventBus.getDefault().post(new NotificationEvent());
        Bundle extras = intent.getExtras();
        if (extras != null) {
            String state = extras.getString(TelephonyManager.EXTRA_STATE);
            Log.w("MY_DEBUG_TAG", state);
            if (state.equals(TelephonyManager.EXTRA_STATE_RINGING)) {
                String phoneNumber = extras
                        .getString(TelephonyManager.EXTRA_INCOMING_NUMBER);
                Log.w("MY_DEBUG_TAG", phoneNumber);
            }
        }
    }
}
