
package jp.minecraftuser.setuden.config;

import java.io.File;
import java.util.ArrayList;
import java.util.logging.Logger;
import jp.minecraftuser.ecoframework.ConfigFrame;
import jp.minecraftuser.ecoframework.PluginFrame;
import static jp.minecraftuser.ecoframework.Utl.sendPluginMessage;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

/**
 * 他プラグイン設定変更クラス
 * plg,log持ってくるの面倒なのでconfig扱いにする(utility系のframeねえな)
 * @author ecolight
 */
public class PluginConfigUpdate extends ConfigFrame {
    protected static Logger log; 
    protected static PluginFrame plg; 

    public PluginConfigUpdate(PluginFrame plg_, String filename_, String name_) {
        super(plg_, filename_, name_);
        plg = plg_;
        log = plg.getLogger();
    }
    
    // replaceGate(plg, "shigen"+(resnum-1), "shigen"+(resnum+1), );
    static public void replaceGate(PluginFrame plg, String A_old, String A_new, String B_old, String B_new, String world, int high_) {
        // プラグインが不足していたら実行せずに終了
        Plugin eg = plg.getPluginFrame("EcoGate");
        if (eg == null) {
            return;
        }
        File fgate = new File(eg.getDataFolder(), "Gates.yml");
        FileConfiguration fcgate = YamlConfiguration.loadConfiguration(fgate);

        // 古い定義がない場合には何もしないで終わる
        if ((fcgate.getString("Gates."+A_old+".world") == null) || (fcgate.getString("Gates."+A_old+".world").length() == 0)) {
            return;
        }
        
//        eg.onDisable();

        // world -> shigen 古い定義からワールド名を取得して新しいワールド名に変更する
        fcgate.set("Gates."+A_new+".world", fcgate.getString("Gates."+A_old+".world"));
        fcgate.set("Gates."+A_new+".x", fcgate.getInt("Gates."+A_old+".x"));
        fcgate.set("Gates."+A_new+".y", fcgate.getInt("Gates."+A_old+".y"));
        fcgate.set("Gates."+A_new+".z", fcgate.getInt("Gates."+A_old+".z"));
        fcgate.set("Gates."+A_new+".yaw", fcgate.getDouble("Gates."+A_old+".yaw"));
        fcgate.set("Gates."+A_new+".pitch", fcgate.getDouble("Gates."+A_old+".pitch"));
        fcgate.set("Gates."+A_old, null); // 古い定義削除
                
        // shigen -> world 新しいワールド側のゲートはワールド名を設定する　それ以外は古い資源ゲートの設定を引き継ぐ
        fcgate.set("Gates."+B_new+".world", world);
        fcgate.set("Gates."+B_new+".x", fcgate.getInt("Gates."+B_old+".x"));
        fcgate.set("Gates."+B_new+".y", high_);
        fcgate.set("Gates."+B_new+".z", fcgate.getInt("Gates."+B_old+".z"));
        fcgate.set("Gates."+B_new+".yaw", fcgate.getDouble("Gates."+B_old+".yaw"));
        fcgate.set("Gates."+B_new+".pitch", fcgate.getDouble("Gates."+B_old+".pitch"));
        fcgate.set("Gates."+B_old, null); // 古い定義削除

        fcgate.set("Links."+A_old, null);
        fcgate.set("Links."+B_old, null);
        fcgate.set("Links."+A_new+".connection", B_new);
        fcgate.set("Links."+B_new+".connection", A_new);

        try {
            fcgate.save(fgate);
        } catch (Exception e) {
            log.info("EcoGate Gate settings update failed."+e.getMessage());
            sendPluginMessage(plg, null, "資源採掘MAP Version.{0} の生成に失敗しました。", A_new);
            return;
        }
//        eg.onEnable();
        eg.reloadConfig();
        log.info("EcoGate Gate settings update complete. before="+A_old+" after="+A_new);
    }
//        PluginConfigUpdate.renameGates(plg, "shigen"+(resnum-1), "shigen"+(resnum+1), null);        // 旧資源のゲートを新資源のゲート定義に変更
//        PluginConfigUpdate.renameGates(plg, "shigen"+(resnum-1)+"b", "shigen"+(resnum+1)+"b", w);   // 
//        PluginConfigUpdate.linkGates(plg, "shigen"+(resnum+1), "shigen"+(resnum+1)+"b");
//        PluginConfigUpdate.linkGates(plg, "shigen"+(resnum+1)+"b", "shigen"+(resnum+1));
//        PluginConfigUpdate.linkGates(plg, "shigen"+(resnum-1), null);
//        PluginConfigUpdate.linkGates(plg, "shigen"+(resnum-1)+"b", null);

