package me.khmdev.Policing;

import java.sql.Timestamp;

import org.bukkit.ChatColor;

import me.khmdev.APIAuxiliar.whereIs.SQLControl;

public class Denuncia {
	private String denunciante,denunciado,server,razon;
	private Timestamp time;
	private int id=-1;
	public Denuncia(String denunciante,String denunciado,
			String se,String ra,Timestamp t){
		this.denunciante=denunciante;server=se;razon=ra;time=t;
		this.denunciado=denunciado;
	}
	public String getPlayer() {
		return denunciante;
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
	public String getDenunciado() {
		return denunciado;
	}
	public void addMensaje(String message) {
		razon+=message;
	}
	public void setRazon(String r) {
		razon=r;
	}
	private static final String a=ChatColor.DARK_PURPLE.toString(),
			b=ChatColor.GREEN.toString();
    public String toString() {
    	String s="";
    	s+=a+"Fecha: "+b+time.toString()+"\n";
    	s+=a+"Denunciante: "+b+denunciante+"\n";
    	s+=a+"Denunciado: "+b+denunciado+"\n";
    	s+=a+"Servidor: "+b+SQLControl.getServerByName(server)+"\n";
    	s+=a+"Razon:\n";
    	s+=razon;
    	return s;
    }
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
}
