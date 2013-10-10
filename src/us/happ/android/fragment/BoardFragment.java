package us.happ.android.fragment;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import us.happ.android.R;
import us.happ.android.activity.MainActivity;
import us.happ.android.adapter.BoardAdapter;
import us.happ.android.model.Mood;
import us.happ.android.service.ServiceHelper;
import us.happ.android.service.ServiceReceiver;
import us.happ.android.utils.ContactsManager;
import us.happ.android.utils.ContactsManager.FetchContactsListener;
import us.happ.android.utils.Happ;
import us.happ.android.utils.Media;
import us.happ.android.utils.SmoothInterpolator;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.view.animation.TranslateAnimation;
import android.view.animation.Animation.AnimationListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.FrameLayout.LayoutParams;

public class BoardFragment extends HappFragment {

	// constants
	private final static int HIPPO_HEIGHT = 58; // 48 + 10
	private final static int FOOTER_HEIGHT = 48; 
	
	// flags
	private boolean refreshing = false;
	private boolean allowPullToRefresh = true;
	
	private View stripView;
	private View sadHippoView;
	private ListView mListView;
	private View mHeader;
	private View mFooter;
	
	private MainActivity mContext;
	private TextView mFooterText;
	private View mFooterSubmit;
	private View hippoStatic;
	private View hippoDynamic;
	private View mHippo;
	private BoardAdapter mListAdapter;
	
	private int footerHeight;
	private int hippoHeight;
	
	private View mView;
	private ContactsManager mContactsManager;
	private String myMessage;
	private int myTagId;

	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		
		mContext = (MainActivity) getActivity();
		mListAdapter = new BoardAdapter(mContext, 0);
		
		setHasOptionsMenu(true);
		
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		mView = inflater.inflate(R.layout.fragment_board, null, false);
		
		// Sad hippo
        stripView = mView.findViewById(R.id.main_strip);
        sadHippoView = mView.findViewById(R.id.main_sad_hippo);
        Happ.showViewIf(sadHippoView, stripView, mListAdapter.getCount() == 0);
        