    // EcoGateプラグインにゲートのリネームを依頼する
    static public void renameGates(PluginFrame plg, String before, String after, World w) {
        // プラグインが不足していたら実行せずに終了
        Plugin eg = plg.getPluginFrame("EcoGate");
        if (eg == null) {
            return;
        }
        File fgate = new File(eg.getDataFolder(), "Gates.yml");
        FileConfiguration fcgate = YamlConfiguration.loadConfiguration(fgate);
        
        if (w == null) {
            // 古い定義からワールド名を取得して新しいワールド名に変更する
            if ((fcgate.getString("Gates."+before+".world") == null) ||
                (fcgate.getString("Gates."+before+".world").length() == 0)) {
                // 古い定義がない場合には何もしないで終わる
                return;
            }
            fcgate.set("Gates."+after+".world", fcgate.getString("Gates."+before+".world"));
        } else {
            // after が作成されたばかりの場合、
            fcgate.set("Gates."+after+".world", w.getName());
        }
        fcgate.set("Gates."+after+".x", fcgate.getInt("Gates."+before+".x"));
        fcgate.set("Gates."+after+".y", fcgate.getInt("Gates."+before+".y"));
        fcgate.set("Gates."+after+".z", fcgate.getInt("Gates."+before+".z"));
        fcgate.set("Gates."+after+".yaw", fcgate.getDouble("Gates."+before+".yaw"));
        fcgate.set("Gates."+after+".pitch", fcgate.getDouble("Gates."+before+".pitch"));
        fcgate.set("Gates."+before, null);
        try {
            fcgate.save(fgate);
        } catch (Exception e) {
            log.info("EcoGate Gate settings update failed."+e.getMessage());
            sendPluginMessage(plg, null, "資源採掘MAP Version.{0} の生成に失敗しました。", after);
            return;
        }
        eg.reloadConfig();
        log.info("EcoGate Gate settings update complete. before="+before+" after="+after);
    }
    
