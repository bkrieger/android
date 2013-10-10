package us.happ.android.fragment;

import java.util.HashSet;

import us.happ.android.R;
import us.happ.android.activity.MainActivity;
import us.happ.android.adapter.ContactsAdapter;
import us.happ.android.utils.Storage;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

public class FriendsFragment extends HappFragment{

	private View mView;
	private ListView mListView;
	private Cursor mCursor;
	private MainActivity mContext;
	private HashSet<String> blockedNumbers;
	private ContactsAdapter mListAdapter;

	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		
		mContext = (MainActivity) getActivity();
		setHasOptionsMenu(true);
		
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
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){

		mView = inflater.inflate(R.layout.fragment_friends, null, false);
		
		mListView = (ListView) mView.findViewById(android.R.id.list);
		mListView.setAdapter(mListAdapter);
		mListView.setFastScrollEnabled(true);
		mListView.setVerticalScrollBarEnabled(false);
		mListView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View v, int position, long id) {
            	mListAdapter.check(position);
            }
        });
		
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

}
