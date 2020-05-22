package com.path.mypath.fragment.user_fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.maps.MapFragment;
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
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.listener.OnResultCallbackListener;
import com.path.mypath.MainActivity;
import com.path.mypath.R;
import com.path.mypath.data_parser.DataArray;
import com.path.mypath.data_parser.DataObject;
import com.path.mypath.fragment.MessageArray;
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
import java.util.ArrayList;
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

    private GoogleSignInClient googleSignInClient;

    private byte[] byteArray;

    private Handler handler = new Handler();

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
                        Log.i("Michael","HOME DATA 取得資料失敗 : "+e.toString());
                        return;
                    }
                    if (documentSnapshot != null){
                        String json = (String) documentSnapshot.get("json");
                        presenter.onCatchHomeDataSuccessful(json);
                    }
                }
            });

            DocumentReference likeShot = firestore.collection("user_like").document(UserDataProvider.getInstance(context).getUserEmail());
            likeShot.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                    if (e != null){
                        Log.i("Michael","LIKE DATA 取得資料失敗 : "+e.toString());
                        return;
                    }
                    if (documentSnapshot != null){
                        String json = (String) documentSnapshot.get("json");
                        presenter.onCatchLikeDataSuccess(json);
                    }
                }
            });

            firestore.collection("personal_chat")
                    .addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot snapshots, @Nullable FirebaseFirestoreException e) {
                            if (e != null){
                                Log.i("Michael","Chat DATA 取得資料失敗 : "+e.toString());
                                return;
                            }
                            if (snapshots != null){
                                ArrayList<String> chatJsonArray = new ArrayList<>();
                                ArrayList<String> roomIdArray = new ArrayList<>();
                                for (QueryDocumentSnapshot data : snapshots){
                                    String json = (String) data.get("json");
                                    chatJsonArray.add(json);
                                    roomIdArray.add(data.getId());
                                }
                                if (chatJsonArray.size() != 0){
                                    presenter.onCatchPersonalChatData(chatJsonArray,roomIdArray);
                                }
                            }
                        }
                    });

            firestore.collection(PERSONAL_DATA)
                    .addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot snapshots, @Nullable FirebaseFirestoreException e) {
                            if (e != null){
                                Log.i("Michael","使用者資料取得失敗 : "+e.toString());
                                return;
                            }
                            if (snapshots != null){
                                ArrayList<String> userJsonArray = new ArrayList<>();
                                for (QueryDocumentSnapshot data : snapshots){
                                    String json = (String) data.get("user_json");
                                    userJsonArray.add(json);
                                }
                                if (userJsonArray.size() != 0){
                                    presenter.onCatchAllUserData(userJsonArray);
                                }
                            }
                        }
                    });

            firestore.collection("user_like")
                    .addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot snapshots, @Nullable FirebaseFirestoreException e) {
                            if (e != null){
                                Log.i("Michael","所有 愛心頁面資料取得失敗 : "+e.toString());
                                return;
                            }
                            if (snapshots != null){
                                ArrayList<String> likeJsonArray = new ArrayList<>();
                                ArrayList<String> emailArray = new ArrayList<>();
                                for (QueryDocumentSnapshot data : snapshots){
                                    String json = (String) data.get("json");
                                    likeJsonArray.add(json);
                                    emailArray.add(data.getId());
                                }
                                if (likeJsonArray.size() != 0){
                                    presenter.onCatchAllLikeData(likeJsonArray,emailArray);
                                }
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

            @Override
            public void onEditNicknameClick() {
                presenter.onEditNicknameClickListener();
            }

            @Override
            public void onEditSentenceClick() {
                presenter.onEditSentenceClickListener();
            }

            @Override
            public void onLogoutClick() {
                presenter.onLogoutClickListener();
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

        this.byteArray = byteArray;
        handler.post(updatePhoto);

    }

    private Runnable updatePhoto = new Runnable() {
        @Override
        public void run() {
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
    };

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
        if (getActivity() != null){
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                }
            });
        }


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

    @Override
    public void showEditNicknameDialog() {
        EditText editText = new EditText(context);
        editText.setHint(getString(R.string.enter_nickname));
        AlertDialog dialog = new AlertDialog.Builder(context)
                .setTitle(getString(R.string.edit_personal_info))
                .setView(editText)
                .setPositiveButton(getString(R.string.confirm), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        presenter.onEditConfirmClickListener(editText.getText().toString(),UserDataProvider.getInstance(context).getUserNickname());
                    }
                }).setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).create();
        dialog.show();
    }

    @Override
    public void showEditSentenceDialog() {
        EditText editText = new EditText(context);
        editText.setHint(getString(R.string.enter_sentence));
        AlertDialog dialog = new AlertDialog.Builder(context)
                .setTitle(getString(R.string.edit_personal_info))
                .setView(editText)
                .setPositiveButton(getString(R.string.confirm), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        presenter.onEditSentenceConfirmClickListener(editText.getText().toString());
                    }
                }).setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).create();
        dialog.show();
    }

    @Override
    public void updateUserData(String newJson, String nickname) {
        UserDataProvider.getInstance(context).saveUserNickname(nickname);
        Map<String,Object> map = new HashMap<>();
        map.put("display_name",nickname);
        firestore.collection("user")
                .document(UserDataProvider.getInstance(context).getUserEmail())
                .set(map,SetOptions.merge());
        Log.i("Michael","user data update successful");

    }

    @Override
    public void updatePersonalData(String newJson) {
        Map<String,Object> map = new HashMap<>();
        map.put("user_json",newJson);
        firestore.collection(PERSONAL_DATA)
                .document(UserDataProvider.getInstance(context).getUserEmail())
                .set(map,SetOptions.merge())
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Log.i("Michael","personal data update success");
                        }
                    }
                });

    }

    @Override
    public void updateLikeData(String likeJson) {
        Map<String,Object> map = new HashMap<>();
        map.put("json",likeJson);
        firestore.collection("user_like")
                .document(UserDataProvider.getInstance(context).getUserEmail())
                .set(map,SetOptions.merge());
        Log.i("Michael","like data update success");
    }

    @Override
    public void updateUserDataSentence(String json, String sentence) {
        Map<String,Object> map = new HashMap<>();
        map.put("user_json",json);
        firestore.collection(PERSONAL_DATA)
                .document(UserDataProvider.getInstance(context).getUserEmail())
                .set(map,SetOptions.merge());
        map = new HashMap<>();
        map.put("sentence",sentence);
        firestore.collection("user")
                .document(UserDataProvider.getInstance(context).getUserEmail())
                .set(map,SetOptions.merge());
        UserDataProvider.getInstance(context).saveUserSentence(sentence);
    }

    @Override
    public void showLogoutConfirmDialog() {
       androidx.appcompat.app.AlertDialog dialog = new androidx.appcompat.app.AlertDialog.Builder(context)
               .setTitle(context.getString(R.string.information))
               .setMessage(context.getString(R.string.confirm_to_logout))
               .setPositiveButton(context.getString(R.string.confirm), new DialogInterface.OnClickListener() {
                   @Override
                   public void onClick(DialogInterface dialog, int which) {
                      presenter.onLogoutConfirmClickListener();
                   }
               }).setNegativeButton(context.getString(R.string.cancel), new DialogInterface.OnClickListener() {
                   @Override
                   public void onClick(DialogInterface dialog, int which) {

                   }
               }).create();
        dialog.show();
    }

    @Override
    public void logout() {
        FirebaseAuth.getInstance().signOut();
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(context.getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(context,gso);
        googleSignInClient.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Log.i("Michael","已登出");
                }
            }
        });
        googleSignInClient.revokeAccess().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Log.i("Michael","google revokeAccess");
                }
            }
        });
        Intent it = new Intent(context, MainActivity.class);
        context.startActivity(it);
        if (getActivity() != null){
            getActivity().finish();
        }

    }

    @Override
    public void updateChatData(String chatJson, String roomId) {
        Map<String,Object> map = new HashMap<>();
        map.put("json",chatJson);
        firestore.collection("personal_chat")
                .document(roomId)
                .set(map,SetOptions.merge())
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Log.i("Michael","update chat data good");
                            presenter.onUpdateNextChatData();
                        }
                    }
                });
    }

    @Override
    public void updateAllUserData(String userEmail, String userJson) {

        Map<String,Object> map = new HashMap<>();
        map.put("json",userJson);

        firestore.collection(PERSONAL_DATA)
                .document(userEmail)
                .set(map,SetOptions.merge())
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Log.i("Michael","update user data good");
                            presenter.onUpdateNextUserData();
                        }
                    }
                });

    }

    @Override
    public void updateAllLikeData(String likeJson, String email) {

        Map<String,Object> map = new HashMap<>();
        map.put("json",likeJson);

        firestore.collection("user_like")
                .document(email)
                .set(map,SetOptions.merge())
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Log.i("Michael","update user like good");
                            presenter.onUpdateNextLikeData();
                        }
                    }
                });
    }
}
