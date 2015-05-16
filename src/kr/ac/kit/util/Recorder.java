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
		// ���ڴ�����
		isRecording = false;
		volumeList = new ArrayList<Integer>();
		setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
		setOutputFormat(MediaRecorder.OutputFormat.AMR_NB);
		setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
	}
	
	/**
	 * ���������� ���ÿ� ���ο� ������ �����Ѵ�.
	 * ������ ����� ���Ͽ� ������ �ȵǹǷ�
	 * ���ο� ���� �̸��� �ڵ����� �����ϴ� �޼ҵ�
	 * 
	 * @return 1�� ������ �����̸��� ���� (������ �ִ� 5��)
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
			Log.e("Recorder", "������ �׳� ����!");
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
			Log.e("Recorder", "�������� �ƴϹǷ� �������ϰ� ����");
			return;
		}
		
		Log.i("Recorder.stopRecord()","is���ڵ� �˻� ���!");
		
		super.stop();
		Log.i("Recorder.stopRecord()","stop() ȣ��Ϸ�");
		
		isRecording = false;
		
		if(recorderListener != null)
		{
			Log.i("Recorder.stopRecord()","stop������ ȣ��");
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
//		// (int) (20 * Math.log10(ratio))���� �� ���� ũ�� ����
//		// by �ֺ� ���� �������� �� �� 300 ���� 150
		
		int db = getMaxAmplitude();
		
		addVolumeRecord(db);

		return db;
	}
}
