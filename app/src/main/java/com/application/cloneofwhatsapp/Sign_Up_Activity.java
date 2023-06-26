package com.application.cloneofwhatsapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.application.cloneofwhatsapp.Models.Users;
import com.application.cloneofwhatsapp.databinding.ActivitySignUpBinding;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

public class Sign_Up_Activity extends AppCompatActivity {
    private ActivitySignUpBinding binding;//with this we don't need to create id for all the elements/layouts in xml
    private FirebaseAuth auth;//to sign up using email and password
    FirebaseDatabase database;//to store user in firebase
    ProgressDialog progress;//loading box show
    GoogleSignInClient gsc;
    GoogleSignInOptions gso;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        //to hide 2nd status bar(app name vadi)
        getSupportActionBar().hide();

        binding=ActivitySignUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());///generate class for all the xml objects like textview,edittext all

        auth=FirebaseAuth.getInstance();
        database=FirebaseDatabase.getInstance();
        progress =new ProgressDialog(Sign_Up_Activity.this);
        progress.setTitle("Creating Account");
        progress.setMessage("We are creating your account");

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        // Build a GoogleSignInClient with the options specified by gso.
        gsc = GoogleSignIn.getClient(this, gso);
        GoogleSignInAccount account=GoogleSignIn.getLastSignedInAccount(this);

        //it is like binding is class and id's simplified new name is object
        binding.signInText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
//                startActivity(new Intent(Sign_Up_Activity.this,Sign_iUpaAtivity.class));
            }
        });

        //sign up with custom name,email and pass wrd
        binding.signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progress.show();//*****//loading box show
                //giving authentication
                auth.createUserWithEmailAndPassword
                        (binding.email.getText().toString(), binding.password.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    //task is on firebase authentication task
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progress.dismiss();//loading box dismiss
                        if(task.isSuccessful())
                        {
                            Users user=new Users(binding.name.getText().toString(),binding.email.getText().toString(),binding.password.getText().toString());
                            //taking id from firebase authentication
                            String id= Objects.requireNonNull(task.getResult().getUser()).getUid();
                            database.getReference().child("Users").child(id).setValue(user);
                            Toast.makeText(Sign_Up_Activity.this, "User created successfully", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(Sign_Up_Activity.this,MainActivity.class));
                        }
                        else
                        {
                            //exception whatever directly message come which exception is
                            Toast.makeText(Sign_Up_Activity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

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
                Toast.makeText(Sign_Up_Activity.this, "Authentication failed.",Toast.LENGTH_SHORT).show();
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

                Intent intent=new Intent(Sign_Up_Activity.this,MainActivity.class);
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
                    Toast.makeText(Sign_Up_Activity.this, "Authentication Successful.",Toast.LENGTH_SHORT).show();
                    Log.d("accountCreate","on account created email:"+email);
                }
                else
                {
                    Toast.makeText(Sign_Up_Activity.this, "Existing user :"+email,Toast.LENGTH_SHORT).show();
                    Log.d("accountCreate","Existing user :"+email);
                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(Sign_Up_Activity.this, "Authentication failed.",Toast.LENGTH_SHORT).show();
            }
        });

    }
}
