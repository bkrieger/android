package us.happ.android.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.widget.EditText;

public class ComposeEditText extends EditText {

	public ComposeEditText(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	@Override
	public InputConnection onCreateInputConnection(EditorInfo attrs){
		InputConnection connection = super.onCreateInputConnection(attrs);
		int imeActions = attrs.imeOptions & EditorInfo.IME_MASK_ACTION;
        if ((imeActions & EditorInfo.IME_ACTION_DONE) != 0) {
            // clear the existing action
        	attrs.imeOptions ^= imeActions;
            // set the DONE action
        	attrs.imeOptions |= EditorInfo.IME_ACTION_SEND;
        }
        if ((attrs.imeOptions & EditorInfo.IME_FLAG_NO_ENTER_ACTION) != 0){
        	attrs.imeOptions &= ~EditorInfo.IME_FLAG_NO_ENTER_ACTION;
        }
        return connection;
	}

}
