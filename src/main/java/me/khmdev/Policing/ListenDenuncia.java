package me.khmdev.Policing;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map.Entry;

import me.khmdev.APIAuxiliar.whereIs.SQLControl;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerEditBookEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

public class ListenDenuncia implements Listener {
	private static HashMap<ItemStack, Denuncia> escritores = new HashMap<>();

	/*
	 * @EventHandler public void chat(AsyncPlayerChatEvent e){
	 * if(escritores.containsKey(e.getPlayer())){ e.setCancelled(true);
	 * escritores.get(e.getPlayer()).addMensaje(e.getMessage()); } }
	 * 
	 * @EventHandler public void write(PlayerEditBookEvent e){ e.isSigning(); }
	 * public static Denuncia getDenuncia(Player pl){ return escritores.get(pl);
	 * } public static boolean estaEscribiendo(Player pl){ return
	 * escritores.containsKey(pl); } public static void addEscritor(Player pl,
	 * String denunciado,String sv){ escritores.put(pl,new
	 * Denuncia(pl.getName(), denunciado, sv, "", new
	 * Timestamp(Calendar.getInstance() .getTimeInMillis()))); } public static
	 * void finEscribir(Player pl){ escritores.remove(pl); } public static void
	 * addEscritor(Player pl, String string) { addEscritor(pl, string,
	 * SQLControl.getMyServerID()); }
	 */
	public static void addEscritor(ItemStack book, Player pl, String string) {
		addEscritor(book, pl, string, SQLControl.getMyServerID());
	}

	public static void addEscritor(ItemStack book, Player pl, String string,
			String sv) {
		escritores.put(book, new Denuncia(pl.getName(), string, sv, "",
				new Timestamp(Calendar.getInstance().getTimeInMillis())));
	}
	private static final String a=ChatColor.DARK_PURPLE.toString(),
			b=ChatColor.GREEN.toString(),c=ChatColor.RED.toString();
	@SuppressWarnings("deprecation")
	@EventHandler
	public void write(PlayerEditBookEvent e) {
		
		Denuncia den=escritores.get(e.getPlayer().getItemInHand());
		if(den==null){return;}
		if(e.isSigning()){
			String r="";
			r+=a+"-------"+b+e.getNewBookMeta().getTitle()+a+"-------\n";

			for (String s : e.getNewBookMeta().getPages()) {
				r+=a+"---------------------\n";
				r+=b+s+"\n";
				r+=a+"---------------------\n";
			}
			den.setRazon(r);
			e.getPlayer().sendMessage(b+"Tu denuncia ha sido enviada");
			Base.getInstance().getDenuncias().denunciar(den);
			escritores.remove(e.getPlayer().getName());
			e.getPlayer().getInventory().setItemInHand(null);
			e.getPlayer().updateInventory();
			return;
		}
		
		escritores.remove(e.getPlayer().getName());
		e.getPlayer().getInventory().setItemInHand(null);
		e.getPlayer().updateInventory();
		e.getPlayer().sendMessage(c+"Has desechado la denuncia");

	}

	@SuppressWarnings("deprecation")
	public static void eliminarEscritor(Player pl) {
		ItemStack it=null;
		for (Entry<ItemStack, Denuncia> e : escritores.entrySet()) {
			if(e.getValue().getPlayer()==pl.getName()){
				it=e.getKey();
				break;
			}
		}
		if(it!=null){
			pl.getInventory().remove(it);
			pl.updateInventory();
			escritores.remove(it);
		}
		
	}
	
	public static void eliminarEscritores() {
		for (Entry<ItemStack, Denuncia> e : escritores.entrySet()) {
			Player pl=Bukkit.getServer().getPlayer(e.getValue().getPlayer());
			if(pl!=null){
				ItemStack it=e.getKey();
				if(it!=null){
					pl.getInventory().remove(it);
					pl.updateInventory();
					escritores.remove(it);
				}
			}
		}
	}
	@SuppressWarnings("deprecation")
	@EventHandler
	public void onquit(PlayerQuitEvent ev) {
		ItemStack it=null;
		for (Entry<ItemStack, Denuncia> e : escritores.entrySet()) {
			if(e.getValue().getPlayer()==ev.getPlayer().getName()){
				it=e.getKey();
				break;
			}
		}
		if(it!=null){
			ev.getPlayer().getInventory().remove(it);
			ev.getPlayer().updateInventory();
			escritores.remove(it);
		}
	}
	
}
