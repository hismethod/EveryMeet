package kr.ac.kit.views.Activities;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.gson.JsonObject;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import kr.ac.kit.R;
import kr.ac.kit.primitive.Room;
import kr.ac.kit.primitive.Singleton;
import kr.ac.kit.room.RoomClient;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


@EActivity(R.layout.activity_create_room)
public class CreateRoomActivity extends AppCompatActivity
{
	@ViewById EditText createRoomTitleEdit;
	@ViewById EditText createRoomPasswordEdit;
	@ViewById EditText createRoomPasswordConfirmEdit;
	private MaterialDialog progressDialog;
	
	private Intent roomActivity;

	@AfterInject
	void init()
	{
		roomActivity = new Intent(this, RoomActivity_.class);
	}
	
	@AfterViews
	void onCreate()
	{
		progressDialog = createProgressDialog(this);
		progressDialog.setCancelable(false);
		progressDialog.setCanceledOnTouchOutside(false);
	}
	
	private MaterialDialog createProgressDialog(Context context)
	{
		return new MaterialDialog.Builder(context)
				.title("��ø���!")
				.content("ȸ�ǽ� ������ ��û ���̿���")
				.progress(true, 0)
				.build();
	}
	
	@Click(R.id.createRoomSubmitBtn)
	void onClickCreateRoomSubmitBtn()
	{
		String title = createRoomTitleEdit.getText().toString();
		String password = createRoomPasswordConfirmEdit.getText().toString();
		Room newRoom = new Room(Singleton.getInstance().getMe(), title, password);
		
		Singleton instance = Singleton.getInstance();
		
		progressDialog.show();
		
		RoomClient.createRoom(title, password, instance.getMe().getName(), instance.getMe(), new Callback<String>()
		{
			@Override
			public void success(String data, Response response)
			{
				Log.i("success", data.toString());
				
				/**
				 * success�޽����� �ް� 2���Ŀ� �Ʒ��� ������
				 * ProgressDialog�� �ʹ� ���������� �������̻��ؼ�
				 * 2�ʰ� �����̸� ����.
				 * Thread.Sleep�� �ٸ���.
				 * ������ ���� ���̾�α׵� �Բ� ���缭 �ȵȴ�.
				 */
				new Handler().postDelayed(new Runnable()
				{
					@Override
					public void run()
					{
						progressDialog.dismiss();
						startActivity(roomActivity);
					}
				}, 2000);
			}

			@Override
			public void failure(RetrofitError retrofitError)
			{
				Log.i("fail", retrofitError.toString());
			}
		});
	}
	
	@Click(R.id.createRoomCancelBtn)
	void onClickCancleBtn()
	{
		moveTaskToBack(false);
		finish();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.room, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings)
		{
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
