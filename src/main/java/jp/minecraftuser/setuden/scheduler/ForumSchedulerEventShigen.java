
package jp.minecraftuser.setuden.scheduler;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.Random;
import jp.minecraftuser.setuden.Setuden;
import jp.minecraftuser.ecoframework.PluginFrame;
import jp.minecraftuser.setuden.config.SetudenConfig;
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
public class ForumSchedulerEventShigen extends ForumSchedulerEvent {
    public ForumSchedulerEventShigen(PluginFrame plg, int youbi, int hour, int min, TypeEvent type, String... params) {
        super(plg, youbi, hour, min, type, params);
        log.info("Loaded generate shigen schedule("+ schedule.size() +")");
    }
    
    @Override
    public void run() {
        
        // 抑止ファイルがあったら何もしない
        File f = new File(plg.getDataFolder().getPath()+"/ignore_schedule");
        if (f.exists()) {
            log.info("ignore generate shigen schedule");
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
        World old = plg.getServer().getWorld("shigen"+(resnum-1));
        ((EcoGateConfig)eg.getDefaultConfig()).getWorlds().deleteWorld("shigen"+(resnum-1));
        World w = ((EcoGateConfig)eg.getDefaultConfig()).getWorlds().addNormalWorld("shigen"+(resnum+1));
        ((Setuden)plg).getDefaultConfig().getConf().set("newestresource", resnum + 1);
        ((SetudenConfig)((Setuden)plg).getDefaultConfig()).saveConfig();
        ((Setuden)plg).getDefaultConfig().reload();
//        plg.incrementResourceNum();

        // 高さランダム化
        Random r = new Random();
        int high = 0;
        String path = plg.getDataFolder().getPath() + "/targetHeight";
        File file = new File(path);
        if (file.exists()) {
            try {
                String str;
                BufferedReader bfReader = new BufferedReader(new InputStreamReader(new FileInputStream(path), Charset.forName("UTF-8")));
                while ((str = bfReader.readLine()) != null) {
                    log.info("detect generation height:" + str);
                    high = Integer.parseInt(str, 10);
                    if (high < -63 || high > 318) {
                        high = r.nextInt(310 + 64) - 62;
                        log.info("detect invalid generation height");
                    }
                }
                bfReader.close();
            } catch(NumberFormatException ex) {
                ex.printStackTrace();
                high = r.nextInt(310 + 64) - 62;

            } catch (IOException ex) {
                ex.printStackTrace();
                high = r.nextInt(310 + 64) - 62;
            }
        } else {
            high = r.nextInt(310 + 64) - 62;
        }
        // ゲートの貼り直し
        PluginConfigUpdate.replaceGate(plg, "shigen"+(resnum-1), "shigen"+(resnum+1), "shigen"+(resnum-1)+"b", "shigen"+(resnum+1)+"b", w.getName(), high);

        // ワールドボーダー設定
        PluginConfigUpdate.clearBorder(plg, "shigen"+(resnum-1));
        PluginConfigUpdate.setBorder(plg, "shigen"+(resnum+1),0,0,3000);
        WorldBorder bd = w.getWorldBorder();
        bd.setCenter(0, 0);
        bd.setDamageAmount(0);
        bd.setDamageBuffer(10);
        bd.setSize(6000);
        bd.setWarningDistance(20);
        bd.setWarningTime(10);

        // ログブロックファイル更新
        PluginConfigUpdate.moveShigenLogBlock(plg, "shigen"+(resnum-1), "shigen"+(resnum+1), resnum-1);

        // PermissionsEx権限設定
        PluginConfigUpdate.moveShigenPermissions(plg, "shigen"+(resnum-1), "shigen"+(resnum+1));

        // 資源ベース作成
        Location l = w.getSpawnLocation();
        l.setX(0.0);
        l.setY(high);
        l.setZ(0.0);
        Builder.mkbase(l);

        // 保護を作成
        Server server = plg.getServer();
        CommandSender sender = plg.getServer().getConsoleSender();
        server.dispatchCommand(sender, "/world " + w.getName());
        server.dispatchCommand(sender, "/pos1 16," + (high - 1) + ",16");
        server.dispatchCommand(sender, "/pos2 -17," + (high - 1) + ",-17");
        server.dispatchCommand(sender, "region define -w " + w.getName() + " base1");
        server.dispatchCommand(sender, "/pos1 16," + (high + 10) + ",16");
        server.dispatchCommand(sender, "/pos2 -17," + (high + 10) + ",-17");
        server.dispatchCommand(sender, "region define -w " + w.getName() + " base2");
        server.dispatchCommand(sender, "/pos1 -5," + (high) + ",6");
        server.dispatchCommand(sender, "/pos2 -5," + (high + 1) + ",0");
        server.dispatchCommand(sender, "region define -w " + w.getName() + " base");
        server.dispatchCommand(sender, "region flag base use -w " + w.getName() + " allow");
        server.dispatchCommand(sender, "region flag base chest-access -w " + w.getName() + " allow");
        server.dispatchCommand(sender, "region flag base water-flow -w " + w.getName() + " deny");
        server.dispatchCommand(sender, "region flag base lava-flow -w " + w.getName() + " deny");
        //server.dispatchCommand(sender, "region flag -w "+ w.getName() + " -h 4 base blocked-cmds /ent,/entrance,/setuden:ent,/setuden:entrance,/home,/homes:home");

        // 旧資源にいる人を強制転送
        for (Player p : old.getPlayers() ) {
            p.teleport(w.getSpawnLocation());
        }

        sendPluginMessage(plg, null, "資源採掘MAP Version.{0} が生成されました。", Integer.toString(resnum+1));
    }
    
}
