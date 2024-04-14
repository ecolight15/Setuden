
package jp.minecraftuser.setuden.command;

import jp.minecraftuser.ecoframework.PluginFrame;
import jp.minecraftuser.ecoframework.CommandFrame;
import jp.minecraftuser.setuden.db.WhoisDB;
import static jp.minecraftuser.ecoframework.Utl.sendPluginMessage;
import static jp.minecraftuser.ecoframework.Utl.sendTagMessage;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * リロードコマンドクラス
 * @author ecolight
 */
public class WhoisCommand extends CommandFrame {

    /**
     * コンストラクタ
     * @param plg_ プラグインインスタンス
     * @param name_ コマンド名
     */
    public WhoisCommand(PluginFrame plg_, String name_) {
        super(plg_, name_);
        setAuthBlock(true);
        setAuthConsole(true);
    }

    /**
     * コマンド権限文字列設定
     * @return 権限文字列
     */
    @Override
    public String getPermissionString() {
        return "setuden.whois";
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
            // 自分対象
            if (!(sender instanceof Player)) {
                sendPluginMessage(plg, sender, "コンソールからはプレイヤー指定無しでは実行できません");
                return true;
            }
            SendLineMessage(sender, ((WhoisDB)plg.getDB("whois")).getWhois(sender.getName()));
        } else {
            // 自分以外
            SendLineMessage(sender, ((WhoisDB)plg.getDB("whois")).getWhois(args[0]));
        }
        return true;
    }
    public void SendLineMessage(CommandSender sender,List<String> msg_list) {
        boolean first = true;
        for (String s : msg_list) {
            if (first) {
                sendPluginMessage(plg, sender, s);
                first = false;
            } else {
                sendTagMessage(plg, sender, "", s);
            }
        }
    }
}
