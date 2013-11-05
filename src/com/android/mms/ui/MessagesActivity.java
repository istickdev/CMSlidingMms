package com.android.mms.ui;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
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

import java.util.Collection;

public class MessagesActivity extends Activity {
    
    private static String COMPOSE_MESSAGE_TAG = "messages";
    private static String CONVERSATION_LIST_TAG = "list";
    
    private SlidingPaneLayout mPane;
    
    private boolean mChangeThread;
    private long mThreadId;
    
    private boolean mDeleteFromList;
    
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
//        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        
        setContentView(R.layout.messages_screen);

        mPane = (SlidingPaneLayout) findViewById(R.id.pane);
        
        mPane.setParallaxDistance(400);
        mPane.setPanelSlideListener(new PanelSlideListener() {
            @Override
            public void onPanelClosed(View view) {
                ComposeMessageFragment composeMessageFragment = getMessageFragment();
                composeMessageFragment.setHasOptionsMenu(true);
                composeMessageFragment.onShow();
                
                ConversationListFragment clf = getListFragment();
                clf.setHasOptionsMenu(false);
                
                
//                Log.d("asdlfkjsdfsdlfkj", "panel closed ***** [mChangeThread] " + mChangeThread);
//                if(mChangeThread) {
//                    mChangeThread = false;
//                }
//                else {
//                    cmf.reloadTitle();
//                }
            }

            @Override
            public void onPanelOpened(View view) {
                ActionBar ab = getActionBar();
                ab.setTitle(R.string.app_label);
                ab.setSubtitle(null);
                ab.setDisplayHomeAsUpEnabled(false);
                
                getListFragment().setHasOptionsMenu(true);
                ComposeMessageFragment composeMessageFragment = getMessageFragment();
                composeMessageFragment.setHasOptionsMenu(false);
                composeMessageFragment.onHide();
                
                InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(mPane.getWindowToken(), 0);
                // Some devices may need to use this method ??
//                imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, 0);
            }

            @Override
            public void onPanelSlide(View view, float offset) {
                
            }
        });

        getFragmentManager().beginTransaction().add(R.id.left_pane, new ConversationListFragment(), CONVERSATION_LIST_TAG).commit();
        getFragmentManager().beginTransaction().add(R.id.right_pane, new ComposeMessageFragment(), COMPOSE_MESSAGE_TAG).commit();
        
        mPane.openPane();
    }
    
    @Override
    protected void onNewIntent(Intent intent) {
        // Handle intents that occur after the activity has already been created.
        getListFragment().sync();
    }
    
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        
        // TODO: this is ugly
        getMessageFragment().onActivityResult(requestCode, resultCode, data);
    }
    
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mPane.openPane();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    
    public void openThread(long threadId) {
        Intent intent = ComposeMessageFragment.createIntent(this, threadId);
        setIntent(intent);
        mThreadId = threadId;
        mChangeThread = true;
        
        ComposeMessageFragment composeMessageFragment = getMessageFragment();
        composeMessageFragment.openThread(threadId, true);
        composeMessageFragment.reloadTitle();
        
//        mPane.closePane();
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
        mPane.closePane();
    }
    
    public void open() {
        mPane.openPane();
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
    
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                if(!mPane.isOpen()) {
                    mPane.openPane();
                    return true;
                }
                else {
                    return super.onKeyDown(keyCode, event);
                }
        }

        return super.onKeyDown(keyCode, event);
    }
}