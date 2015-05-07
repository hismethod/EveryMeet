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

	/* Media 객체들 */
//	private Timer timer = new Timer();
//	private MyTimerTask task = null;
	private VolumeViewThread levelThread = null;
//	private AudioManager audioManager;
	private MediaPlayer player = null;
	private Recorder recorder;

	/* View 객체들 */
	private TextView resultView = null;
	private TextView levelView = null;

	/* HTTP음성인식결과 response 리스너 */
	private RecognizeResponseListener listener = new RecognizeResponseListener()
	{
		@Override //callback메소드
		public void onTaskCompleted(String sentence)
		{
			Log.i("RecognizeResponseListener", "onTaskCompleted() 호출");
			dialogStringStack.append(sentence);
			dialogStringStack.append("\n");
			resultView.setText(dialogStringStack.toString());
			
		}
	};

	/* 레코딩 event 콜백 리스너 */
	private RecorderListener recorderListener = new RecorderListener()
	{
		@Override
		public void onStartRecord()
		{
			Log.i("RecorderListener", "onStartRecord() 호출");
//			levelThread = new VolumeViewThread();
//			levelThread.start();
		}

		@Override
		public void onStopRecord()
		{
			Log.i("RecorderListener", "onStopRecord() 호출");
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

		// 녹음 시작 버튼
		recordBtn.setOnClickListener(new View.OnClickListener()
		{
			public void onClick(View v)
			{
				startRecord();
			}
		});

		// 녹음 중지 버튼
		recordStopBtn.setOnClickListener(new View.OnClickListener()
		{
			public void onClick(View v)
			{
				stopRecord();
				startRecord();
			}
		});

		// 오디오 플레이 버튼
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

				Toast.makeText(getApplicationContext(), "녹음된 파일을 재생합니다.", Toast.LENGTH_SHORT).show();
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
		});

		// 오디오 재생 중지 버튼
		playStopBtn.setOnClickListener(new View.OnClickListener()
		{
			public void onClick(View v)
			{
				if (player == null)
					return;

				Toast.makeText(getApplicationContext(), "재생이 중지되었습니다.", Toast.LENGTH_SHORT).show();

				// 오디오 재생 중지
				player.stop();

				// 오디오 재생에 필요한 메모리들을 해제한다
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
			Log.i("MainActivity", "새로운 Recorder 생성");
		}
		
		recorder.setRecorderListener(recorderListener);
		Log.i("MainActivity", "startRecord() 진입");
		recorder.startRecord();

//		task = new MyTimerTask();
//		Log.i("MainActivity", "새로운 TimerTask 생성");
//		timer.schedule(task, 100, 200);
	}

	public void stopRecord()
	{
//		task.cancel();
		if(recorder != null)
		{Log.i("MainActivity", "TimerTask 취소 및 stopRecord() 진입");
//			audioManager.stopBluetoothSco();
			recorder.stopRecord();
			recorder = null;
		}
		dialogStringStack.append("*****녹음경계*****");
		dialogStringStack.append("\n");
		resultView.setText(dialogStringStack.toString());
	}

	public void updateLevelView(int level)
	{
		levelView.setText(""+level);
	}
}
