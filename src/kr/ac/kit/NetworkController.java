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
	 * ��Ʈ��ũ �ڵ鷯
	 * �������� ��� ��Ʈ��ũ ����� ���⼭ ����Ѵ�.
	 * ��Ʈ��ũ �ڵ鷯 ��ü�� �����Ͽ� ����ϴ� ����� �ΰ����� �����Ѵ�.
	 * 1. ��ü�� �����Ҷ� ip�� port�� �Ѱ��ش�.
	 * 2. �� ��ü�� ����� connect�Լ��� �̿��Ͽ� ������ ��´�.
	 * 
	 * �������� ������ ���Ͽ��ῡ ������ ��� ���ܸ� �߻���Ų��.
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
			System.err.println("���Ͽ������\n");
		}
		
		socket.connect();
	}
	
	/**
	 * ��Ʈ��ũ �ڵ鷯 ��ü�� ����� ���� �ʱ� ���Ͽ��ῡ ����ϰų�
	 * �̹� ����Ǿ��� ������ ���� ������ ��� ����Ѵ�.
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
			System.err.println("���Ͽ������\n");
		}
		
		socket.connect();
	}
	
	/***************************************************
	 * �� �Ʒ��� �������� ��� �޼ҵ带 ������ �Ѵ�. *
	 ***************************************************/
	public void sendMessage(int type, Object message)
	{
		if(socket == null)
		{
			Log.e("NetworkHandler", "���� ���Ͽ��� �ȵ�.\n�޽������� �Ұ�.");
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
				System.out.println("�������� �̺�Ʈ �߻�");
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
