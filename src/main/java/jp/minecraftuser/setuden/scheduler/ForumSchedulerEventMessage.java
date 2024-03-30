
package jp.minecraftuser.setuden.scheduler;

import java.io.File;
import jp.minecraftuser.ecoframework.PluginFrame;
import static jp.minecraftuser.ecoframework.Utl.sendPluginMessage;

/**
 *
 * @author ecolight
 */
public class ForumSchedulerEventMessage extends ForumSchedulerEvent{
    public ForumSchedulerEventMessage(PluginFrame plg, int youbi, int hour, int min, TypeEvent type, String... params) {
        super(plg, youbi, hour, min, type, params);
        log.info("Loaded message schedule("+ schedule.size() +")");
    }

    @Override
    public void run() {
        // 抑止ファイルがあったら何もしない
        File f = new File(plg.getDataFolder().getPath()+"/ignore_schedule");
        if (f.exists()) {
            log.info("ignore generate message schedule: " + params[0]);
            return;
        }
        
        // 全ユーザにメッセージ送出処理
        sendPluginMessage(plg, null, params[0]);
    }

}
