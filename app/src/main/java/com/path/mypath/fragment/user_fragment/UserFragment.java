package com.path.mypath.fragment.user_fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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
import com.path.mypath.data_parser.DataArray;
import com.path.mypath.data_parser.DataObject;
import com.path.mypath.fragment.user_fragment.user_presenter.UserPresenter;
import com.path.mypath.fragment.user_fragment.user_presenter.UserPresenterImpl;
import com.path.mypath.fragment.user_fragment.user_view.UserInfoViewHolder;
import com.path.mypath.fragment.user_fragment.user_view.UserMapViewHolder;
import com.path.mypath.share_page.ShareActivity;
import com.path.mypath.single_view_activity.SingleViewActivity;
import com.path.mypath.tools.GlideEngine;
import com.path.mypath.tools.UserDataProvider;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserFragment extends Fragment implements UserFragmentVu {

    private UserFragmentPresenter presenter;
    private UserPresenter userPresenter;
    private Context context;

    private RecyclerView recyclerView;

    private FirebaseFirestore firestore;

    private StorageReference storageReference;

    private FirebaseAuth mAuth;

    private FirebaseUser user;

    private static final String PERSONAL_DATA = "personal_data";

    private static final String HOME_DATA = "home_data";

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    public static UserFragment newInstance() {
        UserFragment fragment = new UserFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initPresenter();
        initFirebase();
    }

    private void initFirebase() {
        firestore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
    }

    private void initPresenter() {
        presenter = new UserFragmentPresenterImpl(this);
        userPresenter = new UserPresenterImpl();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_user, container, false);
        initView(view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        presenter.onSearchDataFromFirebase();

        //即時監控
        if (user != null && user.getEmail() != null){
            DocumentReference docRef = firestore.collection(PERSONAL_DATA).document(user.getEmail());
            docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot snapshot, @Nullable FirebaseFirestoreException e) {
                    if (e != null){
                        presenter.onCatchFirebaseListenerFail(e.toString());
                        return;
                    }
                    if (snapshot != null && snapshot.exists()){
                        String json = (String) snapshot.get("user_json");
                        if (json != null){
                            presenter.onCatchJsonSuccessful(json);
                        }else {
                            presenter.onCatchFirebaseListenerFail(getString(R.string.catch_no_data));
                        }
                    }
                }
            });

            DocumentReference snapshot = firestore.collection(HOME_DATA).document(HOME_DATA);

            snapshot.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                    if (e != null){
                        Log.i("Michael","LIKE DATA 取得資料失敗 : "+e.toString());
                        return;
                    }
                    if (documentSnapshot != null){
                        String json = (String) documentSnapshot.get("json");
                        presenter.onCatchHomeDataSuccessful(json);
                    }
                }
            });
        }

    }

    private void initView(View view) {
        recyclerView = view.findViewById(R.id.user_fragment_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
    }


    @Override
    public void searchDataFromFirebase() {
        firestore.collection(PERSONAL_DATA)
                .document(UserDataProvider.getInstance(context).getUserEmail())
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful() && task.getResult() != null){
                            DocumentSnapshot snapshot = task.getResult();
                            String json = (String) snapshot.get("user_json");
                            if (json != null){
                                presenter.onCatchJsonSuccessful(json);
                            }
                        }
                    }
                });
    }

    @Override
    public void setRecyclerView(DataObject data) {
        userPresenter.onSetData(data);
        UserAdapter adapter = new UserAdapter(context,userPresenter);
        recyclerView.setAdapter(adapter);
        //上半部VIEW的點擊事件
        adapter.setOnUserInformationClickListener(new UserInfoViewHolder.OnUserInformationClickListener() {
            @Override
            public void onPhotoClick() {
                presenter.onUserPhotoClick();
            }

            @Override
            public void onEditBtnClick() {

            }
        });
        //下半部VIEW的點擊事件
        adapter.setOnUserDownClickListener(new UserMapViewHolder.OnUserDownClickListener() {
            @Override
            public void onIconClick() {
                presenter.onAddIconClickListener();
            }

            @Override
            public void onMapItemClick(DataArray locationArray) {
                Log.i("Michael","點擊了MAP");
                presenter.onMapItemClickListener(locationArray);
            }
        });
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
                                Bitmap bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), uri);
                                bitmap.compress(Bitmap.CompressFormat.JPEG, 80, stream);
                                byte[] byteArray = stream.toByteArray();
                                if (byteArray.length != 0) {
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
    public void updateUserPhoto(byte[] byteArray) {


        if (user != null && user.getEmail() != null){
            StorageReference river = storageReference.child(user.getEmail() + "/" + "user_photo" + "/" + user.getEmail() + ".jpg");
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


    }

    @Override
    public void updateFirebaseData(String json) {
        if (user != null && user.getEmail() != null){
            Map<String,Object> map = new HashMap<>();
            map.put("user_json",json);
            firestore.collection(PERSONAL_DATA)
                    .document(user.getEmail())
                    .set(map, SetOptions.merge())
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                Log.i("Michael","照片更新成功");
                                presenter.onUpdateUserPhotoSuccessful();
                            }
                        }
                    });
        }

    }

    @Override
    public void showToast(String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void intentToRecordActivity() {
        Intent it = new Intent(context, ShareActivity.class);
        context.startActivity(it);
    }

    @Override
    public void saveUserPhoto(String downLoadUrl) {
        UserDataProvider.getInstance(context).saveUserPhotoUrl(downLoadUrl);
    }

    @Override
    public void intentToSingleViewActivity(DataArray locationArray) {
        Intent it = new Intent(context, SingleViewActivity.class);
        it.putExtra("data",locationArray);
        context.startActivity(it);
    }

    @Override
    public String getNickname() {
        return UserDataProvider.getInstance(context).getUserNickname();
    }

    @Override
    public void updateHomeData(String homeJson) {
        Map<String,Object> map = new HashMap<>();
        map.put("json",homeJson);
        firestore.collection(HOME_DATA)
                .document(HOME_DATA)
                .set(map,SetOptions.merge());
    }
}
