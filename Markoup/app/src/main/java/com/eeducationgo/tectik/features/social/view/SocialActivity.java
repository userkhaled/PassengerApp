package com.eeducationgo.tectik.features.social.view;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.databinding.DataBindingUtil;

import com.eeducationgo.tectik.R;
import com.eeducationgo.tectik.databinding.ActivitySocialBinding;
import com.eeducationgo.tectik.features.social.presenter.SocialPresenter;
import com.eeducationgo.tectik.util.BaseActivity;
import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class SocialActivity extends BaseActivity implements SocialView {

    private ActivitySocialBinding binding;
    private FirebaseAuth auth;
    private SocialPresenter presenter;
    private CallbackManager callbackManager;
    private GoogleSignInClient mGoogleSignInClient;
    private static final String TAG = "SocialActivity";
    private static final int RC_SIGN_IN = 9001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);
        binding = DataBindingUtil.setContentView(SocialActivity.this, R.layout.activity_social);


        init();
        initLisetenr();
    }

    private void initLisetenr() {
        registerBtn();
        fbBtn();
        googleBtn();
    }

    private void fbBtn() {
        binding.buttonFb.setOnClickListener(v -> {
            setLoginManager();
        });
    }

    private void setLoginManager() {
        presenter.LoginManager(callbackManager , auth);
    }

    private void registerBtn() {
        binding.buttonNextEmailRegisteration.setOnClickListener(v -> presenter.validationInput(binding.inputUserEmail, binding.inputPassword, auth));

    }

    private void init() {
        presenter = new SocialPresenter(this, this);
        auth = FirebaseAuth.getInstance();
        callbackManager = CallbackManager.Factory.create();

        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        // [END config_signin]
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

    }

    private void googleBtn() {
        binding.buttonGoogle.setOnClickListener(v -> signIn());
    }

    private void authByGoogle(String idToken) {
        presenter.authByGoogle(idToken, auth);
    }


    // [START signin]
    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
    // [END signin]

    private void signOut() {
        // Firebase sign out
        auth.signOut();
        // Google sign out
        mGoogleSignInClient.signOut().addOnCompleteListener(this,
                task -> { });
    }

    @Override
    public void showErrorMessage(String message) {
        snackErrorShow(binding.getRoot(), message);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Log.d(TAG, "firebaseAuthWithGoogle:" + account.getId());
                authByGoogle(account.getIdToken());
            } catch (ApiException e) {
                Log.w(TAG, "Google sign in failed", e);
            }
        }
    }

}