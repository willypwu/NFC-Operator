package com.willy.nfc.nfcoperator;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.FormatException;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.nfc.tech.NfcF;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.willy.nfc.nfcoperator.util.Logger;
import com.willy.nfc.nfcoperator.util.Utilities;

import java.io.IOException;

public class NFCWriterActivity extends BaseActivity {
    private static final String LOG_TAG = "NFCWriterActivity";

    public static Intent getIntent(Context context) {
        Intent intent = new Intent(context, NFCWriterActivity.class);
        return intent;
    }

    private PendingIntent mNfcPendingIntent;
    private IntentFilter[] mWriteTagFilters;
    private String[][] mTechListsArray;

    private EditText mTestEditCheckNum;
    private EditText mTestEditSerialNum;

    private TextView mCheckNum;
    private TextView mSerialNum;
    private Button mWriteConfirmButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nfc_writer_activity);

        setTitle(getResources().getString(R.string.nfc_writer));
        enableBackItem();

        setupViews();
        initNFCSettings();

        Logger.d(LOG_TAG, "onNewIntent // " + getIntent());
        handleIntent(getIntent());
    }

    private void setupViews() {
        mWriteConfirmButton = (Button)findViewById(R.id.write_button);
        mWriteConfirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mDetectedTag == null) {
                    Logger.d(LOG_TAG, "try to write, but cannot detect NFC");
                    Utilities.showToast(NFCWriterActivity.this, R.string.nfc_write_but_cannot_detect);
                    return;
                }
                try {
                    Utilities.writeRecord(mTestEditSerialNum.getText().toString(), mTestEditCheckNum.getText().toString(), mDetectedTag);
                    Utilities.showToast(NFCWriterActivity.this, R.string.nfc_write_successful);
                    mSerialNum.setText(mTestEditSerialNum.getText().toString());
                    mCheckNum.setText(mTestEditCheckNum.getText().toString());
                    Logger.d(LOG_TAG, "write successful // serialNum : " +
                            mTestEditSerialNum.getText().toString() + ", check sum : " +
                            mTestEditCheckNum.getText().toString());
                } catch (IOException e) {
                    Logger.d(LOG_TAG, "try to write, but IOException");
                    Utilities.showToast(NFCWriterActivity.this, R.string.nfc_write_try_again);
                    e.printStackTrace();
                } catch (FormatException e) {
                    Logger.d(LOG_TAG, "try to write, but FormatException");
                    Utilities.showToast(NFCWriterActivity.this, R.string.nfc_write_try_again);
                    e.printStackTrace();
                } finally {
                    mDetectedTag = null;
                }
            }
        });

        mTestEditSerialNum = (EditText) findViewById(R.id.test_edit_serial_num);
        mTestEditCheckNum = (EditText) findViewById(R.id.test_edit_check_num);

        mSerialNum = (TextView) findViewById(R.id.serial_num);
        mCheckNum = (TextView) findViewById(R.id.check_num);
    }

    private void initNFCSettings() {
        mNfcPendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        IntentFilter tagDiscovery = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
        IntentFilter techDetected = new IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED);
        IntentFilter ndefDetected = new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED);
        try {
            ndefDetected.addDataType("*/*");
        } catch (IntentFilter.MalformedMimeTypeException e) {}

        mWriteTagFilters = new IntentFilter[] {ndefDetected, techDetected ,tagDiscovery};

        mTechListsArray = new String[][] { new String[] { NfcF.class.getName() } };
    }

    @Override
    protected void onResume() {
        super.onResume();
        mNfcAdapter.enableForegroundDispatch(NFCWriterActivity.this, mNfcPendingIntent, mWriteTagFilters, mTechListsArray);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mNfcAdapter.disableForegroundDispatch(NFCWriterActivity.this);
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
            Utilities.showToast(NFCWriterActivity.this, R.string.nfc_detect_tag);

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
