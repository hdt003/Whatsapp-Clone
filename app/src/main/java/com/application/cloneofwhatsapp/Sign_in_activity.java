package com.application.cloneofwhatsapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.application.cloneofwhatsapp.Models.Users;
import com.application.cloneofwhatsapp.databinding.ActivitySignInBinding;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;

import java.util.MissingResourceException;

public class Sign_in_activity extends AppCompatActivity {

    private ActivitySignInBinding binding;
    FirebaseAuth auth;//For authentication
    FirebaseDatabase database;//For storing user data in real time database

    GoogleSignInClient gsc;
    GoogleSignInOptions gso;
    ProgressDialog progress;//loading box show

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        //to hide 2nd status bar(app name vadi)
        getSupportActionBar().hide();

        binding = ActivitySignInBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());///generate class for all the xml objects like textview,edittext all

        auth = FirebaseAuth.getInstance();

        database= FirebaseDatabase.getInstance();

        progress = new ProgressDialog(Sign_in_activity.this);
        progress.setTitle("Log In");
        progress.setMessage("We are login to your account");



        binding.signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progress.show();
                auth.signInWithEmailAndPassword(binding.email.getText().toString(), binding.password.getText().toString())
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                progress.hide();
                                if (task.isSuccessful()) {
                                    Intent intent = new Intent(Sign_in_activity.this, MainActivity.class);
                                    startActivity(intent);
                                } else {
                                    Toast.makeText(Sign_in_activity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });

        //it is like binding is class and id's simplified new name is object
        binding.createNewAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //to go to sign up activity from sign in activty
                Intent intent = new Intent(Sign_in_activity.this, Sign_Up_Activity.class);
                startActivity(intent);
            }
        });

        if (auth.getCurrentUser() != null) {
            //to go to main activity activity from sign in activty
            Intent intent = new Intent(Sign_in_activity.this, MainActivity.class);
            startActivity(intent);
        }

        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        // Build a GoogleSignInClient with the options specified by gso.
        gsc = GoogleSignIn.getClient(this, gso);
        GoogleSignInAccount account=GoogleSignIn.getLastSignedInAccount(this);
        if(account!=null)
        {
            Intent intent = new Intent(Sign_in_activity.this, MainActivity.class);
            startActivity(intent);
        }
        binding.btnGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progress.show();
                sign_in();

            }
        });

    }
    int RC_SIGN_IN = 65;//any number
    //For google account next 3 function
    private void sign_in() {
        Intent signInIntent = gsc.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogleAccount(account);

            }
            catch (ApiException e) {
                // The ApiException status code indicates the detailed failure reason.
                // Please refer to the GoogleSignInStatusCodes class reference for more information.
                Log.d("t2", "signInResult:failed code=" + e.getStatusCode());
                Toast.makeText(Sign_in_activity.this, "Authentication failed.",Toast.LENGTH_SHORT).show();
//            updateUI(null);
            }
        }
    }

    private void firebaseAuthWithGoogleAccount(GoogleSignInAccount account)
    {
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(),null);
        auth.signInWithCredential(credential).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                progress.hide();

                Intent intent=new Intent(Sign_in_activity.this,MainActivity.class);
                startActivity(intent);

                FirebaseUser firebaseUser=auth.getCurrentUser();

                String uid=firebaseUser.getUid();
                String email=firebaseUser.getEmail();
//                database.getReference().child("users").child(getCurrentUser()).setValue(firebaseUser.getUid());
                Log.d("taguser","on success user:"+uid);
                Log.d("tagemail","on success email:"+email);

                //storing user to real time database using google account class
                Users user=new Users();//class Users' object user
                user.setUserId(firebaseUser.getUid());
                user.setUserName(firebaseUser.getDisplayName());
                user.setMail(firebaseUser.getEmail());
                user.setProfilePic(firebaseUser.getPhotoUrl().toString());

                database.getReference().child("Users").child(firebaseUser.getUid()).setValue(user);
                //check if user is existing or new
                if(authResult.getAdditionalUserInfo().isNewUser())
                {
                    Toast.makeText(Sign_in_activity.this, "Authentication Successful.",Toast.LENGTH_SHORT).show();
                    Log.d("accountCreate","on account created email:"+email);
                }
                else
                {
                    Toast.makeText(Sign_in_activity.this, "Existing user :"+email,Toast.LENGTH_SHORT).show();
                    Log.d("accountCreate","Existing user :"+email);
                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(Sign_in_activity.this, "Authentication failed.",Toast.LENGTH_SHORT).show();
            }
        });

    }

    //  tap two times on back button to exit the app with time gap between two taps is 2 seco
    boolean doubleBackToExitPressedOnce = false;
    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            moveTaskToBack(true);
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(1);
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
    }
}