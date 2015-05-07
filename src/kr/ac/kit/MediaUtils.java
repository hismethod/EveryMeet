package kr.ac.kit;

import android.media.MediaRecorder;

public class MediaUtils
{
	public static MediaRecorder getRecorder(String path)
	{
		MediaRecorder recorder = new MediaRecorder();
		recorder.setAudioSource(MediaRecorder.AudioSource.DEFAULT);
		recorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB);
		recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
		recorder.setOutputFile(path);
		return recorder;
	}
}