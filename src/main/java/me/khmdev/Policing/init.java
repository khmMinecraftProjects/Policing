package me.khmdev.Policing;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

public class init extends JavaPlugin {
	private Base base;
	private static boolean Bungee = true;

	public static boolean isBungee() {
		return Bungee;
	}

	public void onEnable() {
		if (!hasPluging("APIAuxiliar")) {
			getLogger().severe(
					getName()
							+ " se desactivo debido ha que no encontro la API");
			setEnabled(false);
			return;
		}
		// Bungee=hasPluging("BungeeCord");
		base = new Base(this);
	}

	public boolean onCommand(CommandSender sender, Command cmd, String label,
			String[] args) {

		if (base.onCommand(sender, cmd, label, args)) {
			return true;
		}

		return false;
	}

	public static boolean hasPluging(String s) {
		try {
			return Bukkit.getPluginManager().getPlugin(s).isEnabled();
		} catch (Exception e) {

		}
		return false;
	}

	@Override
	public void onDisable() {
		ListenDenuncia.eliminarEscritores();
	}

}
