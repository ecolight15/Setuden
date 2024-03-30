
package jp.minecraftuser.setuden.command;

import jp.minecraftuser.ecoframework.PluginFrame;
import jp.minecraftuser.ecoframework.CommandFrame;
import jp.minecraftuser.setuden.db.WhoisDB;
import jp.minecraftuser.ecoframework.Utl;
import static jp.minecraftuser.ecoframework.Utl.sendPluginMessage;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

/**
 * リロードコマンドクラス
 * @author ecolight
 */
public class SetmsgCommand extends CommandFrame {

    /**
     * コンストラクタ
     * @param plg_ プラグインインスタンス
     * @param name_ コマンド名
     */
    public SetmsgCommand(PluginFrame plg_, String name_) {
        super(plg_, name_);
    }

    /**
     * コマンド権限文字列設定
     * @return 権限文字列
     */
    @Override
    public String getPermissionString() {
        return "setuden.setmsg";
    }

    /**
     * 処理実行部
     * @param sender コマンド送信者
     * @param args パラメタ
     * @return コマンド処理成否
     */
    @Override
    public boolean worker(CommandSender sender, String[] args) {
        if (args.length == 0) {
            ((WhoisDB)plg.getDB("whois")).updateMassage((OfflinePlayer) sender, null);
            sendPluginMessage(plg, sender, ((WhoisDB)plg.getDB("whois")).getWhois(sender.getName()));
        } else {
            // 自分のメッセージ設定
            StringBuilder str = new StringBuilder();
            for (int i = 0; i < args.length; i++) {
                if (i != 0) {
                    str.append(" ");
                }
                str.append(args[i]);
            }
            ((WhoisDB)plg.getDB("whois")).updateMassage((OfflinePlayer) sender, Utl.repColor(str.toString()));
            sendPluginMessage(plg, sender, ((WhoisDB)plg.getDB("whois")).getWhois(sender.getName()));
        }
        return true;
    }
    
}
