package com.application.cloneofwhatsapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.application.cloneofwhatsapp.Adapters.InnerChatAdapter;
import com.application.cloneofwhatsapp.Models.MessagesModel;
import com.application.cloneofwhatsapp.databinding.ActivityChatDetailBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;

public class ChatDetailActivity extends AppCompatActivity {
    ActivityChatDetailBinding binding;
    FirebaseDatabase database;
    FirebaseAuth auth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_detail);
        getSupportActionBar().hide();
        //binding
        binding=ActivityChatDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        auth=FirebaseAuth.getInstance();
        database=FirebaseDatabase.getInstance();
        //sender id firebase authentication mathi lavya
        final String senderID=auth.getUid();
        //putExtra use karine chatUserAdapter mathi receiver no data aa activity ma lavya
        String receiverID=getIntent().getStringExtra("userId");
        String receiverProfilePic=getIntent().getStringExtra("userProfilePic");
        String receiverUserName=getIntent().getStringExtra("userName");

        //back Button
        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });


        binding.receiverUserName.setText(receiverUserName);
        //get profile Image from picasso storage with id in string receiverProfilePic
        Picasso.get().load(receiverProfilePic).placeholder(R.drawable.userimg).into(binding.receiverProfileImage);

        final ArrayList<MessagesModel> list=new ArrayList<>();
        final InnerChatAdapter chatAdapter=new InnerChatAdapter(list,this);


        binding.RecyclerView.setAdapter(chatAdapter);

        LinearLayoutManager layoutManager=new LinearLayoutManager(this);
        binding.RecyclerView.setLayoutManager(layoutManager);

        /////////

        final String senderRoom=senderID+receiverID;
        final String receiverRoom=receiverID+senderID;

        //////changed to show all recycler view when main activity created

    /////////////

        database.getReference().child("chats").child(senderRoom).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //to show one chat at time
                list.clear();
                //data tya sudhi levano database mathi k jyare sudhi children aavata jay //for each loop
                for(DataSnapshot snapshot1:snapshot.getChildren())
                {
//                            setting data from message model to recycler view
                    MessagesModel model=snapshot1.getValue(MessagesModel.class);
                    list.add(model);
                }
                chatAdapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        binding.sendMessageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //on click start
                String message=binding.enterMessageBox.getText().toString();
                long time=new Date().getTime();
//                Toast.makeText(ChatDetailActivity.this, "time ="+time, Toast.LENGTH_SHORT).show();
                //message getting pass to class MessageModel
                final MessagesModel model=new MessagesModel(senderID,message,time);
                //setting enterMessagebox text empty after sending button click
                binding.enterMessageBox.setText("");

                database.getReference().child("chats").child(senderRoom).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        //to show one chat at time
                        list.clear();
                        //data tya sudhi levano database mathi k jyare sudhi children aavata jay //for each loop
                        for(DataSnapshot snapshot1:snapshot.getChildren())
                        {
//                            setting data from message model to recycler view
                            MessagesModel model=snapshot1.getValue(MessagesModel.class);
                            list.add(model);
                        }
                        chatAdapter.notifyDataSetChanged();
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });



                //real time database storing chats //push will add new folder named with timestemp converted in string
                database.getReference().child("chats").child(senderRoom).push().setValue(model).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        database.getReference().child("chats").child(receiverRoom).push().setValue(model).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {

                            }
                        });
                    }
                });

                //on click over
            }
        });
    }
}