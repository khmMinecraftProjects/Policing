package me.khmdev.Policing;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import me.khmdev.APIAuxiliar.whereIs.ConstantesServerSQL;
import me.khmdev.APIBase.API;
import me.khmdev.APIBase.Almacenes.local.ConfigFile;
import me.khmdev.APIBase.Almacenes.sql.AlmacenSQL;
import me.khmdev.APIBase.Almacenes.sql.Consulta;
import me.khmdev.APIBase.Almacenes.sql.FieldSQL;
import me.khmdev.APIBase.Almacenes.sql.FieldSQLChange;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class border extends BukkitRunnable implements Listener {
	private HashMap<String, Ban> bans = new HashMap<>();
	private List<String> bn = new ArrayList<>(), kk = new ArrayList<>();
	private Timestamp now = new Timestamp(Calendar.getInstance()
			.getTimeInMillis());
	private AlmacenSQL sql;

	public List<String> getBanR() {
		return bn;
	}

	public List<String> getkickR() {
		return kk;
	}

	public border(JavaPlugin pl) {
		sql = API.getInstance().getSql();
		ConfigFile conf = new ConfigFile(pl.getDataFolder(), "Mensajes");
		FileConfiguration sec = conf.getConfig();
		if (!sec.isList("Bans")) {
			sec.set("Bans", bn);
		}
		bn = sec.getStringList("Bans");
		if (!sec.isList("Kicks")) {
			sec.set("Kicks", kk);
		}
		kk = sec.getStringList("Kicks");
		conf.saveConfig();
	}

	@EventHandler
	public void logeIn(PlayerJoinEvent e) {
		isBan(e.getPlayer());
	}

	private void isBan(Player pl){
		now = new Timestamp(Calendar.getInstance().getTimeInMillis());
		Ban b = bans.get(pl.getName());
		if (b != null) {
			if (now.compareTo(b.getTime()) >= 0) {
				bans.remove(pl.getName());
			} else {
				if (b.getTime() != null) {
					pl.kickPlayer(
							"Baneado hasta " + b.getTime().toString()
									+ "\nPor " + b.getRazon());
				} else {
					pl.kickPlayer(
							"Ban permanente por " + b.getRazon());
				}
			}
		}
	}
	@Override
	public void run() {
		sql.sendUpdate("UPDATE `" + ConstantesBansSQL.tablaBan + "` SET "
				+ ConstantesBansSQL.banCaducado + " = '1' WHERE "
				+ ConstantesBansSQL.banTipo + " = " + ConstantesBansSQL.tipoBan
				+ " and " + ConstantesBansSQL.banFecha
				+ " <= CURRENT_TIMESTAMP and " + ConstantesBansSQL.banCaducado
				+ " = '0'");

		Consulta c = sql.getValue(ConstantesBansSQL.tablaBan, new FieldSQL(
				ConstantesBansSQL.banCaducado, 0));
		if (c == null) {
			return;
		}
		ResultSet r=c.getR();
		try {
			while (r.next()) {
				if (r.getString(ConstantesBansSQL.banTipo).equalsIgnoreCase(
						ConstantesBansSQL.tipoBan)) {
					System.out.println("ban");
					Timestamp t = r.getTimestamp(ConstantesBansSQL.banFecha);
					String raz = r.getString(ConstantesBansSQL.banRazon);
					String ser = r.getString(ConstantesBansSQL.banServidor);
					String us = r.getString(ConstantesBansSQL.banUsuario);
					int id= r.getInt(ConstantesBansSQL.banId);
					Ban b = new Ban(us, ser, raz, t);
					b.setId(id);
					bans.put(us, b);
				}else{
					String us = r.getString(ConstantesBansSQL.banUsuario);
					Player pl=Bukkit.getPlayerExact(us.trim());
					if(pl!=null){
						pl.kickPlayer(
								"Has sido kickeado por "+
										r.getString(ConstantesBansSQL.banRazon)
								);
						int id= r.getInt(ConstantesBansSQL.banId);

						sql.changeData(ConstantesBansSQL.tablaBan,
								new FieldSQLChange(ConstantesBansSQL.banCaducado,1,
										new FieldSQL(ConstantesBansSQL.banId, id)));
					}
				}

			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			c.close();
		}
		for (Player pl : Bukkit.getServer().getOnlinePlayers()) {
			isBan(pl);
		}
	}

	public Collection<Ban> getBans() {
		return bans.values();
	}

	public String getBanR(int i) {
		return i < 0 || i >= bn.size() ? null : bn.get(i);
	}

	public String getKickR(int i) {
		return i < 0 || i >= kk.size() ? null : kk.get(i);
	}

	public void banear(Ban b) {
		String sv = b.getServer();
		if (sv == null) {
			sv = ConstantesServerSQL.ServerNull;
		}
		String t = "null";
		if (b.getTime() != null) {
			t = b.getTime().toString();
		}
		sql.createField(ConstantesBansSQL.tablaBan, new FieldSQL(
				ConstantesBansSQL.banUsuario, b.getPlayer()), new FieldSQL(
				ConstantesBansSQL.banRazon, b.getRazon()), new FieldSQL(
				ConstantesBansSQL.banServidor, sv), new FieldSQL(
				ConstantesBansSQL.banFecha, t));
		run();
	}

	public Ban getBan(String string) {
		return bans.get(string);
	}

	public boolean desBanear(Ban b) {
		int id = b.getId();
		if (id == -1) {
			return false;
		}
		sql.changeData(ConstantesBansSQL.tablaBan, new FieldSQLChange(
				ConstantesBansSQL.banCaducado, 1, new FieldSQL(
						ConstantesBansSQL.banCaducado, id)));
		bans.remove(b);

		return true;
	}

	public Ban getBanID(int i) {
		for (Ban b : bans.values()) {
			if (b.getId() == i) {
				return b;
			}
		}
		return null;
	}

	public void kick(Player v, String r) {
		v.kickPlayer(r);
		sql.createField(ConstantesBansSQL.tablaBan, new FieldSQL(
				ConstantesBansSQL.banUsuario, v.getName()), new FieldSQL(
				ConstantesBansSQL.banTipo, ConstantesBansSQL.tipoKick),
				new FieldSQL(ConstantesBansSQL.banRazon, r), new FieldSQL(
						ConstantesBansSQL.banCaducado, 1));
	}

	public void kick(String v, String r) {
		sql.createField(ConstantesBansSQL.tablaBan, new FieldSQL(
				ConstantesBansSQL.banUsuario, v), new FieldSQL(
				ConstantesBansSQL.banTipo, ConstantesBansSQL.tipoKick),
				new FieldSQL(ConstantesBansSQL.banRazon, r), new FieldSQL(
						ConstantesBansSQL.banCaducado, 0));
	}
}
