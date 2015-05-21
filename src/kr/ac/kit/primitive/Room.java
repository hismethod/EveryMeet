package kr.ac.kit.primitive;

import java.util.ArrayList;
import java.util.List;

/**
 * @author JM
 * Room 클래스는 회의가 진행되는 공간을 정의한다.
 * 쉽게말해서 회의실이다. Room == 회의실
 * 참여 멤버가 추가된다면 여기에 추가 될 것이다.
 */
public class Room
{
	/* Member */
	private String title;
	private String password;
	private String leaderName;
	private List<User> userList = new ArrayList<User>();
	
	/* Getter & Setter */
	public String getTitle(){return title;}
	public String getPassword(){return password;}
	public String getLeaderName()	{return leaderName;}
	public List<User> getUserList(){return userList;}
	public void setTitle(String title){this.title = title;}
	public void setPassword(String password){this.password = password;}
	public void setLeaderName(String leaderName){this.leaderName = leaderName;}
	public void setUserList(List<User> userList){this.userList = userList;}
	
	/* Constructor */
	public Room(){}

	public Room(String title, String password)
	{
		this.title = title;
		this.password = password;
	}
	
	public Room(User leader, String title, String password)
	{
		userList.add(leader);
		this.title = title;
		this.password = password;
		this.leaderName = leader.getName();
	}
	
	/* Methods */
	public void createRoom(String title, String password)
	{
		this.title = title;
		this.password = password;
	}
	
	public void createRoom(User leader, String title, String password)
	{
		userList.add(leader);
		this.title = title;
		this.password = password;
		this.leaderName = leader.getName();
	}
	
}
