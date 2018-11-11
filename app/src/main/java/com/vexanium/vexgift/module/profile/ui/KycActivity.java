package com.vexanium.vexgift.module.profile.ui;

import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
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
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.socks.library.KLog;
import com.vexanium.vexgift.R;
import com.vexanium.vexgift.annotation.ActivityFragmentInject;
import com.vexanium.vexgift.app.App;
import com.vexanium.vexgift.app.ConstantGroup;
import com.vexanium.vexgift.app.StaticGroup;
import com.vexanium.vexgift.base.BaseActivity;
import com.vexanium.vexgift.bean.fixture.FixtureData;
import com.vexanium.vexgift.bean.model.Country;
import com.vexanium.vexgift.bean.model.Kyc;
import com.vexanium.vexgift.bean.model.User;
import com.vexanium.vexgift.bean.response.CountriesResponse;
import com.vexanium.vexgift.bean.response.HttpResponse;
import com.vexanium.vexgift.module.profile.presenter.IProfilePresenter;
import com.vexanium.vexgift.module.profile.presenter.IProfilePresenterImpl;
import com.vexanium.vexgift.module.profile.view.IProfileView;
import com.vexanium.vexgift.util.ImagePathMarshmallow;
import com.vexanium.vexgift.util.JsonUtil;
import com.vexanium.vexgift.util.RxBus;
import com.vexanium.vexgift.util.TpUtil;
import com.vexanium.vexgift.util.ViewUtil;
import com.vexanium.vexgift.widget.dialog.DialogAction;
import com.vexanium.vexgift.widget.dialog.DialogOptionType;
import com.vexanium.vexgift.widget.dialog.VexDialog;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

@ActivityFragmentInject(contentViewId = R.layout.activity_kyc, toolbarTitle = R.string.myprofile_kyc, withLoadingAnim = true)
public class KycActivity extends BaseActivity<IProfilePresenter> implements IProfileView {

    private static final String KEY_KYC_FRONT_ID_URI = "key_kyc_front_id_uri";
    private static final String KEY_KYC_BACK_ID_URI = "key_kyc_back_id_uri";
    private static final String KEY_KYC_SELFIE_ID_URI = "key_kyc_selfie_id_uri";
    private static final String KEY_KYC_COUNTRY_POSITION = "key_kyc_country_position";
    private static final String KEY_KYC_ID_POSITION = "key_kyc_id_position";
    private static final String KEY_KYC_NAME_STRING = "key_kyc_name_string";
    private static final String KEY_KYC_ID_STRING = "key_kyc_id_string";

    String frontIdView;
    String backIdView;
    String selfieIdView;

    ArrayList<Country> countries = new ArrayList<>();

    TpUtil tpUtil;

    Spinner spCountry, spIdType;
    EditText etName, etIdNumber;


    String fileName = "", mCurrentPhotoPath = "";
    Uri fileUri;


