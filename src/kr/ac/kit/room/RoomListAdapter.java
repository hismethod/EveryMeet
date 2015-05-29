package kr.ac.kit.room;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import kr.ac.kit.listener.PrepareRoomListener;
import kr.ac.kit.primitive.Room;
import kr.ac.kit.primitive.Singleton;
import kr.ac.kit.views.view.RoomItemView;
import kr.ac.kit.views.view.RoomItemView_;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

@EBean
public class RoomListAdapter extends BaseAdapter
{
	List<Room> roomList;

	@Bean(RealizedRoomFinder.class)
	RoomFinder roomFinder;

	@RootContext
	Context context;
	
	PrepareRoomListener listener;

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
	private List<Room> roomList;

	@Override
	public List<Room> findAll()
	{
		roomList = new ArrayList<Room>();
		RoomClient.listRoom(new Callback<Object>()
		{
			@Override
			public void success(Object data, Response response)
			{
				ObjectMapper mapper = new ObjectMapper();
				try
				{
					roomList = mapper.readValue(Singleton.getInstance().gson.toJson(data),
							new TypeReference<ArrayList<Room>>(){});
				}
				catch (IOException e){e.printStackTrace();}
			}

			@Override
			public void failure(RetrofitError retrofitError)
			{
				Log.e("fail", retrofitError.toString());
			}
		});
		
		return roomList;
	}
}

