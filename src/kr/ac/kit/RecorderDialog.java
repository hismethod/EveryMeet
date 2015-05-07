package kr.ac.kit;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.widget.TextView;

public class RecorderDialog extends Dialog
{
	private static final long SECOND = 1000;

	private TextView levelView;
	private Timer time;
	private MyTimerTask task;
	private Recorder recorder;

	private long duration;

	public RecorderDialog(Context context)
	{
		super(context);
		setContentView(R.layout.listening);
		time = new Timer();
		levelView = (TextView) findViewById(R.id.text_recordLevel);
	}

	public void show(String path)
	{
		levelView.setText("0");
		recorder = Recorder.getRecorder(path);
		recorder.startRecord();
		
		task = new MyTimerTask();
		time.schedule(task, 0, 200);
		super.show();
	}

	@Override
	public void dismiss()
	{
		if (!isShowing())
		{
			return;
		}
		super.dismiss();
		task.cancel();
		recorder.stopRecord();
	}

	private int getDuration()
	{
		return (int) (duration / SECOND);
	}

	private void setDuration(int duration)
	{
		this.duration = duration * SECOND;
	}

	class MyTimerTask extends TimerTask
	{
		@Override
		public void run()
		{
			int ratio = recorder.getMaxAmplitude() / 300;
			int db = 0;
			if (ratio > 1)
				db = (int) (20 * Math.log10(ratio));
			
			// (int) (20 * Math.log10(ratio))진폭 및 볼륨 크기 조정
			// by 주변 소음 가져오는 값 약 300 까지 150
			handler.sendEmptyMessage(db);
		}
	}

	Handler handler = new Handler()
	{
		public void handleMessage(Message msg)
		{
			levelView.setText("" + msg.what);
		};
	};
}