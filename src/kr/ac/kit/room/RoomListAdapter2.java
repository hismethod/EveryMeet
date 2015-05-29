package kr.ac.kit.room;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import kr.ac.kit.R;
import kr.ac.kit.primitive.Room;

class RoomListAdapter2 extends BaseAdapter
{
	Context context;
	LayoutInflater inflater;
	private List<Room> roomList = null;
	private ArrayList<Room> arraylist;

	public RoomListAdapter2(Context context, List<Room> roomList)
	{
		inflater = LayoutInflater.from(context);
		this.context = context;
		this.roomList = roomList;
		this.arraylist = new ArrayList<Room>();
		this.arraylist.addAll(roomList);
	}

	public class ViewHolder
	{
		TextView title;
		TextView leaderName;
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

	public View getView(final int position, View view, ViewGroup parent)
	{
		final ViewHolder holder;
		if (view == null)
		{
			holder = new ViewHolder();
			view = inflater.inflate(R.layout.item_room, null);
			// Locate the TextViews in listview_item.xml
			holder.title = (TextView) view.findViewById(R.id.itemRoomTitle);
			holder.leaderName = (TextView) view.findViewById(R.id.itemRoomLeaderName);
			view.setTag(holder);
		} else
		{
			holder = (ViewHolder) view.getTag();
		}
		// Set the results into TextViews
		holder.title.setText(roomList.get(position).getTitle());
		holder.leaderName.setText(roomList.get(position).getLeaderName());

		// Listen for ListView Item Click
		view.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View arg0)
			{

			}
		});

		return view;
	}

	// Filter Class
	public void filter(String charText)
	{
		charText = charText.toLowerCase(Locale.getDefault());
		roomList.clear();
		if (charText.length() == 0)
		{
			roomList.addAll(arraylist);
		} else
		{
			for (Room room : arraylist)
			{
				if (room.getTitle().contains(charText))
				{
					roomList.add(room);
				}
			}
		}
		notifyDataSetChanged();
	}
}