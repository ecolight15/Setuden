
package jp.minecraftuser.setuden.command;

import jp.minecraftuser.ecoframework.PluginFrame;
import jp.minecraftuser.ecoframework.CommandFrame;
import static jp.minecraftuser.ecoframework.Utl.sendPluginMessage;

import jp.minecraftuser.setuden.config.SetudenConfig;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * リロードコマンドクラス
 * @author ecolight
 */
public class EntCommand extends CommandFrame {
    private static SetudenConfig ecaConf = null;

    /**
     * コンストラクタ
     * @param plg_ プラグインインスタンス
     * @param name_ コマンド名
     */
    public EntCommand(PluginFrame plg_, String name_) {
        super(plg_, name_);
        ecaConf = (SetudenConfig)conf;
    }

    /**
     * コマンド権限文字列設定
     * @return 権限文字列
     */
    @Override
    public String getPermissionString() {
        return "setuden.entrance";
    }

    /**
     * 処理実行部
     * @param sender コマンド送信者
     * @param args パラメタ
     * @return コマンド処理成否
     */
    @Override
    public boolean worker(CommandSender sender, String[] args) {
        // ワールド指定でイベント中なら該当ワールド以外は有効とする
        World w = ecaConf.getEventWorld();
        Player player = (Player) sender;
        if (((w == null) && (ecaConf.getEventFlag())) ||
            ((w != null) && (w.getName().equalsIgnoreCase(player.getWorld().getName())))){
            sendPluginMessage(plg, sender, "現在特定イベントのためent(entrance)コマンドは抑止されています");
            return true;
        }
        if (player != null) {
            if (!player.getWorld().getName().equalsIgnoreCase("world")) {
                w = plg.getServer().getWorld("world");
                if (w != null) {
                    Location loc = w.getSpawnLocation();
                    loc.setX(-57.0D);
                    loc.setY(65.0D);
                    loc.setZ(2.0D);
                    player.teleport(loc);
                } else {
                    Location loc = plg.getServer().getWorlds().get(0).getSpawnLocation();
                    player.teleport(loc);
                }
                return true;
            }
            sendPluginMessage(plg, sender, "エントランスではこのコマンドは使用できません");
            return true;
        }
        return true;
    }
    
}
