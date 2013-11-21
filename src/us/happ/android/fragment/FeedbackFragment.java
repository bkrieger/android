package us.happ.android.fragment;

import us.happ.android.R;
import us.happ.android.activity.MainActivity;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

public class FeedbackFragment extends HappFragment{

	private MainActivity mContext;
	private ActionBar actionbar;

	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		
		mContext = (MainActivity) getActivity();
		setHasOptionsMenu(true);
		
		// Action bar
		actionbar = mContext.getSupportActionBar();
		
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){

		View v = inflater.inflate(R.layout.fragment_feedback, null, false);
		
		return v;
	}
	
	@Override
	public void onResume(){
		super.onResume();
		if (actionbar != null){
			actionbar.setTitle(getResources().getString(R.string.title_feedback));
			actionbar.setDisplayShowTitleEnabled(true);
		}
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
		menu.clear();
		inflater.inflate(R.menu.feedback, menu);
		super.onCreateOptionsMenu(menu, inflater);
	}
	
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {   
    	switch (item.getItemId()) {        
    	case R.id.action_submit:
	    		submitFeedback();
    			mContext.returnToBoard();
    	  	  return true;
          default:            
        	  return super.onOptionsItemSelected(item);
    	}
    }
	
	private void submitFeedback(){
		mContext.startFeedbackService();
	}
}
