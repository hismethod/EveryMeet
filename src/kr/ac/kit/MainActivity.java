package kr.ac.kit;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

class MainController
{
	private StringBuilder dialogStringStack = new StringBuilder();
	
	private NetworkController networkHandler = new NetworkController();

	/* Media ��ü�� */
	private Timer timer = new Timer();
	private MyTimerTask task = null;
	private VolumeViewThread levelThread = null;
//	private AudioManager audioManager;
	private MediaPlayer player = null;
	private Recorder recorder;

	/* HTTP�����νİ�� response ������ */
	private RecognizeResponseListener listener = new RecognizeResponseListener()
	{
		@Override //callback�޼ҵ�
		public void onTaskCompleted(String sentence)
		{
			Log.i("RecognizeResponseListener", "onTaskCompleted() ȣ��");
			dialogStringStack.append(sentence);
			dialogStringStack.append("\n");
		}
	};

	/* ���ڵ� event �ݹ� ������ */
	private RecorderListener recorderListener = new RecorderListener()
	{
		@Override
		public void onStartRecord()
		{
			Log.i("RecorderListener", "onStartRecord() ȣ��");
		}

		@Override
		public void onStopRecord()
		{
			Log.i("RecorderListener", "onStopRecord() ȣ��");
			AsyncDictationHTTPClient asyncDictationHTTPClient = new AsyncDictationHTTPClient(listener);
			asyncDictationHTTPClient.execute(Recorder.getCurrentFilePath());
			/*
			 * execute�� �����
			 * RecognizeResponseListner�� onTaskCompleted �ݹ� �޼ҵ� ȣ��
			 */
		}
	};
	
	public String getDialogue()
	{
		return dialogStringStack.toString();
	}
	
	public void startRecord()
	{
		if(recorder == null)
		{
			recorder = Recorder.getRecorder(Recorder.generateFileName());
			Log.i("MainActivity", "���ο� Recorder ����");
		}
		
		recorder.setRecorderListener(recorderListener);
		Log.i("MainActivity", "startRecord() ����");
		recorder.startRecord();

		task = new MyTimerTask();
		Log.i("MainActivity", "���ο� TimerTask ����");
		timer.schedule(task, 100, 200);
	}

	public void stopRecord()
	{
		task.cancel();
		if(recorder != null)
		{Log.i("MainActivity", "TimerTask ��� �� stopRecord() ����");
			recorder.stopRecord();
			recorder = null;
		}
		dialogStringStack.append("*****�������*****");
		dialogStringStack.append("\n");
	}
	
	public void playAudio()
	{
		if (player != null)
		{
			player.stop();
			player.release();
			player = null;
		}

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
	
	public void stopAudio()
	{
		if (player == null)
			return;

		// ����� ��� ����
		player.stop();

		// ����� ����� �ʿ��� �޸𸮵��� �����Ѵ�
		player.release();
		player = null;
	}
	
	Handler handler = new Handler()
	{
		@Override
		public void handleMessage(Message msg)
		{
			Log.i("volume", msg.what + "");
		}
	};
	
	class MyTimerTask extends TimerTask
	{
		@Override
		public void run()
		{
			handler.sendEmptyMessage(recorder.getVolumeLevel());
		}
	}
	
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
}

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
				mainController.stopRecord();
				mainController.startRecord();
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
