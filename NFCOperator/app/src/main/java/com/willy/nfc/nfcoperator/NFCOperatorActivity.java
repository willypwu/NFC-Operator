package com.willy.nfc.nfcoperator;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.balysv.materialripple.MaterialRippleLayout;
import com.willy.nfc.nfcoperator.util.Utilities;

import java.util.ArrayList;

public class NFCOperatorActivity extends BaseActivity {
    private static final String LOG_TAG = "NFCOperatorActivity";

    private static final int SELECT_ITEM_NFC_READER = 0;
    private static final int SELECT_ITEM_NFC_WRITER = 1;

    private ListView mListView;
    private SelectAdapter mSelectAdapter;

    private LayoutInflater mInflater;

    private ArrayList<String> mData = new ArrayList<>();

    private boolean mNFCAvaliable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nfc_operator_activity);

        mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        setTitle(getResources().getString(R.string.action_bar_title_nfc_operator));

        mNFCAvaliable = isNFCAvaliable();

        setupListData();
        setupViews();
    }

    private void setupListData() {
        mData.add(getResources().getString(R.string.nfc_reader));
        mData.add(getResources().getString(R.string.nfc_writer));
    }

    private void setupViews() {
        if (!mNFCAvaliable) {
            TextView listViewTitle = (TextView) findViewById(R.id.list_title);
            listViewTitle.setVisibility(View.VISIBLE);
            listViewTitle.setText(getResources().getString(R.string.nfc_not_avaliable));
        }

        setupListView();
    }

    private void setupListView() {
        mSelectAdapter = new SelectAdapter();
        mListView = (ListView) findViewById(R.id.list_view);
        mListView.setAdapter(mSelectAdapter);
        mListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == SELECT_ITEM_NFC_READER) {
                    Intent intent = NFCReaderActivity.getIntent(NFCOperatorActivity.this);
                    Utilities.startActivitySafely(NFCOperatorActivity.this, intent);
                } else if (position == SELECT_ITEM_NFC_WRITER) {
                    Intent intent = NFCWriterActivity.getIntent(NFCOperatorActivity.this);
                    Utilities.startActivitySafely(NFCOperatorActivity.this, intent);
                }
            }
        });
    }

    private class SelectAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mData.size();
        }

        @Override
        public String getItem(int position) {
            return mData.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            convertView = mInflater.inflate(R.layout.specific_list_item, parent, false);
            String content = getItem(position);
            TextView title = (TextView) convertView.findViewById(R.id.content);
            title.setText(content);

            if (!mNFCAvaliable) {
                convertView.setBackgroundDrawable(getResources().getDrawable(R.drawable.common_corner_bg_dark));

                MaterialRippleLayout ripple = (MaterialRippleLayout) convertView.findViewById(R.id.ripple_effect);
                ripple.setEnabled(false);
                ripple.setClickable(false);
            }

            return convertView;

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mNfcAdapter.enableForegroundDispatch(NFCOperatorActivity.this,
                PendingIntent.getActivity(this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0)
                , null, null);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mNfcAdapter.disableForegroundDispatch(NFCOperatorActivity.this);
    }
}
