
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
public class SetEntCommand extends CommandFrame {
    private static SetudenConfig ecaConf = null;

    /**
     * コンストラクタ
     *
     * @param plg_  プラグインインスタンス
     * @param name_ コマンド名
     */
    public SetEntCommand(PluginFrame plg_, String name_) {
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
        return "setuden.setentrance";
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
        // ワールド指定でイベント中なら該当ワールド以外は有効とする
        Player player = (Player) sender;

        if (player != null) {
            WarpManager.sendSetWarp(player, "entrance", player.getLocation(), false, true);
            return true;


        }
        return true;
    }

}
