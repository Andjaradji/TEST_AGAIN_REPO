package com.vexanium.vexgift.module.wallet.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.socks.library.KLog;
import com.vexanium.vexgift.R;
import com.vexanium.vexgift.annotation.ActivityFragmentInject;
import com.vexanium.vexgift.app.StaticGroup;
import com.vexanium.vexgift.base.BaseActivity;
import com.vexanium.vexgift.bean.model.User;
import com.vexanium.vexgift.bean.model.Wallet;
import com.vexanium.vexgift.bean.response.HttpResponse;
import com.vexanium.vexgift.bean.response.WalletResponse;
import com.vexanium.vexgift.bean.response.WithdrawResponse;
import com.vexanium.vexgift.database.TableContentDaoUtil;
import com.vexanium.vexgift.module.wallet.presenter.IWalletPresenter;
import com.vexanium.vexgift.module.wallet.presenter.IWalletPresenterImpl;
import com.vexanium.vexgift.module.wallet.view.IWalletView;
import com.vexanium.vexgift.util.ViewUtil;
import com.vexanium.vexgift.widget.CustomSeekBar;
import com.vexanium.vexgift.widget.dialog.DialogAction;
import com.vexanium.vexgift.widget.dialog.DialogOptionType;
import com.vexanium.vexgift.widget.dialog.VexDialog;

import java.io.Serializable;

@ActivityFragmentInject(contentViewId = R.layout.activity_wallet_withdraw, toolbarTitle = R.string.wallet_withdraw)
public class WalletWithdrawActivity extends BaseActivity<IWalletPresenter> implements IWalletView {

    public static int step = 0;
    User user;
    CustomSeekBar customSeekBar;
    WalletResponse walletResponse;
    EditText etAmount;
    float personalBalance;
    float withdrawAmount;
    boolean isEditFromSeekbar = false;
    boolean isEditFromButton = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initView() {
        mPresenter = new IWalletPresenterImpl(this);
        user = User.getCurrentUser(this);

//        customSeekBar = findViewById(R.id.cs)
        etAmount = findViewById(R.id.et_withdraw_amount);
        customSeekBar = findViewById(R.id.cs_amount);

        ViewUtil.setOnClickListener(this, this,
                R.id.btn_25, R.id.btn_50, R.id.btn_75, R.id.btn_scan, R.id.btn_withdraw, R.id.ib_history);

        walletResponse = TableContentDaoUtil.getInstance().getWallet();
        if (walletResponse != null && walletResponse.getWallet() != null) {
            Wallet wallet = walletResponse.getWallet();
            personalBalance = wallet.getPersonalWalletBalance();//13631.31946f;
            ViewUtil.setText(this, R.id.tv_personal_balance, personalBalance + "");
            customSeekBar.setMax(100);
            customSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    float amount = ((float) progress / 100) * personalBalance;
                    KLog.v("HMtes " + (step++) + " seekbar progress=[" + progress + "] amount=[" + amount + "] fromUser=[" + fromUser + "]  fromButton=[" + isEditFromButton + "]");
                    if (fromUser || isEditFromButton) {
                        etAmount.setText(amount + "");
                        isEditFromSeekbar = true;
                        isEditFromButton = false;
                    }
                    setEnableWithdrawButton(amount > 0);
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });
        }


        etAmount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                KLog.v("HMtes " + (step++) + " ET changed " + charSequence.toString());
                if (personalBalance > 0 && !isEditFromSeekbar) {
                    try {
                        withdrawAmount = Float.valueOf(charSequence.toString());
                    } catch (Exception e) {
                        withdrawAmount = 0;
                    }

                    int percentage = (int) ((withdrawAmount / personalBalance) * 100);
                    KLog.v("HMtes " + (step++) + " ET changed " + charSequence.toString() + "  percentage=[" + percentage + "] withdrawAmount=[" + withdrawAmount + "] edit");

                    customSeekBar.setProgress(percentage);

                    setEnableWithdrawButton(withdrawAmount > 0);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                isEditFromSeekbar = false;
            }
        });

        setEnableWithdrawButton(withdrawAmount > 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                toast("Cancelled");
            } else {
                ((EditText) findViewById(R.id.et_address)).setText(result.getContents());
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.ib_history:
                Intent intent = new Intent(this, WalletWithdrawHistoryActivity.class);
                startActivity(intent);
                break;
            case R.id.btn_25:
                updateSeekbar(25);
                break;
            case R.id.btn_50:
                updateSeekbar(50);
                break;
            case R.id.btn_75:
                updateSeekbar(75);
                break;
            case R.id.btn_scan:
                IntentIntegrator integrator = new IntentIntegrator(this);
                integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);
                integrator.setPrompt("Scan a Vex Address");
                integrator.setCameraId(0);  // Use a specific camera of the device
                integrator.setBeepEnabled(true);
                integrator.setBarcodeImageEnabled(true);
                integrator.setOrientationLocked(false);
                integrator.initiateScan();
                break;
            case R.id.btn_withdraw:
                if (withdrawAmount > 0)
                    doWithdraw();
                else {

                }
                break;

        }
    }

    @Override
    public void handleResult(Serializable data, HttpResponse errorResponse) {
        if (data != null) {
            if (data instanceof WithdrawResponse) {
                WithdrawResponse withdrawResponse = (WithdrawResponse) data;
                new VexDialog.Builder(this)
                        .optionType(DialogOptionType.YES_NO)
                        .title(getString(R.string.wallet_withdraw_input_success_title))
                        .content(getString(R.string.wallet_referral_uncounted_empty_text))
                        .positiveText(getString(R.string.wallet_withdraw_input_success_button))
                        .onPositive(new VexDialog.MaterialDialogButtonCallback() {
                            @Override
                            public void onClick(@NonNull VexDialog dialog, @NonNull DialogAction which) {
                                Intent intent = new Intent(WalletWithdrawActivity.this, WalletWithdrawHistoryActivity.class);
                                startActivity(intent);
                            }
                        })
                        .negativeText(getString(R.string.cancel))
                        .autoDismiss(true)
                        .show();

            }
        } else if (errorResponse != null) {
            StaticGroup.showCommonErrorDialog(this, errorResponse);
        }
    }

    private void setEnableWithdrawButton(boolean isEnable) {
        findViewById(R.id.btn_withdraw).setAlpha(isEnable ? 1.0f : 0.5f);
    }

    private void doWithdraw() {
        String destinationAddress = ((TextView) findViewById(R.id.et_address)).getText().toString();
        boolean isValid = ViewUtil.validateEmpty(this, getString(R.string.validate_empty_field), R.id.et_address);
        if (withdrawAmount <= 0) {
            ((EditText) findViewById(R.id.et_withdraw_amount)).setError(getString(R.string.wallet_withdraw_amount_error));
            isValid = false;
        }
        if (walletResponse != null && walletResponse.getWallet() != null && user != null && isValid) {
            mPresenter.requestDoWithdraw(user.getId(), walletResponse.getWallet().getId(), withdrawAmount, destinationAddress);
        }
    }

    private void updateSeekbar(int progress) {
        KLog.v("HMtes " + (step++) + " updateSeekbar progress " + progress + "");
        isEditFromButton = true;
        customSeekBar.setProgress(progress);
        withdrawAmount = (float) (personalBalance * ((double) progress / 100));

        KLog.v("HMtes " + (step++) + " updateSeekbar progress pb[" + personalBalance + "] * pro[" + progress + "] / 100 = [" + withdrawAmount + "]");

        etAmount.setText(withdrawAmount + "");
    }
}
