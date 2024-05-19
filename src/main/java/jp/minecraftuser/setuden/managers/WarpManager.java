package jp.minecraftuser.setuden.managers;

import jp.minecraftuser.setuden.Setuden;
import jp.minecraftuser.setuden.tasks.PluginMessageTask;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashMap;

public class WarpManager {

    public static HashMap<String, Location> pendingTeleports = new HashMap<>();

    //ワープ座標の設定を送信するメソッド
    //globalがtrueなら別鯖からでもアクセスできる
    public static void sendSetWarp(CommandSender sender, String warp_name, Location loc, boolean hidden, boolean global) {
        ByteArrayOutputStream b = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(b);
        try {
            out.writeUTF("SetWarp");
            out.writeUTF(sender.getName());
            out.writeUTF(warp_name);
            out.writeUTF(loc.getWorld().getName());
            out.writeDouble(loc.getX());
            out.writeDouble(loc.getY());
            out.writeDouble(loc.getZ());
            out.writeFloat(loc.getYaw());
            out.writeFloat(loc.getPitch());
            out.writeBoolean(hidden);
            out.writeBoolean(global);
        } catch (IOException e) {
            e.printStackTrace();
        }
        new PluginMessageTask(b).runTaskAsynchronously(Setuden.instance);

    }

    //ワープへの移動を送信するメソッド
    public static void sendWarpPlayer(CommandSender sender, String warp_player, String warp_name, boolean permission, boolean bypass) {
        ByteArrayOutputStream b = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(b);
        try {
            out.writeUTF("WarpPlayer");
            out.writeUTF(sender.getName());
            out.writeUTF(warp_player);
            out.writeUTF(warp_name);
            out.writeBoolean(permission);
            out.writeBoolean(bypass);

        } catch (IOException e) {
            e.printStackTrace();
        }
        new PluginMessageTask(b).runTaskAsynchronously(Setuden.instance);
    }

    //受信した座標にテレポートするメソッド
    public static void warpPlayerToLocation(final String player, Location location) {
        Player p = Bukkit.getPlayer(player);
        if (p != null) {
            //既にプレイヤーが入ればテレポート
            p.teleport(location);
        } else {
            //まだプレイヤーのログインが終わっていない場合は保留リストに追加
            pendingTeleports.put(player, location);

            //100経過後に保留リストから削除
            Bukkit.getScheduler().runTaskLaterAsynchronously(Setuden.instance, new Runnable() {
                @Override
                public void run() {
                    if (pendingTeleports.containsKey(player)) {
                        pendingTeleports.remove(player);
                    }
                }
            }, 100);
        }
    }

    public static void sendVersion() {
        ByteArrayOutputStream b = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(b);
        try {
            out.writeUTF("sendversion");
            out.writeUTF(ChatColor.RED + "warps - " + ChatColor.GOLD + Setuden.instance.getDescription().getVersion());
        } catch (IOException e) {
            e.printStackTrace();
        }
        new PluginMessageTask(b).runTaskAsynchronously(Setuden.instance);
    }
}
