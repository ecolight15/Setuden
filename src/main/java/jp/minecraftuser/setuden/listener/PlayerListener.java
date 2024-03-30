/**
 * プレイヤー関連イベントリスナ―
 * 
 * PlayerQuit
 *   whoisログ
 * PlayerInteract
 *   スポーンエッグの利用制限機能
 * PlayerInteractEntity
 *   スポーンエッグの利用制限機能
 * PlayerJoin
 *   新規プレイヤー検出
 *   whoisログ
 */

package jp.minecraftuser.setuden.listener;

import java.io.File;
import jp.minecraftuser.ecoframework.PluginFrame;
import jp.minecraftuser.ecoframework.ListenerFrame;
import static jp.minecraftuser.ecoframework.Utl.sendPluginMessage;
import jp.minecraftuser.setuden.timer.LoginDBTimer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.PlayerInventory;

/**
 * プレイヤーイベント処理リスナークラス
 * @author ecolight
 */
public class PlayerListener extends ListenerFrame {
    private static int prev_visitor = -1;
    /**
     * コンストラクタ
     * @param plg_ プラグインインスタンス
     * @param name_ 名前
     */
    public PlayerListener(PluginFrame plg_, String name_) {
        super(plg_, name_);
        int visitor = new File(plg.getServer().getWorlds().get(0).getWorldFolder().getPath()+"/playerdata").listFiles().length;
        prev_visitor = visitor;
    }

    @EventHandler(priority=EventPriority.LOWEST)
    public void PlayerQuit(PlayerQuitEvent event) {
        Player pl = event.getPlayer();
        new LoginDBTimer(plg, "logout_timer", pl, false).runTaskLaterAsynchronously(plg, 1);
        log.info("★ PlayerQuit["+event.getPlayer().getName()+"]");
    }

    /**
     * エコ卵ガードイベント処理
     * @param event イベント情報
     */
    @EventHandler
    public void PlayerInteract(PlayerInteractEvent event)
    {
        if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            if (event.getItem() != null) {
                if (event.getItem().getType().name().equalsIgnoreCase("MONSTER_EGG")) {
                    if (!event.getPlayer().isOp()) {
                        sendPluginMessage(plg, event.getPlayer(), "現在スポーンエッグの利用は制限されています");
                        event.setCancelled(true);
                    }
                }
            }
        }
    }

    @EventHandler
    public void PlayerInteractEntity(PlayerInteractEntityEvent event)
    {
        if (!event.getPlayer().isOp()) {
            PlayerInventory i = event.getPlayer().getInventory();
            if (i.getItemInMainHand() != null) {
                if (i.getItemInMainHand().getType().name().equalsIgnoreCase("MONSTER_EGG")) {
                    sendPluginMessage(plg, event.getPlayer(), "現在スポーンエッグの利用は制限されています");
                    event.setCancelled(true);
                }
            }
            if (i.getItemInOffHand() != null) {
                if (i.getItemInOffHand().getType().name().equalsIgnoreCase("MONSTER_EGG")) {
                    sendPluginMessage(plg, event.getPlayer(), "現在スポーンエッグの利用は制限されています");
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler(priority=EventPriority.LOWEST)
    public void PlayerJoin(PlayerJoinEvent event) {
        Player pl = event.getPlayer();
        log.info("PlayerJoinEvent:" + event.getPlayer().getName());

        // 新規プレイヤー検出処理
        plg.getServer().savePlayers();
        int visitor = new File(plg.getServer().getWorld(conf.getString("level-name")).getWorldFolder().getPath()+"/playerdata").listFiles().length;
        if (prev_visitor != visitor) {
            plg.getServer().broadcastMessage("["+pl.getName()+"]さんは"+visitor+"人目の新規さんです。");
        }
        prev_visitor = visitor;
        new LoginDBTimer(plg, "logout_timer", pl, true).runTaskLaterAsynchronously(plg, 1);
     }

//    @EventHandler(priority=EventPriority.LOWEST)
//    public void InventoryClick(InventoryClickEvent event) {
//        if (event.getWhoClicked() instanceof Player) {
//            Player p = (Player) event.getWhoClicked();
//            if (!p.isOp()) {
//                if (p.getGameMode() != GameMode.SURVIVAL) {
//                    event.setCancelled(true);
//                    p.sendMessage("クリエイティブにおけるインベントリ操作は抑止されています");
//                }
//            }
//        }
//    }
    
}
