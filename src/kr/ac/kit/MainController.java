package kr.ac.kit;

import java.util.Timer;
import java.util.TimerTask;
import android.annotation.SuppressLint;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class MainController
{
	private StringBuilder dialogStringStack = new StringBuilder();
	
	private NetworkController networkHandler = new NetworkController();

	/************************ Media 객체들 *************************/
	/* private AudioManager audioManager; */
	private MyTimerTask task = null;
	private VolumeViewThread levelThread = null;
	private Timer timer = new Timer();
	private MediaPlayer player = null;
	private Recorder recorder;
	
	long startTime = 0L;
	long endTime = 0L;
	
	/********************   Getter & Setter   **********************/
	 
	public String getDialogue()
	{
		return dialogStringStack.toString();
	}

	/************************** 리스너들 ***************************/
	/** HTTP음성인식결과 response 리스너 */
	private RecognizeResponseListener listener = new RecognizeResponseListener()
	{
		@Override //callback메소드
		public void onTaskCompleted(String sentence)
		{
			Log.i("RecognizeResponseListener", "onTaskCompleted() 호출");
			endTime = System.currentTimeMillis();
			
			Log.e("RecognizeResponseListener", "인식된문장 : " + sentence + "(" + ( endTime - startTime )/1000.0f + "초전)"); 
			
			dialogStringStack.append(sentence);
			dialogStringStack.append("\n");
		}
	};

	/** 레코딩 event 콜백 리스너 */
	private RecorderListener recorderListener = new RecorderListener()
	{
		@Override
		public void onStartRecord()
		{
			Log.i("RecorderListener", "onStartRecord() 호출");
		}

		@Override
		public void onStopRecord(boolean doDictation)
		{
			Log.i("RecorderListener", "onStopRecord() 호출");
			if(doDictation)
			{
				AsyncDictationHTTPClient asyncDictationHTTPClient = new AsyncDictationHTTPClient(listener);
				startTime = System.currentTimeMillis();
				asyncDictationHTTPClient.execute(Recorder.getCurrentFilePath());
				/* execute의 결과로
				 * RecognizeResponseListner의 onTaskCompleted 콜백 메소드 호출 */
			}
			else
				return;
		}
	};
	
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

	public void stopRecord(boolean doDictation)
	{
		task.cancel();
		if(recorder != null)
		{Log.i("MainActivity", "TimerTask 취소 및 stopRecord() 진입");
			recorder.stopRecord(doDictation);
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
	
	/******************* 스레드 관련 *******************/
	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler()
	{
		int time = 0;
		int continuous = 0;
		int recentAvg = 0;
		
		@Override
		public void handleMessage(Message msg)
		{
			int volume = msg.what;
			time+=200;
			recentAvg += volume;
			
			Log.i("volume", volume + "");
			if(time%600==0)
			{
				recentAvg /= 3;
				
				if(recentAvg <= 6000)
					continuous++;
				else
					continuous = 0;
				
				Log.i("최근평균", recentAvg + "");
				recentAvg = 0;
			}
			
			if(continuous >= 3)
			{
				if(time <= 3200)
					stopRecord(false);
				else
					stopRecord(true);
				time = 0;
				continuous = 0;
				startRecord();
			}
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
