package kr.ac.kit.views.Activities;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ItemClick;
import org.androidannotations.annotations.ViewById;

import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.MaterialDialog.ButtonCallback;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.widget.ListView;
import kr.ac.kit.R;
import kr.ac.kit.primitive.Room;
import kr.ac.kit.primitive.Singleton;
import kr.ac.kit.room.RoomClient;
import kr.ac.kit.room.RoomListAdapter;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

@EActivity(R.layout.activity_enter_room)
public class RoomListActivity extends AppCompatActivity
{
	@ViewById(R.id.enterRoomListView) ListView roomListView;
	@Bean RoomListAdapter adapter;
	
	private MaterialDialog progressDialog;
	private Intent roomActivity;
	private String password;
	
	@AfterInject
	void init()
	{
		progressDialog = buildProgressDialog();
		progressDialog.setCancelable(false);
		progressDialog.setCanceledOnTouchOutside(false);
		roomActivity = new Intent(this, RoomActivity_.class);
	}

	@AfterViews
	void bindAdapter()
	{
//		progressDialog.show();
		
		roomListView.setAdapter(adapter);
	}
	
	private MaterialDialog buildProgressDialog()
	{
		return new MaterialDialog.Builder(this)
				.title("잠시만요!")
				.content("회의실 개설을 요청 중이에요")
				.progress(true, 0)
				.build();
	}
	
	@ItemClick(R.id.enterRoomListView)
	void roomListItemClicked(final Room room)
	{
		Log.i("listView", room.getTitle() + " " + room.getLeaderName());
		new MaterialDialog.Builder(this)
		.title("잠깐만요!")
		.content("비밀번호를 입력해주세요")
		.inputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD)
        .input("비밀번호", null, new MaterialDialog.InputCallback()
        {
            @Override
            public void onInput(MaterialDialog dialog, CharSequence input) {
            }
        })
        .positiveText("들어가기").callback(new ButtonCallback()
        {
        	@Override
            public void onPositive(MaterialDialog dialog)
        	{
        		RoomClient.enterRoom(room.getTitle(), dialog.getInputEditText().getText().toString(), Singleton.getInstance().getMe(), new Callback<String>()
				{
					@Override
					public void success(String arg0, Response arg1)
					{
						roomActivity.putExtra("roomTitle", room.getTitle());
						roomActivity.putExtra("leaderName", arg0);
						startActivity(roomActivity);
					}
					
					@Override
					public void failure(RetrofitError arg0)
					{
					}
				});
        		
            }
		})
		.show();
	}
	
}