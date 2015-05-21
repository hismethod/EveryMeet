package kr.ac.kit.views.Activities;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import kr.ac.kit.R;
import kr.ac.kit.views.Dialogs.NameDialog;

@EActivity(R.layout.activity_main)
public class MainActivity extends AppCompatActivity
{
	@ViewById TextView hello;
	private NameDialog dialog;
	
	@AfterInject
	public void doAfterInject()
	{
		dialog = new NameDialog(this, "환영합니다! 성함이 어떻게 되세요?");
	}
	
	@AfterViews
	public void doAfterViews()
	{
		dialog.showDialog();
		dialog.setOnDismissListener(listener);
	}
	
	OnDismissListener listener = new OnDismissListener()
	{
		@Override
		public void onDismiss(DialogInterface di)
		{
			/**
			 * 여기에 사용자의 이름가지고 뭐 어떻게 하는 거 나와야한다
			 * SharedPreference에 저장하던지 DB에 저장하던지 후처리 할 것.
			 */
			hello.setText(dialog.getName());
		}
	};
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
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
