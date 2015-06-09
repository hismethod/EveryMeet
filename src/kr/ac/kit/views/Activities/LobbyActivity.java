package kr.ac.kit.views.Activities;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.sharedpreferences.Pref;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.MaterialDialog.ButtonCallback;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.daimajia.androidanimations.library.YoYo.AnimatorCallback;
import com.nineoldandroids.animation.Animator;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.text.InputType;
import android.util.Log;
import kr.ac.kit.R;
import kr.ac.kit.primitive.Room;
import kr.ac.kit.primitive.Singleton;
import kr.ac.kit.primitive.User;
import kr.ac.kit.room.RoomClient;
import kr.ac.kit.util.AppSharedPreference_;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

@EActivity(R.layout.activity_lobby)
public class LobbyActivity extends AppCompatActivity
{
//	private NameDialog dialog;
	
	/* Views */
	@ViewById CardView lobbyTopCard;
	@ViewById CardView lobbyBottomLeftCard;
	@ViewById CardView lobbyBottomRightCard;
	
	@Pref AppSharedPreference_ sharedPref;
	
	private MaterialDialog initNameDialog;
	private MaterialDialog loadRoomListDialog;

	@AfterInject
	public void doAfterInject()
	{
		initNameDialog = buildInitNameDialog(this);
		initNameDialog.setCancelable(false);
		initNameDialog.setCanceledOnTouchOutside(false);
		loadRoomListDialog = buildLoadRoomListDialog(this);
		loadRoomListDialog.setCanceledOnTouchOutside(false);
		
	}
	
	@AfterViews
	public void doAfterViews()
	{
		//Singleton.getInstance().getMe().getName().equals("")
		if(true)
		{
			initNameDialog.show();
		}
	}
	
	public void exit()
	{
		moveTaskToBack(true);
		finish();
	}
	
	private MaterialDialog buildInitNameDialog(Context context)
	{
		return new MaterialDialog.Builder(context)
			.title("안녕하세요! 처음이시죠?")
			.content("사용할 이름을 입력해주세요.")
			.widgetColor(R.color.md_teal_500)
			.inputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_NORMAL)
	        .input("이름입력", null, new MaterialDialog.InputCallback()
			{
				@Override
				public void onInput(MaterialDialog dialog, CharSequence input){}
			})
			.positiveText("확인")
	        .negativeText("취소").callback(new ButtonCallback()
	        {
	        	@Override
	            public void onPositive(MaterialDialog dialog) {
	        		/**
	    			 * 여기에 사용자의 이름가지고 뭐 어떻게 하는 거 나와야한다
	    			 * SharedPreference에 저장하던지 DB에 저장하던지 후처리 할 것.
	    			 */
	    			User me = Singleton.getInstance().getMe();
	    			me.setName(initNameDialog.getInputEditText().getText().toString());
	    			Singleton.getInstance().setMe(me);
	            }
	        	@Override
	            public void onNegative(MaterialDialog dialog) {
	        		exit();
	            }
			})
			.build();
	}
	
	private MaterialDialog buildLoadRoomListDialog(Context context)
	{
		return new MaterialDialog.Builder(context)
			.title("잠시만요!")
			.content("회의실 목록을 요청 중이에요")
			.progress(true, 0)
			.build();
	}
	
	void requestRoomList(final Intent intent)
	{
		final List<Room> roomList = new ArrayList<Room>();
		
		RoomClient.listRoom(new Callback<Object>()
		{
			@Override
			public void success(Object data, Response response)
			{
				ObjectMapper mapper = new ObjectMapper();
				try
				{
					roomList.addAll((Collection<? extends Room>) mapper.readValue(Singleton.getInstance().gson.toJson(data),
							new TypeReference<ArrayList<Room>>(){}));
					Singleton.getInstance().setRoomList(roomList);
					startActivity(intent);
					loadRoomListDialog.dismiss();
				}
				catch (IOException e){e.printStackTrace();}
			}

			@Override
			public void failure(RetrofitError retrofitError)
			{
				Log.e("fail", retrofitError.toString());
			}
		});
	}
	
	@Click(R.id.lobbyTopCard)
	void onClickLobbyTopCard()
	{
		YoYo.with(Techniques.Swing).duration(500).playOn(lobbyTopCard);
	}
	
	@Click(R.id.lobbyBottomLeftCard)
	void onClickLobbyBottomLeftCard()
	{
		final Intent intent = new Intent(this, CreateRoomActivity_.class);
		YoYo.with(Techniques.Bounce).duration(500).onEnd(new AnimatorCallback()
		{
			@Override
			public void call(Animator animator)
			{
				startActivity(intent);
			}
		}).playOn(lobbyBottomLeftCard);
	}
	
	@Click(R.id.lobbyBottomRightCard)
	void onClickLobbyBottomRightCard()
	{
		loadRoomListDialog.show();
		final Intent roomListIntent = new Intent(this, RoomListActivity_.class);
		YoYo.with(Techniques.Bounce).duration(500).onEnd(new AnimatorCallback()
		{
			@Override
			public void call(Animator animator)
			{
				requestRoomList(roomListIntent);
			}
		}).playOn(lobbyBottomRightCard);
	}
}
