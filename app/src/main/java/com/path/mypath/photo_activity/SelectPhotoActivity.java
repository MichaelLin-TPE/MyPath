package com.path.mypath.photo_activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.service.autofill.UserData;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.listener.OnResultCallbackListener;
import com.path.mypath.R;
import com.path.mypath.tools.GlideEngine;
import com.path.mypath.tools.UserDataProvider;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class SelectPhotoActivity extends AppCompatActivity implements SelectPhotoActivityVu {

    private SelectPhotoActivityPresenter presenter;

    private FirebaseFirestore firestore;

    private StorageReference river;

    private ViewPager viewPager;

    private ImageView ivBack,ivAdd;

    private EditText edContent;

    private ArrayList<byte[]> photoArray;

    private ArrayList<String> photoUrlArray;

    private Button btnFinish;

    private int photoIndex;

    private static final String PERSONAL_DATA = "personal_data";

    private static final String HOME_DATA = "home_data";

    private static final String PUBLIC_DATA = "public_data";

    private ProgressBar progressBar;

    private TextView tvWaitContent;

    private AlertDialog dialogWait;

    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_photo);
        initPresenter();
        initFirebase();
        initView();
        handler = new Handler();
        /**
         * 一開始近來頁面直接讓使用者選照片
         */
        PictureSelector.create(this)
                .openGallery(PictureMimeType.ofImage())
                .loadImageEngine(GlideEngine.createGlideEngine())
                .maxSelectNum(3)
                .compress(true)
                .enableCrop(true)
                .hideBottomControls(false)
                .showCropFrame(false)
                .freeStyleCropEnabled(true)
                .forResult(new OnResultCallbackListener() {
                    @Override
                    public void onResult(List<LocalMedia> result) {
                        ArrayList<byte[]> photoArray = new ArrayList<>();
                        for (int i = 0; i < result.size(); i++) {
                            File file = new File(result.get(i).getCutPath());
                            Uri uri = Uri.fromFile(file);
                            try {
                                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                                bitmap.compress(Bitmap.CompressFormat.JPEG, 80, stream);
                                byte[] byteArray = stream.toByteArray();
                                if (byteArray.length != 0) {
                                    photoArray.add(byteArray);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        if (photoArray.size() != 0){
                            presenter.onCatchUserPhotoByte(photoArray);
                        }
                    }
                });

        /**
         *即時取得USER DATA;
         */
        DocumentReference userSnapshot = firestore.collection(PERSONAL_DATA).document(UserDataProvider.getInstance(this).getUserEmail());
        userSnapshot.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if (e != null){
                    Log.i("Michael","個人資料取得失敗 : "+e.toString());
                    return;
                }
                if (documentSnapshot != null){
                    String json = (String) documentSnapshot.get("user_json");
                    presenter.onCatchUserDataSuccessful(json);
                }
            }
        });

        /**
         * 即時取得HOME DATA
         */
        DocumentReference homeSnapshot = firestore.collection(HOME_DATA).document(HOME_DATA);
        homeSnapshot.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if (e != null){
                    Log.i("Michael","主資料取得失敗 : "+e.toString());
                    return;
                }
                if (documentSnapshot != null){
                    String json = (String) documentSnapshot.get("json");

                    presenter.onCatchHomeDataSuccessful(json);
                }
            }
        });
        /**
         * 即時取得搜尋頁面資料
         */
        DocumentReference publicSnapshot = firestore.collection(PUBLIC_DATA).document(PUBLIC_DATA);
        publicSnapshot.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if (e != null){
                    Log.i("Michael","取得搜尋頁面資料失敗 : "+e.toString());
                    return;
                }
                if (documentSnapshot != null){
                    String json = (String) documentSnapshot.get("public_json");
                    presenter.onCatchSearchPageDataSuccessful(json);
                }
            }
        });
    }

    private void initView() {
        viewPager = findViewById(R.id.photo_view_pager);
        ivBack = findViewById(R.id.photo_toolbar_back);
        ivAdd = findViewById(R.id.photo_toolbar_add);
        edContent = findViewById(R.id.photo_edit_content);
        ivAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.onAddButtonClickListener();
            }
        });
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.onBackButtonClickListener();
            }
        });
        btnFinish = findViewById(R.id.photo_btn_finish);
        btnFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.onFinishButtonClickListener(edContent.getText().toString());
            }
        });
    }

    private void initFirebase() {
        firestore = FirebaseFirestore.getInstance();
        river = FirebaseStorage.getInstance().getReference();
    }

    private void initPresenter() {
        presenter = new SelectPhotoActivityPresenterImpl(this);
    }

    @Override
    public void uploadPhotoToFirebase(ArrayList<byte[]> byteArray) {
        this.photoArray = byteArray;
        photoUrlArray = new ArrayList<>();
        handler.post(updatePhoto);
    }

    private Runnable updatePhoto = new Runnable() {
        @Override
        public void run() {
            View view = View.inflate(SelectPhotoActivity.this,R.layout.waiting_dialog,null);
            progressBar = view.findViewById(R.id.wait_progressbar);
            tvWaitContent = view.findViewById(R.id.wait_content);
            dialogWait = new AlertDialog.Builder(SelectPhotoActivity.this)
                    .setView(view).setCancelable(false).create();
            dialogWait.show();
            uploadPhoto();
        }
    };

    private void uploadPhoto() {
        if (photoIndex < photoArray.size()){
            tvWaitContent.setText(String.format(Locale.getDefault(),"正在上傳第 %d 張照片...請稍後.",photoIndex+1));
            StorageReference storage = river.child(UserDataProvider.getInstance(this).getUserEmail() + "/" + edContent.getText().toString()
                    + "/" + edContent.getText().toString()+photoIndex+".jpg");
            UploadTask task = storage.putBytes(photoArray.get(photoIndex));
            task.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    storage.getDownloadUrl()
                            .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String downLoadUrl = uri.toString();
                                    photoUrlArray.add(downLoadUrl);
                                    Log.i("Michael","上傳成功 圖片網址 : "+downLoadUrl);
                                    photoIndex ++;
                                    uploadPhoto();
                                }
                            });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.i("Michael", "上傳錯誤 : " + e.toString());
                    dialogWait.dismiss();
                    presenter.onCatchUploadPhotoFailListener(e.toString(),photoIndex);
                }
            });
        }else {
            dialogWait.dismiss();
            photoArray = null;
            photoIndex = 0;
            presenter.onShowFinishInformation();
        }
    }

    @Override
    public void showToast(String message) {
        Toast.makeText(this,message,Toast.LENGTH_LONG).show();
    }

    @Override
    public void showPublicConfirmDialog() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AlertDialog dialog = new AlertDialog.Builder(SelectPhotoActivity.this)
                        .setTitle(getString(R.string.information))
                        .setMessage(getString(R.string.confirm_public_or_private))
                        .setCancelable(false)
                        .setPositiveButton(getString(R.string.public_data), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                presenter.onPublicConfirmClickListener(photoUrlArray,edContent.getText().toString());
                            }
                        }).setNegativeButton(getString(R.string.private_data), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                presenter.onPrivateConfirmClickListener(photoUrlArray,edContent.getText().toString());
                            }
                        }).create();
                dialog.show();
            }
        });

    }

    @Override
    public void setViewPager(ArrayList<byte[]> byteArray) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(byteArray,this);
        viewPager.setAdapter(adapter);
    }

    @Override
    public void showSelectPhotoPage() {
        PictureSelector.create(this)
                .openGallery(PictureMimeType.ofImage())
                .loadImageEngine(GlideEngine.createGlideEngine())
                .maxSelectNum(3)
                .compress(true)
                .enableCrop(true)
                .hideBottomControls(false)
                .showCropFrame(false)
                .freeStyleCropEnabled(true)
                .forResult(new OnResultCallbackListener() {
                    @Override
                    public void onResult(List<LocalMedia> result) {
                        ArrayList<byte[]> photoArray = new ArrayList<>();
                        for (int i = 0; i < result.size(); i++) {
                            File file = new File(result.get(i).getCutPath());
                            Uri uri = Uri.fromFile(file);
                            try {
                                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                                bitmap.compress(Bitmap.CompressFormat.JPEG, 80, stream);
                                byte[] byteArray = stream.toByteArray();
                                if (byteArray.length != 0) {
                                    photoArray.add(byteArray);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        if (photoArray.size() != 0){
                            presenter.onCatchUserPhotoByte(photoArray);
                        }
                    }
                });
    }

    @Override
    public void showWaitingDialog(String message) {
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(getString(R.string.information))
                .setMessage(message)
                .setPositiveButton(getString(R.string.confirm), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        presenter.onBackConfirmClickListener();
                    }
                }).setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).create();
        dialog.show();
    }

    @Override
    public void closePage() {
        finish();
    }

    @Override
    public void setFinishButtonEnable(boolean enable) {
        btnFinish.setEnabled(enable);
        ivAdd.setEnabled(enable);
    }

    @Override
    public String getUserPhoto() {
        return UserDataProvider.getInstance(this).getUserPHotoUrl();
    }

    @Override
    public String getUserEmail() {
        return UserDataProvider.getInstance(this).getUserEmail();
    }

    @Override
    public String getUserNickname() {
        return UserDataProvider.getInstance(this).getUserNickname();
    }

    @Override
    public void updateUserData(String json) {
        Map<String,Object> map = new HashMap<>();
        map.put("user_json",json);
        firestore.collection(PERSONAL_DATA)
                .document(UserDataProvider.getInstance(this).getUserEmail())
                .set(map, SetOptions.merge())
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            presenter.onUpdateSuccessful();
                        }
                    }
                });
    }

    @Override
    public void updateHomeData(String homeJson) {
        Map<String,Object> map = new HashMap<>();
        map.put("json",homeJson);
        firestore.collection(HOME_DATA)
                .document(HOME_DATA)
                .set(map,SetOptions.merge())
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Log.i("Michael","主資料更新成功");
                        }
                    }
                });

    }

    @Override
    public void updateSearchData(String searchJson) {
        Map<String,Object> map = new HashMap<>();
        map.put("public_json",searchJson);
        firestore.collection(PUBLIC_DATA)
                .document(PUBLIC_DATA)
                .set(map,SetOptions.merge())
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Log.i("Michael","搜尋資料更新成功");
                        }
                    }
                });
    }


}
