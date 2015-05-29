package kr.ac.kit.util;

import java.io.IOException;

import org.codehaus.jackson.map.ObjectMapper;

import com.loopj.android.http.*;

import android.util.Log;
import kr.ac.kit.primitive.Room;

public class EveryMeetHttpClient
{
	private static final String BASE_URL = "http://localhost:1337";
	public static final String CREATE_ROOM = "/create/room";

	private AsyncHttpClient client = new AsyncHttpClient();
	private ObjectMapper mapper = new ObjectMapper();
	
	ResponseHandlerInterface handler;
	
	public EveryMeetHttpClient(){}
	public EveryMeetHttpClient(ResponseHandlerInterface responseHandler)
	{
		handler = responseHandler;
	}

	public void get(String url, RequestParams params, AsyncHttpResponseHandler responseHandler)
	{
		client.get(getAbsoluteUrl(url), params, responseHandler);
	}

	public void post(String url, RequestParams params, AsyncHttpResponseHandler responseHandler)
	{
		client.post(getAbsoluteUrl(url), params, responseHandler);
	}
	
	public void getRelatedKeywords(String title, ResponseHandlerInterface responseHandler)
	{
		RequestParams params = new RequestParams();
		params.put("query", title);
		client.get(getAbsoluteUrl("/relatedKeywords"), params, responseHandler);
	}
	
	public void postCreateRoom(Room room, AsyncHttpResponseHandler responseHandler) throws IOException
	{
		String roomJson = mapper.writeValueAsString(room);
		RequestParams params = new RequestParams();
		params.put("roomJson", roomJson);
		
		Log.i("roomJson", roomJson);
		client.post(getAbsoluteUrl(CREATE_ROOM), params, responseHandler);
	}

	private static String getAbsoluteUrl(String relativeUrl)
	{
		return BASE_URL + relativeUrl;
	}
}
