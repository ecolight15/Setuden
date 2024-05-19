package jp.minecraftuser.setuden.listener;


import jp.minecraftuser.setuden.Setuden;
import jp.minecraftuser.setuden.managers.WarpManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

public class WarpMessageListener implements PluginMessageListener {

    /**
     * プラグインメッセージ処理リスナークラス
     * @author ecolight
     */
    @Override
    public void onPluginMessageReceived(String channel, Player player, byte[] message) {
        DataInputStream in = new DataInputStream(new ByteArrayInputStream(message));
        String task = null;
        try {
            task = in.readUTF();

            if (task.equals("TeleportPlayerToLocation")) {
                //受信したメッセージを元にプレイヤーを指定の座標にテレポートする
                WarpManager.warpPlayerToLocation(in.readUTF(), new Location(Bukkit.getWorld(in.readUTF()), in.readDouble(), in.readDouble(), in.readDouble(), in.readFloat(), in.readFloat()));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (task.equals("GetVersion")) {
            String name = null;
            try {
                name = in.readUTF();
            } catch (IOException e) {

            }
            if (name != null) {
                Player p = Bukkit.getPlayer(name);

                p.sendMessage(ChatColor.RED + "warps - " + ChatColor.GOLD + Setuden.instance.getDescription().getVersion());
            }
            WarpManager.sendVersion();
            Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "warps - " + ChatColor.GOLD + Setuden.instance.getDescription().getVersion());
        }

    }
}
