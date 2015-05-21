package kr.ac.kit.views.Activities;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import kr.ac.kit.R;
import kr.ac.kit.controller.MainController;

@EActivity(R.layout.activity_test)
public class TestActivity extends AppCompatActivity
{
	/* View 객체들 */
	@ViewById TextView resultView;
	@ViewById TextView levelView;
	
	private MainController mainController;
	
	/* Methods */
	@AfterInject
	public void init()
	{
		mainController = new MainController();
	}
	
	@AfterViews
	public void initView()
	{
		resultView.setMovementMethod(new ScrollingMovementMethod());
	}
	
	@Click(R.id.recordBtn) // 녹음 시작 버튼
	public void onClickRecordBtn()
	{
		mainController.startRecord();
	}
	
	@Click(R.id.recordStopBtn) // 녹음 중지 버튼
	public void onClickRecordStopBtn()
	{
		mainController.stopRecord(true);
	}

	@Click(R.id.playBtn) // 오디오 플레이 버튼
	public void onClickPlayBtn()
	{
		mainController.playAudio();
	}

	@Click(R.id.playStopBtn) // 오디오 재생 중지 버튼
	public void onClickPlayStopBtn()
	{
		mainController.stopAudio();
	}

	protected void onPause()
	{
		super.onPause();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.test, menu);
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
