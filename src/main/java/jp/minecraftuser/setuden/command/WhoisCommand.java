
package jp.minecraftuser.setuden.command;

import jp.minecraftuser.ecoframework.PluginFrame;
import jp.minecraftuser.ecoframework.CommandFrame;
import jp.minecraftuser.setuden.Setuden;
import jp.minecraftuser.setuden.db.WhoisDB;
import static jp.minecraftuser.ecoframework.Utl.sendPluginMessage;

import jp.minecraftuser.setuden.trie.Trie;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.*;

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
            sendPluginMessage(plg, sender, ((WhoisDB)plg.getDB("whois")).getWhois(sender.getName(),sender.isOp()));
        } else {
            // 自分以外
            sendPluginMessage(plg, sender, ((WhoisDB)plg.getDB("whois")).getWhois(args[0],sender.isOp()));
        }
        return true;
    }

    /**
     * コマンド別タブコンプリート処理
     *
     * @param sender  コマンド送信者インスタンス
     * @param cmd     コマンドインスタンス
     * @param string  コマンド文字列
     * @param strings パラメタ文字列配列
     * @return 保管文字列配列
     */
    @Override
    protected List<String> getTabComplete(CommandSender sender, Command cmd, String string, String[] strings) {
        List<String> list = new ArrayList<>();
        if (strings.length == 1) {
            if(strings[0].length() >= 2) {
                Trie playerNameList = ((Setuden) plg).offline_player_name_list;
                list = playerNameList.startsWith(strings[0]);
                Collections.sort(list);
            }else{
                for (Player p : plg.getServer().getOnlinePlayers()) {
                    if (p.getName().toLowerCase().startsWith(strings[0].toLowerCase())) {
                        list.add(p.getName());
                    }
                }
            }
        }
        return list;
    }
}
