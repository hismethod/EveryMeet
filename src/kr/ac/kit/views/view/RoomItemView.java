package kr.ac.kit.views.view;

import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

import android.content.Context;
import android.widget.LinearLayout;
import android.widget.TextView;
import kr.ac.kit.R;
import kr.ac.kit.primitive.Room;

@EViewGroup(R.layout.item_room)
public class RoomItemView extends LinearLayout
{
	
	@ViewById TextView itemRoomTitle;
	@ViewById TextView itemRoomLeaderName;

	public RoomItemView(Context context)
	{
		super(context);
	}

	public void bind(Room room)
	{
		itemRoomTitle.setText(room.getTitle());
		itemRoomLeaderName.setText(room.getLeaderName());
	}
}