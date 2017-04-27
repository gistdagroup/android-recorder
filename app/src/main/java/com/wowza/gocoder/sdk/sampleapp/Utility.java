package com.wowza.gocoder.sdk.sampleapp;

import android.content.Context;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Utility {

    public static String getUUID(Context ctx) {
        String androidId = Settings.System.getString(ctx.getContentResolver(), Settings.Secure.ANDROID_ID);
        if (androidId != null && !TextUtils.isEmpty(androidId)) {
            return SHA1Hash(androidId).substring(0, 14);
        }
        return "";
    }

    private static String SHA1Hash(String str) {
        MessageDigest digest = null;
        try {
            digest = MessageDigest.getInstance("SHA-1");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        byte[] result = null;

        try {
            result = digest.digest(str.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        StringBuilder sb = convertByteToHex(result);
        return sb.toString().toLowerCase();
    }

    @NonNull
    private static StringBuilder convertByteToHex(byte[] result) {
        StringBuilder sb = new StringBuilder();
        for (byte b : result) {
            sb.append(String.format("%02X", b));
        }
        return sb;
    }

}
