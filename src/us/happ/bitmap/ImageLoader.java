package us.happ.bitmap;

import us.happ.adapter.BoardAdapter.AvatarData;
import us.happ.utils.ContactsManager;
import us.happ.utils.Happ;
import us.happ.utils.Media;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.util.Log;

public class ImageLoader extends ImageResizer {
	
	private Context mContext;
	
	public ImageLoader(Context context, int imageHeight) {
		super(context, imageHeight);
		mContext = context;
	}

	@Override
	protected Bitmap processBitmap(Object data) {
		AvatarData d = (AvatarData) data;
		int id = d.id;
		int photoId = d.avatarId;
		Bitmap avatar;
		if (Happ.hasIceCreamSandwich)
			avatar = BitmapFactory.decodeStream(ContactsManager.openDisplayPhoto(id));
		else
			avatar = ContactsManager.fetchThumbnail(photoId);

		Bitmap b =  Media.makeHappAvatar(mContext, avatar, d.decay, Long.parseLong(d.number));
		return b;
	}

}
