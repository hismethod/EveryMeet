package kr.ac.kit.views.Activities;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Fullscreen;
import org.androidannotations.annotations.sharedpreferences.Pref;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import kr.ac.kit.R;
import kr.ac.kit.primitive.Singleton;
import kr.ac.kit.primitive.User;
import kr.ac.kit.util.AppSharedPreference_;

@EActivity(R.layout.activity_splash)
@Fullscreen
public class SplashActiivty extends Activity
{
	int SPLASH_TIME = 2000;
	
	@Pref
	AppSharedPreference_ pref;

	@AfterInject
	void init()
	{
		User me = new User(pref.myName().toString());
		Singleton.getInstance().setMe(me);
	}

	@AfterViews
	void onCreate()
	{
		new Handler().postDelayed(new Runnable()
		{
			@Override
			public void run()
			{
				overridePendingTransition(0, android.R.anim.slide_out_right);
				startActivity(new Intent(SplashActiivty.this, LobbyActivity_.class));
				finish();
			}
		}, SPLASH_TIME);
	}
}