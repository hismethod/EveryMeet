package kr.ac.kit;

import android.app.Activity;
import android.media.AudioManager;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity
{
	/* View ��ü�� */
	private TextView resultView = null;
	private TextView levelView = null;
	private MainController mainController = null;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		mainController = new MainController();
		
		resultView = (TextView) findViewById(R.id.resultView);
		levelView = (TextView) findViewById(R.id.levelView);

		Button recordBtn = (Button) findViewById(R.id.recordBtn);
		Button recordStopBtn = (Button) findViewById(R.id.recordStopBtn);
		Button playBtn = (Button) findViewById(R.id.playBtn);
		Button playStopBtn = (Button) findViewById(R.id.playStopBtn);

		resultView.setMovementMethod(new ScrollingMovementMethod());

		// ���� ���� ��ư
		recordBtn.setOnClickListener(new View.OnClickListener()
		{
			public void onClick(View v)
			{
				mainController.startRecord();
			}
		});

		// ���� ���� ��ư
		recordStopBtn.setOnClickListener(new View.OnClickListener()
		{
			public void onClick(View v)
			{
				mainController.stopRecord(true);
			}
		});

		// ����� �÷��� ��ư
		playBtn.setOnClickListener(new View.OnClickListener()
		{
			public void onClick(View v)
			{
				mainController.playAudio();
			}
		});

		// ����� ��� ���� ��ư
		playStopBtn.setOnClickListener(new View.OnClickListener()
		{
			public void onClick(View v)
			{
				mainController.stopAudio();
			}
		});
	}

	protected void onPause()
	{
		super.onPause();
	}
}
