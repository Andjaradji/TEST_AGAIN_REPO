package com.vexanium.vexgift.module.wallet.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.ContentViewEvent;
import com.crashlytics.android.answers.CustomEvent;
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
import com.vexanium.vexgift.util.ClickUtil;
import com.vexanium.vexgift.util.ViewUtil;
import com.vexanium.vexgift.widget.CustomSeekBar;
import com.vexanium.vexgift.widget.dialog.DialogAction;
import com.vexanium.vexgift.widget.dialog.DialogOptionType;
import com.vexanium.vexgift.widget.dialog.VexDialog;

import java.io.Serializable;

@ActivityFragmentInject(contentViewId = R.layout.activity_wallet_withdraw, toolbarTitle = R.string.wallet_withdraw, withLoadingAnim = true)
public class WalletWithdrawActivity extends BaseActivity<IWalletPresenter> implements IWalletView {

    public static int step = 0;
    User user;
    CustomSeekBar customSeekBar;
    WalletResponse walletResponse;
    EditText etAmount, etTotal;

    float personalBalance;
    float withdrawAmount;
    float totalAmount;
    boolean isEditFromSeekbar = false;
    boolean isEditFromButton = false;
    boolean isOverLimit = false;
    float minimumWithdraw = 0;
    float withdrawFee = 0;
    String minimumWithdrawPrompt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initView() {
        mPresenter = new IWalletPresenterImpl(this);
        user = User.getCurrentUser(this);

        minimumWithdraw = (float) StaticGroup.getMinimumWithdrawAmount();
        withdrawFee = (float) StaticGroup.getWithdrawFee();
        ((TextView) findViewById(R.id.tv_withdraw_fee)).setText(withdrawFee + " VEX");

        etAmount = findViewById(R.id.et_withdraw_amount);
        etTotal = findViewById(R.id.et_withdraw_total_amount);
        customSeekBar = findViewById(R.id.cs_amount);

        ViewUtil.setOnClickListener(this, this,
                R.id.btn_25, R.id.btn_50, R.id.btn_75, R.id.btn_scan, R.id.btn_withdraw, R.id.ib_history);

        walletResponse = TableContentDaoUtil.getInstance().getWallet();
        if (walletResponse != null && walletResponse.getWallet() != null) {
            Wallet wallet = walletResponse.getWallet();

            personalBalance = wallet.getPersonalWalletBalance();
//            personalBalance = 13631.31946f;

            ViewUtil.setText(this, R.id.tv_personal_balance, personalBalance + "");
            customSeekBar.setMax(100);
            customSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    float amount = ((float) progress / 100) * personalBalance;
                    KLog.v("HMtes " + (step++) + " seekbar progress=[" + progress + "] amount=[" + amount + "] fromUser=[" + fromUser + "]  fromButton=[" + isEditFromButton + "]");
                    if (fromUser || isEditFromButton) {
                        etAmount.setText(amount + "");
                        totalAmount = amount + withdrawFee;
                        if (totalAmount < 0) totalAmount = 0;
                        etTotal.setText(totalAmount + "");
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

        minimumWithdrawPrompt = StaticGroup.getStringValFromSettingKey("wallet_withdraw_prompt");
        if(TextUtils.isEmpty(minimumWithdrawPrompt)){
            minimumWithdrawPrompt = "Your personal wallet amount is below the minimum withdraw amount.\nMinimum Withdraw amount is";
        }
        etAmount.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (personalBalance <= minimumWithdraw && minimumWithdraw != 0) {
                    if(ClickUtil.isFastDoubleClick()) return true;
                    toast(minimumWithdrawPrompt+" "+minimumWithdraw+" VEX");
                    return true;
                }else {
                    if(ClickUtil.isFastDoubleClick()) return false;
                    return false;
                }
            }
        });
        customSeekBar.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                if (personalBalance <= minimumWithdraw && minimumWithdraw != 0) {
                    if(ClickUtil.isFastDoubleClick()) return true;
                    toast(minimumWithdrawPrompt+" "+minimumWithdraw+" VEX");
                    return true;
                }else {
                    if(ClickUtil.isFastDoubleClick()) return false;
                    return false;
                }
            }
        });

        if (getIntent().hasExtra("history")) {
            boolean isGoToHistory = getIntent().getBooleanExtra("history", true);
            if (isGoToHistory) {
                Intent intent = new Intent(this, WalletWithdrawHistoryActivity.class);
                startActivity(intent);
            }
        }

        if (personalBalance <= minimumWithdraw) {
//            etAmount.setEnabled(false);
            etTotal.setEnabled(false);
//            customSeekBar.setEnabled(false);
        } else {
//            etAmount.setEnabled(true);
            etTotal.setEnabled(false);
//            customSeekBar.setEnabled(true);
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
                    totalAmount = withdrawAmount + withdrawFee;
                    if (totalAmount < 0) totalAmount = 0;
                    etTotal.setText(totalAmount + "");

                    setEnableWithdrawButton(withdrawAmount > 0);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                isEditFromSeekbar = false;
                if (!isOverLimit) {
                    try {
                        withdrawAmount = Float.valueOf(etAmount.getText().toString());
                    } catch (Exception e) {
                        withdrawAmount = 0;
                    }
                    if (withdrawAmount > personalBalance) {
                        withdrawAmount = personalBalance;
                        isOverLimit = true;
                        etAmount.setText(withdrawAmount + "");
                        totalAmount = withdrawAmount + withdrawFee;
                        if (totalAmount < 0) totalAmount = 0;
                        etTotal.setText(totalAmount + "");

                    }
                } else {
                    isOverLimit = false;
                }
            }
        });

        String withdrawNoteTitle = StaticGroup.getStringValFromSettingKey("withdraw_note_title");
        if (!TextUtils.isEmpty(withdrawNoteTitle)) {
            ViewUtil.setText(this, R.id.tv_desc_title, withdrawNoteTitle);
        }
        String withdrawNoteDetail = StaticGroup.getStringValFromSettingKey("withdraw_note_detail");
        if (!TextUtils.isEmpty(withdrawNoteDetail)) {
            ViewUtil.setText(this, R.id.tv_desc, withdrawNoteDetail);
        }

        setEnableWithdrawButton(withdrawAmount > 0);

        Answers.getInstance().logContentView(new ContentViewEvent()
                .putContentName("Wallet Withdraw View")
                .putContentType("Wallet")
                .putContentId("wallet withdraw"));
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
                if (personalBalance <= minimumWithdraw && minimumWithdraw != 0) {
                    toast(minimumWithdrawPrompt+" "+minimumWithdraw+" VEX");
                }else {
                    updateSeekbar(25);
                    Answers.getInstance().logCustom(new CustomEvent("click button withdraw amount")
                            .putCustomAttribute("percentage", 25));
                }
                break;
            case R.id.btn_50:
                if (personalBalance <= minimumWithdraw && minimumWithdraw != 0) {
                    toast(minimumWithdrawPrompt+" "+minimumWithdraw+" VEX");
                }else {
                    updateSeekbar(50);
                    Answers.getInstance().logCustom(new CustomEvent("click button withdraw amount")
                            .putCustomAttribute("percentage", 50));
                }
                break;
            case R.id.btn_75:
                if (personalBalance <= minimumWithdraw && minimumWithdraw != 0) {
                    toast(minimumWithdrawPrompt+" "+minimumWithdraw+" VEX");
                }else {
                    updateSeekbar(75);
                    Answers.getInstance().logCustom(new CustomEvent("click button withdraw amount")
                            .putCustomAttribute("percentage", 75));
                }
                break;
            case R.id.btn_scan:
                IntentIntegrator integrator = new IntentIntegrator(this);
                integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);
                integrator.setPrompt(getString(R.string.scan_vex_address_promt));
                integrator.setCameraId(0);
                integrator.setBeepEnabled(true);
                integrator.setBarcodeImageEnabled(true);
                integrator.setOrientationLocked(false);
                integrator.initiateScan();
                break;
            case R.id.btn_withdraw:
                if (withdrawAmount > minimumWithdraw) {
                    doWithdraw();
                } else {
                    String minWithdraw = getString(R.string.wallet_withdraw_minimum_amount_dialog_text) + " " + minimumWithdraw + " VEX";
                    new VexDialog.Builder(this)
                            .optionType(DialogOptionType.OK)
                            .title(getString(R.string.wallet_withdraw_minimum_amount_dialog_title))
                            .content(minWithdraw)
                            .autoDismiss(true)
                            .show();
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
                        .content(getString(R.string.wallet_withdraw_input_success_text))
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
        final String destinationAddress = ((TextView) findViewById(R.id.et_address)).getText().toString();
        boolean isValid = ViewUtil.validateEmpty(this, getString(R.string.validate_empty_field), R.id.et_address);
        try {
            totalAmount = Float.valueOf(etTotal.getText().toString());
        } catch (Exception e) {
            totalAmount = 0;
        }
        if (totalAmount <= 0) {
            ((EditText) findViewById(R.id.et_withdraw_amount)).setError(getString(R.string.wallet_withdraw_amount_error));
            isValid = false;
        }
        if (walletResponse != null && walletResponse.getWallet() != null && user != null && isValid) {
            String content = String.format(getString(R.string.wallet_withdraw_dialog_detail), " " + totalAmount);
            new VexDialog.Builder(this)
                    .optionType(DialogOptionType.YES_NO)
                    .title(getString(R.string.wallet_withdraw_dialog_title))
                    .content(content)
                    .onPositive(new VexDialog.MaterialDialogButtonCallback() {
                        @Override
                        public void onClick(@NonNull VexDialog dialog, @NonNull DialogAction which) {
                            if (ClickUtil.isFastDoubleClick()) return;
                            mPresenter.requestDoWithdraw(user.getId(), walletResponse.getWallet().getId(), totalAmount, destinationAddress);

                        }
                    })
                    .autoDismiss(true)
                    .show();
        }
    }

    private void updateSeekbar(int progress) {
        KLog.v("HMtes " + (step++) + " updateSeekbar progress " + progress + "");
        isEditFromButton = true;
        customSeekBar.setProgress(progress);
        withdrawAmount = (float) (personalBalance * ((double) progress / 100));

        KLog.v("HMtes " + (step++) + " updateSeekbar progress pb[" + personalBalance + "] * pro[" + progress + "] / 100 = [" + withdrawAmount + "]");

        etAmount.setText(withdrawAmount + "");
        totalAmount = withdrawAmount + withdrawFee;
        if (totalAmount < 0) totalAmount = 0;
        etTotal.setText(totalAmount + "");

    }
}
