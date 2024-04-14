
package jp.minecraftuser.setuden.scheduler;

import java.io.File;
import java.text.MessageFormat;
import jp.minecraftuser.ecoframework.PluginFrame;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/**
 *
 * @author ecolight
 */
public class ForumSchedulerEventKick extends ForumSchedulerEvent{
    public ForumSchedulerEventKick(PluginFrame plg, int youbi, int hour, int min, TypeEvent type, String... params) {
        super(plg, youbi, hour, min, type, params);
        log.info("Loaded kick schedule("+ schedule.size() +")");
    }
    
    @Override
    public void run() {
        // 抑止ファイルがあったら何もしない
        File f = new File(plg.getDataFolder().getPath()+"/ignore_schedule");
        if (f.exists()) {
            log.info("ignore kick schedule");
            return;
        }
        
        // 全ユーザのKICK処理
        if (params.length >= 2 && plg.getPluginFrame("EcoAdmin") != null) {
            //EcoAdmin側でkickする。
            //メッセージの引数を渡してるけど、kickメッセージはMysqlPlayerDataBridgeのsaveAndKickCommandが表示されます。
            //引数を渡さない場合ロックされません。
            plg.getServer().dispatchCommand(plg.getServer().getConsoleSender(), "ecoadmin:lock "+params[1]);
        }else{
            String command = "mpdb saveAndkick";
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
        }
    }
}
