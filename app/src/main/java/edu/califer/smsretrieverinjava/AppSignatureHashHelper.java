package edu.califer.smsretrieverinjava;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.util.Base64;
import android.util.Log;

import androidx.annotation.NonNull;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;

public class AppSignatureHashHelper extends ContextWrapper {

    public AppSignatureHashHelper( Context context) {
        super(context);
    }

    private final String TAG = AppSignatureHashHelper.class.getSimpleName();

    private static final String HASH_TYPE = "SHA-256";
    private static final int NUM_HASHED_BYTES = 9;
    private static final int NUM_BASE64_CHAR = 11;

    ArrayList<String> get() {
        ArrayList<String> appSignaturesHashs = new ArrayList<String>();
        try {
            // Get all package details
            String packageName = getPackageName();
            PackageManager packageManager = getPackageManager();
            Signature[] signatures = packageManager.getPackageInfo(
                    packageName,
                    PackageManager.GET_SIGNING_CERTIFICATES
            ).signingInfo.getApkContentsSigners();
            for (Signature signature : signatures) {
                String hash = hash(packageName, signature.toCharsString());
                appSignaturesHashs.add(String.format("%s", hash));
            }
        } catch (Exception e) {
            Log.e(TAG, "Package not found", e);
        }
        return appSignaturesHashs;
    }

    @TargetApi(19)
    @NonNull
    private String hash(String packageName, String signature) {
        String appInfo = packageName + " " + signature;
        try {

            MessageDigest messageDigest = MessageDigest.getInstance(HASH_TYPE);
            messageDigest.update(appInfo.getBytes(StandardCharsets.UTF_8));

            byte[] hashSignature = messageDigest.digest();

            // truncated into NUM_HASHED_BYTES
            hashSignature = Arrays.copyOfRange(hashSignature, 0, NUM_HASHED_BYTES);
            // encode into Base64
            String base64Hash = Base64.encodeToString(hashSignature, Base64.NO_PADDING | Base64.NO_WRAP);
            base64Hash = base64Hash.substring(0, NUM_BASE64_CHAR);
            return base64Hash;
        } catch (NoSuchAlgorithmException exception) {
            Log.e(TAG, "No Such Algorithm Exception", exception);
        }
        return null;
    }

}
