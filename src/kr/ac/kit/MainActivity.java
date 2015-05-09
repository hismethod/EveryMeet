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

	/* Media 객체들 */
	private Timer timer = new Timer();
	private MyTimerTask task = null;
	private VolumeViewThread levelThread = null;
//	private AudioManager audioManager;
	private MediaPlayer player = null;
	private Recorder recorder;

	/* HTTP음성인식결과 response 리스너 */
	private RecognizeResponseListener listener = new RecognizeResponseListener()
	{
		@Override //callback메소드
		public void onTaskCompleted(String sentence)
		{
			Log.i("RecognizeResponseListener", "onTaskCompleted() 호출");
			dialogStringStack.append(sentence);
			dialogStringStack.append("\n");
		}
	};

	/* 레코딩 event 콜백 리스너 */
	private RecorderListener recorderListener = new RecorderListener()
	{
		@Override
		public void onStartRecord()
		{
			Log.i("RecorderListener", "onStartRecord() 호출");
		}

		@Override
		public void onStopRecord()
		{
			Log.i("RecorderListener", "onStopRecord() 호출");
			AsyncDictationHTTPClient asyncDictationHTTPClient = new AsyncDictationHTTPClient(listener);
			asyncDictationHTTPClient.execute(Recorder.getCurrentFilePath());
			/*
			 * execute의 결과로
			 * RecognizeResponseListner의 onTaskCompleted 콜백 메소드 호출
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
			Log.i("MainActivity", "새로운 Recorder 생성");
		}
		
		recorder.setRecorderListener(recorderListener);
		Log.i("MainActivity", "startRecord() 진입");
		recorder.startRecord();

		task = new MyTimerTask();
		Log.i("MainActivity", "새로운 TimerTask 생성");
		timer.schedule(task, 100, 200);
	}

	public void stopRecord()
	{
		task.cancel();
		if(recorder != null)
		{Log.i("MainActivity", "TimerTask 취소 및 stopRecord() 진입");
			recorder.stopRecord();
			recorder = null;
		}
		dialogStringStack.append("*****녹음경계*****");
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
			// 오디오를 플레이 하기위해 MediaPlayer 객체 player를 생성한다.
			player = new MediaPlayer();

			// 재생할 오디오 파일 저장위치를 설정
			player.setDataSource(Recorder.getCurrentFilePath());
			// 웹상에 있는 오디오 파일을 재생할때
			// player.setDataSource(Audio_Url);

			// 오디오 재생준비,시작
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

		// 오디오 재생 중지
		player.stop();

		// 오디오 재생에 필요한 메모리들을 해제한다
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
				// 메시지 얻어오기
				Message msg = handler.obtainMessage();

				// 메시지 ID 설정
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
	/* View 객체들 */
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

		// 녹음 시작 버튼
		recordBtn.setOnClickListener(new View.OnClickListener()
		{
			public void onClick(View v)
			{
				mainController.startRecord();
			}
		});

		// 녹음 중지 버튼
		recordStopBtn.setOnClickListener(new View.OnClickListener()
		{
			public void onClick(View v)
			{
				mainController.stopRecord();
				mainController.startRecord();
			}
		});

		// 오디오 플레이 버튼
		playBtn.setOnClickListener(new View.OnClickListener()
		{
			public void onClick(View v)
			{
				mainController.playAudio();
			}
		});

		// 오디오 재생 중지 버튼
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
