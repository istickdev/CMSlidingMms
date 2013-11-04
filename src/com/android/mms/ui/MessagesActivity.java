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
import android.widget.Toast;

import com.android.mms.R;

public class MessagesActivity extends Activity {
    private SlidingPaneLayout mPane;
    
    private boolean mChangeThread;
    private long mThreadId;
    
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        
        setContentView(R.layout.messages_screen);

        mPane = (SlidingPaneLayout) findViewById(R.id.pane);
        mPane.openPane();
        mPane.setParallaxDistance(400);
        mPane.setPanelSlideListener(new PanelSlideListener() {
            @Override
            public void onPanelClosed(View view) {
                ComposeMessageFragment cmf = (ComposeMessageFragment) getFragmentManager().findFragmentByTag("pane2");
                
                getFragmentManager().findFragmentById(R.id.pane1).setHasOptionsMenu(false);
                getFragmentManager().findFragmentById(R.id.pane2).setHasOptionsMenu(true);
                
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
                getFragmentManager().findFragmentById(R.id.pane2).setHasOptionsMenu(false);
            }

            @Override
            public void onPanelSlide(View view, float offset) {
                
            }
        });

        getFragmentManager().beginTransaction().add(R.id.pane1, new ConversationListFragment(), "pane1").commit();
        getFragmentManager().beginTransaction().add(R.id.pane2, new ComposeMessageFragment(), "pane2").commit();
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
        composeFragment.openThread(threadId);
        composeFragment.reloadTitle();
        
//        mPane.closePane();
    }
    
    public void close() {
        mPane.closePane();
    }
    
    public void open() {
        mPane.openPane();
    }
}