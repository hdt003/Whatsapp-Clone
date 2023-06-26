package com.application.cloneofwhatsapp.Adapters;

import android.content.Context;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.application.cloneofwhatsapp.Models.MessagesModel;
import com.application.cloneofwhatsapp.R;
import com.google.firebase.auth.FirebaseAuth;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class InnerChatAdapter extends RecyclerView.Adapter{

    ArrayList<MessagesModel> list;
    Context context;
    int SENDER_VIEW_TYPE=1;
    int RECEIVER_VIEW_TYPE=2;
    public InnerChatAdapter(ArrayList<MessagesModel> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @Override
    public int getItemViewType(int position) {
        //checking if sender's id or receiver's id message and set integer values accordingly

        if(list.get(position).getuId().equals(FirebaseAuth.getInstance().getUid()))
        {
            return SENDER_VIEW_TYPE;
        }
        else{
            return RECEIVER_VIEW_TYPE;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //setting views according to sending or receiving
        if(viewType==SENDER_VIEW_TYPE)
        {
            View view= LayoutInflater.from(context).inflate(R.layout.sample_sender,parent,false);
            return new SenderViewHolder(view);
        }
        else
        {
            View view= LayoutInflater.from(context).inflate(R.layout.sample_receiver,parent,false);
            return new ReceiverViewHolder(view);
        }
//        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(InnerChatAdapter.this));
//        ReceiverViewHolder.setReverseLayout(true);


    }



    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        MessagesModel messagesModel=list.get(position);

        if(holder.getClass()==SenderViewHolder.class)
        {
            ((SenderViewHolder)holder).senderMsg.setText(messagesModel.getMessage());
            //time not working!
            //Timestamp to HH:mm:ss format
            Long Timestamp =messagesModel.getTimeStamp();
            String time = DateUtils.formatDateTime(context, Timestamp, DateUtils.FORMAT_SHOW_TIME);
            ((SenderViewHolder)holder).senderTime.setText(time);

        }
        else
        {
            ((ReceiverViewHolder)holder).receiverMsg.setText(messagesModel.getMessage());
            //time not working!
            Long Timestamp =messagesModel.getTimeStamp();
            String time = DateUtils.formatDateTime(context, Timestamp, DateUtils.FORMAT_SHOW_TIME);
            ((ReceiverViewHolder)holder).receiverTime.setText(time);

        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }



    public class ReceiverViewHolder extends RecyclerView.ViewHolder{
        TextView receiverMsg,receiverTime;
        public ReceiverViewHolder(@NonNull View itemView) {
            super(itemView);
        receiverMsg=itemView.findViewById(R.id.receiverText);
        receiverTime=itemView.findViewById(R.id.receiverTime);
        }
    }

    public class SenderViewHolder extends RecyclerView.ViewHolder{
        TextView senderMsg,senderTime;
        public SenderViewHolder(@NonNull View itemView) {
            super(itemView);
            senderMsg=itemView.findViewById(R.id.senderText);
            senderTime=itemView.findViewById(R.id.senderTime);
        }
    }

}
