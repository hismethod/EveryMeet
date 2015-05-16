package kr.ac.kit.util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.media.MediaRecorder;
import android.os.Environment;
import android.util.Log;
import kr.ac.kit.listener.RecorderListener;

public class Recorder extends MediaRecorder
{
	final private static File RECORDED_FILE = Environment.getExternalStorageDirectory();
	final private static int MAX_FILE_NO = 5;

	private static int fileNo = 1;
	private static String currentFilePath;
	
	private boolean isRecording;
	private List<Integer> volumeList;
	
	private RecorderListener recorderListener;
	
	public static Recorder getRecorder(String outputFilePath)
	{
		return new Recorder(outputFilePath);
	}
	
	public Recorder(String outputFilePath)
	{
		initiallizeRecorder();
		setOutputFile(outputFilePath);
	}
	
	public static String getCurrentFilePath()
	{
		return currentFilePath;
	}
	
	public boolean isRecording(){return isRecording;}
	
	public void setRecorderListener(RecorderListener listener)	
	{
		recorderListener = listener;
	}

	public void initiallizeRecorder()
	{
		// 레코더셋팅
		isRecording = false;
		volumeList = new ArrayList<Integer>();
		setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
		setOutputFormat(MediaRecorder.OutputFormat.AMR_NB);
		setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
	}
	
	/**
	 * 녹음중지와 동시에 새로운 파일을 녹음한다.
	 * 이전에 저장된 파일에 덮어씌우면 안되므로
	 * 새로운 파일 이름을 자동으로 생성하는 메소드
	 * 
	 * @return 1씩 증가된 파일이름을 리턴 (파일은 최대 5개)
	 */
	public static String generateFileName()
	{
		StringBuilder fileName = new StringBuilder(RECORDED_FILE.getAbsolutePath());
		fileName.append("/rec");
		fileName.append(fileNo++);
		fileName.append(".3gp");
		
		if(fileNo > MAX_FILE_NO)
			fileNo = 1;
		
		currentFilePath = fileName.toString();
		
		return currentFilePath;
	}
	
	public void startRecord()
	{
		if(isRecording == true)
		{
			Log.e("Recorder", "녹음중 그냥 리턴!");
			return;
		}
		
		try
		{
			super.prepare();
			Log.i("Recorder", "prepare");
			super.start();
			Log.i("Recorder", "start");
		}
		catch (IllegalStateException e){e.printStackTrace();}
		catch (IOException e){e.printStackTrace();}
		
		isRecording = true;
		Log.i("Recorder", "isRecording == true");
		
		if(recorderListener != null)
		{
			recorderListener.onStartRecord();
		}
	}
	
	public void stopRecord(boolean doDictation)
	{
		if(isRecording == false)
		{
			Log.e("Recorder", "녹음중이 아니므로 중지안하고 리턴");
			return;
		}
		
		Log.i("Recorder.stopRecord()","is레코딩 검사 통과!");
		
		super.stop();
		Log.i("Recorder.stopRecord()","stop() 호출완료");
		
		isRecording = false;
		
		if(recorderListener != null)
		{
			Log.i("Recorder.stopRecord()","stop리스너 호출");
			recorderListener.onStopRecord(doDictation);
		}
	}
	
	public List<Integer> getVolumeList()
	{
		return volumeList;
	}
	
	public void addVolumeRecord(int volume)
	{
		volumeList.add(volume);
	}
	
	public int getVolumeLevel()
	{
//		int ratio = getMaxAmplitude();
//		int db = 0;
//		if (ratio > 1)
//			db = (int) (20 * Math.log10(ratio));
//
//		// (int) (20 * Math.log10(ratio))진폭 및 볼륨 크기 조정
//		// by 주변 소음 가져오는 값 약 300 까지 150
		
		int db = getMaxAmplitude();
		
		addVolumeRecord(db);

		return db;
	}
}
