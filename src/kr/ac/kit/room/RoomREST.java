package kr.ac.kit.room;

import com.google.gson.JsonObject;

import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.POST;

interface RoomREST
{
	@POST("/room/create")
	@FormUrlEncoded
	void createRoom(
			@Field("title") String title,
			@Field("password") String password,
			@Field("leaderName") String leaderName,
			@Field("user") String jsonStringUser, Callback<String> callBack);
	
	@POST("/room/enter")
	@FormUrlEncoded
	void enterRoom(
			@Field("title") String title,
			@Field("password") String password,
			@Field("user") String jsonStringUser, Callback<String> callback);
	
	@GET("/room/list")
	void listRoom(Callback<Object> callback);
	
	@POST("/room/find")
	@FormUrlEncoded
	void findRoom(@Field("title") String title, Callback<JsonObject> callback);
}