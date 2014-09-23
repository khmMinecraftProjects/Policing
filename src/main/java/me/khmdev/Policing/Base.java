package me.khmdev.Policing;

import java.sql.Timestamp;
import java.util.Calendar;

import me.khmdev.APIAuxiliar.whereIs.SQLControl;
import me.khmdev.APIBase.API;
import me.khmdev.APIBase.Almacenes.sql.player.SQLPlayerData;
import me.khmdev.APIBase.Auxiliar.Auxiliar;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public class Base implements Listener {
	private static Base instance;
	private JavaPlugin plugin;
	private GestorDenuncias denuncias;
	private border border;
	private static final int timeBorder = 30, timeDenuncias = 30;
	private ItemStack book = new ItemStack(Material.BOOK_AND_QUILL);

	public Base(JavaPlugin pl) {

		plugin = pl;
		instance = this;
		if (API.getInstance().getSql().isEnable()) {
			/*
			 * Denuncias
			 */
			denuncias = new GestorDenuncias();
			Bukkit.getServer().getPluginManager().registerEvents(denuncias, pl);
			Bukkit.getServer().getPluginManager()
					.registerEvents(new ListenDenuncia(), pl);

			Bukkit.getServer()
					.getScheduler()
					.runTaskTimerAsynchronously(pl, denuncias, 20,
							timeDenuncias * 20);

			/*
			 * Border
			 */
			border = new border(pl);
			Bukkit.getServer().getPluginManager()
			.registerEvents(border, pl);
			Bukkit.getServer()
					.getScheduler()
					.runTaskTimerAsynchronously(pl, border, 
							20, timeBorder * 20);
			/*
			 * send sql
			 */
			for (String s : ConstantesBansSQL.sql) {
				API.getInstance().getSql().sendUpdate(s);
			}
		}
	}

	private String helpDenunciar() {
		String s = a+"/denunciar <usuario> para denunciar un usuario";

		return s;
	}

	private String helpDenuncias() {
		String s = a+"/denuncias listar"+b+"               listar todas las denuncias\n";
		s += a+"/denuncias <id>"+b+"                 ver denuncia\n";
		s += a+"/denuncias solucionado <id>"+b+"     eliminar denuncia";

		return s;
	}
	private String a=ChatColor.GREEN.toString(),b=ChatColor.DARK_AQUA.toString(),
			c=ChatColor.BLUE.toString(),d=ChatColor.DARK_PURPLE.toString(),
			e=ChatColor.RED.toString();
	private String helpBorde() {
		String s = a+"/border ban <usuario> <tiempo> <razon/>"+b+"    banear usuario\n";
		s += b+"<tiempo> : (0-9)*(m/h/d)   Ejemplo: 32d = 32 dias\n";
		s += b+"<tiempo> : permanente   para ban permanente\n";

		s += a+"/border kick <usuario> <razon/id>"+b+"    kickear usuario\n";	
		s += a+"/border Fkick <usuario> <razon/id>"+b+"   kickear en la nw o al conectarse\n";
		s += a+"/border unban <usuario>"+b+"  listar baneados\n";
		s += a+"/border kickR"+b+"            listar mensajes de kick\n";
		s += a+"/border banR"+b+"             listar mensajes de kick\n";
		s += a+"/border bans"+b+"             listar baneados\n";

		return s;
	}

	public boolean onCommand(CommandSender sender, Command cmd, String label,
			String[] args) {
		if (cmd.getName().equalsIgnoreCase("denunciar")) {
			if (args.length == 0) {
				sender.sendMessage(helpDenunciar());
				return true;
			}
			Player pl = sender instanceof Player ? (Player) sender : null;
			if (pl == null) {
				return true;
			}
			if (!existe(args[0])) {
				sender.sendMessage(e+"El usuario " + args[0]
						+ " nunca se ha conectado");
				return true;
			}
			if(pl.getInventory().firstEmpty()==-1){
				sender.sendMessage(e+"Inventario lleno");
				return true;
			}
			ListenDenuncia.eliminarEscritor(pl);

			
			//ItemMeta meta=book.getItemMeta();
			//meta.setDisplayName(d+"Denuncia para "+args[0]);
			//book.setItemMeta(meta);
			pl.getInventory().addItem(book);
			ListenDenuncia.addEscritor(book, pl, args[0]);
			sender.sendMessage(a+"Se te a dado un libro");
			sender.sendMessage(a+"Escribe las razones de la denuncia y firmalo cundo termines");

			return true;
		}
		if (cmd.getName().equalsIgnoreCase("denuncias")) {
			if (args.length == 0) {
				sender.sendMessage(helpDenuncias());
				return true;
			}
			if (args[0].equalsIgnoreCase("listar")) {
				if(denuncias.getDenuncias().size()==0){
					sender.sendMessage(e+"No hay denuncias");
					return true;
				}
				for (Denuncia d : denuncias.getDenuncias()) {
					sender.sendMessage(c+d.getId() + "- Denunciado: "
							+ d.getDenunciado() + ", Denunciante: "
							+ d.getPlayer());
				}
				return true;
			} else if (args.length >= 2
					&& args[0].equalsIgnoreCase("solucionado")) {
				int i = Auxiliar.getNatural(args[1], -1);
				Denuncia d = denuncias.getDenunciaId(i);
				if (d == null) {
					sender.sendMessage(e+"No existe esa denuncia");
					return true;
				}
				denuncias.solucionar(d);
				sender.sendMessage(a+"Denuncia solucionada");
				return true;
			} else {
				int i = Auxiliar.getNatural(args[0], -1);
				Denuncia d = denuncias.getDenunciaId(i);
				if (d == null) {
					sender.sendMessage(e+"No existe esa denuncia");
					return true;
				}
				sender.sendMessage(d.toString());

				return true;
			}

		}
		if (cmd.getName().equalsIgnoreCase("border")) {
			if (args.length == 0) {
				sender.sendMessage(helpBorde());
				return true;
			}
			if (args[0].equalsIgnoreCase("bans")) {
				if(border.getBans().size()==0){
					sender.sendMessage(e+"No hay usuarios baneados");
					return true;
				}
				for (Ban b : border.getBans()) {
					String t = b.getId() + "- " + b.getTime() == null ? "permanente"
							: b.getTime().toString();
					sender.sendMessage(c+b.getPlayer() + "(" + t + "): "
							+ b.getRazon());
				}
				return true;
			} else if (args[0].equalsIgnoreCase("banR")) {
				int i = 0;
				if(border.getBanR().size()==0){
					sender.sendMessage(e+"No mensajes predeterminados");
					return true;
				}
				for (String b : border.getBanR()) {
					sender.sendMessage(c+i + "- " + b);i++;
				}
				return true;
			} else if (args[0].equalsIgnoreCase("kickR")) {
				if(border.getkickR().size()==0){
					sender.sendMessage(e+"No mensajes predeterminados");
					return true;
				}
				int i = 0;
				for (String b : border.getkickR()) {
					sender.sendMessage(c+i + "- " + b);i++;
				}
				return true;
			} else if (args.length >= 2 && args[0].equalsIgnoreCase("unban")) {
				Ban b = null;

				if (Auxiliar.isNumeric(args[1])) {
					b = border.getBanID(Auxiliar.getNatural(args[1], -1));
					if (b == null) {
						sender.sendMessage(e+"No se ha encontrado ban");
						return true;
					}
				}else{
					if (!existe(args[1])) {
						sender.sendMessage(e+"El usuario " + args[1]
								+ " nunca se ha conectado");
						return true;
					}
					b = border.getBan(args[1]);
					if (b == null) {
						sender.sendMessage(e+args[1] + " no esta baneado");
						return true;
					}
				}
				
				border.desBanear(b);
				sender.sendMessage(b+args[1] + " desbaneado");
				return true;
			}
			
			if (args.length < 3) {
				sender.sendMessage(helpBorde());
				return true;
			}
			
			if (args[0].equalsIgnoreCase("Fkick")) {
				
				if (!existe(args[1])) {
					sender.sendMessage(e+"El usuario " + args[1]
							+ " nunca se ha conectado");
					return true;
				}
				String r = args[2];
				if (Auxiliar.isNumeric(r)) {
					r = border.getKickR(Auxiliar.getNatural(r, -1));
					if (r == null) {
						sender.sendMessage(e+"No existe ese mensaje predeterminado");
						return true;
					}
				}else{
					r=getString(args, 2);
				}
				
				Player v = Bukkit.getPlayer(args[1]);
				if (v != null) {
					border.kick(v,r);
					sender.sendMessage(b+args[1] + " kickeado");
					return true;
				}
				
				border.kick(args[1],r);
				sender.sendMessage(b+args[1] + " sera kickeado");
				return true;
			} else if (args[0].equalsIgnoreCase("kick")) {
				Player v = Bukkit.getPlayer(args[1]);
				if (v == null) {
					sender.sendMessage(e+"El usuario " + args[1]
							+ " no esta conectado en este servidor");
					return true;
				}
				String r = args[2];
				if (Auxiliar.isNumeric(r)) {
					r = border.getKickR(Auxiliar.getNatural(r, -1));
					if (r == null) {
						sender.sendMessage(e+"No existe ese mensaje predeterminado");
						return true;
					}
				}else{
					r=getString(args, 2);
				}
				border.kick(v,r);
				sender.sendMessage(b+args[1] + " kickeado");
				return true;
			}

			if (args.length < 4) {
				sender.sendMessage(helpBorde());
				return true;
			}
			if (args[0].equalsIgnoreCase("ban")) {
				if (!existe(args[1])) {
					sender.sendMessage(e+"El usuario " + args[1]
							+ " nunca se ha conectado");
					return true;
				}
				Timestamp t = null;
				if (!args[2].equalsIgnoreCase("permanente")) {
					t = getTime(args[2]);
					if (t == null) {
						sender.sendMessage(e+"No ha escrito correctamente el tiempo");
						return true;
					}
				}
				String r = args[3];
				if (Auxiliar.isNumeric(r)) {
					r = border.getBanR(Auxiliar.getNatural(r, -1));
					if (r == null) {
						sender.sendMessage(e+"No existe ese mensaje predeterminado");
						return true;
					}
				}else{
					r=getString(args, 3);
				}
				
				Ban b = new Ban(args[0], SQLControl.getMyServerID(), r, t);
				border.banear(b);
				sender.sendMessage(b+args[1] + " baneado");
				return true;
			} 
			sender.sendMessage(helpBorde());
			return true;
		}

		return false;
	}
	private String getString(String[] ar,int i){
		String s="";
		for (int j = i; j < ar.length; j++) {
			s+=i;
			if(i!=ar.length-1){s+=" ";}
		}
		return s;
	}

	private Timestamp getTime(String s) {
		Timestamp t = null, now = new Timestamp(Calendar.getInstance()
				.getTimeInMillis());
		if (s.contains("m")) {
			int i = Auxiliar.getNatural(s.replace("m", ""), -1);
			if (i != -1) {
				t = new Timestamp(now.getTime() + i * 60000);
			}
		} else if (s.contains("h")) {
			int i = Auxiliar.getNatural(s.replace("h", ""), -1);
			if (i != -1) {
				t = new Timestamp(now.getTime() + i * 3600000);
			}
		} else if (s.contains("d")) {
			int i = Auxiliar.getNatural(s.replace("d", ""), -1);
			if (i != -1) {
				t = new Timestamp(now.getTime() + i * 86400000);
			}
		}
		return t;
	}

	private boolean existe(String string) {
		return SQLPlayerData.existUser(string);
	}

	public static Base getInstance() {
		return instance;
	}

	public Plugin getPlugin() {
		return plugin;
	}

	public GestorDenuncias getDenuncias() {
		return denuncias;
	}

	public border getBorder() {
		return border;
	}

}
