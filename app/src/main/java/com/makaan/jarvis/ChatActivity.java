package com.makaan.jarvis;

import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Display;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.makaan.R;
import com.makaan.jarvis.message.Message;
import com.makaan.jarvis.ui.ConversationAdapter;
import com.makaan.jarvis.message.*;
import com.makaan.util.AnimUtil;
import com.makaan.util.AppBus;
import com.squareup.otto.Bus;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by sunil on 11/01/16.
 */
public class ChatActivity extends AppCompatActivity {

    //TODO WIP

    private Bus eventBus;
    private ConversationAdapter mAdapter;
    private LinearLayoutManager mLayoutManager;

    @Bind(R.id.conversation_list_view)
    RecyclerView mConRecyclerView;

    @Bind(R.id.btnSend)
    ImageView mButtonSend;

    @Bind(R.id.inputMsg)
    EditText mCompose;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setUpWindow();
        super.onCreate(savedInstanceState);
        setupConversationView(savedInstanceState);

        eventBus = AppBus.getInstance();
    }


    private void setUpWindow() {

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND,
                WindowManager.LayoutParams.FLAG_DIM_BEHIND);

        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.alpha = 1.0f;
        params.dimAmount = 0.4f;
        getWindow().setAttributes(params);

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;

        if (height > width) {
            getWindow().setLayout((int) (width * .9), (int) (height * .9));
        } else {
            getWindow().setLayout((int) (width * .9), (int) (height * .9));
        }
    }

    private void setupConversationView(@Nullable Bundle savedInstanceState){

        setContentView(R.layout.chat_activity);

        ButterKnife.bind(this);

        mAdapter = new ConversationAdapter(ChatActivity.this);
        mLayoutManager = new LinearLayoutManager(ChatActivity.this);
        mLayoutManager.setStackFromEnd(true);

        mConRecyclerView.setLayoutManager(mLayoutManager);
        mConRecyclerView.setAdapter(mAdapter);

        mButtonSend.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                sendMessage(mCompose.getText().toString());
                mCompose.setText("");
                scrollToEnd();

            }
        });

        mCompose.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                scrollToEnd();
                return false;
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            this.moveTaskToBack(true);
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void sendMessage(String text){
        if(TextUtils.isEmpty(text)){
            Message message = new Message();
            message.messageType = MessageType.inText;
            message.message = "Hello!!!";
            mAdapter.addMessage(message);
        }else if("1".equalsIgnoreCase(text)){
            Message message = new Message();
            message.messageType = MessageType.sellerOverView;
            mAdapter.addMessage(message);
        } else{
            Message message = new Message();
            message.messageType = MessageType.outText;
            message.message = text;
            mAdapter.addMessage(message);
        }
    }

    private void scrollToEnd(){
        if(mAdapter.getItemCount()>0){
            mConRecyclerView.scrollToPosition(mAdapter.getItemCount()-1);
        }
    }
}
