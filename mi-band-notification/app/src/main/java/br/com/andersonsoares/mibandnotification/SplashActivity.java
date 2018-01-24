package br.com.andersonsoares.mibandnotification;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;



public class SplashActivity extends AppCompatActivity {

    private final static int REQUEST_PERMISSIONS_CODE = 128;
    private android.widget.ImageView splash;
    private android.widget.RelativeLayout activitysplash;
    private ImageView logo;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);


        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_NETWORK_STATE) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED ) {
            ActivityCompat.requestPermissions(this, new String[]{
                            Manifest.permission.ACCESS_NETWORK_STATE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.RECEIVE_SMS,
                            Manifest.permission.READ_PHONE_STATE},
                    REQUEST_PERMISSIONS_CODE);
        } else {
            open();
        }
    }



    public void open() {
        main();
       /* new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                call();
            }
        }, 1200);*/
    }

    public void call(){

       /* Usuario usuario = new LocalDbImplement<Usuario>(SplashActivity.this).getDefault(Usuario.class);
        if(usuario != null){
            RegistrationIntentService.start(this);
            startService(new Intent(this,DownloadService.class));
            File filedownload = usuario.getFileSplash(this);
            if (filedownload != null && filedownload.exists()) {
                Picasso.with(this)
                        .load(filedownload)
                        .fit().centerInside()
                        .noFade()
                        .into(splash, new Callback() {
                            @Override
                            public void onSuccess() {
                                logo.setVisibility(View.INVISIBLE);
                                splash.setVisibility(View.VISIBLE);
                                Log.d("evento", "onSuccess: ");
                                openMain();
                            }

                            @Override
                            public void onError() {
                                Log.d("evento", "onError: ");
                                main();
                            }
                        });
            }else{
                main();
            }
        }else{
            Intent intent = new Intent();
            intent.setClass(SplashActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        }*/
    }

    void openMain(){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                main();
            }
        }, 1000);
    }

    void main(){
        Intent intent = new Intent();
        intent.setClass(SplashActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PERMISSIONS_CODE:
                for (int i = 0; i < permissions.length; i++) {

                    if (permissions[i].equalsIgnoreCase(android.Manifest.permission.ACCESS_NETWORK_STATE)
                            && grantResults[i] == PackageManager.PERMISSION_DENIED) {
                        Toast.makeText(this, "Favor habilitar a permissão para usar o aplicativo!", Toast.LENGTH_LONG).show();
                        finishAffinity();
                        return;
                    } else if (permissions[i].equalsIgnoreCase(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            && grantResults[i] == PackageManager.PERMISSION_DENIED) {
                        Toast.makeText(this, "Favor habilitar a permissão para usar o aplicativo!", Toast.LENGTH_LONG).show();
                        finishAffinity();
                        return;
                    } else if (permissions[i].equalsIgnoreCase(android.Manifest.permission.READ_EXTERNAL_STORAGE)
                            && grantResults[i] == PackageManager.PERMISSION_DENIED) {
                        Toast.makeText(this, "Favor habilitar a permissão para usar o aplicativo!", Toast.LENGTH_LONG).show();
                        finishAffinity();
                        return;
                    }else if (permissions[i].equalsIgnoreCase(Manifest.permission.RECEIVE_SMS)
                            && grantResults[i] == PackageManager.PERMISSION_DENIED) {
                        Toast.makeText(this, "Favor habilitar a permissão para usar o aplicativo!", Toast.LENGTH_LONG).show();
                        finishAffinity();
                        return;
                    }else if (permissions[i].equalsIgnoreCase(Manifest.permission.READ_PHONE_STATE)
                            && grantResults[i] == PackageManager.PERMISSION_DENIED) {
                        Toast.makeText(this, "Favor habilitar a permissão para usar o aplicativo!", Toast.LENGTH_LONG).show();
                        finishAffinity();
                        return;
                    }
                }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        open();
    }
}
