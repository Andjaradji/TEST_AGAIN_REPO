package com.vexanium.vexgift.module.profile.ui;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.Spinner;

import com.socks.library.KLog;
import com.vexanium.vexgift.R;
import com.vexanium.vexgift.annotation.ActivityFragmentInject;
import com.vexanium.vexgift.app.App;
import com.vexanium.vexgift.app.ConstantGroup;
import com.vexanium.vexgift.base.BaseActivity;
import com.vexanium.vexgift.bean.fixture.FixtureData;
import com.vexanium.vexgift.bean.model.Kyc;
import com.vexanium.vexgift.bean.model.User;
import com.vexanium.vexgift.bean.response.HttpResponse;
import com.vexanium.vexgift.module.profile.presenter.IProfilePresenter;
import com.vexanium.vexgift.module.profile.presenter.IProfilePresenterImpl;
import com.vexanium.vexgift.module.profile.view.IProfileView;
import com.vexanium.vexgift.util.JsonUtil;
import com.vexanium.vexgift.util.RxBus;
import com.vexanium.vexgift.util.ViewUtil;
import com.vexanium.vexgift.widget.dialog.DialogAction;
import com.vexanium.vexgift.widget.dialog.DialogOptionType;
import com.vexanium.vexgift.widget.dialog.VexDialog;

import java.io.File;
import java.io.Serializable;

@ActivityFragmentInject(contentViewId = R.layout.activity_kyc, toolbarTitle = R.string.myprofile_kyc, withLoadingAnim = true)
public class KycActivity extends BaseActivity<IProfilePresenter> implements IProfileView {

