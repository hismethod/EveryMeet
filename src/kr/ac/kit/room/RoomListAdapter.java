package kr.ac.kit.room;

import java.util.ArrayList;
import java.util.List;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import kr.ac.kit.listener.PrepareRoomListener;
import kr.ac.kit.primitive.Room;
import kr.ac.kit.primitive.Singleton;
import kr.ac.kit.views.view.RoomItemView;
import kr.ac.kit.views.view.RoomItemView_;

@EBean
public class RoomListAdapter extends BaseAdapter
{
	@Bean(RealizedRoomFinder.class) RoomFinder roomFinder;
	@RootContext Context context;
	private PrepareRoomListener listener;

	private List<Room> roomList;

	@AfterInject
	void initAdapter()
	{
		roomList = roomFinder.findAll();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{

		RoomItemView roomItemView;
		if (convertView == null)
		{
			roomItemView = RoomItemView_.build(context);
		} else
		{
			roomItemView = (RoomItemView) convertView;
		}

		roomItemView.bind(getItem(position));

		return roomItemView;
	}

	@Override
	public int getCount()
	{
		return roomList.size();
	}

	@Override
	public Room getItem(int position)
	{
		return roomList.get(position);
	}

	@Override
	public long getItemId(int position)
	{
		return position;
	}
}

@EBean
class RealizedRoomFinder implements RoomFinder
{
	@RootContext Context context;
		
	@AfterInject
	void init()
	{
	}

	@Override
	public List<Room> findAll()
	{
		return Singleton.getInstance().getRoomList();
	}
}

