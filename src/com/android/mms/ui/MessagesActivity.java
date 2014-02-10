package com.android.mms.ui;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SlidingPaneLayout;
import android.support.v4.widget.SlidingPaneLayout.PanelSlideListener;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.android.mms.R;
import com.android.mms.data.Conversation;

import java.util.Collection;

public class MessagesActivity extends Activity implements ComposeMessageFragment.PaneController {
    
    private static String COMPOSE_MESSAGE_TAG = "messages";
    private static String CONVERSATION_LIST_TAG = "list";
    
    private SlidingPaneLayout mSlidingPane;
    
    private long mThreadId;
    
    private boolean mDeleteFromList;
    
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_UNCHANGED);
      
        setContentView(R.layout.messages_screen);
        
        mSlidingPane = (SlidingPaneLayout) findViewById(R.id.pane);
        mSlidingPane.setParallaxDistance(400);
        mSlidingPane.setPanelSlideListener(new PanelSlideListener() {
            @Override
            public void onPanelClosed(View view) {
                onShowConversation();
            }

            @Override
            public void onPanelOpened(View view) {
                onShowConversationList();

                // Some devices may need to use this method ??
//                imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, 0);
            }

            @Override
            public void onPanelSlide(View view, float offset) {
                
            }
        });
        
        Intent intent = getIntent();
        String action = intent.getAction();
        ComposeMessageFragment cmf = (ComposeMessageFragment) getFragmentManager().findFragmentByTag(COMPOSE_MESSAGE_TAG);
        cmf.setPaneController(this);
        
        if(savedInstanceState != null) {
            
        } else {
            if(action == null || action.equals(Intent.ACTION_MAIN)) {
                mSlidingPane.openPane();
                cmf.setShouldHaveFocus(false);
            }
        }
        
//        getMessageFragment().setIntent(getIntent());

//        getFragmentManager().beginTransaction().add(R.id.left_pane, new ConversationListFragment(), CONVERSATION_LIST_TAG).commit();
//        getFragmentManager().beginTransaction().add(R.id.right_pane, new ComposeMessageFragment(), COMPOSE_MESSAGE_TAG).commit();
        
//        mSlidingPane.openPane();
    }
    
    private void initActionBar() {
        ActionBar ab = getActionBar();
        ab.setTitle(R.string.app_label);
        ab.setSubtitle(null);
        ab.setDisplayHomeAsUpEnabled(false);
    }
    
    public void onResume() {
        super.onResume();
        
        // Handle intents that occur after the activity has already been created.
        getListFragment().sync();
        
        
//        mSlidingPane.closePaneNoAnimation();
        
        Intent intent = getIntent();
        String action = intent.getAction();
        ComposeMessageFragment cmf = (ComposeMessageFragment) getFragmentManager().findFragmentByTag(COMPOSE_MESSAGE_TAG);
        cmf.setPaneController(this); //TODO don't think I need this
//        if(mSlidingPane.isOpen()) {
//            cmf.setShouldHaveFocus(false);
//        } else {
//            cmf.setShouldHaveFocus(true);
//        }
        
        if(action != null && (action.equals(Intent.ACTION_VIEW) || action.startsWith("android.intent.action.SEND"))) {
            log("3we are setting HAS FOCUS");
//            cmf.setShouldHaveFocus(true);
            onShowConversation();
            Conversation conversation = Conversation.get(this, intent.getData(), false);
            mThreadId = conversation.getThreadId();
            cmf.openThread(intent, false);
        }
        
        boolean ex = (intent.getExtras() != null);
        log("[onResume] " + intent.getAction() + "\n" + intent.getDataString() +
                "\n" + "hasex: " + ex);
        Log.d("Mms-------------", intent.toString());
        Log.d("Mms-------------", intent.toURI());
        if(ex) {
            log("extra text: " + intent.getExtras().getString(Intent.EXTRA_TEXT));
        }
        
        
//        String action = getIntent().getAction();
//        if(action != null && !action.equals(Intent.ACTION_MAIN)) {
//            getMessageFragment().openThread(getIntent(), true);
//        }
        
    }
    
    public void onPause() {
        super.onPause();
    }
    
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        
        // TODO: this is ugly
        getMessageFragment().onActivityResult(requestCode, resultCode, data);
    }
    
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mSlidingPane.openPane();
                initActionBar();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    
    public void openThread(long threadId) {
        Intent intent = ComposeMessageFragment.createIntent(this, threadId);
        if(intent == null) {
            log("created intent is null!");
        }
//        setIntent(intent);
        
        ComposeMessageFragment composeMessageFragment = getMessageFragment();
        composeMessageFragment.setShouldHaveFocus(true);
        // If we are opening a thread that is already open, we will just show the pane
        // and load message content. If threadId is zero, we are creating a new message,
        // so we want to just clear the current conversation no matter what (even if there
        // is no conversation) to avoid text getting uncleared.
        if(mThreadId == threadId && threadId != 0) {
            mSlidingPane.closePane();
            composeMessageFragment.reloadTitle();
        }
        else {
            composeMessageFragment.openThread(intent, true);
        }
        mThreadId = threadId;
        
//        composeMessageFragment.openThread(threadId, true);
//        composeMessageFragment.reloadTitle();
        
//        mSlidingPane.closePaneNoAnimation();
        
//        mPane.closePane();
    }
    
    private void onShowConversationList() {
        initActionBar();
        getListFragment().setHasOptionsMenu(true);
        ComposeMessageFragment composeMessageFragment = getMessageFragment();
        composeMessageFragment.setShouldHaveFocus(false);
        composeMessageFragment.setHasOptionsMenu(false);
        composeMessageFragment.onHide();
        
        InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mSlidingPane.getWindowToken(), 0);
    }
    
    private void onShowConversation() {
        ComposeMessageFragment composeMessageFragment = getMessageFragment();
        composeMessageFragment.setShouldHaveFocus(true);
        composeMessageFragment.setHasOptionsMenu(true);
        composeMessageFragment.onShow();
        
        ConversationListFragment clf = getListFragment();
        clf.setHasOptionsMenu(false);
    }
    
    private ComposeMessageFragment getMessageFragment() {
        return (ComposeMessageFragment) getFragmentManager().findFragmentByTag(COMPOSE_MESSAGE_TAG);
    }
    
    private ConversationListFragment getListFragment() {
        return (ConversationListFragment) getFragmentManager().findFragmentByTag(CONVERSATION_LIST_TAG);
    }
    
    public void onThreadDelete(Collection<Long> threadIds) {
        if(threadIds.contains(mThreadId)) {
        }
    }
    
    public void close() {
        mSlidingPane.closePane();
    }
    
    public void open() {
        mSlidingPane.openPane();
    }
    
    public void setDeleteFromList(boolean deleteFromList) {
        mDeleteFromList = deleteFromList;
    }
    
    public boolean getDeleteFromList() {
        return mDeleteFromList;
    }
    
    public long getThreadId() {
        return mThreadId;
    }
    
    protected void onNewIntent(Intent newIntent) {
        super.onNewIntent(newIntent);
        this.setIntent(newIntent);
        
    }
    
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                if(!mSlidingPane.isOpen()) {
                    open();
                    return true;
                }
                else {
                    return super.onKeyDown(keyCode, event);
                }
                
            default:
                return getMessageFragment().onKeyDown(keyCode, event);
        }
    }
    
    void log(String msg) {
        Log.d("Mms   ____", msg);
//        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}