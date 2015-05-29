package kr.ac.kit.views.Activities;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.sharedpreferences.Pref;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.daimajia.androidanimations.library.YoYo.AnimatorCallback;
import com.nineoldandroids.animation.Animator;

import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.view.Menu;
import android.view.MenuItem;
import kr.ac.kit.R;
import kr.ac.kit.primitive.Singleton;
import kr.ac.kit.primitive.User;
import kr.ac.kit.util.AppSharedPreference_;
import kr.ac.kit.views.Dialogs.NameDialog;

@EActivity(R.layout.activity_lobby)
public class LobbyActivity extends AppCompatActivity
{
	private NameDialog dialog;
	
	/* Views */
	@ViewById CardView lobbyTopCard;
	@ViewById CardView lobbyBottomLeftCard;
	@ViewById CardView lobbyBottomRightCard;
	
	@Pref AppSharedPreference_ sharedPref;
	
	@AfterInject
	public void doAfterInject()
	{
		dialog = new NameDialog(this, "   환영합니다! 성함이 어떻게 되세요?");
	}
	
	@AfterViews
	public void doAfterViews()
	{
		//Singleton.getInstance().getMe().getName().equals("")
		if(true)
		{
			initUserName();
		}
	}
	
	private void initUserName()
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
			User me = Singleton.getInstance().getMe();
			me.setName(dialog.getName());
			Singleton.getInstance().setMe(me);
		}
	};
	
	@Click(R.id.lobbyTopCard)
	void onClickLobbyTopCard()
	{
		YoYo.with(Techniques.Swing).duration(500).playOn(lobbyTopCard);
	}
	
	@Click(R.id.lobbyBottomLeftCard)
	void onClickLobbyBottomLeftCard()
	{
		final Intent intent = new Intent(this, CreateRoomActivity_.class);
		YoYo.with(Techniques.Bounce).duration(500).onEnd(new AnimatorCallback()
		{
			@Override
			public void call(Animator animator)
			{
				startActivity(intent);
			}
		}).playOn(lobbyBottomLeftCard);
	}
	
	@Click(R.id.lobbyBottomRightCard)
	void onClickLobbyBottomRightCard()
	{
		final Intent intent = new Intent(this, RoomListActivity_.class);
		YoYo.with(Techniques.Bounce).duration(500).onEnd(new AnimatorCallback()
		{
			@Override
			public void call(Animator animator)
			{
				startActivity(intent);
			}
		}).playOn(lobbyBottomRightCard);
	}
	

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
