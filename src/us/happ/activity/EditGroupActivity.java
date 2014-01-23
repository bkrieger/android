package us.happ.activity;

import us.happ.R;
import us.happ.adapter.SearchAdapter;
import us.happ.database.GroupTable;
import us.happ.provider.GroupContentProvider;
import us.happ.utils.Happ;
import android.annotation.SuppressLint;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.Contacts;
import android.provider.ContactsContract.Data;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;

public class EditGroupActivity extends ActionBarActivity implements LoaderManager.LoaderCallbacks<Cursor>{
	
	private final static int LOADER_ID = 0x01;
	
	private EditText mSearchET;

	private String mCurFilter;

	private SearchAdapter mSearchAdapter;

	private ListView mSearchList;
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_editgroup);
		getWindow().setBackgroundDrawable(null); // optimization to reduce overdraw
		
		ActionBar actionbar = getSupportActionBar();
		actionbar.setHomeButtonEnabled(true);
		actionbar.setDisplayHomeAsUpEnabled(true);
		actionbar.setTitle("Back");
		actionbar.setDisplayShowTitleEnabled(true);
		actionbar.setDisplayShowHomeEnabled(false);
		
		
		mSearchET = (EditText) findViewById(R.id.search);		
		mSearchET.requestFocus();
		
		mSearchET.addTextChangedListener(new TextWatcher(){

			@Override
			public void afterTextChanged(Editable s) {
				if (s.length() > 1){
					search(s.toString());
					mSearchList.setVisibility(View.VISIBLE);
				} else {
					mSearchList.setVisibility(View.GONE);
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {}
			
		});
		
		mSearchList = (ListView) findViewById(R.id.search_list);
		mSearchAdapter = new SearchAdapter(this, null);
		mSearchList.setAdapter(mSearchAdapter);
//		getSupportLoaderManager().initLoader(LOADER_ID, null, this);

	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.group, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			return true;
		case R.id.action_done:
			finish();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	
	@Override
	public void onBackPressed(){
		if (mSearchList.isShown()){
			mSearchList.setVisibility(View.GONE);
		} else {
			finish();
		}
	}

	private void search(String newText){
		
		String newFilter = !TextUtils.isEmpty(newText) ? newText : null;
		
		// Don't do anything is filter hasn't changed
		if (mCurFilter == null && newFilter == null){
			return;
		}
		
		if (mCurFilter != null && mCurFilter.equals(newFilter)){
			return;
		}
		
		mCurFilter = newFilter;
		mSearchAdapter.setKeyWord(mCurFilter);
		getSupportLoaderManager().restartLoader(LOADER_ID, null, this);
		
	}
	
	@SuppressLint("InlinedApi")
	static final String[] CONTACTS_PROJECTION = new String[] {
        Phone._ID,
        (Happ.hasHoneycomb ? Phone.DISPLAY_NAME_PRIMARY : Phone.DISPLAY_NAME),
        Phone.NUMBER,
        Phone.PHOTO_ID,
        Phone.CONTACT_ID
    };
	
	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		
		Uri contentUri;
		
		if (mCurFilter == null){
			contentUri = Phone.CONTENT_URI;
		} else {
			contentUri = Uri.withAppendedPath(
                Phone.CONTENT_FILTER_URI,
                Uri.encode(mCurFilter));
		}
		
		return new CursorLoader(
				this, 
				contentUri,
				CONTACTS_PROJECTION, 
				null, 
				null, 
				null
		);
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		mSearchAdapter.swapCursor(cursor);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		mSearchAdapter.swapCursor(null);
	}
}
