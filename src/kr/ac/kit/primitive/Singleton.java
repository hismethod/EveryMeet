package kr.ac.kit.primitive;

import com.google.gson.Gson;

public class Singleton
{
	// Private constructor prevents instantiation from other classes
	private Singleton(){}
	private User me;
	public Gson gson = new Gson();

	public User getMe(){return me;}
	public void setMe(User user){this.me = user;}

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