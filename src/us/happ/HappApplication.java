package us.happ;

import org.acra.ACRA;
import org.acra.annotation.ReportsCrashes;

import android.app.Application;

@ReportsCrashes(
      formKey = "", // This is required for backward compatibility but not used
      formUri = "http://www.happ.us/dev/err-android"
)
public class HappApplication extends Application {
	
	@Override
	public void onCreate(){
		super.onCreate();
		
		ACRA.init(this);
	}
	
}
