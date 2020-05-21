package com.path.mypath.edit_page;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.listener.OnResultCallbackListener;
import com.makeramen.roundedimageview.RoundedImageView;
import com.path.mypath.R;
import com.path.mypath.home_activity.HomeActivity;
import com.path.mypath.share_page.ShareActivity;
import com.path.mypath.tools.GlideEngine;
import com.path.mypath.tools.UserDataProvider;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.List;
import java.util.Map;

public class EditActivity extends AppCompatActivity implements EditActivityVu {

    private EditActivityPresenter presenter;

    private FirebaseAuth mAuth;

    private FirebaseUser user;

    private FirebaseFirestore firestore;

    private EditText edNickname, edSentence;

    private RoundedImageView ivPhoto;

    private StorageReference storage;

    private ImageView ivAdd;

    private TextView tvAccountInfo;

    private Switch swAccount;

    private static final String PERSONAL_DATA = "personal_data";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        initFirebase();
        initPresenter();
        initView();
    }

    private void initView() {
        tvAccountInfo = findViewById(R.id.edit_public_account_info);
        swAccount = findViewById(R.id.edit_switch_account);
        ivAdd = findViewById(R.id.edit_add_icon);
        ivPhoto = findViewById(R.id.edit_photo);
        edNickname = findViewById(R.id.edit_edit_name);
        edSentence = findViewById(R.id.edit_edit_sentence);

        ivAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.onUserPhotoSelectClickListener();
            }
        });

        swAccount.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Log.i("Michael","目前狀態 : "+isChecked);
                presenter.onCatchAccountMode(isChecked);
            }
        });

        ivPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.onUserPhotoSelectClickListener();
            }
        });

        Button btnSave = findViewById(R.id.edit_btn_save);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.onSaveButtonClickListener(edNickname.getText().toString(), edSentence.getText().toString(), user.getEmail());
            }
        });
    }

    private void initFirebase() {
        mAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        user = mAuth.getCurrentUser();
        storage = FirebaseStorage.getInstance().getReference();
    }

    private void initPresenter() {
        presenter = new EditActivityPresenterImpl(this);
    }

    @Override
    public void selectPhoto() {
        PictureSelector.create(this)
                .openGallery(PictureMimeType.ofImage())
                .loadImageEngine(GlideEngine.createGlideEngine())
                .maxSelectNum(1)
                .compress(true)
                .enableCrop(true)
                .hideBottomControls(false)
                .showCropFrame(false)
                .freeStyleCropEnabled(true)
                .forResult(new OnResultCallbackListener() {
                    @Override
                    public void onResult(List<LocalMedia> result) {

                        for (int i = 0; i < result.size(); i++) {
                            File file = new File(result.get(i).getCutPath());
                            Uri uri = Uri.fromFile(file);
                            try {
                                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                                bitmap.compress(Bitmap.CompressFormat.JPEG, 80, stream);
                                byte[] byteArray = stream.toByteArray();
                                if (byteArray.length != 0) {
                                    ivPhoto.setImageBitmap(bitmap);
                                    presenter.onCatchUserPhotoByte(byteArray);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
    }

    @Override
    public void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    @Override
    public void setDataToFirebase(Map<String, Object> map, Map<String, Object> mapJson) {
        if (user != null && user.getEmail() != null) {
            firestore.collection("user")
                    .document(user.getEmail())
                    .set(map, SetOptions.merge())
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                Log.i("Michael","資料更新成功");
                                presenter.onDataUpdateSuccessfulListener();
                            }
                        }
                    });
            firestore.collection(PERSONAL_DATA)
                    .document(user.getEmail())
                    .set(mapJson,SetOptions.merge())
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                Log.i("Michael","個人資料更新成功");
                            }
                        }
                    });
        }

    }

    @Override
    public void uploadPhotoToFirebase(byte[] byteArray) {
        StorageReference river = storage.child(user.getEmail() + "/" + "user_photo" + "/" + user.getEmail() + ".jpg");
        UploadTask task = river.putBytes(byteArray);
        task.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                river.getDownloadUrl()
                        .addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                String downLoadUrl = uri.toString();
                                presenter.onCatchPhotoUrl(downLoadUrl);
                            }
                        });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.i("Michael", "上傳錯誤 : " + e.toString());
            }
        });
    }

    @Override
    public void intentToShareActivity() {
        Intent it = new Intent(this, HomeActivity.class);
        startActivity(it);
        finish();
    }

    @Override
    public void saveUserData(String email, String downloadUrl, String nickname, String sentence) {
        UserDataProvider.getInstance(this).saveUserEmail(email);
        UserDataProvider.getInstance(this).saveUserSentence(sentence);
        UserDataProvider.getInstance(this).saveUserNickname(nickname);
        UserDataProvider.getInstance(this).saveUserPhotoUrl(downloadUrl);
    }

    @Override
    public void setAccountInfo(String message) {
        tvAccountInfo.setText(message);
    }
}
