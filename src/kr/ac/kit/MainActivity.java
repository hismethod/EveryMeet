package kr.ac.kit;

import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity
{
	private String currentFilePath;
	private StringBuilder dialogStringStack = new StringBuilder();

	/* Media ��ü�� */
//	private Timer timer = new Timer();
//	private MyTimerTask task = null;
	private VolumeViewThread levelThread = null;
//	private AudioManager audioManager;
	private MediaPlayer player = null;
	private Recorder recorder;

	/* View ��ü�� */
	private TextView resultView = null;
	private TextView levelView = null;

	/* HTTP�����νİ�� response ������ */
	private RecognizeResponseListener listener = new RecognizeResponseListener()
	{
		@Override //callback�޼ҵ�
		public void onTaskCompleted(String sentence)
		{
			Log.i("RecognizeResponseListener", "onTaskCompleted() ȣ��");
			dialogStringStack.append(sentence);
			dialogStringStack.append("\n");
			resultView.setText(dialogStringStack.toString());
			
		}
	};

	/* ���ڵ� event �ݹ� ������ */
	private RecorderListener recorderListener = new RecorderListener()
	{
		@Override
		public void onStartRecord()
		{
			Log.i("RecorderListener", "onStartRecord() ȣ��");
//			levelThread = new VolumeViewThread();
//			levelThread.start();
		}

		@Override
		public void onStopRecord()
		{
			Log.i("RecorderListener", "onStopRecord() ȣ��");
//			levelThread.interrupt();
			AsyncDictationHTTPClient asyncDictationHTTPClient = new AsyncDictationHTTPClient(listener);
			asyncDictationHTTPClient.execute(Recorder.getCurrentFilePath());
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

//		audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
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
				startRecord();
			}
		});

		// ���� ���� ��ư
		recordStopBtn.setOnClickListener(new View.OnClickListener()
		{
			public void onClick(View v)
			{
				stopRecord();
				startRecord();
			}
		});

		// ����� �÷��� ��ư
		playBtn.setOnClickListener(new View.OnClickListener()
		{
			public void onClick(View v)
			{
				if (player != null)
				{
					player.stop();
					player.release();
					player = null;
				}

				Toast.makeText(getApplicationContext(), "������ ������ ����մϴ�.", Toast.LENGTH_SHORT).show();
				try
				{
					// ������� �÷��� �ϱ����� MediaPlayer ��ü player�� �����Ѵ�.
					player = new MediaPlayer();

					// ����� ����� ���� ������ġ�� ����
					player.setDataSource(Recorder.getCurrentFilePath());
					// ���� �ִ� ����� ������ ����Ҷ�
					// player.setDataSource(Audio_Url);

					// ����� ����غ�,����
					player.prepare();
					player.start();
				} catch (Exception e)
				{
					Log.e("SampleAudioRecorder", "Audio play failed.", e);
				}
			}
		});

		// ����� ��� ���� ��ư
		playStopBtn.setOnClickListener(new View.OnClickListener()
		{
			public void onClick(View v)
			{
				if (player == null)
					return;

				Toast.makeText(getApplicationContext(), "����� �����Ǿ����ϴ�.", Toast.LENGTH_SHORT).show();

				// ����� ��� ����
				player.stop();

				// ����� ����� �ʿ��� �޸𸮵��� �����Ѵ�
				player.release();
				player = null;
			}
		});
	}

	protected void onPause()
	{
		if (recorder != null)
		{
			recorder.release();
			recorder = null;
		}

		if (player != null)
		{
			player.release();
			player = null;
		}

		super.onPause();
	}

//	class MyTimerTask extends TimerTask
//	{
//		@Override
//		public void run()
//		{
//			handler.sendEmptyMessage(getVolumeLevel());
//		}
//	}
	
	class VolumeViewThread extends Thread implements Runnable
	{
		@Override
		public void run()
		{
			super.run();
			while (recorder.isRecording())
			{
				// �޽��� ������
				Message msg = handler.obtainMessage();

				// �޽��� ID ����
				msg.what = recorder.getVolumeLevel();

				handler.sendMessage(msg);

				try
				{
					Thread.sleep(1000);
				}catch (InterruptedException e){e.printStackTrace();}
			}
		}
	}
	
	Handler handler = new Handler()
	{
		@Override
		public void handleMessage(Message msg)
		{
			updateLevelView(msg.what);
		}
	};

	public void startRecord()
	{
		if(recorder == null)
		{
//			audioManager.startBluetoothSco();
			recorder = Recorder.getRecorder(Recorder.generateFileName());
			Log.i("MainActivity", "���ο� Recorder ����");
		}
		
		recorder.setRecorderListener(recorderListener);
		Log.i("MainActivity", "startRecord() ����");
		recorder.startRecord();

//		task = new MyTimerTask();
//		Log.i("MainActivity", "���ο� TimerTask ����");
//		timer.schedule(task, 100, 200);
	}

	public void stopRecord()
	{
//		task.cancel();
		if(recorder != null)
		{Log.i("MainActivity", "TimerTask ��� �� stopRecord() ����");
//			audioManager.stopBluetoothSco();
			recorder.stopRecord();
			recorder = null;
		}
		dialogStringStack.append("*****�������*****");
		dialogStringStack.append("\n");
		resultView.setText(dialogStringStack.toString());
	}

	public void updateLevelView(int level)
	{
		levelView.setText(""+level);
	}
}
