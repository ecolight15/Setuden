
package jp.minecraftuser.setuden.scheduler;

import jp.minecraftuser.ecoframework.PluginFrame;
import static jp.minecraftuser.ecoframework.Utl.sendPluginMessage;
import org.bukkit.World;

/**
 *
 * @author ecolight
 */
public class ForumSchedulerEventSave extends ForumSchedulerEvent{
    public ForumSchedulerEventSave(PluginFrame plg, int youbi, int hour, int min, TypeEvent type, String... params) {
        super(plg, youbi, hour, min, type, params);
        log.info("Loaded save schedule("+ schedule.size() +")");
    }
    
    @Override
    public void run() {
        // SAVE処理
        sendPluginMessage(plg, null, "プレイヤーデータとワールドデータをセーブ中...");
        plg.getServer().savePlayers();
        for (World wl: plg.getServer().getWorlds()) {
            wl.save();
        }
        sendPluginMessage(plg, null, "プレイヤーデータとワールドデータのセーブが完了しました");
    }
}
