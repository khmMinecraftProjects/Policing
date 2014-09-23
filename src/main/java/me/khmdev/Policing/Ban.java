package me.khmdev.Policing;

import java.sql.Timestamp;

public class Ban {
	private String player,server,razon;
	private Timestamp time;
	private int id=-1;
	public Ban(String pl,String se,String ra,Timestamp t){
		player=pl;server=se;razon=ra;time=t;
	}
	public String getPlayer() {
		return player;
	}

	public String getServer() {
		return server;
	}

	public String getRazon() {
		return razon;
	}
	public Timestamp getTime() {
		return time;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
}
