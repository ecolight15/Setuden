package jp.minecraftuser.setuden.tasks;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import jp.minecraftuser.setuden.Setuden;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class PluginMessageTask extends BukkitRunnable {

	private final ByteArrayOutputStream bytes;

	public PluginMessageTask(ByteArrayOutputStream bytes) {
		this.bytes = bytes;
	}
	
	public void run() {
		getOnlinePlayers().get(0).sendPluginMessage(
				Setuden.instance,
				Setuden.OUTGOING_PLUGIN_CHANNEL,
				bytes.toByteArray());
		
	}
	
	public static List<Player> getOnlinePlayers() {
		List<Player> players = new ArrayList<>();
		for (Player all : Bukkit.getOnlinePlayers()) {
			players.add(all);
		}
		return players;
	}
		
}