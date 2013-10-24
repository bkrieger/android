package us.happ.android.fragment;

import java.util.HashSet;

import us.happ.android.R;
import us.happ.android.activity.MainActivity;
import us.happ.android.adapter.ContactsAdapter;
import us.happ.android.utils.Storage;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.database.Cursor;
import android.graphics.Canvas;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
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
	private ListView mListView;
	private Cursor mCursor;
	private MainActivity mContext;
	private HashSet<String> blockedNumbers;
	private ContactsAdapter mListAdapter;
	private View sectionOverlay;
	private TextView sectionOverlayText;
	private TextView counterView;

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
//		actionbar = getSupportActionBar();
//		actionbar.setHomeButtonEnabled(true);
//		actionbar.setDisplayHomeAsUpEnabled(true);
//		actionbar.setTitle(getResources().getString(R.string.title_friends));
//		actionbar.setDisplayShowTitleEnabled(true);
//		actionbar.setDisplayShowHomeEnabled(false);
		
	}
	
	@Override
	public void onAttach(Activity activity){
		super.onAttach(activity);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){

		mView = inflater.inflate(R.layout.fragment_contacts, null, false);
		
		mListView = (ListView) mView.findViewById(android.R.id.list);
		
		View header = getActivity().getLayoutInflater().inflate(R.layout.list_header_contacts, null, true);
		mListView.addHeaderView(header, null, false);
		counterView = (TextView) header.findViewById(R.id.contacts_counter);
		updateCounter();
		View resetView = header.findViewById(R.id.contacts_reset);
		resetView.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				mListAdapter.resetChecks();
				updateCounter();
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
		
		sectionOverlay = mView.findViewById(R.id.contact_section_overlay);
		sectionOverlayText = (TextView) sectionOverlay.findViewById(R.id.contact_section_overlay_text);
		
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

		private int sectionOverlayHeight = 0;
		private int currItem = -1;

		@Override
		public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
			if (firstVisibleItem == 0){
				sectionOverlay.setVisibility(View.GONE);
				return;
			}
			
			sectionOverlay.setVisibility(View.VISIBLE);
			
			if (mListAdapter.isPositionASection(firstVisibleItem) && mListView.getChildAt(1)!=null){
				
				if (currItem != firstVisibleItem){
					sectionOverlayText.setText(mListAdapter.getSectionTextForPosition(firstVisibleItem - 1));
				}
				
				int itemTop = mListView.getChildAt(1).getTop();
				if (sectionOverlayHeight == 0) sectionOverlayHeight = sectionOverlay.getHeight();
				int marginTop = 0;
				if (itemTop < sectionOverlayHeight){
					marginTop = itemTop - sectionOverlayHeight;
				}
				FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) sectionOverlay.getLayoutParams();
				lp.setMargins(0, marginTop, 0, 0);
				sectionOverlay.setLayoutParams(lp);
			} else {
				if (currItem != firstVisibleItem){
					FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) sectionOverlay.getLayoutParams();
					lp.setMargins(0, 0, 0, 0);
					sectionOverlay.setLayoutParams(lp);
					sectionOverlayText.setText(mListAdapter.getSectionTextForPosition(firstVisibleItem - 1));
				}
			}

			currItem = firstVisibleItem;
		}

		@Override
		public void onScrollStateChanged(AbsListView view, int scrollState) {
			
		}
		
	};

}
