package kr.ac.kit.views.Activities;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import com.skyfishjy.library.RippleBackground;

import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import kr.ac.kit.R;
import kr.ac.kit.controller.MainController;


@EActivity(R.layout.activity_room)
public class RoomActivity extends AppCompatActivity
{
	@ViewById RippleBackground rippleBackground;
	@ViewById ImageView micImageView;
	private boolean nowRecording; 
	private MainController mainController;
	
	@AfterInject
	void init()
	{
		nowRecording = false;
		mainController = new MainController();
	}
	
	@AfterViews
	void onCreate()
	{
		
	}
	
	@Click(R.id.micImageView)
	void onClickMicBtn()
	{
		if(!nowRecording)
		{
			nowRecording = true;
			rippleBackground.startRippleAnimation();
			mainController.startRecord();
		}
		else
		{
			nowRecording = false;
			rippleBackground.stopRippleAnimation();
			mainController.stopRecord(true);
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.room, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings)
		{
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
