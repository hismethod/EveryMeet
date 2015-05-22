package kr.ac.kit.views.Activities;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import com.rey.material.widget.EditText;

import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ScrollView;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.ViewSwitcher.ViewFactory;
import kr.ac.kit.R;

@EActivity(R.layout.activity_hello)
public class HelloActivity extends AppCompatActivity
{
	@ViewById EditText initNameEditText;
	@ViewById ScrollView scrollView;
	@ViewById TextSwitcher helloSwitcher;
	@ViewById TextView helloSwitchText;
	
	private String enableString = "";
	private String disableString = "";

	private TextWatcher textWatcher = new TextWatcher()
	{
		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count){}
		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after){}
		@Override
		public void afterTextChanged(Editable str)
		{
			if (isNull(str.toString().trim()))
			{
				helloSwitcher.setClickable(false);
				helloSwitcher.setBackgroundColor(Color.parseColor("#EC407A"));
			} else
			{
				helloSwitcher.setClickable(true);
				helloSwitcher.setBackgroundColor(Color.parseColor("#FFFFFF"));
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
		
		helloSwitcher.setInAnimation(AnimationUtils.loadAnimation(this, android.R.anim.fade_in));
		helloSwitcher.setOutAnimation(AnimationUtils.loadAnimation(this, android.R.anim.fade_out));
		helloSwitcher.setFactory(new ViewFactory()
		{
			@Override
			public View makeView()
			{
				helloSwitchText.setTextColor(Color.parseColor(""));
				helloSwitchText.setGravity(Gravity.CENTER_HORIZONTAL);
				return helloSwitchText;
			}
		});
		helloSwitcher.setText(disableString);
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
