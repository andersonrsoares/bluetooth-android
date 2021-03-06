package br.com.andersonsoares.mibandnotification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;

import org.greenrobot.eventbus.EventBus;

public class SmsReceiver extends BroadcastReceiver {
    public SmsReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        EventBus.getDefault().post(new NotificationEvent());
        //---get the SMS message passed in---
        Bundle bundle = intent.getExtras();
        SmsMessage[] msgs = null;
        String messageReceived = "";
        if (bundle != null) {
            //---retrieve the SMS message received---
            Object[] pdus = (Object[]) bundle.get("pdus");
            msgs = new SmsMessage[pdus.length];
            for (int i = 0; i < msgs.length; i++)

            {
                msgs[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                messageReceived += msgs[i].getMessageBody().toString();
                messageReceived += "\n";
            }

            //---display the new SMS message---
            //Toast.makeText(context, messageReceived, Toast.LENGTH_SHORT).show();

            // Get the Sender Phone Number

            String senderPhoneNumber=msgs[0].getOriginatingAddress ();

        }


    }
}
