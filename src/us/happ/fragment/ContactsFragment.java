package us.happ.fragment;

import java.util.HashSet;

import us.happ.activity.MainActivity;
import us.happ.adapter.ContactsAdapter;
import us.happ.R;
import us.happ.utils.Happ;
import us.happ.utils.SmoothInterpolator;
import us.happ.utils.Storage;
import us.happ.view.ContactsListView;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Canvas;
import android.os.Build.VERSION;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBar;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.view.animation.ScaleAnimation;
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
	private boolean hasChanged;

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
	
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){

		mView = inflater.inflate(R.layout.fragment_contacts, null, false);
		
		mListView = (ContactsListView) mView.findViewById(android.R.id.list);
		
		final ViewGroup header = (ViewGroup) getActivity().getLayoutInflater().inflate(R.layout.list_header_contacts, null, true);
		mListView.addHeaderView(header, null, false);
		counterView = (TextView) header.findViewById(R.id.contacts_counter);
		updateCounter();

		// TODO change to viewstub?
		View hintView = header.findViewById(R.id.contacts_hint);
		if (!Storage.getHintContacts(mContext)){
			header.removeView(hintView);
		} else {
			hintView.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View v) {
					v.setClickable(false);
					fadeSlideUpAnimation(header, v);
					Storage.setHintContacts(mContext, false);
				}
			});
		}
		
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
	
		// For some reason fast scroll doesn't work on kitkat without this line
		if (Happ.hasKitkat) mListView.setFastScrollAlwaysVisible(true);
	
		mListView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View v, int position, long id) {
            	mListAdapter.check(mListAdapter.getTruePosition(position));
            	updateCounter();
            	hasChanged = true;
            }
        });
		
		mListView.setOnScrollListener(mScrollListener);
		
		hasChanged = false;
		
		return mView;
	}
	
	@Override
	public void onViewStateRestored(Bundle savedInstanceState){
		super.onViewStateRestored(savedInstanceState);
		mListView.setSelection(0); // scroll to top
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
	public void onDestroyView(){
		if (hasChanged){
			mContext.startUpdateFriendsService();
		}
		super.onDestroyView();
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
	
	// TODO refactor to another file to be reused
	// Slide up animation for the header
	private void fadeSlideUpAnimation(final ViewGroup container, final View v){
		Animation anim = new AlphaAnimation(1f, 0f);
		anim.setDuration(300);
		anim.setFillAfter(true);
		anim.setFillEnabled(true);
		anim.setAnimationListener(new AnimationListener(){

			@Override
			public void onAnimationEnd(Animation animation) {
				int index = container.indexOfChild(v);
				int width = v.getWidth();
				int height = v.getHeight();
				container.removeView(v);
				final View viewStub = new View(container.getContext());
				container.addView(viewStub, index);
				viewStub.setLayoutParams(new LayoutParams(width, height));
				
				Animation anim = new SlideUpAnimation(viewStub, height);
				anim.setInterpolator(new SmoothInterpolator());
				anim.setDuration(500);
				anim.setAnimationListener(new AnimationListener(){

					@Override
					public void onAnimationEnd(Animation animation) {
						container.removeView(viewStub);
					}

					@Override
					public void onAnimationRepeat(Animation animation) {}

					@Override
					public void onAnimationStart(Animation animation) {}
					
				});
				viewStub.startAnimation(anim);
			}

			@Override
			public void onAnimationRepeat(Animation animation) {}

			@Override
			public void onAnimationStart(Animation animation) {}
			
		});

		v.startAnimation(anim);
	}
	
	class SlideUpAnimation extends Animation {
		private View v;
		private int height;
		
		public SlideUpAnimation(View v, int height) {
			this.v = v;
			this.height = height;
		}
		
		@Override
		 protected void applyTransformation(float interpolatedTime, Transformation t) {
			 LayoutParams lp = (LayoutParams) v.getLayoutParams();
			 lp.height = (int) (height*(1-interpolatedTime));
			 v.setLayoutParams(lp);
	     }
		
		@Override
		public boolean willChangeBounds(){
			return true;
		}
	}

}
