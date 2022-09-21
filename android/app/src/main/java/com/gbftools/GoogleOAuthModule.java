package com.gbftools;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.facebook.react.bridge.ActivityEventListener;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class GoogleOAuthModule extends ReactContextBaseJavaModule implements ActivityEventListener {
    private final ReactApplicationContext reactContext;
    private static final int GOOGLE_SING_IN = 1;
    private Callback mCallback = null;
    private FirebaseAuth mAuth;

    GoogleOAuthModule(ReactApplicationContext context) {
        super(context);
        reactContext = context;
        this.reactContext.addActivityEventListener(this);
        mAuth = FirebaseAuth.getInstance();
    }

    @NonNull
    @Override
    public String getName() {
        return "GoogleOAuthModule";
    }

    @ReactMethod
    public void checkUser(final Callback callback) {
        Activity activity = (MainApplication.getCurrentActivity() == null) ? getCurrentActivity() : MainApplication.getCurrentActivity();
        if (activity == null) {
            callback.invoke("cancel", "No activity available");
            return;
        }
        mCallback = callback;
        FirebaseUser currentUser = mAuth.getCurrentUser();
        getUserInfo(currentUser);
        mCallback = null;
    }

    @ReactMethod
    public void signIn(final Callback callback) {
        try {
            Activity activity = (MainApplication.getCurrentActivity() == null) ? getCurrentActivity() : MainApplication.getCurrentActivity();
            if (activity == null) {
                callback.invoke("cancel", "No activity available");
                return;
            }

            mCallback = callback;

            Intent signInIntent = getGoogleSignInClient(activity).getSignInIntent();
            activity.startActivityForResult(signInIntent, GOOGLE_SING_IN);
        } catch (Exception e) {
            mCallback.invoke("fail", e.getMessage());
        }
    }

    @ReactMethod
    public void signOut(Callback callback) {
        Activity activity = (MainApplication.getCurrentActivity() == null) ? getCurrentActivity() : MainApplication.getCurrentActivity();
        if (activity == null) {
            callback.invoke("cancel", "No activity available");
            return;
        }

        mCallback = callback;

        getGoogleSignInClient(activity).signOut().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                mCallback.invoke("success");
            } else {
                mCallback.invoke("fail", task.getException());
            }
        });
    }

    /**
     * 取得Google登入使用者資料
     *
     * @param user Google登入帳戶
     */
    private void getUserInfo(FirebaseUser user) {
//        if (account != null) {
//            WritableMap response = Arguments.createMap();
//            response.putString("id", account.getId());
//            response.putString("name", account.getDisplayName());
//            response.putString("email", account.getEmail());
//            response.putString("idToken", account.getIdToken());
//            mCallback.invoke("success", response);
        if (user != null) {
            System.out.println("leilei android user: " + user);
        } else {
            mCallback.invoke("cancel", "user is null");
        }
    }

    /**
     * 取得GoogleSignInOptions
     */
    private GoogleSignInClient getGoogleSignInClient(Activity activity) {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("611154343645-nsq9uplo33booriltnm45nt3r3lkoo9t.apps.googleusercontent.com")
                .requestEmail()
                .requestProfile()
                .build();
        return GoogleSignIn.getClient(activity, gso);
    }

    @Override
    public void onActivityResult(Activity activity, int requestCode, int resultCode, @Nullable Intent intent) {
        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (mCallback != null && requestCode == GOOGLE_SING_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(intent);
            try {
                Log.d("GBFTools", "leilei =================: " + task.getResult(ApiException.class));
                firebaseAuthWithGoogle(task.getResult(ApiException.class).getIdToken());
            } catch (ApiException e) {
                Log.e("GBFTools", "leilei fail: " + e);
                mCallback.invoke("fail", e);
                e.printStackTrace();
            }
        }
        mCallback = null;
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d("GBFTools", "leilei signInWithCredential:success");
                        FirebaseUser user = mAuth.getCurrentUser();
                        getUserInfo(user);
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w("GBFTools", "leilei signInWithCredential:failure", task.getException());
                        getUserInfo(null);
                    }
                });
    }

    @Override
    public void onNewIntent(Intent intent) {

    }
}