    // EcoGate プラグインに指定名2つのゲート接続を依頼する
    static public void linkGates(PluginFrame plg, String point1, String point2) {

        // プラグインが不足していたら実行せずに終了
        PluginFrame eg = plg.getPluginFrame("EcoGate");
        if (eg == null) {
            return;
        }
 
        File fgate = new File(eg.getDataFolder(), "Gates.yml");
        FileConfiguration fcgate = YamlConfiguration.loadConfiguration(fgate);

        if (point1 == null) {
            return;
        }
        else if (point2 == null) {
            fcgate.set("Links."+point1, null);
        } else {
            fcgate.set("Links."+point1+".connection", point2);
        }
        try {
            fcgate.save(fgate);
        } catch (Exception e) {
            log.info("EcoGate Gate settings update failed."+e.getMessage());
            sendPluginMessage(plg, null, "資源採掘MAP Version.{0} の生成に失敗しました。", point1);
            return;
        }
        eg.reloadConfig();
        log.info("EcoGate Gate settings update complete. Link point1="+point1+" point2="+point2);
    }
    static public void setBorder(PluginFrame plg, String world, double centerX, double centerZ, int radius) {
        // プラグインが不足していたら実行せずに終了
        Plugin wb = Bukkit.getPluginManager().getPlugin("WorldBorder");
        if (wb == null) {
            return;
        }

        // ワールドボーダー設定
        wb.onDisable();
        FileConfiguration fc = wb.getConfig();
        fc.set("worlds."+world+".x", centerX);
        fc.set("worlds."+world+".z", centerZ);
        fc.set("worlds."+world+".radiusX", radius);
        fc.set("worlds."+world+".radiusZ", radius);
        fc.set("worlds."+world+".wrapping", false);
        wb.saveConfig();
        log.info("World Border config update. world="+world+" x="+centerX+" z="+centerZ+" radius="+radius);
        wb.onEnable();
    }
    static public void clearBorder(PluginFrame plg, String world) {
        // プラグインが不足していたら実行せずに終了
        Plugin wb = Bukkit.getPluginManager().getPlugin("WorldBorder");
        if (wb == null) {
            return;
        }

        // ワールドボーダー設定
        wb.onDisable();
        FileConfiguration fc = wb.getConfig();
        fc.set("worlds."+world, null);
        wb.saveConfig();
        log.info("World Border config update. clearWorld="+world);
        wb.onEnable();
    }
    static public void moveShigenPermissions(PluginFrame plg, String before, String after) {
        plg.getServer().dispatchCommand(plg.getServer().getConsoleSender(), "setuden permission set home true " + after);
        plg.getServer().dispatchCommand(plg.getServer().getConsoleSender(), "setuden permission unset home " + before + " force");
        log.info("Permissions settings update complete. before="+before+" after="+after);
    }
    static public void moveEndPermissions(PluginFrame plg, String before, String after) {
        plg.getServer().dispatchCommand(plg.getServer().getConsoleSender(), "setuden permission set end true " + after);
        plg.getServer().dispatchCommand(plg.getServer().getConsoleSender(), "setuden permission unset end " + before + " force");
        log.info("Permissions settings update complete. before="+before+" after="+after);
    }
    static public void moveShigenLogBlock(PluginFrame plg, String before, String after, int before_) {
        String delauto = "shigen"+(before_ - 1); // LogBlock定義削除対象(削除資源World番号の一つ前)
        String addauto = "shigen"+(before_ + 1); // LogBlock定義追加対象(追加資源World番号)
        // プラグインが不足していたら実行せずに終了
        Plugin lb = Bukkit.getPluginManager().getPlugin("LogBlock");
        if (lb == null) {
            return;
        }

        lb.onDisable();

        File f = new File(lb.getDataFolder(), "config.yml");
        FileConfiguration fclb = YamlConfiguration.loadConfiguration(f);

        // 旧資源を削除、新資源を追加
        ArrayList<String> list = (ArrayList<String>) fclb.getStringList("loggedWorlds");
        list.remove(before);
        list.add(after);
        fclb.set("loggedWorlds", list);

        // 削除資源の定義は削除する
        list = (ArrayList<String>) fclb.getStringList("clearlog.auto");
        list.remove("world \""+delauto+"\" before 0 days all");
        list.remove("world \""+delauto+"\" player lavaflow waterflow leavesdecay before 0 days all");
        // 今週削除資源の定義を追加してレコード削除
        list.add("world \""+addauto+"\" before 0 days all");
        list.add("world \""+addauto+"\" player lavaflow waterflow leavesdecay before 0 days all");
        fclb.set("clearlog.auto", list);
        try {
            fclb.save(f);
        } catch (Exception e) {
            log.info("LogBlock update failed."+e.getMessage());
            sendPluginMessage(plg, null, "資源採掘MAP Version.{0} の生成に失敗しました。", after);
            return;
        }

        File oldFile = new File(lb.getDataFolder(), before+".yml");
        File newFile = new File(lb.getDataFolder(), after+".yml");
        oldFile.renameTo(newFile);
        FileConfiguration fclbf = YamlConfiguration.loadConfiguration(newFile);
        fclbf.set("table", "lb-"+after);
        try {
            fclbf.save(newFile);
        } catch (Exception e) {
            log.info("LogBlock detail update failed."+e.getMessage());
            sendPluginMessage(plg, null, "資源採掘MAP Version.{0}.yml の生成に失敗しました。", after);
            return;
        }

        lb.onEnable();
        log.info("LogBlock settings update complete. before="+before+" after="+after);
    }
    static public void moveEndLogBlock(PluginFrame plg, String before, String after, int before_) {
        String delauto = "end"+(before_ - 1); // LogBlock定義削除対象(削除資源World番号の一つ前)
        String addauto = "end"+(before_); // LogBlock定義追加対象(削除資源World番号)
        // プラグインが不足していたら実行せずに終了
        Plugin lb = Bukkit.getPluginManager().getPlugin("LogBlock");
        if (lb == null) {
            return;
        }

        lb.onDisable();
        File f = new File(lb.getDataFolder(), "config.yml");
        FileConfiguration fclb = YamlConfiguration.loadConfiguration(f);
        ArrayList<String> list = (ArrayList<String>) fclb.getStringList("loggedWorlds");
        list.remove(before);
        list.add(after);
        fclb.set("loggedWorlds", list);
        list = (ArrayList<String>) fclb.getStringList("clearlog.auto");
        // 先週削除資源の定義は削除する
        list.remove("world \""+delauto+"\" before 0 days all");
        list.remove("world \""+delauto+"\" player lavaflow waterflow leavesdecay before 0 days all");
        // 今週削除資源の定義を追加してレコード削除
        list.add("world \""+addauto+"\" before 0 days all");
        list.add("world \""+addauto+"\" player lavaflow waterflow leavesdecay before 0 days all");
        fclb.set("clearlog.auto", list);
        try {
            fclb.save(f);
        } catch (Exception e) {
            log.info("LogBlock update failed."+e.getMessage());
            sendPluginMessage(plg, null, "資源採掘MAP Version.{0} の生成に失敗しました。", after);
            return;
        }

        File oldFile = new File(lb.getDataFolder(), before+".yml");
        File newFile = new File(lb.getDataFolder(), after+".yml");
        oldFile.renameTo(newFile);
        FileConfiguration fclbf = YamlConfiguration.loadConfiguration(newFile);
        fclbf.set("table", "lb-"+after);
        try {
            fclbf.save(newFile);
        } catch (Exception e) {
            log.info("LogBlock detail update failed."+e.getMessage());
            sendPluginMessage(plg, null, "資源採掘MAP Version.{0}.yml の生成に失敗しました。", after);
            return;
        }

        lb.onEnable();
        log.info("LogBlock settings update complete. before="+before+" after="+after);
    }


}
