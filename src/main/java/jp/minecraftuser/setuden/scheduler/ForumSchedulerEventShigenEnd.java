
package jp.minecraftuser.setuden.scheduler;

import java.io.File;
import jp.minecraftuser.setuden.Setuden;
import jp.minecraftuser.ecoframework.PluginFrame;
import jp.minecraftuser.setuden.config.PluginConfigUpdate;
import static jp.minecraftuser.ecoframework.Utl.sendPluginMessage;
import jp.minecraftuser.ecogate.EcoGate;
import jp.minecraftuser.ecogate.config.EcoGateConfig;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.WorldBorder;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author ecolight
 */
public class ForumSchedulerEventShigenEnd extends ForumSchedulerEventShigen {
    public ForumSchedulerEventShigenEnd(PluginFrame plg, int youbi, int hour, int min, TypeEvent type, String... params) {
        super(plg, youbi, hour, min, type, params);
        log.info("Loaded generate shigen end schedule("+ schedule.size() +")");
    }
    
    @Override
    public void run() {
        // 抑止ファイルがあったら何もしない
        File f = new File(plg.getDataFolder().getPath()+"/ignore_schedule");
        if (f.exists()) {
            log.info("ignore generate shigen end schedule");
            return;
        }
        
        // 最新資源番号取得
        int resnum = ((Setuden)plg).getDefaultConfig().getInt("newestresource");

        // プラグインが不足していたら実行せずに終了
        EcoGate eg = (EcoGate) plg.getPluginFrame("EcoGate");
        if (eg == null) {
            return;
        }

        // 資源マップの削除作成
        World old = plg.getServer().getWorld("end"+(resnum-1));
        ((EcoGateConfig)eg.getDefaultConfig()).getWorlds().deleteWorld("end"+(resnum-1));
        World w = ((EcoGateConfig)eg.getDefaultConfig()).getWorlds().addEndWorld("end"+(resnum));

        // ゲートの貼り直し
        PluginConfigUpdate.replaceGate(plg, "end"+(resnum-1), "end"+(resnum), "end"+(resnum-1)+"b", "end"+(resnum)+"b", w.getName(), 40);

        // ワールドボーダー設定
        PluginConfigUpdate.clearBorder(plg, "end"+(resnum-1));
        PluginConfigUpdate.setBorder(plg, "end"+(resnum),0,0,2000);

        WorldBorder bd = w.getWorldBorder();
        bd.setCenter(0, 0);
        bd.setDamageAmount(2);
        bd.setDamageBuffer(10);
        bd.setSize(1000);
        bd.setWarningDistance(20);
        bd.setWarningTime(10);

        // ログブロックファイル更新
        PluginConfigUpdate.moveEndLogBlock(plg, "end"+(resnum-1), "end"+(resnum), resnum-1);

        // PermissionsEx権限設定
        PluginConfigUpdate.moveEndPermissions(plg, "end"+(resnum-1), "end"+(resnum));

        // 資源ベース作成
        Location l = w.getSpawnLocation();
        l.setX(0.0);
        l.setY(40.0);
        l.setZ(0.0);
        Builder.mkbaseend(l);

        // 保護を作成
        Server server = plg.getServer();
        CommandSender sender = plg.getServer().getConsoleSender();
        server.dispatchCommand(sender, "/world " + w.getName());
        server.dispatchCommand(sender, "/pos1 16,39,16");
        server.dispatchCommand(sender, "/pos2 -17,39,-17");
        server.dispatchCommand(sender, "region define -w " + w.getName() + " base");
        server.dispatchCommand(sender, "region flag base enderdrafon-block-damage -w " + w.getName() + " deny");

        // 旧資源にいる人を強制転送
        for (Player p : old.getPlayers() ) {
            p.teleport(w.getSpawnLocation());
        }
        sendPluginMessage(plg, null, "資源エンド Version.{0} が生成されました。", Integer.toString(resnum+1));
    }
}
