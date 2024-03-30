
package jp.minecraftuser.setuden.scheduler;

import java.text.SimpleDateFormat;
import java.util.Date;
import jp.minecraftuser.ecoframework.PluginFrame;
import static jp.minecraftuser.ecoframework.Utl.sendPluginMessage;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

/**
 *
 * @author ecolight
 */
public class ForumSchedulerEventSignal extends ForumSchedulerEvent {
    private SimpleDateFormat sdf1 = new SimpleDateFormat("HH");
    public ForumSchedulerEventSignal(PluginFrame plg, int youbi, int hour, int min, TypeEvent type, String... params) {
        super(plg, youbi, hour, min, type, params);
        log.info("Loaded signal schedule("+ schedule.size() +")");
    }
    
    @Override
    public void run() {
        // 時報処理
        for (Player p : plg.getServer().getOnlinePlayers()) {
            sendPluginMessage(plg, p, "時報:{0}時になりました", sdf1.format(new Date()));
            p.getWorld().playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 0.5f, 2.0f);
//            p.getWorld().playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 0.5f, 2.0f);
        }
    }
}