    private String frontIdUri, backIdUri, selfieIdUri;
    private int countryPos = 0, idPos = 0;
    private String kycName, kycIdNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            frontIdUri = savedInstanceState.getString(KEY_KYC_FRONT_ID_URI);
            backIdUri = savedInstanceState.getString(KEY_KYC_BACK_ID_URI);
            selfieIdUri = savedInstanceState.getString(KEY_KYC_SELFIE_ID_URI);
            countryPos = savedInstanceState.getInt(KEY_KYC_COUNTRY_POSITION);
            idPos = savedInstanceState.getInt(KEY_KYC_ID_POSITION);
            kycName = savedInstanceState.getString(KEY_KYC_NAME_STRING);
            kycIdNumber = savedInstanceState.getString(KEY_KYC_ID_STRING);


        }
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(KEY_KYC_FRONT_ID_URI, frontIdUri);
        outState.putString(KEY_KYC_BACK_ID_URI, backIdUri);
        outState.putString(KEY_KYC_SELFIE_ID_URI, selfieIdUri);

        countryPos = spCountry.getSelectedItemPosition();
        outState.putInt(KEY_KYC_COUNTRY_POSITION, countryPos);

        idPos = spIdType.getSelectedItemPosition();
        outState.putInt(KEY_KYC_ID_POSITION, idPos);

        kycName = etName.getText().toString();
        outState.putString(KEY_KYC_NAME_STRING, kycName);

        kycIdNumber = etIdNumber.getText().toString();
        outState.putString(KEY_KYC_ID_STRING, kycIdNumber);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void initView() {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "MyPicture");
        values.put(MediaStore.Images.Media.DESCRIPTION, "Photo taken on " + System.currentTimeMillis());

        mPresenter = new IProfilePresenterImpl(this);

        findViewById(R.id.btn_next).setOnClickListener(this);

        spCountry = findViewById(R.id.sp_country);
        spIdType = findViewById(R.id.sp_document_type);
        etName = findViewById(R.id.et_myprofile_name);
        etIdNumber = findViewById(R.id.myprofile_id_number);

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
        spIdType.setAdapter(spDocTypeAdapter);


        tpUtil = new TpUtil(this);
        String countriesStr = tpUtil.getString(TpUtil.KEY_COUNTRY_LIST, "");
        if (!TextUtils.isEmpty(countriesStr)) {
            try {
                countries = (ArrayList<Country>) JsonUtil.toObject(countriesStr, CountriesResponse.class);
            } catch (Exception e) {
                mPresenter.getCountries();
            }
        } else {
            mPresenter.getCountries();
        }

        if (countries != null) {
            updateCountryAdapter();
            ViewUtil.setOnClickListener(this, this,
                    R.id.btn_next,
                    R.id.ll_document_front_button,
                    R.id.ll_document_back_button,
                    R.id.ll_document_selfie_button);
        }

        if (frontIdUri != null) {
            frontIdView = setIdPhoto(Uri.parse(frontIdUri), R.id.iv_document_front);
        }

        if (backIdUri != null) {
            backIdView = setIdPhoto(Uri.parse(backIdUri), R.id.iv_document_back);
        }

        if (selfieIdUri != null) {
            selfieIdView = setIdPhoto(Uri.parse(selfieIdUri), R.id.iv_document_selfie);
        }

        if (kycName != null) {
            etName.setText(kycName);
        }

        if (kycIdNumber != null) {
            etIdNumber.setText(kycIdNumber);
        }

        if (countryPos >= 0) {
            spCountry.setSelection(countryPos);
        }

        if (idPos >= 0) {
            spIdType.setSelection(idPos);
        }
    }

    @Override
    public void handleResult(Serializable data, HttpResponse errorResponse) {
        KLog.v("KycActivity handleResult : " + JsonUtil.toString(data));
        if (data != null) {
            if (data instanceof CountriesResponse) {
                tpUtil.put(TpUtil.KEY_COUNTRY_LIST, JsonUtil.toString(data));
                countries = ((CountriesResponse) data).getCountryArrayList();
                updateCountryAdapter();
            }

        } else if (errorResponse != null) {
            KLog.v("MyProfileActivity handleResult error : " + errorResponse.getMeta().getMessage());
            StaticGroup.showCommonErrorDialog(this, errorResponse);
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
                getPhoto(ConstantGroup.KYC_FRONT_PHOTO_RESULT_CODE, ConstantGroup.KYC_CAMERA_FRONT_PHOTO_RESULT_CODE);
                break;
            case R.id.ll_document_back_button:
                getPhoto(ConstantGroup.KYC_BACK_PHOTO_RESULT_CODE, ConstantGroup.KYC_CAMERA_BACK_PHOTO_RESULT_CODE);
                break;
            case R.id.ll_document_selfie_button:
                getPhoto(ConstantGroup.KYC_SELFIE_PHOTO_RESULT_CODE, ConstantGroup.KYC_CAMERA_SELFIE_PHOTO_RESULT_CODE);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case ConstantGroup.KYC_FRONT_PHOTO_RESULT_CODE:
                if (resultCode == RESULT_OK) {
                    frontIdUri = data.getData().toString();
                    frontIdView = setIdPhoto(Uri.parse(frontIdUri), R.id.iv_document_front);
                }
                break;
            case ConstantGroup.KYC_BACK_PHOTO_RESULT_CODE:
                if (resultCode == RESULT_OK) {
                    backIdUri = data.getData().toString();
                    backIdView = setIdPhoto(Uri.parse(backIdUri), R.id.iv_document_back);
                }
                break;
            case ConstantGroup.KYC_SELFIE_PHOTO_RESULT_CODE:
                if (resultCode == RESULT_OK) {
                    selfieIdUri = data.getData().toString();
                    selfieIdView = setIdPhoto(Uri.parse(selfieIdUri), R.id.iv_document_selfie);
                }
                break;
            case ConstantGroup.KYC_CAMERA_FRONT_PHOTO_RESULT_CODE:
                if (resultCode == RESULT_OK) {
                    if(Build.VERSION.SDK_INT > 22){
                        frontIdUri = fileUri.toString();
                        frontIdView = setIdPhotoWithUrl(ImagePathMarshmallow.getPath(KycActivity.this ,fileUri), R.id.iv_document_front);
                    }else{
                        frontIdUri = fileUri.toString();
                        frontIdView = setIdPhoto(Uri.parse(frontIdUri), R.id.iv_document_front);
                    }
                }
                break;
            case ConstantGroup.KYC_CAMERA_BACK_PHOTO_RESULT_CODE:
                if (resultCode == RESULT_OK) {
                    if(Build.VERSION.SDK_INT > 22){
                        backIdUri = fileUri.toString();
                        backIdView = setIdPhotoWithUrl(ImagePathMarshmallow.getPath(KycActivity.this ,fileUri), R.id.iv_document_back);
                    }else{
                        backIdUri = fileUri.toString();
                        backIdView = setIdPhoto(Uri.parse(frontIdUri), R.id.iv_document_back);
                    }
                }
                break;
            case ConstantGroup.KYC_CAMERA_SELFIE_PHOTO_RESULT_CODE:
                if (resultCode == RESULT_OK) {
                    if(Build.VERSION.SDK_INT > 22){
                        selfieIdUri = fileUri.toString();
                        selfieIdView = setIdPhotoWithUrl(ImagePathMarshmallow.getPath(KycActivity.this ,fileUri), R.id.iv_document_selfie);
                    }else{
                        selfieIdUri = fileUri.toString();
                        selfieIdView = setIdPhoto(Uri.parse(frontIdUri), R.id.iv_document_selfie);
                    }
                }
                break;
        }
    }

    public static Uri getOutputMediaFileUri(Context context) {
        File mediaStorageDir = new File(
                context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "Camera");
        //If File is not present create directory
        if (!mediaStorageDir.exists()) {
            if (mediaStorageDir.mkdir())
                KLog.e("Create Directory", "Main Directory Created : " + mediaStorageDir);
        }

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
                Locale.getDefault()).format(new Date());//Get Current timestamp
        File mediaFile = new File(mediaStorageDir.getPath() + File.separator
                + "IMG_" + timeStamp + ".jpg");//create image path with system mill and image format
        return Uri.fromFile(mediaFile);

    }

    private void updateCountryAdapter() {
        ArrayAdapter<Country> spCountryAdapter = new ArrayAdapter<Country>(this, android.R.layout.simple_spinner_item, countries) {
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                TextView view = (TextView) super.getView(position, convertView, parent);
                view.setText(countries.get(position).getCountryName());
                view.setPadding(0, view.getPaddingTop(), view.getPaddingRight(), view.getPaddingBottom());
                return view;
            }

            @Override
            public View getDropDownView(int position, View convertView,
                                        ViewGroup parent) {
                TextView label = (TextView) super.getDropDownView(position, convertView, parent);
                label.setText(countries.get(position).getCountryName());

                return label;
            }

            @Nullable
            @Override
            public Country getItem(int position) {
                return super.getItem(position);
            }

        };
        spCountryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spCountry.setAdapter(spCountryAdapter);
        if (countryPos >= 0) {
            spCountry.setSelection(countryPos);
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

    private String setIdPhotoWithUrl(String url, @IdRes int idRes){
        ViewUtil.setImageUrl(KycActivity.this, idRes, url);
        File file = new File(url);
        if (file.exists()) {
            return url;
        } else {
            KLog.v("KycActivity", "setIdPhoto: photoUri no exists");
            return "";
        }
    }

    private void getPhoto(final int code, final int code2) {

        View view = View.inflate(this, R.layout.include_kyc_photo_method, null);
        final TextView tvCamera = view.findViewById(R.id.tv_camera);
        final TextView tvGallery = view.findViewById(R.id.tv_gallery);

        final VexDialog vexDialog = new VexDialog.Builder(this)
                .optionType(DialogOptionType.NONE)
                .title("Choose method")
                .onPositive(new VexDialog.MaterialDialogButtonCallback() {
                    @Override
                    public void onClick(@NonNull VexDialog dialog, @NonNull DialogAction which) {
                        dialog.dismiss();
                    }
                })
                .addCustomView(view)
                .autoDismiss(false)
                .canceledOnTouchOutside(true)
                .show();

        tvCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkCameraPermission(code)) {

                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    fileUri = getOutputMediaFileUri(KycActivity.this);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT,fileUri);
                    if (intent.resolveActivity(getPackageManager()) != null) {
                        startActivityForResult(intent, code2);
                    }
                }
                if (vexDialog != null) {
                    vexDialog.dismiss();
                }
            }
        });

        tvGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkPermission(code)) {
                    Intent intent = new Intent(Intent.ACTION_PICK);
                    intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
                    startActivityForResult(Intent.createChooser(intent, "Select Photo"), code);
                }
                if (vexDialog != null) {
                    vexDialog.dismiss();
                }
            }
        });

    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void doSubmitKyc() {
        Kyc kyc = new Kyc();
        User user = User.getCurrentUser(App.getContext());
        if (user == null) return;

        kyc.setId(user.getId());

        String name = ((TextInputEditText) findViewById(R.id.et_myprofile_name)).getText().toString();
        String idType = (String) ((Spinner) findViewById(R.id.sp_document_type)).getSelectedItem();
        String country = ((Country) ((Spinner) findViewById(R.id.sp_country)).getSelectedItem()).getCountryName();
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


    private boolean checkCameraPermission(int requestCode) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.CAMERA)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.CAMERA},
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
                case ConstantGroup.KYC_CAMERA_FRONT_PHOTO_RESULT_CODE:
                    getPhoto(ConstantGroup.KYC_FRONT_PHOTO_RESULT_CODE, ConstantGroup.KYC_CAMERA_FRONT_PHOTO_RESULT_CODE);
                    break;
                case ConstantGroup.KYC_BACK_PHOTO_RESULT_CODE:
                case ConstantGroup.KYC_CAMERA_BACK_PHOTO_RESULT_CODE:
                    getPhoto(ConstantGroup.KYC_BACK_PHOTO_RESULT_CODE, ConstantGroup.KYC_CAMERA_BACK_PHOTO_RESULT_CODE);
                    break;
                case ConstantGroup.KYC_SELFIE_PHOTO_RESULT_CODE:
                case ConstantGroup.KYC_CAMERA_SELFIE_PHOTO_RESULT_CODE:
                    getPhoto(ConstantGroup.KYC_SELFIE_PHOTO_RESULT_CODE, ConstantGroup.KYC_CAMERA_SELFIE_PHOTO_RESULT_CODE);
                    break;
            }
        }

    }

}
