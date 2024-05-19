package jp.minecraftuser.setuden.listener;

import jp.minecraftuser.ecoframework.ListenerFrame;
import jp.minecraftuser.ecoframework.PluginFrame;
import jp.minecraftuser.setuden.managers.WarpManager;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerJoinEvent;
/**
 * プレイヤー接続イベント処理リスナークラス
 * @author ecolight
 */
public class WarpListener extends ListenerFrame {

    public WarpListener(PluginFrame plg_, String name_) {
        super(plg_, name_);

    }

    //プレイヤー接続の際に、テレポートが保留状態であれば、テレポートする
    @EventHandler(priority = EventPriority.HIGHEST)
    public void playerConnect(PlayerJoinEvent e) {
        if (WarpManager.pendingTeleports.containsKey(e.getPlayer().getName())) {
            Location l = WarpManager.pendingTeleports.get(e.getPlayer().getName());
            e.getPlayer().teleport(l);
        }
    }

}
