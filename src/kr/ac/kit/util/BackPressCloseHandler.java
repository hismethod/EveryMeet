package kr.ac.kit.util;

import android.app.Activity;
import android.widget.Toast;
import kr.ac.kit.controller.MainController;

public class BackPressCloseHandler
{

	private long backKeyPressedTime = 0;
	private Toast toast;

	private Activity activity;

	public BackPressCloseHandler(Activity context)
	{
		this.activity = context;
	}

	public void onBackPressed(MainController mc)
	{
		if (System.currentTimeMillis() > backKeyPressedTime + 2000)
		{
			backKeyPressedTime = System.currentTimeMillis();
			showGuide();
			return;
		}
		if (System.currentTimeMillis() <= backKeyPressedTime + 2000)
		{
			mc.exitRoom();
			activity.finish();
			toast.cancel();
		}
	}

	public void showGuide()
	{
		toast = Toast.makeText(activity, "\'�ڷ�\'��ư�� �ѹ� �� �����ø� ����˴ϴ�.", Toast.LENGTH_SHORT);
		toast.show();
	}
}