package kr.ac.kit.views.Activities;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ItemClick;
import org.androidannotations.annotations.ViewById;

import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ListView;
import kr.ac.kit.R;
import kr.ac.kit.primitive.Room;
import kr.ac.kit.room.RoomListAdapter;

@EActivity(R.layout.activity_enter_room)
public class RoomListActivity extends AppCompatActivity
{
	@ViewById(R.id.enterRoomListView)
	ListView roomListView;

	@Bean RoomListAdapter adapter;

	@AfterViews
	void bindAdapter()
	{
		roomListView.setAdapter(adapter);
	}

	@ItemClick(R.id.enterRoomListView)
	void roomListItemClicked(Room room)
	{
		Log.i("listView", room.getTitle() + " " + room.getLeaderName());
	}
	
}