    String frontIdView;
    String backIdView;
    String selfieIdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initView() {
        mPresenter = new IProfilePresenterImpl(this);

        findViewById(R.id.btn_next).setOnClickListener(this);

        Spinner spDocType = findViewById(R.id.sp_document_type);
        ArrayAdapter<String> spDocTypeAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, FixtureData.documentType) {
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                view.setPadding(0, view.getPaddingTop(), view.getPaddingRight(), view.getPaddingBottom());
                return view;
            }
        };
        spDocTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spDocType.setAdapter(spDocTypeAdapter);

        Spinner spCountry = findViewById(R.id.sp_country);
        ArrayAdapter<String> spCountryAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, FixtureData.countries) {
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                view.setPadding(0, view.getPaddingTop(), view.getPaddingRight(), view.getPaddingBottom());
                return view;
            }
        };
        spCountryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spCountry.setAdapter(spCountryAdapter);

        ViewUtil.setOnClickListener(this, this,
                R.id.btn_next,
                R.id.ll_document_front_button,
                R.id.ll_document_back_button,
                R.id.ll_document_selfie_button);
    }

    @Override
    public void handleResult(Serializable data, HttpResponse errorResponse) {
        KLog.v("KycActivity handleResult : " + JsonUtil.toString(data));
        if (data != null) {


        } else if (errorResponse != null) {
            KLog.v("MyProfileActivity handleResult error : " + errorResponse.getMeta().getMessage());
            toast(errorResponse.getMeta().getStatus() + " : " + errorResponse.getMeta().getMessage());
        } else {
            RxBus.get().post("kycStatusUpdate", true);
            new VexDialog.Builder(KycActivity.this)
                    .optionType(DialogOptionType.OK)
                    .okText("OK")
                    .title("Submit Success")
                    .content("Your KYC submission has been submit. Please wait, our Team will review your submission")
                    .onPositive(new VexDialog.MaterialDialogButtonCallback() {
                        @Override
                        public void onClick(@NonNull VexDialog dialog, @NonNull DialogAction which) {
                            finish();
                        }
                    })
                    .cancelable(false)
                    .show();
        }
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.btn_next:
                doSubmitKyc();
                break;
            case R.id.ll_document_front_button:
                getPhoto(ConstantGroup.KYC_FRONT_PHOTO_RESULT_CODE);
                break;
            case R.id.ll_document_back_button:
                getPhoto(ConstantGroup.KYC_BACK_PHOTO_RESULT_CODE);
                break;
            case R.id.ll_document_selfie_button:
                getPhoto(ConstantGroup.KYC_SELFIE_PHOTO_RESULT_CODE);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case ConstantGroup.KYC_FRONT_PHOTO_RESULT_CODE:
                if (resultCode == RESULT_OK) {
                    final Uri selectedUri = data.getData();
                    frontIdView = setIdPhoto(selectedUri, R.id.iv_document_front);
                }
                break;
            case ConstantGroup.KYC_BACK_PHOTO_RESULT_CODE:
                if (resultCode == RESULT_OK) {
                    final Uri selectedUri = data.getData();
                    backIdView = setIdPhoto(selectedUri, R.id.iv_document_back);
                }
                break;
            case ConstantGroup.KYC_SELFIE_PHOTO_RESULT_CODE:
                if (resultCode == RESULT_OK) {
                    final Uri selectedUri = data.getData();
                    selfieIdView = setIdPhoto(selectedUri, R.id.iv_document_selfie);
                }
                break;
        }
    }

    private String setIdPhoto(Uri selectedUri, @IdRes int idRes) {
        if (selectedUri != null) {
            ViewUtil.setImageUrl(KycActivity.this, idRes, selectedUri);
            String photoUrl = getPath(selectedUri);
            KLog.a("HPtes path " + photoUrl);
            File file = new File(photoUrl);
            if (file.exists()) {
                return photoUrl;
            } else {
                KLog.v("KycActivity", "setIdPhoto: photoUri no exists");
                return "";
            }
        }

        return "";
    }

    private void getPhoto(int code) {
        if (checkPermission(code)) {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
            startActivityForResult(Intent.createChooser(intent, "Select Photo"), code);
        }
    }


    private void doSubmitKyc() {
        Kyc kyc = new Kyc();
        User user = User.getCurrentUser(App.getContext());
        if (user == null) return;

        kyc.setId(user.getId());

        String name = ((TextInputEditText) findViewById(R.id.et_myprofile_name)).getText().toString();
        String idType = (String) ((Spinner) findViewById(R.id.sp_document_type)).getSelectedItem();
        String country = (String) ((Spinner) findViewById(R.id.sp_country)).getSelectedItem();
        String idNumber = ((TextInputEditText) findViewById(R.id.myprofile_id_number)).getText().toString();
        CheckBox checkBox = (findViewById(R.id.cb_addree));

        boolean isValid = true;
        if (TextUtils.isEmpty(name)) {
            ((TextInputEditText) findViewById(R.id.et_myprofile_name)).setError(getString(R.string.validate_empty_field));
            isValid = false;
        }
        if (TextUtils.isEmpty(idType)) {
            isValid = false;
        }
        if (TextUtils.isEmpty(country)) {
            isValid = false;
        }
        if (TextUtils.isEmpty(idNumber)) {
            ((TextInputEditText) findViewById(R.id.myprofile_id_number)).setError(getString(R.string.validate_empty_field));
            isValid = false;
        }
        if (TextUtils.isEmpty(frontIdView)) {
            KLog.v("KycActivity", "doSubmitKyc: frontView " + frontIdView);
            toast(getString(R.string.validate_all));
            isValid = false;
        }
        if (TextUtils.isEmpty(backIdView)) {
            KLog.v("KycActivity", "doSubmitKyc: backView " + backIdView);

            toast(getString(R.string.validate_all));
            isValid = false;
        }
        if (TextUtils.isEmpty(selfieIdView)) {
            KLog.v("KycActivity", "doSubmitKyc: selfieView " + selfieIdView);

            toast(getString(R.string.validate_all));
            isValid = false;
        }
        if (!checkBox.isChecked()) {
            toast(getString(R.string.validate_checkbox_aggree_content));
            isValid = false;
        }

        if (isValid) {
            kyc.setIdName(name);
            kyc.setIdType(idType);
            kyc.setIdNumber(idNumber);
            kyc.setIdCountry(country);
            kyc.setIdImageFront(frontIdView);
            kyc.setIdImageBack(backIdView);
            kyc.setIdImageSelfie(selfieIdView);

            mPresenter.submitKyc(kyc);
        }
    }

    public String getPath(Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        startManagingCursor(cursor);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    private boolean checkPermission(int requestCode) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)

            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        requestCode);

                return false;
            } else {
                return true;

            }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            switch (requestCode) {
                case ConstantGroup.KYC_FRONT_PHOTO_RESULT_CODE:
                    getPhoto(requestCode);
                    break;
                case ConstantGroup.KYC_BACK_PHOTO_RESULT_CODE:
                    getPhoto(requestCode);
                    break;
                case ConstantGroup.KYC_SELFIE_PHOTO_RESULT_CODE:
                    getPhoto(requestCode);
                    break;
            }
        }

    }
}
