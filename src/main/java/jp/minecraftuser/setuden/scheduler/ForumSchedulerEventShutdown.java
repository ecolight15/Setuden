
package jp.minecraftuser.setuden.scheduler;

import java.io.File;
import jp.minecraftuser.ecoframework.PluginFrame;
import static jp.minecraftuser.ecoframework.Utl.sendPluginMessage;

/**
 *
 * @author ecolight
 */
public class ForumSchedulerEventShutdown extends ForumSchedulerEvent {
    public ForumSchedulerEventShutdown(PluginFrame plg, int youbi, int hour, int min, TypeEvent type, String... params) {
        super(plg, youbi, hour, min, type, params);
        log.info("Loaded shutdown schedule("+ schedule.size() +")");
    }
    
    @Override
    public void run() {
        // 抑止ファイルがあったら何もしない
        File f = new File(plg.getDataFolder().getPath()+"/ignore_schedule");
        if (f.exists()) {
            log.info("ignore shutdown schedule");
            return;
        }
        
        // 初回は遅延処理のみ、2回目でシャットダウン開始する
        sendPluginMessage(plg, null, "サーバーをシャットダウンしています...");
        plg.getServer().shutdown();
    }

}
