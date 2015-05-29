package kr.ac.kit.room;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import kr.ac.kit.primitive.Room;
import kr.ac.kit.primitive.User;
import retrofit.Callback;
import retrofit.RestAdapter;

public class RoomClient
{
	public static String API_URL = "http://192.168.124.101:1337";
	private static Gson gson = new Gson();
	
	public static void createRoom(Room room, Callback<JsonObject> callback)
	{
		JsonObject jsonObject = gson.toJsonTree(room).getAsJsonObject();

		RestAdapter restAdapter = new RestAdapter.Builder()
				.setLogLevel(RestAdapter.LogLevel.HEADERS_AND_ARGS)
				.setEndpoint(API_URL)
				.build();

		RoomREST post = restAdapter.create(RoomREST.class);

		post.createRoom(jsonObject, callback);
	}
	
	public static void enterRoom(String title, String password, User user, Callback<String> callback)
	{
		RestAdapter restAdapter = new RestAdapter.Builder()
				.setLogLevel(RestAdapter.LogLevel.HEADERS_AND_ARGS)
				.setEndpoint(API_URL)
				.build();
		
		RoomREST post = restAdapter.create(RoomREST.class);
		post.enterRoom(title, password, gson.toJson(user), callback);
	}
	
	public static void listRoom(Callback<Object> callback)
	{
		RestAdapter restAdapter = new RestAdapter.Builder()
				.setLogLevel(RestAdapter.LogLevel.HEADERS_AND_ARGS)
				.setEndpoint(API_URL)
				.build();
		RoomREST post = restAdapter.create(RoomREST.class);
		post.listRoom(callback);
	}
}
