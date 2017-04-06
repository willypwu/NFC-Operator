package com.willy.nfc.nfcoperator.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.nfc.FormatException;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.widget.Toast;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

public class Utilities {

    public static boolean startActivitySafely(Context context, Intent intent) {
        try {
            context.startActivity(intent);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void showToast(Activity activity, int strId) {
        Toast.makeText(activity, activity.getResources().getString(strId), Toast.LENGTH_SHORT).show();
    }

    public static void writeRecord(String serialNum, String checkNum, Tag tag) throws IOException, FormatException {
        NdefRecord[] records = { createRecord(serialNum), createRecord(checkNum) };
        NdefMessage message = new NdefMessage(records);
        Ndef ndef = Ndef.get(tag);
        ndef.connect();
        ndef.writeNdefMessage(message);
        ndef.close();
    }

    public static String parseRecord(byte[] msg) {
        if (msg == null || msg.length == 0) {
            return null;
        }

        String text = null;
        String textEncoding = ((msg[0] & 128) == 0) ? "UTF-8" : "UTF-16"; // Get the Text Encoding
        int languageCodeLength = msg[0] & 0063; // Get the Language Code, e.g. "en"

        try {
            // Get the Text
            text = new String(msg, languageCodeLength + 1, msg.length - languageCodeLength - 1, textEncoding);
        } catch (UnsupportedEncodingException e) {
            Logger.e("UnsupportedEncoding", e.toString());
        }
        return text;
    }

    public static NdefRecord createRecord(String text) throws UnsupportedEncodingException {
        String lang       = "en";
        byte[] textBytes  = text.getBytes();
        byte[] langBytes  = lang.getBytes("US-ASCII");
        int    langLength = langBytes.length;
        int    textLength = textBytes.length;
        byte[] payload    = new byte[1 + langLength + textLength];

        // set status byte (see NDEF spec for actual bits)
        payload[0] = (byte) langLength;

        // copy langbytes and textbytes into payload
        System.arraycopy(langBytes, 0, payload, 1, langLength);
        System.arraycopy(textBytes, 0, payload, 1 + langLength, textLength);

        NdefRecord recordNFC = new NdefRecord(NdefRecord.TNF_WELL_KNOWN,  NdefRecord.RTD_TEXT,  new byte[0], payload);
        return recordNFC;
    }
}

