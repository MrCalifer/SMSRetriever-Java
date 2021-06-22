package edu.califer.smsretrieverinjava;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import com.google.android.gms.auth.api.phone.SmsRetriever;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.Status;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SMSReceiver extends BroadcastReceiver {


    private OTPReceiverListener otpReceiverListener;

    public void setOtpReceiverListener(OTPReceiverListener otpReceiverListener) {
        this.otpReceiverListener = otpReceiverListener;
    }


    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent != null){
            if (intent.getAction().equals(SmsRetriever.SMS_RETRIEVED_ACTION)){
                Bundle extras = intent.getExtras();
                Status status = (Status) extras.get(SmsRetriever.EXTRA_STATUS);

                switch(status.getStatusCode()) {
                    case CommonStatusCodes.SUCCESS:
                        // Get SMS message contents
                        String message = (String) extras.get(SmsRetriever.EXTRA_SMS_MESSAGE);
                        // Extract one-time code from the message and complete verification
                        //Pattern.compile("[0-9]+") check a pattern with only digit
                        Pattern pattern = Pattern.compile("\\d+");
                        Matcher matcher =pattern.matcher(message);
                        if (matcher.find()){
                            String OTP =matcher.group();
                            if (otpReceiverListener != null){
                                otpReceiverListener.onOTPReceived(OTP);
                            }
                        }

                        break;
                    case CommonStatusCodes.TIMEOUT:
                        if (otpReceiverListener != null){
                            otpReceiverListener.onFailure(" TIMEOUT! Unable to retrieve the OTP.");
                        }
                        break;
                }
            }
        }

    }




    interface OTPReceiverListener {
        void onOTPReceived(String OTP);
        void onFailure(String message);
    }
}

