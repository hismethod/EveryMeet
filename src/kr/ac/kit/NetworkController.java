package kr.ac.kit;

import java.net.URISyntaxException;
import java.util.Scanner;

import org.json.JSONException;
import org.json.JSONObject;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import android.util.Log;

public class NetworkController
{
	public static String SERVER_IP = "";
	public static int SERVER_PORT = 0;
	
	public final static int CREATE_ROOM = 1;
	public final static int ENTER_ROOM = 2;
	public final static int START_METTING = 3;
	public final static int STOP_METTING = 4;
	public final static String CREATE_ROOM_KEY = "CREATE_ROOM";
	public final static String ENTER_ROOM_KEY = "ENTER_ROOM";
	public final static String START_METTING_KEY = "START_METTING";
	public final static String STOP_METTING_KEY = "STOP_METTING";
	
	private Socket socket = null;
	
	/**
	 * 네트워크 핸들러
	 * 서버와의 모든 네트워크 통신은 여기서 담당한다.
	 * 네트워크 핸들러 객체를 생성하여 통신하는 방법은 두가지를 제공한다.
	 * 1. 객체를 생성할때 ip와 port를 넘겨준다.
	 * 2. 빈 객체를 만들고 connect함수를 이용하여 소켓을 얻는다.
	 * 
	 * 여러가지 이유로 소켓연결에 실패할 경우 예외를 발생시킨다.
	 * 
	 */
	public NetworkController(){}
	public NetworkController(String ip, int port)
	{
		SERVER_IP = ip;
		SERVER_PORT = port;
		
		try
		{
			socket = IO.socket(String.format("http://%s:%s", SERVER_IP, SERVER_PORT));
		}
		catch (URISyntaxException e)
		{
			e.printStackTrace();
			System.err.println("소켓연결실패\n");
		}
		
		socket.connect();
	}
	
	/**
	 * 네트워크 핸들러 빈객체를 만들고 나서 초기 소켓연결에 사용하거나
	 * 이미 연결되었다 할지라도 새로 접속할 경우 사용한다.
	 * @param ip
	 * @param port
	 */
	public void connect(String ip, int port)
	{
		SERVER_IP = ip;
		SERVER_PORT = port;
		
		try
		{
			socket = IO.socket(String.format("http://%s:%s", SERVER_IP, SERVER_PORT));
		}
		catch (URISyntaxException e)
		{
			e.printStackTrace();
			System.err.println("소켓연결실패\n");
		}
		
		socket.connect();
	}
	
	/***************************************************
	 * 이 아래로 여러가지 통신 메소드를 만들어야 한다. *
	 ***************************************************/
	public void sendMessage(int type, Object message)
	{
		if(socket == null)
		{
			Log.e("NetworkHandler", "아직 소켓연결 안됨.\n메시지전송 불가.");
		}
		
		switch(type)
		{
		case CREATE_ROOM :
			socket.emit(CREATE_ROOM_KEY, message);
			break;
		case ENTER_ROOM : 
			socket.emit(ENTER_ROOM_KEY, message);
			break;
		case START_METTING : 
			socket.emit(START_METTING_KEY, message);
			break;
		case STOP_METTING :
			socket.emit(STOP_METTING_KEY, message);
			break;
			
		default : 
			break;
		}
		
	}
	
	public void listen()
	{
		socket.on(Socket.EVENT_CONNECT, new Emitter.Listener()
		{
			@Override
			public void call(Object... args)
			{
			}

		}).on(Socket.EVENT_DISCONNECT, new Emitter.Listener()
		{
			@Override
			public void call(Object... args)
			{
				System.out.println("서버종료 이벤트 발생");
				socket.disconnect();
			}
		}).on("toclient", new Emitter.Listener()
		{
			@Override
			public void call(Object... args)
			{
				JSONObject obj = (JSONObject) args[0];

				try
				{
					System.out.println(obj.get("msg"));
				} catch (JSONException e)
				{
					e.printStackTrace();
				}
			}

		}).on("msg", new Emitter.Listener()
		{
			@Override
			public void call(Object... args)
			{
				JSONObject obj = (JSONObject) args[0];

				try
				{
					System.out.println(obj.get("msg"));
				} catch (JSONException e)
				{
					e.printStackTrace();
				}
			}

		}).on("exit", new Emitter.Listener()
		{
			@Override
			public void call(Object... args)
			{
				socket.disconnect();
			}
		});
	}
}
