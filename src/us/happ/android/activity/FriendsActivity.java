package us.happ.android.activity;

import java.util.HashSet;

import us.happ.android.R;
import us.happ.android.adapter.ContactsAdapter;
import us.happ.android.utils.Storage;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

public class FriendsActivity extends ActionBarActivity{

	private ListView mListView;
	private ActionBar actionbar;
	private ContactsAdapter mListAdapter;
	private HashSet<String> blockedNumbers;
	private Cursor mCursor;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_friends);
		
		mListView = (ListView) findViewById(android.R.id.list);
		mCursor = getContentResolver().query(Phone.CONTENT_URI, null, null, null, Phone.DISPLAY_NAME);
		blockedNumbers = Storage.getBlockedNumbers(this);
		mListAdapter = new ContactsAdapter(this, mCursor, blockedNumbers);
		mListView.setAdapter(mListAdapter);
		mListView.setFastScrollEnabled(true);
		mListView.setVerticalScrollBarEnabled(false);
		mListView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View v, int position, long id) {
            	onContactItemClick(position); // -1 for header
            }
        });
		
		// Action bar
		actionbar = getSupportActionBar();
		actionbar.setHomeButtonEnabled(true);
		actionbar.setDisplayHomeAsUpEnabled(true);
		actionbar.setTitle(getResources().getString(R.string.title_friends));
		actionbar.setDisplayShowTitleEnabled(true);
		actionbar.setDisplayShowHomeEnabled(false);
		
	}
	
	public void onContactItemClick(int position){
		mListAdapter.check(position);
	}
	
	@Override
	public void onPause(){
		super.onPause();
		Storage.setBlockedNumbers(this, blockedNumbers);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_BACK)) { // Back key pressed
			finish();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
}
