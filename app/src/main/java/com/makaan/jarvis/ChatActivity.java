package com.makaan.jarvis;

import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Display;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.makaan.R;
import com.makaan.analytics.MakaanEventPayload;
import com.makaan.analytics.MakaanTrackerConstants;
import com.makaan.jarvis.event.ChatHistoryEvent;
import com.makaan.jarvis.event.IncomingMessageEvent;
import com.makaan.jarvis.event.OutgoingMessageEvent;
import com.makaan.jarvis.event.SendRequirementEvent;
import com.makaan.jarvis.ui.ConversationAdapter;
import com.makaan.jarvis.message.*;
import com.makaan.util.AppBus;
import com.makaan.util.NetworkUtil;
import com.segment.analytics.Properties;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by sunil on 11/01/16.
 */
public class ChatActivity extends AppCompatActivity {

    private Bus eventBus;
    private ConversationAdapter mAdapter;
    private LinearLayoutManager mLayoutManager;

    @Bind(R.id.conversation_list_view)
    RecyclerView mConRecyclerView;

    @Bind(R.id.jarvis_head)
    ImageView mButtonSend;

    @Bind(R.id.compose)
    EditText mCompose;
    public static final String LAUNCH_PAGE="launchPage";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setUpWindow();
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.zoom_in, R.anim.zoom_out);
        setupConversationView(savedInstanceState);

        eventBus = AppBus.getInstance();
        eventBus.register(ChatActivity.this);

        JarvisClient.getInstance().refreshJarvisSocket();
    }

    @Override
    protected void onDestroy() {
        if(null!=eventBus){
            eventBus.unregister(ChatActivity.this);
        }

        super.onDestroy();
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
            getWindow().setLayout((int) (width * .90), (int) (height * .90));
        } else {
            getWindow().setLayout((int) (width * .90), (int) (height * .90));
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


        mCompose.setOnEditorActionListener(
                new EditText.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView tv, int actionId, KeyEvent event) {
                        if (actionId == EditorInfo.IME_ACTION_SEND) {
                            sendMessage(mCompose.getText().toString());
                            mCompose.setText("");
                            return true;
                        }
                        return false;
                    }
                });

        if(!JarvisClient.getInstance().getFetchingStatus()) {
            bootUpChat();
        }
    }


    private void sendMessage(String text){
        if(TextUtils.isEmpty(text)){
            finish();
        }else{
            if(!NetworkUtil.isNetworkAvailable(this)){
                showError(getResources().getString(R.string.no_network_connection_short));
                return;
            }

            if(TextUtils.isEmpty(JarvisConstants.DELIVERY_ID)){
                showError(getResources().getString(R.string.generic_error));
                return;
            }

            if(JarvisClient.getInstance().getFetchingStatus()){
                showError(getResources().getString(R.string.chat_initializing));
                return;
            }

            Message message = new Message();
            message.messageType = MessageType.outText;
            message.message = text;
            JarvisClient.getInstance().getChatMessages().add(message);
            addChatMessage(message);
            sendMessageToService(message);
        }
    }

    private void scrollToEnd(){
        if(mAdapter.getItemCount()>0){
            mConRecyclerView.scrollToPosition(mAdapter.getItemCount() - 1);
        }
    }

    private void sendMessageToService(SocketMessage message){
        OutgoingMessageEvent event = new OutgoingMessageEvent();
        event.message = message;
        message.timestamp = System.currentTimeMillis();
        eventBus.post(event);
    }

    @Subscribe
    public void onIncomingMessage(IncomingMessageEvent event){
        if(isFinishing()){
            return;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            if(isDestroyed()){
                return;
            }
        }

        final Message message = (Message) event.message;
        addChatMessage(message);
    }

    @Subscribe
    public void onSendRequirementMessage(SendRequirementEvent event){
        if(isFinishing()){
            return;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            if(isDestroyed()){
                return;
            }
        }

        final Message message = (Message) event.message;
        sendMessageToService(message);
    }

    @Subscribe
    public void onChatHistory(ChatHistoryEvent event){
        ChatMessages messages = JarvisClient.getInstance().getChatMessages();
        mAdapter.removeAllItems();
        addChatMessages(messages);
    }

    private void addChatMessage(final Message message){

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mAdapter.addMessage(message);
                scrollToEnd();
            }
        });
    }

    private void addChatMessages(final List<Message> messages){

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mAdapter.addMessages(messages);
                scrollToEnd();
            }
        });
    }


    private void bootUpChat(){
        mAdapter.removeAllItems();
        ChatMessages messages = JarvisClient.getInstance().getChatMessages();
        for(Message message : messages){
            addChatMessage(message);
        }
    }

    private void showError(String message){
        final Snackbar snackbar = Snackbar
                .make(findViewById(android.R.id.content), message, Snackbar.LENGTH_SHORT)
                .setAction("OK", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                    }
                });

        snackbar.show();
    }

    @Override
    public void onBackPressed() {
        String launchPage=null;
        if(getIntent()!=null && getIntent().getExtras()!=null && !TextUtils.isEmpty(getIntent().getExtras().getString(LAUNCH_PAGE))) {
            launchPage = getIntent().getExtras().getString(LAUNCH_PAGE);
        }
        if(launchPage!=null && !TextUtils.isEmpty(launchPage)){
            Properties properties= MakaanEventPayload.beginBatch();
            properties.put(MakaanEventPayload.CATEGORY, MakaanTrackerConstants.Category.buyerMManual);
            properties.put(MakaanEventPayload.LABEL, launchPage);
            MakaanEventPayload.endBatch(this, MakaanTrackerConstants.Action.close);
        }
        super.onBackPressed();
    }

    @OnClick(R.id.close)
    public void onChatCloseClick(){
        String launchPage=null;
        if(getIntent()!=null && getIntent().getExtras()!=null && !TextUtils.isEmpty(getIntent().getExtras().getString(LAUNCH_PAGE))) {
            launchPage = getIntent().getExtras().getString(LAUNCH_PAGE);
        }
        if(launchPage!=null && !TextUtils.isEmpty(launchPage)){
            Properties properties= MakaanEventPayload.beginBatch();
            properties.put(MakaanEventPayload.CATEGORY, MakaanTrackerConstants.Category.buyerMManual);
            properties.put(MakaanEventPayload.LABEL, launchPage);
            MakaanEventPayload.endBatch(this, MakaanTrackerConstants.Action.close);
        }
        finish();
    }

}
