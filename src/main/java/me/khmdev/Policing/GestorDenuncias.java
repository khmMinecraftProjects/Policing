package me.khmdev.Policing;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import me.khmdev.APIAuxiliar.whereIs.ConstantesServerSQL;
import me.khmdev.APIBase.API;
import me.khmdev.APIBase.Almacenes.sql.AlmacenSQL;
import me.khmdev.APIBase.Almacenes.sql.Consulta;
import me.khmdev.APIBase.Almacenes.sql.FieldSQL;
import me.khmdev.APIBase.Almacenes.sql.FieldSQLChange;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class GestorDenuncias extends BukkitRunnable implements Listener {
	private List<Denuncia> denuncias = new ArrayList<>();
	private static int maxId = 0;
	private AlmacenSQL sql;

	public GestorDenuncias() {
		sql = API.getInstance().getSql();

	}
	public List<String> getDenunciantes(){
		List<String> l=new ArrayList<>();
		for (Denuncia d : denuncias) {
			l.add(d.getPlayer());
		}
		return l;
	}
	public List<Denuncia> getDenuncias(){
		return denuncias;
	}
	public Denuncia getDenuncia(int i){
		return i<0||i>=denuncias.size()?null:denuncias.get(i);
	}
	@EventHandler
	public void logeIn(PlayerJoinEvent e) {
		if (e.getPlayer().hasPermission("denuncias.command")
				&& denuncias.size() != 0) {
			e.getPlayer().sendMessage(
					"Hay denuncias, usa /denuncias para verlas");
		}
	}

	@Override
	public void run() {
		Consulta c = sql.getValue(ConstantesBansSQL.tablaDenuncia,
				new FieldSQL(ConstantesBansSQL.denSolucionado, 0));
		if(c==null){return;}
		ResultSet r=c.getR();
		denuncias.clear();
		int i = maxId;
		if (r == null) {
			return;
		}
		try {
			while (r.next()) {
				Timestamp t = r.getTimestamp(ConstantesBansSQL.denFecha);
				String raz = r.getString(ConstantesBansSQL.denRazon);
				String ser = r.getString(ConstantesBansSQL.denServidor);
				String dencunciante = r
						.getString(ConstantesBansSQL.denDenunciante);
				String denunciado = r
						.getString(ConstantesBansSQL.denDenunciado);
				i = i >= r.getInt(ConstantesBansSQL.denId) ? i : r
						.getInt(ConstantesBansSQL.denId);
				Denuncia den = new Denuncia(dencunciante, denunciado, ser, raz,
						t);
				den.setId(r.getInt(ConstantesBansSQL.denId));
				denuncias.add(den);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			c.close();
		}
		if (i > maxId) {
			for (Player pl : Bukkit.getServer().getOnlinePlayers()) {
				if (pl.hasPermission("denuncias.command")) {
					pl.sendMessage("Hay nuevas denuncias, usa /denuncias para verlas");
				}
			}
		}
		maxId = i;
	}

	public void denunciar(Denuncia d) {
		denuncias.add(d);

		String sv=d.getServer();
		if (sv == null) {
			sv=ConstantesServerSQL.ServerNull;
		}
			sql.createField(
					ConstantesBansSQL.tablaDenuncia,
					new FieldSQL(ConstantesBansSQL.denDenunciante, d
							.getPlayer()),
					new FieldSQL(ConstantesBansSQL.denDenunciado, d
							.getDenunciado()),
					new FieldSQL(ConstantesBansSQL.denFecha, d.getTime()
							.toString()), new FieldSQL(
							ConstantesBansSQL.denRazon, d.getRazon()),
					new FieldSQL(ConstantesBansSQL.denServidor, sv));
		run();
	}
	public boolean solucionar(Denuncia d) {
		int id=d.getId();
		if(id==-1){
			return false;
		}
		sql.changeData(ConstantesBansSQL.tablaDenuncia, 
				new FieldSQLChange(ConstantesBansSQL.denSolucionado,
						1,new FieldSQL(ConstantesBansSQL.denId, id)));
		denuncias.remove(d);

		return true;
	}
	public Denuncia getDenunciaId(int i) {
		for (Denuncia d : denuncias) {
			if(d.getId()==i){
				return d;
			}
		}
		return null;
	}
}
