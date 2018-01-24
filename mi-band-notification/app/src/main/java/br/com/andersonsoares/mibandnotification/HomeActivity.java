package br.com.andersonsoares.mibandnotification;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.zhaoxiaodan.miband.MiBand;
import com.zhaoxiaodan.miband.model.VibrationMode;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class HomeActivity extends AppCompatActivity {

    //MiBand miband;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        EventBus.getDefault().register(this);
        MainActivity.miband.startVibration(VibrationMode.VIBRATION_WITH_LED);
    }


    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(NotificationEvent event)
    {
        /* Do something */
        MainActivity.miband.startVibration(VibrationMode.VIBRATION_WITH_LED);
    };
}
