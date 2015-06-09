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
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

import com.skyfishjy.library.RippleBackground;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import kr.ac.kit.R;
import kr.ac.kit.controller.MainController;
import kr.ac.kit.primitive.Room;
import kr.ac.kit.primitive.Singleton;
import kr.ac.kit.primitive.User;
import kr.ac.kit.room.RoomClient;
import kr.ac.kit.util.BackPressCloseHandler;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

@EActivity(R.layout.activity_room)
public class RoomActivity extends AppCompatActivity
{
	@ViewById RippleBackground rippleBackground;
	@ViewById ImageView micImageView;
	@ViewById(R.id.RoomDrawerLayout) DrawerLayout drawer;
	@ViewById(R.id.RoomFrameLayout) FrameLayout frameLayout;
	@ViewById(R.id.RoomDrawerListView) ListView drawerListView;
	@ViewById(R.id.startView) TextView statusView;
	
	@ViewById(R.id.startBtn) Button startBtn;
	@ViewById(R.id.stopBtn) Button stopBtn;

	private boolean nowRecording;
	private MainController mainController;
	
	private BackPressCloseHandler backButtonHandler;
	private ActionBarDrawerToggle toggle;
	
	private List<User> userList;
	private List<String> userNameList;
	private ArrayAdapter<String> userListAdapter;
	private String roomTitle;
	private String leaderName;
	private boolean isLeader;
	
	@AfterInject
	void afterInject()
	{
		roomTitle = getIntent().getExtras().getString("roomTitle");
		leaderName = getIntent().getExtras().getString("leaderName");
		userList = new ArrayList<User>();
		userNameList = new ArrayList<String>();
		userNameList.add("회의실 : " + roomTitle);
		requestUserList(roomTitle);
		
		Singleton.getInstance().setCurrentTitle(roomTitle);
		nowRecording = false;
		mainController = new MainController();
		userListAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, userNameList);
		backButtonHandler = new BackPressCloseHandler(this);
		isLeader = false;
		Log.e("", Singleton.getInstance().getMe().getName());
		Log.e("", leaderName);
		
		if(leaderName.equals(Singleton.getInstance().getMe().getName()))
		{
			isLeader = true;
		}
	}
	
	@AfterViews
	void onCreate()
	{
		toggle = new ActionBarDrawerToggle(this, drawer, R.string.open_drawer, R.string.close_drawer)
		{
			@Override
			public void onDrawerClosed(View drawerView)
			{
				super.onDrawerClosed(drawerView);
			}

			@Override
			public void onDrawerOpened(View drawerView)
			{
				super.onDrawerOpened(drawerView);
			}
		};
		drawer.setDrawerListener(toggle);
        drawerListView.setAdapter(userListAdapter);
//        getActionBar().setDisplayHomeAsUpEnabled(true);
        
        if(isLeader){
        	startBtn.setText("회의시작");
        	stopBtn.setText("회의종료");
        }
        else{
        	startBtn.setVisibility(View.INVISIBLE);
        	stopBtn.setVisibility(View.INVISIBLE);
        	startBtn.setText("마이크켜기");
        	stopBtn.setText("마이크끄기");
        }
        
	}
	
	@Click(R.id.startBtn)
	void onClickStartBtn()
	{
		if(!nowRecording)
		{
			nowRecording = true;
			rippleBackground.startRippleAnimation();
			mainController.startRecord();
			if(isLeader){
				statusView.setText("회의가 진행중입니다");
			}
			else{
				statusView.setText("마이크가 켜져있습니다");
			}
		}
		
	}
	
	@Click(R.id.stopBtn)
	void onClickStopBtn()
	{
			nowRecording = false;
			rippleBackground.stopRippleAnimation();
			mainController.stopRecord(true);
			if(isLeader){
				statusView.setText("회의가 종료 상태입니다");
				mainController.sendFinishMeeting(roomTitle);
			}
			else{
				statusView.setText("마이크가 꺼져있습니다");
			}
	}

	@Click(R.id.micImageView)
	void onClickMicBtn()
	{
		if(!nowRecording)
		{
			nowRecording = true;
			rippleBackground.startRippleAnimation();
			mainController.startRecord();
			if(isLeader){
				statusView.setText("회의가 진행중입니다");
			}
			else{
				statusView.setText("마이크가 켜져있습니다");
			}
		}
		else
		{
			nowRecording = false;
			rippleBackground.stopRippleAnimation();
			mainController.stopRecord(true);
			if(isLeader){
				statusView.setText("회의가 일시중지 상태입니다");
			}
			else{
				statusView.setText("마이크가 꺼져있습니다");
			}
		}
	}
	
	void requestUserList(String title)
	{
		RoomClient.getUserList(title, new Callback<Object>()
		{
			@Override
			public void success(Object data, Response response)
			{
				ObjectMapper mapper = new ObjectMapper();
				try
				{
					userList.addAll((Collection<? extends User>) mapper.readValue(Singleton.getInstance().gson.toJson(data),
							new TypeReference<ArrayList<User>>(){}));
					for(User user : userList)
					{
						userNameList.add(user.getName());
					}
					userListAdapter.notifyDataSetChanged();
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
	
	protected void onPostCreate(Bundle savedInstanceState){
	    super.onPostCreate(savedInstanceState);
	    toggle.syncState();
	}
	
	@Override
    public void onBackPressed() {
        backButtonHandler.onBackPressed(mainController);
    }
	 
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
	    super.onConfigurationChanged(newConfig);
	    toggle.onConfigurationChanged(newConfig);
	}
	 
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    if(toggle.onOptionsItemSelected(item)){
	        return true;
	    }
	    return super.onOptionsItemSelected(item);
	}
}
