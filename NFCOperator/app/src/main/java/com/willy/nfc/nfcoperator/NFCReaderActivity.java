package com.willy.nfc.nfcoperator;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.widget.TextView;
import android.widget.Toast;

import com.willy.nfc.nfcoperator.util.Logger;
import com.willy.nfc.nfcoperator.util.Utilities;

public class NFCReaderActivity extends BaseActivity {
    private static final String LOG_TAG = "NFCReaderActivity";

    public static Intent getIntent(Context context) {
        Intent intent = new Intent(context, NFCReaderActivity.class);
        return intent;
    }

    private TextView mCheckNum;
    private TextView mSerialNum;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.nfc_reader_activity);
        setTitle(getResources().getString(R.string.nfc_reader));
        enableBackItem();

        setupViews();
    }

    private void setupViews() {
        mSerialNum = (TextView) findViewById(R.id.serial_num);
        mCheckNum = (TextView) findViewById(R.id.check_num);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mNfcAdapter.enableForegroundDispatch(NFCReaderActivity.this,
                PendingIntent.getActivity(this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0)
                , null, null);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mNfcAdapter.disableForegroundDispatch(NFCReaderActivity.this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        Logger.d(LOG_TAG, "onNewIntent // " + intent);
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        if (intent == null) {
            return;
        }

        Logger.d(LOG_TAG, "handleIntent : " + intent.getAction());

        if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction())
                || NfcAdapter.ACTION_TECH_DISCOVERED.equals(intent.getAction())
                || NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction())) {
            mDetectedTag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            Logger.d(LOG_TAG, "handleIntent mDetectedTag // " + mDetectedTag);
            Toast.makeText(NFCReaderActivity.this, "Detect NFC", Toast.LENGTH_SHORT).show();

            try {
                Parcelable[] rawMsgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
                NdefMessage[] msgs = new NdefMessage[rawMsgs.length];
                for (int i = 0; i < rawMsgs.length; i++) {
                    msgs[i] = (NdefMessage) rawMsgs[i];
                }
                String serialNum = Utilities.parseRecord(msgs[0].getRecords()[0].getPayload());
                mSerialNum.setText(serialNum);
                Logger.d(LOG_TAG, "handleIntent parse record // serialNum : " + serialNum);

                String checkNum = Utilities.parseRecord(msgs[0].getRecords()[1].getPayload());
                mCheckNum.setText(checkNum);

                Logger.d(LOG_TAG, "handleIntent parse record // checkNum : " + checkNum);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, 0);
    }
}
