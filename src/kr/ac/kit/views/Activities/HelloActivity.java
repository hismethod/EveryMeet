package kr.ac.kit.views.Activities;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import com.rey.material.widget.EditText;

import android.graphics.Color;
import android.graphics.Rect;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ScrollView;
import android.widget.TextView;
import kr.ac.kit.R;

@EActivity(R.layout.activity_hello)
public class HelloActivity extends AppCompatActivity
{
	@ViewById EditText initNameEditText;
	@ViewById ScrollView scrollView;
	@ViewById TextView helloSwitchText;
	@ViewById CardView cardViewSave;
	
	private String enableString = "어플리케이션 시작!";
	private String disableString = "성함을 입력하시면\n입장하실 수 있어요";
	
	Animation fadeIn;

	private TextWatcher textWatcher = new TextWatcher()
	{
		int flag = 3;
		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count){}
		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after){}
		@Override
		public void afterTextChanged(Editable str)
		{
			if (isNull(str.toString().trim()))
			{
				if(flag == 0)
					return;
				helloSwitchText.setText(disableString);
				helloSwitchText.startAnimation(fadeIn);
				//helloSwitchText.setClickable(false);
				//helloSwitchText.setTextColor(Color.parseColor("#FFFFFF"));
				flag = 0;
			} else
			{
				if(flag == 1)
					return;
				helloSwitchText.setText(enableString);
				helloSwitchText.startAnimation(fadeIn);
				//helloSwitchText.setClickable(true);
				//helloSwitchText.setTextColor(Color.parseColor("#FFFFFF"));
				flag = 1;
			}
		}
	};

	@AfterInject
	public void doAfterInject()
	{
	}

	@AfterViews
	public void doAfterViews()
	{
		helloSwitchText.setFocusable(true);
		helloSwitchText.setClickable(true);
		helloSwitchText.setFocusableInTouchMode(true);
		initNameEditText.setFocusableInTouchMode(true);
		initNameEditText.addTextChangedListener(textWatcher);
		initNameEditText.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener()
		{
			@Override
			public void onGlobalLayout()
			{
				if (isKeyboardShown(initNameEditText.getRootView()))
				{
					scrollDown();
				}
			}
		});
		fadeIn = AnimationUtils.loadAnimation(this,android.R.anim.fade_in);
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

	/*
	 * HERE ARE PRIAVTE METHODS
	 */
	private void scrollDown()
	{
		scrollView.post(new Runnable()
		{
			@Override
			public void run()
			{
				scrollView.scrollTo(0, 100);
			}
		});
	}

	private boolean isKeyboardShown(View rootView)
	{
		/*
		 * 128dp = 32dp * 4, minimum button height 32dp and generic 4 rows soft
		 * keyboard
		 */
		final int SOFT_KEYBOARD_HEIGHT_DP_THRESHOLD = 128;

		Rect r = new Rect();
		rootView.getWindowVisibleDisplayFrame(r);
		DisplayMetrics dm = rootView.getResources().getDisplayMetrics();
		/*
		 * heightDiff = rootView height - status bar height (r.top) - visible
		 * frame height (r.bottom - r.top)
		 */
		int heightDiff = rootView.getBottom() - r.bottom;
		/* Threshold size: dp to pixels, multiply with display density */
		boolean isKeyboardShown = heightDiff > SOFT_KEYBOARD_HEIGHT_DP_THRESHOLD * dm.density;

		return isKeyboardShown;
	}

	private static boolean isNull(String query)
	{
		if (query.isEmpty() || query.equals(null))
		{
			return true;
		} else
			return false;
	}

}
