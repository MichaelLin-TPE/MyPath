package com.path.mypath;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.path.mypath.edit_page.EditActivity;
import com.path.mypath.home_activity.HomeActivity;
import com.path.mypath.tools.UserDataProvider;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements MainActivityVu {

    private MainActivityPresenter presenter;


    private static final int REQUEST_LOCATION = 1;

    private static String[] PERMISSION_LOCATION = {"android.permission.ACCESS_FINE_LOCATION"
            , "android.permission.ACCESS_COARSE_LOCATION"
            , "android.permission.ACCESS_BACKGROUND_LOCATION"
            , "android.permission.READ_EXTERNAL_STORAGE"
            , "android.permission.WRITE_EXTERNAL_STORAGE"};

    private static final String USER = "user";

    private static final String PERSONAL_DATA = "personal_data";

    private int permission;

    private SignInButton btnLogin;

    private static final int RC_SIGN_IN = 0;

    private GoogleSignInClient googleSignInClient;

    private FirebaseAuth mAuth;

    private FirebaseUser user;

    private FirebaseFirestore firestore;

    private String email;

    private AlertDialog waitDialog;

    private Handler handler;

    private LoginButton fbLoginBtn;

    private CallbackManager callbackManager;

    @Override
    protected void onStart() {
        super.onStart();
        user = mAuth.getCurrentUser();
        if (user != null){
            //這邊導下一頁

            presenter.onCatchCurrentUser(user.getEmail());
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initFirebase();
        initPresenter();
        initView();
        handler = new Handler();


        //紀錄FB應用程式的啟用作業
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);

        verifyLocationPermissions(this);



    }

    private void initFirebase() {

        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this,gso);

        mAuth = FirebaseAuth.getInstance();

        firestore = FirebaseFirestore.getInstance();
    }

    private void initView() {
        Toolbar toolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);
        btnLogin = findViewById(R.id.main_sign_in_btn);
        btnLogin.setSize(SignInButton.SIZE_STANDARD);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.onButtonLoginClickListener();
            }
        });
        callbackManager = CallbackManager.Factory.create();
        fbLoginBtn  = findViewById(R.id.facebook_login_btn);
        fbLoginBtn.setReadPermissions("email");
        fbLoginBtn.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                handelFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Log.i("Michael","FB登入取消");
            }

            @Override
            public void onError(FacebookException error) {
                Log.i("Michael","FB登入錯誤 : "+error.toString());
            }
        });
    }

    private void handelFacebookAccessToken(AccessToken token) {
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            //登入成功
                            Log.i("Michael","登入成功");
                            user = mAuth.getCurrentUser();
                            if (user != null){
                                presenter.onShowWaitDialog();
                                presenter.onRegisterAccountToFirebase(user.getEmail(),user.getUid());
                            }
                        }else {
                            //登入失敗
                            user = mAuth.getCurrentUser();
                            if (user != null){
                                Log.i("Michael","有用戶");
                            }else {
                                Log.i("Michael","沒用互");
                            }
                            Log.i("Michael",task.getException().toString());
                            Toast.makeText(MainActivity.this,getString(R.string.login_fail)+task.getException().toString(),Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    private void initPresenter() {
        presenter = new MainActivityPresenterImpl(this);
    }

    private void verifyLocationPermissions(MainActivity mainActivity) {
        try {
            permission = ActivityCompat.checkSelfPermission(mainActivity,
                    "android.permission.ACCESS_FINE_LOCATION");

            if (permission != PackageManager.PERMISSION_GRANTED) {
                btnLogin.setClickable(false);
                ActivityCompat.requestPermissions(mainActivity, PERMISSION_LOCATION, REQUEST_LOCATION);
            }else {
                btnLogin.setClickable(true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    //取得權限
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case REQUEST_LOCATION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    NotificationManagerCompat manager = NotificationManagerCompat.from(MainActivity.this);
                    boolean isEnable = manager.areNotificationsEnabled();
                    if (!isEnable) {
                        AlertDialog dialog = new AlertDialog.Builder(this)
                                .setTitle(getString(R.string.permission))
                                .setMessage(getString(R.string.is_open_notification))
                                .setPositiveButton(getString(R.string.confirm), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                            //这种方案适用于 API 26, 即8.0（含8.0）以上可以用
                                            Intent intent = new Intent();
                                            intent.setAction(Settings.ACTION_APPLICATION_SETTINGS);
                                            intent.putExtra(Settings.EXTRA_APP_PACKAGE, getPackageName());
                                            intent.putExtra(Settings.EXTRA_CHANNEL_ID, getApplicationInfo().uid);
                                            startActivity(intent);
                                        } else {
                                            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP_MR1) {
                                                toSystemConfig();
                                            } else {
                                                try {
                                                    toApplicationInfo();
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                    toSystemConfig();
                                                }
                                            }
                                        }
                                    }
                                }).setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                    }
                                }).create();
                        dialog.show();
                    }
                }
                break;
        }

    }

    private void toApplicationInfo() {
        Intent localIntent = new Intent();
        localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        localIntent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        localIntent.setData(Uri.fromParts("package", getPackageName(), null));
        startActivity(localIntent);
    }

    private void toSystemConfig() {
        try {
            Intent intent = new Intent(Settings.ACTION_SETTINGS);
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void startGoogleLogin() {
        Intent it = googleSignInClient.getSignInIntent();
        startActivityForResult(it,RC_SIGN_IN);
    }

    @Override
    public void intentToShareActivity() {
        waitDialog.dismiss();
        Intent it = new Intent(this, HomeActivity.class);
        startActivity(it);
        finish();
    }


    @Override
    public void setUserDataToFireStore(Map<String, Object> userMap, String email, String userJson) {
        firestore.collection(USER)
                .document(email)
                .set(userMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            presenter.onSetFirebaseDataSuccessful();
                        }else {
                            Log.i("Michael","創建FireStore資料失敗");
                        }
                    }
                });
        Map<String,Object> map = new HashMap<>();
        map.put("user_json",userJson);
        firestore.collection(PERSONAL_DATA)
                .document(email)
                .set(map)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Log.i("Michael","基本資料建置成功");
                        }
                    }
                });
    }

    @Override
    public void checkUserData(final String email, final String uid) {
        firestore.collection(USER)
                .document(email)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful() && task.getResult() != null){
                            DocumentSnapshot snapshot = task.getResult();
                            if (snapshot != null){
                                String mail = (String) snapshot.get("email");
                                if (mail == null){
                                    FirebaseInstanceId.getInstance().getInstanceId()
                                            .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                                                @Override
                                                public void onComplete(@NonNull Task<InstanceIdResult> task) {
                                                    if (!task.isSuccessful()){
                                                        Log.i("Michael","申請TOKEN 失敗 : "+task.getException());
                                                        return;
                                                    }
                                                    String token = task.getResult().getToken();
                                                    presenter.onCatchNoData(email,uid,token);
                                                }
                                            });
                                }else {
                                    presenter.onCatchCurrentUser(user.getEmail());
                                }
                            }else {
                                FirebaseInstanceId.getInstance().getInstanceId()
                                        .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                                            @Override
                                            public void onComplete(@NonNull Task<InstanceIdResult> task) {
                                                if (!task.isSuccessful()){
                                                    Log.i("Michael","申請TOKEN 失敗 : "+task.getException());
                                                    return;
                                                }
                                                String token = task.getResult().getToken();
                                                presenter.onCatchNoData(email,uid,token);
                                            }
                                        });
                            }
                        }
                    }
                });
    }

    @Override
    public void intentToEditActivity() {
        waitDialog.dismiss();
        Intent it = new Intent(this,EditActivity.class);
        startActivity(it);
        finish();
    }

    @Override
    public void updateUserData(String email) {
        this.email = email;
        handler = new Handler();
        handler.post(updateData);
    }

    @Override
    public void showWaitDialog() {
        View view = View.inflate(this,R.layout.waiting_dialog,null);
        TextView tvContent = view.findViewById(R.id.wait_content);
        tvContent.setText(getString(R.string.please_wait));
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setView(view).setCancelable(false);

        if (waitDialog == null){
            waitDialog = builder.create();
        }
        if (!waitDialog.isShowing()){
            waitDialog.show();
        }

    }

    private Runnable updateData = new Runnable() {
        @Override
        public void run() {
            firestore.collection(USER)
                    .document(email)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful() && task.getResult() != null){
                                DocumentSnapshot snapshot = task.getResult();
                                String email = (String) snapshot.get("email");
                                String nickname = (String) snapshot.get("display_name");
                                String photoUrl = (String) snapshot.get("photo");
                                String sentence = (String) snapshot.get("sentence");
                                if (nickname != null && !nickname.isEmpty()
                                        && sentence != null && !sentence.isEmpty()
                                        && photoUrl != null && !photoUrl.isEmpty()){
                                    UserDataProvider.getInstance(MainActivity.this).saveUserNickname(nickname);
                                    UserDataProvider.getInstance(MainActivity.this).saveUserPhotoUrl(photoUrl);
                                    UserDataProvider.getInstance(MainActivity.this).saveUserSentence(sentence);
                                    UserDataProvider.getInstance(MainActivity.this).saveUserEmail(email);
                                    Log.i("Michael","更新資料完成");
                                    FirebaseInstanceId.getInstance().getInstanceId()
                                            .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                                                @Override
                                                public void onComplete(@NonNull Task<InstanceIdResult> task) {
                                                    if (!task.isSuccessful()){
                                                        Log.i("Michael","申請TOKEN 失敗 : "+task.getException());
                                                        return;
                                                    }
                                                    String token = task.getResult().getToken();
                                                    Map<String,Object> map = new HashMap<>();
                                                    map.put("cloud_token",token);
                                                    Log.i("Michael",token);
                                                    firestore.collection(USER)
                                                            .document(email)
                                                            .set(map, SetOptions.merge());
                                                    presenter.onUpdateDataSuccessful();
                                                }
                                            });
                                }else {
                                    presenter.onCheckUserDataFail();
                                }

                            }
                        }
                    });

        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        callbackManager.onActivityResult(requestCode,resultCode,data);

        if (requestCode == RC_SIGN_IN && data != null){
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> task) {
        try{
            Log.i("Michael","Google登入成功");
            GoogleSignInAccount account = task.getResult(ApiException.class);
            if (account == null){
                Log.i("Michael","account 不存在");
                Toast.makeText(this,"account 不存在",Toast.LENGTH_LONG).show();
                return;
            }
            registerFirebaseAuth(account);
        }catch (ApiException e){
            Log.i("Michael","SignInResult:failed code = "+e.getStatusCode());
        }
    }

    private void registerFirebaseAuth(final GoogleSignInAccount account) {
        presenter.onShowWaitDialog();
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(),null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            user = mAuth.getCurrentUser();
                            if (user != null){
                                presenter.onRegisterAccountToFirebase(user.getEmail(),user.getUid());
                            }
                        }else {
                            task.getException().printStackTrace();
                            Log.i("Michael","signInWithCredential:failure : "+task.getException());
                        }
                    }
                });
    }
}
