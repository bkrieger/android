package us.happ.android.fragment;

import java.util.HashSet;

import us.happ.android.R;
import us.happ.android.activity.MainActivity;
import us.happ.android.adapter.ContactsAdapter;
import us.happ.android.utils.Storage;
import us.happ.android.view.ContactsListView;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Canvas;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class ContactsFragment extends HappFragment{

	private View mView;
	private ContactsListView mListView;
	private Cursor mCursor;
	private MainActivity mContext;
	private HashSet<String> blockedNumbers;
	private ContactsAdapter mListAdapter;
	private TextView counterView;
	private ActionBar actionbar;
	private AlertDialog mDialog;

	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		
		mContext = (MainActivity) getActivity();
		setHasOptionsMenu(true);
		
		// TODO cursor loader?
		mCursor = mContext.getContentResolver().query(Phone.CONTENT_URI, null, null, null, Phone.DISPLAY_NAME);
		blockedNumbers = Storage.getBlockedNumbers(mContext);
		mListAdapter = new ContactsAdapter(mContext, mCursor, blockedNumbers);
		
		// Action bar
		actionbar = mContext.getSupportActionBar();
		
		// Alert Dialog
		AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(mContext);
		dialogBuilder.setTitle("Check All");
		dialogBuilder
			.setMessage("Warning: Doing this will select all your contacts as friends")
			.setPositiveButton("Confirm", new DialogInterface.OnClickListener(){
				public void onClick(DialogInterface dialog, int id){
					mListAdapter.resetChecks();
					updateCounter();
				}
			})
			.setNegativeButton("Cancel", new DialogInterface.OnClickListener(){
				public void onClick(DialogInterface dialog, int id){
					dialog.cancel();
				}
			});
		mDialog = dialogBuilder.create();
	}
	
	@Override
	public void onResume(){
		super.onResume();
		if (actionbar != null){
			actionbar.setTitle(getResources().getString(R.string.title_friends));
			actionbar.setDisplayShowTitleEnabled(true);
		}
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){

		mView = inflater.inflate(R.layout.fragment_contacts, null, false);
		
		mListView = (ContactsListView) mView.findViewById(android.R.id.list);
		
		View header = getActivity().getLayoutInflater().inflate(R.layout.list_header_contacts, null, true);
		mListView.addHeaderView(header, null, false);
		counterView = (TextView) header.findViewById(R.id.contacts_counter);
		updateCounter();
		View resetView = header.findViewById(R.id.contacts_reset);
		resetView.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				mDialog.show();
			}
		});
		
		mListView.setAdapter(mListAdapter);
		mListView.setFastScrollEnabled(true);
		mListView.setVerticalScrollBarEnabled(false);
		mListView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View v, int position, long id) {
            	mListAdapter.check(mListAdapter.getTruePosition(position));
            	updateCounter();
            }
        });
		
		mListView.setOnScrollListener(mScrollListener);
		
		return mView;
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
		menu.clear();
		inflater.inflate(R.menu.friends, menu);
		super.onCreateOptionsMenu(menu, inflater);
	}
	
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {   
    	switch (item.getItemId()) {        
    	case R.id.action_friends_done:
    			mContext.returnToBoard();
    	  	  return true;
          default:            
        	  return super.onOptionsItemSelected(item);
    	}
    }
	
	@Override
	public void onPause(){
		super.onPause();
		Storage.setBlockedNumbers(mContext, blockedNumbers);
	}
	
	@Override
	public boolean onBackPressed() {
		return false;
	}
	
	@Override
	public void onDestroy(){
		mCursor.close();
		super.onDestroy();
	}
	
	public void updateCounter(){
		counterView.setText(mListAdapter.getTotalChecked() + "/" + mListAdapter.getTotalContacts());
	}
	
	OnScrollListener mScrollListener = new OnScrollListener(){

		private int currItem = -1;

		@Override
		public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
			
			if (firstVisibleItem == 0){
				mListView.enableOverlay(false);
				return;
			}
			
			mListView.enableOverlay(true);
			
			if (mListAdapter.isPositionASection(firstVisibleItem) && mListView.getChildAt(1)!=null){
				
				if (currItem != firstVisibleItem){
					mListView.setSectionText(mListAdapter.getSectionTextForPosition(firstVisibleItem - 1));
				}
				
				int itemTop = mListView.getChildAt(1).getTop();
				int sectionOverlayHeight = mListView.getSectionHeight();
				int marginTop = 0;
				if (itemTop < sectionOverlayHeight){
					marginTop = itemTop - sectionOverlayHeight;
				}
				mListView.setSectionTop(marginTop);
			} else {
				if (currItem != firstVisibleItem){
					mListView.setSectionTop(0);
					mListView.setSectionText(mListAdapter.getSectionTextForPosition(firstVisibleItem - 1));
				}
			}

			currItem = firstVisibleItem;
		}

		@Override
		public void onScrollStateChanged(AbsListView view, int scrollState) {
			
		}
		
	};

}
