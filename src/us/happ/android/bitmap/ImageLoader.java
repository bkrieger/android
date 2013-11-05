package us.happ.android.bitmap;

import us.happ.android.adapter.BoardAdapter.AvatarData;
import us.happ.android.utils.Media;
import android.content.Context;
import android.graphics.Bitmap;

public class ImageLoader extends ImageResizer {
	
	private Context mContext;
	
	public ImageLoader(Context context, int imageHeight) {
		super(context, imageHeight);
		mContext = context;
	}

	@Override
	protected Bitmap processBitmap(Object data) {
		AvatarData d = (AvatarData) data;
		Bitmap b =  Media.makeHappAvatar(mContext, d.bitmap, d.decay, Long.parseLong(d.number));
		return b;
	}

}
