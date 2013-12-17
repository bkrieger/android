package us.happ.utils;

import java.util.HashMap;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.Build.VERSION;
import android.support.v4.util.LruCache;

public class BitmapCache {
	private LruCache<String, Bitmap> mMemoryCache;
	private HashMap<String, Float> mDecayMap;
	
	public BitmapCache(){
		// Get max available VM memory, exceeding this amount will throw an
	    // OutOfMemory exception. Stored in kilobytes as LruCache takes an
	    // int in its constructor.
	    final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);

	    // Use 1/8th of the available memory for this memory cache.
	    final int cacheSize = maxMemory / 8;

	    mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
	        @Override
	        protected int sizeOf(String key, Bitmap bitmap) {
	            // The cache size will be measured in kilobytes rather than number of items.
	            return (int) (getSizeInBytes(bitmap) / 1024);
	        }
	    };
	    
	    mDecayMap = new HashMap<String, Float>();
	}
	
	@SuppressLint("NewApi")
	public static long getSizeInBytes(Bitmap bitmap) {
	    if(VERSION.SDK_INT >= 12) {
	        return bitmap.getByteCount();
	    } else {
	        return bitmap.getRowBytes() * bitmap.getHeight();
	    }
	}
	
	public void addBitmapToMemoryCache(String key, Bitmap bitmap, float decay) {
//	    if (getBitmapFromMemCache(key) == null) {
	    mMemoryCache.put(key, bitmap);
	    mDecayMap.put(key, decay);
//	    }
	}

	public Bitmap getBitmapFromMemCache(String key, float decay) {
	    Bitmap bitmap = mMemoryCache.get(key);
		if (mDecayMap.containsKey(key) && Math.abs(mDecayMap.get(key) - decay) > 0.05){
			mMemoryCache.remove(key);
			return null;
		}
		return bitmap;
	}
}
