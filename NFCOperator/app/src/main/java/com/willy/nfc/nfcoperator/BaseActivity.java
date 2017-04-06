package com.willy.nfc.nfcoperator;

import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.willy.nfc.nfcoperator.util.Logger;
import com.willy.nfc.nfcoperator.widget.TouchEffectTextView;

public class BaseActivity extends AppCompatActivity {
    private static final String LOG_TAG = "BaseActivity";

    protected NfcAdapter mNfcAdapter;
    protected Tag mDetectedTag = null;

    private TextView mTitle;
    private TouchEffectTextView mBack;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);

        setupActionBar();
    }

    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setDisplayShowTitleEnabled(false);
        LayoutInflater mInflater = LayoutInflater.from(this);

        View mCustomView = mInflater.inflate(R.layout.specific_custom_actionbar, null);
        actionBar.setCustomView(mCustomView);
        actionBar.setDisplayShowCustomEnabled(true);

        mTitle = (TextView) findViewById(R.id.title);

        mBack = (TouchEffectTextView) findViewById(R.id.left_control);

        actionBar.setElevation(0);
    }

    public void setTitle(String title) {
        mTitle.setText(title);
    }

    public void enableBackItem() {
        mBack.setText(getResources().getString(R.string.string_back));
        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public boolean isNFCAvaliable() {
        Logger.d(LOG_TAG, "mNfcAdapter : " + mNfcAdapter);
        if (mNfcAdapter == null) {
            Logger.d(LOG_TAG, "not support NFC");
            return false;
        } else if (!mNfcAdapter.isEnabled()) {
            Logger.d(LOG_TAG, "NFC is disabled");
            return false;
        } else {
            Logger.d(LOG_TAG, "NFC is ok");
            return true;
        }
    }
}
