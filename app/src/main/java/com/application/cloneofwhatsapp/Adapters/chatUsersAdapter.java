package com.application.cloneofwhatsapp.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.application.cloneofwhatsapp.ChatDetailActivity;
import com.application.cloneofwhatsapp.Models.Users;
import com.application.cloneofwhatsapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Objects;

//Recyclerview making for chat user of main activity page
public class chatUsersAdapter extends RecyclerView.Adapter<chatUsersAdapter.ViewHolder>{

    ArrayList<Users> list;
    Context context;
    FirebaseAuth auth;
    public chatUsersAdapter(ArrayList<Users> list,Context context)
    {
        this.list=list;
        this.context=context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.sample_show_user,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Users users=list.get(position);//we will get user with its position
        //user photo get from picasso
        Picasso.get().load(users.getProfilePic()).placeholder(R.drawable.userimg).into(holder .image);
        ///////////
        auth=FirebaseAuth.getInstance();
        FirebaseUser firebaseUser=auth.getCurrentUser();
        assert firebaseUser != null;
        if(Objects.equals(firebaseUser.getUid(), users.getUserId()))
        {
            holder.chatUserName.setText(users.getUserName()+" - (ME)");
        }
        else
        {
            holder.chatUserName.setText(users.getUserName());
        }
        ///////////
//        holder.chatUserName.setText(users.getUserName());


        //clicking on user than pass data and go to chatDetailActivity
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(context, ChatDetailActivity.class);
                intent.putExtra("userId",users.getUserId());
                intent.putExtra("userProfilePic",users.getProfilePic());
                //////////
                if(Objects.equals(firebaseUser.getUid(), users.getUserId()))
                {
                    intent.putExtra("userName",users.getUserName()+" - (ME)");
                }
                else{
                    intent.putExtra("userName",users.getUserName());
                }
                /////////
//                intent.putExtra("userName",users.getUserName());
                context.startActivity(intent);
            }
        });
    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    //taking ids of all elements from each item of user
    public class ViewHolder extends RecyclerView.ViewHolder
    {
        ImageView image;
        TextView chatUserName,lastMessage;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        image=itemView.findViewById(R.id.receiver_profile_image);
//            if(auth.getCurrentUser()==null)
//                {
        chatUserName=itemView.findViewById(R.id.chatUserName);
        lastMessage=itemView.findViewById(R.id.lastMessage);
        }
    }
}
