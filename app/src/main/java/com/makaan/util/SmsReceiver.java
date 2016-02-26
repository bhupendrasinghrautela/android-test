package com.makaan.util;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

import com.makaan.activity.pyr.PyrOtpVerification;

/**
 * Created by aishwarya on 24/12/15.
 */
public class SmsReceiver extends BroadcastReceiver {

    public static final String SMS_EXTRA_NAME ="pdus";
    private String message="";
    private String OTP ;
    private static final String OTP_ADDRESS = "MAKAAN";
    private OnVerificationListener listener;
    public void setOnVerificationListener(OnVerificationListener listener){
        this.listener = listener;
    }

    public interface OnVerificationListener{
        public void getOTP(String OTP);
    }
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e("Sms","Received");
        Bundle extras = intent.getExtras();
        if(extras !=null) {
            Object[] smsExtra = (Object[]) extras.get(SMS_EXTRA_NAME);

            // Get ContentResolver object for pushing encrypted SMS to the incoming folder
            ContentResolver contentResolver = context.getContentResolver();

            for (int i = 0; i < smsExtra.length; ++i) {
                SmsMessage sms = SmsMessage.createFromPdu((byte[]) smsExtra[i]);

                String body = sms.getMessageBody().toString();
                String address = sms.getOriginatingAddress();
                if(address.length()>=6 && address.substring(address.length()-6).equals(OTP_ADDRESS)){
                    OTP = body.substring(4,8);
                }
            }
            //If we have got the Otp well use it in the Listener
            if(OTP != null) {
                Toast.makeText(context, OTP, Toast.LENGTH_SHORT).show();
                listener.getOTP(OTP);
            }
        }
    }
}

