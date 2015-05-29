package kr.ac.kit.room;

import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;

import android.content.Context;
import android.view.ViewGroup;
import kr.ac.kit.primitive.Room;
import kr.ac.kit.views.view.RecyclerViewAdapterBase;
import kr.ac.kit.views.view.RoomItemView;
import kr.ac.kit.views.view.RoomItemView_;
import kr.ac.kit.views.view.ViewWrapper;

@EBean
public class RoomAdapter extends RecyclerViewAdapterBase<Room, RoomItemView>
{

	@RootContext
	Context context;

	@Override
	protected RoomItemView onCreateItemView(ViewGroup parent, int viewType)
	{
		return RoomItemView_.build(context);
	}

	@Override
	public void onBindViewHolder(ViewWrapper<RoomItemView> viewHolder, int position)
	{
		RoomItemView view = viewHolder.getView();
		Room room = items.get(position);

		view.bind(room);
	}

}