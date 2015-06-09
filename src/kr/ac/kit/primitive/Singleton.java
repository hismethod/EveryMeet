package kr.ac.kit.primitive;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;

public class Singleton
{
	// Private constructor prevents instantiation from other classes
	private Singleton()
	{
		roomList = new ArrayList<Room>();
	}
	private User me;
	private List<Room> roomList;
	private String currentTitle;
	public Gson gson = new Gson();

	public User getMe(){return me;}
	public List<Room> getRoomList(){return roomList;}
	public String getCurrentTitle(){return currentTitle;}
	public void setMe(User user){this.me = user;}
	public void setRoomList(List<Room> roomList){this.roomList = roomList;}
	public void setCurrentTitle(String title){currentTitle = title;}


	/**
	 * SingletonHolder is loaded on the first execution of
	 * Singleton.getInstance() or the first access to SingletonHolder.INSTANCE,
	 * not before.
	 */
	private static class SingletonHolder
	{
		public static final Singleton INSTANCE = new Singleton();
	}

	public static Singleton getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
}