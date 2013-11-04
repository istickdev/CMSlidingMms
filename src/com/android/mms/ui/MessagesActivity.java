package com.android.mms.ui;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.SlidingPaneLayout;
import android.support.v4.widget.SlidingPaneLayout.PanelSlideListener;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.android.mms.R;

import java.util.Collection;

public class MessagesActivity extends Activity {
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
                ComposeMessageFragment cmf = (ComposeMessageFragment) getFragmentManager().findFragmentByTag("pane2");
                cmf.setHasOptionsMenu(true);
                cmf.onShow();
                
                getFragmentManager().findFragmentById(R.id.pane1).setHasOptionsMenu(false);
                
                
                Log.d("asdlfkjsdfsdlfkj", "panel closed ***** [mChangeThread] " + mChangeThread);
                if(mChangeThread) {
                    mChangeThread = false;
                    
                    // TODO: move title update to this class
//                    cmf.reloadTitle();
                }
                else {
                    cmf.reloadTitle();
                }
            }

            @Override
            public void onPanelOpened(View view) {
                ActionBar ab = getActionBar();
                ab.setTitle(R.string.app_label);
                ab.setSubtitle(null);
                ab.setDisplayHomeAsUpEnabled(false);
                
                getFragmentManager().findFragmentById(R.id.pane1).setHasOptionsMenu(true);
                ComposeMessageFragment cmf = (ComposeMessageFragment) getFragmentManager().findFragmentByTag("pane2");
                cmf.setHasOptionsMenu(false);
                cmf.onHide();
                
                InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(mPane.getWindowToken(), 0);
                // Some devices may need to use this method ??
//                imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, 0);
            }

            @Override
            public void onPanelSlide(View view, float offset) {
                
            }
        });

        getFragmentManager().beginTransaction().add(R.id.pane1, new ConversationListFragment(), "pane1").commit();
        getFragmentManager().beginTransaction().add(R.id.pane2, new ComposeMessageFragment(), "pane2").commit();
        
        mPane.openPane();
    }
    
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        
        // TODO: this is ugly
        getFragmentManager().findFragmentById(R.id.pane2).onActivityResult(requestCode, resultCode, data);
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
        
        ComposeMessageFragment composeFragment = (ComposeMessageFragment) getFragmentManager().findFragmentByTag("pane2");
        composeFragment.openThread(threadId, true);
        composeFragment.reloadTitle();
        
//        mPane.closePane();
    }
    
    private ComposeMessageFragment getMessageFragment() {
        return (ComposeMessageFragment) getFragmentManager().findFragmentByTag("pane2");
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
}