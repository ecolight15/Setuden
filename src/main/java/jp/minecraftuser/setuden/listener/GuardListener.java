/**
 * 保護関連イベントリスナ―
 * 
 * PlayerTeleport
 *   神殿へのエンパ抑止
 * PlayerCommandPreprocess
 *   禁止コマンド検知処理(エントランスspawn/help/pl 他
*/

package jp.minecraftuser.setuden.listener;

import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import jp.minecraftuser.ecoframework.PluginFrame;
import jp.minecraftuser.ecoframework.ListenerFrame;
import static jp.minecraftuser.ecoframework.Utl.sendPluginMessage;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

/**
 * プレイヤーイベント処理リスナークラス
 * @author ecolight
 */
public class GuardListener extends ListenerFrame {
    /**
     * コンストラクタ
     * @param plg_ プラグインインスタンス
     * @param name_ 名前
     */
    public GuardListener(PluginFrame plg_, String name_) {
        super(plg_, name_);
    }

    @EventHandler(priority=EventPriority.LOWEST)
    public void PlayerTeleport(PlayerTeleportEvent event)
    {
        // エンパ抑止
        if (event.getCause() == TeleportCause.ENDER_PEARL) {
            Location loc = event.getTo();
            if (loc.getWorld().getName().equals("world")) {
                if ((Math.abs(loc.getX()) < 30) &&
                    (Math.abs(loc.getZ()) < 30)) {
                    Player p = event.getPlayer();
                    if (p != null) {
                        p.sendMessage("[" + plg.getName() + "] 神殿領域内へのテレポートは禁止されています");
                        try {
                            SimpleDateFormat sd = new SimpleDateFormat("[yyyy/MM/dd HH:mm:ss.SSS]");
                            StringBuilder sb = new StringBuilder(sd.format(new Date()));
                            sb.append(" SERVER SPAWN teleport[player=");
                            sb.append(p.getName());
                            sb.append("] worldname=");
                            sb.append(p.getWorld().getName());
                            sb.append(" loc=");
                            sb.append(p.getLocation().toString());
                            sb.append("\r\n");
                            File logf = new File(plg.getDataFolder().getAbsolutePath()+"/server_spawn_teleport_reject.txt");
                            FileWriter w = new FileWriter(logf, true);

                            w.write(sb.toString());
                            w.close();
                        } catch (Exception ex) {
                        }
                    }
                    event.setCancelled(true);
                }
            }
        }
    }
 
    @EventHandler(priority=EventPriority.LOWEST)
    public void PlayerCommandPreprocess(PlayerCommandPreprocessEvent e)
    {
        String s = e.getMessage();
        Player p = e.getPlayer();
        if (p.isOp()) return;
        SimpleDateFormat sd = new SimpleDateFormat("[yyyy/MM/dd HH:mm:ss.SSS]");
        if (
            (s.equals("/pl")) ||
            (s.startsWith("/pl ")) ||
            (s.equals("/plugin")) ||
            (s.startsWith("/plugin "))) {
            try {
                File log = new File(plg.getDataFolder().getAbsolutePath()+"/command_reject.txt");
                FileWriter w = new FileWriter(log, true);
                
                w.write(sd.format(new Date())+" player["+p.getName()+"] cmd["+s+"]\r\n");
                w.close();
            } catch (Exception ex) {
            }
            p.sendMessage("§eセキュリティ上の問題によりpluginコマンドの使用を抑止しています。");
//            p.kickPlayer("禁止コマンドの実行を検知、管理ログに記録されました。故意に実行したと判断される場合にはBAN(アクセス禁止処分)となる場合があります。");
            e.setCancelled(true);
            return;
        }
        if (
            (s.equals("/help")) ||
            (s.startsWith("/help ")) ||
            (s.equals("/?")) ||
            (s.startsWith("/? "))) {
            try {
                File log = new File(plg.getDataFolder().getAbsolutePath()+"/command_reject.txt");
                FileWriter w = new FileWriter(log, true);
                
                w.write(sd.format(new Date())+" player["+p.getName()+"] cmd["+s+"]\r\n");
                w.close();
            } catch (Exception ex) {
            }
            p.sendMessage("§eセキュリティ上の問題によりhelpコマンドの使用を抑止しています。");
//            p.kickPlayer("禁止コマンドの実行を検知、管理ログに記録されました。故意に実行したと判断される場合にはBAN(アクセス禁止処分)となる場合があります。");
            e.setCancelled(true);
            return;
        }
        if (
            (s.startsWith("/minecraft:gamemode")) ||
            (s.startsWith("/gamemode")) ||
            (s.startsWith("/ecoadmin:gm")) ||
            (s.startsWith("/gm")) ||
            (s.startsWith("/spigot:restart")) ||
            (s.startsWith("/bukkit:reload")) ||
            (s.startsWith("/minecraft:reload")) ||
            (s.startsWith("/reload")) ||
            (s.startsWith("/minecraft:stop")) ||
            (s.startsWith("/stop")) ||
            (s.equals("/minecraft:fill")) ||
            (s.equals("/fill")) ||
            (s.equals("/he")) ||
            (s.startsWith("/he ")) ||
            (s.equals("/hawkeye")) ||
            (s.startsWith("/hawkeye ")) ||
            (s.equals("/co ")) ||
            (s.startsWith("/minecraft:op ")) ||
            (s.startsWith("/op ")) ||
            (s.equals("/minecraft:deop")) ||
            (s.equals("/deop")) ||
            (s.startsWith("/deop ")) ||
            (s.startsWith("/lp ")) ||
            (s.startsWith("/luckperms:")) ||
            (s.startsWith("/perm")) ||
            (s.startsWith("/bukkit")) ||
            (s.startsWith("/minecraft:give ")) ||
            (s.startsWith("/give "))) {
            plg.getServer().broadcastMessage("プレイヤー["+p.getDisplayName()+"]が["+s+"]コマンドを試行しました");
            try {
                File log = new File(plg.getDataFolder().getAbsolutePath()+"/command_reject.txt");
                FileWriter w = new FileWriter(log, true);
                
                w.write(sd.format(new Date())+" player["+p.getName()+"] cmd["+s+"]\r\n");
                w.close();
            } catch (Exception ex) {
            }
            p.kickPlayer("禁止コマンドの実行を検知、管理ログに記録されました。故意に実行したと判断される場合にはBAN(アクセス禁止処分)となる場合があります。");
            e.setCancelled(true);
            return;
        }
        if (s.startsWith("/spawn")) {
            if (p.getWorld().getName().equalsIgnoreCase("world")) {
                sendPluginMessage(plg, p, "エントランスでのスポーンコマンドは禁止されています。");
                e.setCancelled(true);
                try {
                    File log = new File(plg.getDataFolder().getAbsolutePath()+"/spawn_reject.txt");
                    FileWriter w = new FileWriter(log, true);
                    w.write(sd.format(new Date())+" player["+p.getName()+"] world["+p.getWorld().getName()+"]\r\n");
                    w.close();
                } catch (Exception ex) {
                }
            }
        }
        
    }


}
