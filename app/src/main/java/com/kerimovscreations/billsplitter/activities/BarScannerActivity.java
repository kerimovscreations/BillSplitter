package com.kerimovscreations.billsplitter.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.zxing.Result;
import com.kerimovscreations.billsplitter.R;
import com.kerimovscreations.billsplitter.utils.BaseActivity;

import butterknife.BindView;
import butterknife.OnClick;
import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class BarScannerActivity extends BaseActivity implements ZXingScannerView.ResultHandler {

    public static final String BAR_CODE = "BAR_CODE";

    @BindView(R.id.scanner_view)
    ZXingScannerView mScannerView;
    @BindView(R.id.action_btn)
    View mActionBtn;
    @BindView(R.id.result_layout)
    View mResultLayout;
    @BindView(R.id.result)
    TextView mResult;

    String mResultCode;
    boolean mScannerResumed = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.onCreateSetContentView(R.layout.activity_bar_scanner);
    }

    @Override
    public void initVars() {
        mActionBtn.setVisibility(View.GONE);
        mScannerView.setResultHandler(this);
        updateResultViewVisibility();
    }

    @Override
    public void onResume() {
        super.onResume();
        mScannerView.startCamera();
    }

    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();
    }

    @Override
    public void handleResult(Result rawResult) {
        // Do something with the result here
        Log.v("BAR_SCAN", rawResult.getText()); // Prints scan results
        Log.v("BAR_SCAN", rawResult.getBarcodeFormat().toString()); // Prints the scan format (qrcode, pdf417 etc.)

        mResultCode = rawResult.getText();

        mActionBtn.setVisibility(View.VISIBLE);

        mScannerResumed = true;

        updateResultViewVisibility();
    }

    /**
     * Click handlers
     */

    @OnClick(R.id.back_ic)
    void onBack() {
        finish();
    }

    @OnClick(R.id.action_btn)
    void onAction() {
        Intent intent = new Intent();
        intent.putExtra(BAR_CODE, mResultCode);
        setResult(RESULT_OK, intent);
        finish();
    }

    @OnClick(R.id.retry_btn)
    void onRetry() {
        mScannerResumed = false;
        mScannerView.resumeCameraPreview(this);
        updateResultViewVisibility();
    }

    /**
     * UI
     */

    void updateResultViewVisibility() {
        if (mScannerResumed) {
            mResultLayout.setVisibility(View.VISIBLE);
            mResult.setText(mResultCode);
        } else {
            mResultLayout.setVisibility(View.GONE);
        }
    }
}