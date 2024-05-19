
package jp.minecraftuser.setuden.command;

import jp.minecraftuser.ecoframework.CommandFrame;
import jp.minecraftuser.ecoframework.PluginFrame;
import jp.minecraftuser.setuden.config.SetudenConfig;
import jp.minecraftuser.setuden.managers.WarpManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * リロードコマンドクラス
 *
 * @author ecolight
 */
public class EntCommand extends CommandFrame {
    private static SetudenConfig ecaConf = null;

    /**
     * コンストラクタ
     *
     * @param plg_  プラグインインスタンス
     * @param name_ コマンド名
     */
    public EntCommand(PluginFrame plg_, String name_) {
        super(plg_, name_);
        ecaConf = (SetudenConfig) conf;
    }

    /**
     * コマンド権限文字列設定
     *
     * @return 権限文字列
     */
    @Override
    public String getPermissionString() {
        return "setuden.entrance";
    }

    /**
     * 処理実行部
     *
     * @param sender コマンド送信者
     * @param args   パラメタ
     * @return コマンド処理成否
     */
    @Override
    public boolean worker(CommandSender sender, String[] args) {
        Player player = (Player) sender;
        if (player != null) {
            WarpManager.sendWarpPlayer(player, player.getName(), "entrance", true, true);
            return true;
        }
        return true;
    }

}
