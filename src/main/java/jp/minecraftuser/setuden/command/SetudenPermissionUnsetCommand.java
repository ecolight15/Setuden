
package jp.minecraftuser.setuden.command;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import jp.minecraftuser.ecoframework.PluginFrame;
import jp.minecraftuser.ecoframework.CommandFrame;
import static jp.minecraftuser.ecoframework.Utl.sendPluginMessage;
import jp.minecraftuser.setuden.config.SetudenPermissionConfig;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

/**
 * Permission設定コマンドクラス
 * @author ecolight
 */
public class SetudenPermissionUnsetCommand extends CommandFrame {
    MessageFormat mf = new MessageFormat("lp group default permission unset {0} global {1}");

    /**
     * コンストラクタ
     * @param plg_ プラグインインスタンス
     * @param name_ コマンド名
     */
    public SetudenPermissionUnsetCommand(PluginFrame plg_, String name_) {
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
        return "setuden.permission.unset";
    }

    /**
     * 処理実行部
     * @param sender コマンド送信者
     * @param args パラメタ
     * @return コマンド処理成否
     */
    @Override
    public boolean worker(CommandSender sender, String[] args) {
        // パラメータチェック:1～3まで
        // setname 
        // setname world
        // setname world force
        if (!checkRange(sender, args, 1, 3)) return true;

        if (args.length >= 2) {
            // パラメータ数２の場合はPermissionセット名、ワールド名と断定する
            World w = plg.getServer().getWorld(args[1]);
            if (w != null) {
                String name = w.getName();
                List<String> set = getPermissions(args[0]);
                if (set != null) {
                    Server s = plg.getServer();
                    ConsoleCommandSender co = s.getConsoleSender();
                    for (String permission : set) {
                        String param[] = {permission, name};
                        s.dispatchCommand(co, mf.format(param));
                    }
                    sendPluginMessage(plg, sender, "PermissionSet[{0}]をWorld[{1}]から解除しました", args[0], name);
                } else {
                    sendPluginMessage(plg, sender, "PermissionSetが見つかりませんでした");
                }
            } else {
                if (args.length >= 3) {
                    if (args[2].equalsIgnoreCase("force")) {
                        String name = args[1];
                        List<String> set = getPermissions(args[0]);
                        if (set != null) {
                            Server s = plg.getServer();
                            ConsoleCommandSender co = s.getConsoleSender();
                            for (String permission : set) {
                                String param[] = {permission, name};
                                s.dispatchCommand(co, mf.format(param));
                            }
                            sendPluginMessage(plg, sender, "PermissionSet[{0}]をWorld[{1}]から解除しました", args[0], name);
                            return true;
                        } else {
                            sendPluginMessage(plg, sender, "PermissionSetが見つかりませんでした");
                        }
                    } else {
                        sendPluginMessage(plg, sender, "force以外のパラメータが指定されています");
                    }
                } else {
                    sendPluginMessage(plg, sender, "ワールド名の指定が不正です。強制的に設定する場合には更にforceオプションをつけてください。");
                }
            }
        } else {
            // プレイヤーの場合は自分のいるワールド指定でパラメータ指定したパーミッションセットをdefaultグループ、globalリージョンで登録する
            // プレイヤーでない場合には、パラメーター指定が１つの場合、パラメータ不足としてエラーにする ※ワールドとパーミッションセット、有効無効の指定が必要なため
            if (!(sender instanceof Player)) {
                sendPluginMessage(plg, sender, "コンソールやブロックからは permissionset world 指定なしで実行できません");
            } else {
                // プレイヤーの場合には、現在のワールドに対して指定パーミッションセットを登録する
                String name = ((Player)sender).getWorld().getName();
                List<String> set = getPermissions(args[0]);
                if (set != null) {
                    Server s = plg.getServer();
                    ConsoleCommandSender co = s.getConsoleSender();
                    for (String permission : set) {
                        String param[] = {permission, name};
                        s.dispatchCommand(co, mf.format(param));
                    }
                    sendPluginMessage(plg, sender, "PermissionSet[{0}]をWorld[{1}]から解除しました", args[0], name);
                    return true;
                } else {
                    sendPluginMessage(plg, sender, "PermissionSetが見つかりませんでした");
                }
            }
        }
        return false;
    }

    /**
     * パーミッションセット設定ファイルから指定セット名のパーミッション一覧を取得する
     * @param setname パーミッションセット名を指定する(yamlの1段目)
     * @return パーミッションセット(yamlの2段目)
     */
    private List<String> getPermissions(String setname) {
        List<String> result = null;
        SetudenPermissionConfig perm = (SetudenPermissionConfig) plg.getPluginConfig("permissions");
        Set<String> permission_set = perm.getSectionList("");
        if (permission_set != null) {
            if (permission_set.contains(setname)) {
                // setnameのパーミッションセットがあることは確定したので、それをもとにセットの内容を取り出す
                perm.registerArrayString(setname);
                result = new ArrayList<>();
                for (String permission : perm.getArrayList(setname)) {
                    result.add(permission);
                }
            }
        }
        return result;
    }
}
