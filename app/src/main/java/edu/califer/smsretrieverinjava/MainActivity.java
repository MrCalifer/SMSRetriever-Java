package edu.califer.smsretrieverinjava;

import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.auth.api.phone.SmsRetriever;
import com.google.android.gms.auth.api.phone.SmsRetrieverClient;

public class MainActivity extends AppCompatActivity {

    private IntentFilter intentFilter;
    private SMSReceiver smsReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d("Hash" , new AppSignatureHashHelper(this).get().toString());


        initSmsListener();
        initBroadcast();
    }

    private void initBroadcast() {
        intentFilter = new IntentFilter(SmsRetriever.SMS_RETRIEVED_ACTION);
        smsReceiver = new SMSReceiver();
        smsReceiver.setOtpReceiverListener(new SMSReceiver.OTPReceiverListener() {
            @Override
            public void onOTPReceived(String OTP) {
                Toast.makeText(MainActivity.this, OTP, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(String message) {

            }
        });
    }

    private void initSmsListener() {
        SmsRetrieverClient client = SmsRetriever.getClient(this);
        client.startSmsRetriever();
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(smsReceiver, intentFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(smsReceiver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        smsReceiver = null;
    }
}