        // Set up lists
 		mListView = (ListView) mView.findViewById(android.R.id.list);
 		View header = getActivity().getLayoutInflater().inflate(R.layout.list_header_board, null, true);
 		mListView.addHeaderView(header, null, false);
 		mHeader = header.findViewById(R.id.board_header);
 		// TODO remove the need of viewholder
 		ViewHolder holder = new ViewHolder();
 		holder.tag = (ImageView) mHeader.findViewById(R.id.board_tag);
 		holder.message = (TextView) mHeader.findViewById(R.id.board_message);
 		holder.meWrap = mHeader.findViewById(R.id.board_mewrap);
 		holder.tagLine = mHeader.findViewById(R.id.board_tagline);
 		mHeader.setTag(holder);
 		mHeader.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				mContext.compose();
			}
 		});
 		updateHeader();
 		
 		// footer
		mFooter = mView.findViewById(R.id.actionbar_footer);
		footerHeight = (int) Media.pxFromDp(mContext, FOOTER_HEIGHT);
		mFooterText = (TextView) mFooter.findViewById(R.id.actionbar_footer_text);
		mFooterSubmit = mFooter.findViewById(R.id.actionbar_footer_submit);
		// Absorbs touch events
		mFooter.setOnTouchListener(new OnTouchListener(){
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				return true;
			}
        });
		
		mFooterSubmit.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				final String number = Happ.implode(mListAdapter.getCheckedNumbers(), ";"); 
				Intent smsIntent = new Intent(Intent.ACTION_VIEW);
				smsIntent.setType("vnd.android-dir/mms-sms");
				smsIntent.setData(Uri.parse("smsto:" + number));
		        startActivity(smsIntent);
			}
			
		});
		
		// dancing hippo
		hippoStatic = header.findViewById(R.id.hippo_static);
		hippoDynamic = header.findViewById(R.id.hippo_dynamic);
		
		mHippo = header.findViewById(R.id.hippo);
		
		mListView.setAdapter(mListAdapter);
		
		hippoHeight = (int) Media.pxFromDp(mContext, HIPPO_HEIGHT);
		
		mListView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View v, int position, long id) {
            	onBoardItemClick(position-1); // -1 for header
            }
        });
		
		// pull to refresh
		mListView.setOnTouchListener(pullToRefreshListener);
		
		// Contact Manager
		mContactsManager = ContactsManager.getInstance(mContext);
        
		
		return mView;
	}
	
	class ViewHolder {
		TextView message;
		ImageView tag;
		View meWrap;
		View tagLine;
	}
	
	public void setHeader(String message, int tagId){
		myMessage = message;
		myTagId = tagId;
		updateHeader();
	}
	
	private void updateHeader(){
		
		ViewHolder holder = (ViewHolder) mHeader.getTag();
		
		Happ.showViewIf(holder.tagLine, holder.meWrap, myMessage == null);
		if (myMessage != null){
			holder.message.setText(myMessage);
			holder.tag.setImageBitmap(BitmapFactory.decodeResource(getResources(), Mood.resIdFromTag(myTagId)));
		}
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
		menu.clear();
		inflater.inflate(R.menu.board, menu);
		super.onCreateOptionsMenu(menu, inflater);
	}
	

	@Override
    public boolean onOptionsItemSelected(MenuItem item) {   
    	switch (item.getItemId()) {        
    	case R.id.action_compose:
    	  	  mContext.compose();
    	  	  return true;
          default:            
        	  return super.onOptionsItemSelected(item);
    	}
    }
	
	private void fetch(){
		refreshing = true;
		mContext.startFetchService();
	}
	
	public void onFetchResult(String results){
		refreshing = false;
		resetHeaderPadding(false);
		
		if (results == null) return;
		
		ArrayList<Mood> moods = new ArrayList<Mood>();
		
		try {
			JSONObject jResults = new JSONObject(results);
			JSONObject data = jResults.getJSONObject("data");
			JSONObject me = data.getJSONObject("me");
			JSONArray contacts = data.getJSONArray("contacts");
			
			// Update me
			if (me.has("_id")){
				setHeader(me.getString("message"), me.getInt("tag"));
			} else {
				setHeader(null, 0);
			}
			
			Happ.showViewIf(sadHippoView, stripView, contacts.length() == 0);
			
			JSONObject d;
			Mood m;
			for (int i = 0; i < contacts.length(); i++){
				d = (JSONObject) contacts.get(i);
				
				m = new Mood(
						d.getString("_id"), 
						d.getString("message"), 
						d.getLong("timestamp"), 
						d.getInt("duration"),
						d.getInt("tag")
					);
				moods.add(m);
			}
			
		} catch (JSONException e){}

		mListAdapter.updateData(moods);
	}
	
	@Override
	public void onResume(){
		super.onResume();
		if (mContactsManager.hasFetchedContacts()){
			fetch();
			mContext.showSpinner();
		} else {
			mContactsManager.addFetchContactsListener(new FetchContactsListener(){
				@Override
				public void onContactsFetched() {
					fetchContactsHandler.sendEmptyMessage(0);
				}
			});
		}
	}
	
	// TODO more elegant solution? Make the callback not return from async task
	private Handler fetchContactsHandler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			mContext.setProgressDialog(mContext.getResources().getString(R.string.dialog_retrieve_moods));
			fetch();
		}
	};
	
	public boolean onBackPressed(){
		return closeFooter(true);
	}
	
	/**
	 * PULL TO REFRESH
	 */
	OnTouchListener pullToRefreshListener = new OnTouchListener(){
		
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			if (refreshing) return false;
			
			final int y = (int) event.getY();
			
		    switch (event.getAction()) {
		    	case MotionEvent.ACTION_CANCEL:
		    		allowPullToRefresh = true;
					resetHeaderPadding(true);
					break;
		    	case MotionEvent.ACTION_UP:
		    		allowPullToRefresh = true;
					resetHeaderPadding(true);
					break;
	            case MotionEvent.ACTION_DOWN:
	                mLastMotionY = y;
	                allowPullToRefresh = mListView.getChildAt(0).getTop() == 0;
	                break;
	            case MotionEvent.ACTION_MOVE:
	            	if (allowPullToRefresh && mListView.getChildAt(0).getTop() < 0)
	            		allowPullToRefresh = false;
	            	if (allowPullToRefresh)
	            		applyHeaderPadding(event);
	                break;
		    }
			return false;
		}

	};
	
	private int mLastMotionY;
	private void applyHeaderPadding(MotionEvent ev) {
		// clever hack :D
		if (mListView.getFirstVisiblePosition() > 0 || mListView.getChildAt(0).getTop() != 0) return;
		
		int topPadding = (int) ((ev.getY() - mLastMotionY) / 2);
		
		if (topPadding < 0) topPadding = 0;
		
		LayoutParams lp = (LayoutParams) mHeader.getLayoutParams();
		lp.setMargins(0, topPadding, 0, 0);
		mHeader.setLayoutParams(lp);
		
		lp = (LayoutParams) mHippo.getLayoutParams();
		lp.setMargins(0, -1*hippoHeight + topPadding, 0, 0);
		mHippo.setLayoutParams(lp);

	}
	
	private void resetHeaderPadding(boolean refresh) {

		 LayoutParams lp = (LayoutParams) mHeader.getLayoutParams();
		 int startMargin = lp.topMargin;
		 
		 if (startMargin == 0) return;
		 
		 boolean shouldRefresh = refresh && startMargin >= hippoHeight;
		 int endMargin = shouldRefresh ? hippoHeight : 0;
		 
		 Happ.showViewIf(hippoDynamic, hippoStatic, shouldRefresh);
		 BounceAnimation a = new BounceAnimation(startMargin, endMargin);
		 a.setInterpolator(new SmoothInterpolator());
		 a.setDuration(500);
		 mHeader.startAnimation(a);
		
		 if (shouldRefresh){
			 fetch();
		 }
	 }
	
	private class BounceAnimation extends Animation {
		 private int startMargin;
		 private int endMargin;
		 
		 public BounceAnimation(int startMargin, final int endMargin){
			 this.startMargin = startMargin;
			 this.endMargin = endMargin;
		 }
		 
		 @Override
		 protected void applyTransformation(float interpolatedTime, Transformation t) {
			 
			 int padding = (int) (endMargin + (startMargin-endMargin)*(1 - interpolatedTime));
			 
			 LayoutParams lp = (LayoutParams) mHeader.getLayoutParams();
			 lp.setMargins(0, padding, 0, 0);
			 mHeader.setLayoutParams(lp);
			 
			 lp = (LayoutParams) mHippo.getLayoutParams();
			 lp.setMargins(0, -1*hippoHeight + padding, 0, 0);
			 mHippo.setLayoutParams(lp);
	     }
		 
		 @Override
		 public boolean willChangeBounds(){
			 return true;
		 }
	}
	
	/**
	 * FOOTER
	 */
	public boolean closeFooter(boolean shouldAnimate){
		if (mListAdapter.isCheckboxShown()){
			mListAdapter.hideCheckbox();
			hideFooter(shouldAnimate);
			return true;
		}
		return false;
	}
	
	private void onBoardItemClick(int position){
		boolean wasCheckShown = mListAdapter.isCheckboxShown();
    	mListAdapter.check(position);
    	if (!wasCheckShown && mListAdapter.isCheckboxShown()){
    		showFooter();
    		mFooterSubmit.setEnabled(true);
    	} else if (wasCheckShown && !mListAdapter.isCheckboxShown()){
    		hideFooter(true);
    		mFooterSubmit.setEnabled(false);
    	}
    	
    	mFooterText.setText("Text " + Happ.implode(mListAdapter.getCheckedNames(), ", "));
	}
	
	private void showFooter(){
		mFooter.setVisibility(View.VISIBLE);
		TranslateAnimation a = new TranslateAnimation (0, 0, footerHeight, 0);
		a.setFillEnabled(true);
		a.setFillAfter(true);
	 	a.setInterpolator(new SmoothInterpolator());
	 	a.setDuration(500);
	 	a.setAnimationListener(new AnimationListener(){

			@Override
			public void onAnimationEnd(Animation animation) {
				mListView.setPadding(0, 0, 0, footerHeight);
			}

			@Override
			public void onAnimationRepeat(Animation animation) {}

			@Override
			public void onAnimationStart(Animation animation) {}
	 		
	 	});
	 	mFooter.startAnimation(a);
	}
	
	private void hideFooter(boolean shouldAnimate){
		TranslateAnimation a = new TranslateAnimation (0, 0, 0, footerHeight);
		a.setInterpolator(new SmoothInterpolator());
		a.setFillEnabled(true);
		a.setFillAfter(true);
		a.setAnimationListener(new AnimationListener(){

			@Override
			public void onAnimationEnd(Animation animation) {
				mFooter.setVisibility(View.GONE);
			}

			@Override
			public void onAnimationRepeat(Animation animation) {}

			@Override
			public void onAnimationStart(Animation animation) {}

		});
		int duration = shouldAnimate ? 500 : 0;
	 	a.setDuration(duration);
	 	mFooter.startAnimation(a);
	 	mListView.setPadding(0, 0, 0, 0);
	}
}
