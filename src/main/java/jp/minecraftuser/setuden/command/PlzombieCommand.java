
package jp.minecraftuser.setuden.command;

import jp.minecraftuser.ecoframework.PluginFrame;
import jp.minecraftuser.ecoframework.CommandFrame;
import jp.minecraftuser.setuden.config.SetudenConfig;
import org.bukkit.command.CommandSender;

/**
 * プレイヤーゾンビ制御コマンドクラス
 * @author ecolight
 */
public class PlzombieCommand extends CommandFrame {
    private static SetudenConfig ecaConf = null;

    /**
     * コンストラクタ
     * @param plg_ プラグインインスタンス
     * @param name_ コマンド名
     */
    public PlzombieCommand(PluginFrame plg_, String name_) {
        super(plg_, name_);
        ecaConf = (SetudenConfig)conf;
        setAuthBlock(true);
        setAuthConsole(true);
    }

    /**
     * コマンド権限文字列設定
     * @return 権限文字列
     */
    @Override
    public String getPermissionString() {
        return "setuden.plzombie";
    }

    /**
     * 処理実行部
     * @param sender コマンド送信者
     * @param args パラメタ
     * @return コマンド処理成否
     */
    @Override
    public boolean worker(CommandSender sender, String[] args) {
        if (args.length < 1) {
            sender.sendMessage("[" + plg.getName()+"] パラメータが不足しています");
            sender.sendMessage("[" + plg.getName()+"] /plzombie true     : 有効にする");
            sender.sendMessage("[" + plg.getName()+"] /plzombie false    : 無効にする");
            sender.sendMessage("[" + plg.getName()+"] /plzombie status   : 現在の設定を見る");
        } else {
            if (args[0].equalsIgnoreCase("true")) {
                if (ecaConf.getPlayerZombieFlag()) {
                    sender.sendMessage("[" + plg.getName()+"] 既にプレイヤーゾンビ制御は有効状態です");
                } else {
                    ecaConf.setPlayerZombie(sender.getName(), true);
                    plg.getServer().broadcastMessage("プレイヤー["+sender.getName()+"]によりゲームマップにおけるプレイヤーゾンビ制御が有効になりました");
                }
            } else if (args[0].equalsIgnoreCase("false")) {
                if (!ecaConf.getPlayerZombieFlag()) {
                    sender.sendMessage("[" + plg.getName()+"] 既にプレイヤーゾンビ制御は無効状態です");
                } else {
                    ecaConf.setPlayerZombie(sender.getName(), false);
                    plg.getServer().broadcastMessage("プレイヤー["+sender.getName()+"]によりゲームマップにおけるプレイヤーゾンビ制御が無効になりました");
                }
            } else if (args[0].equalsIgnoreCase("status")) {
                if (ecaConf.getPlayerZombieFlag()) {
                    sender.sendMessage("[" + plg.getName()+"] プレイヤーゾンビ制御は現在有効です");
                } else {
                    sender.sendMessage("[" + plg.getName()+"] プレイヤーゾンビ制御は現在無効です");
                }
                sender.sendMessage("[" + plg.getName()+"] 最後に制御を変更したユーザーは["+ecaConf.getPlayerZombieLastSettingUser()+"]です");
            } else {
                sender.sendMessage("[" + plg.getName()+"] パラメータが不正です");
                sender.sendMessage("[" + plg.getName()+"] /plzombie true     : 有効にする");
                sender.sendMessage("[" + plg.getName()+"] /plzombie false    : 無効にする");
                sender.sendMessage("[" + plg.getName()+"] /plzombie status   : 現在の設定を見る");
            }
        }
        return true;
    }
    
}
