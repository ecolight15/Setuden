
package jp.minecraftuser.setuden;

import java.sql.SQLException;
import java.util.logging.Level;
import jp.minecraftuser.ecoframework.PluginFrame;
import jp.minecraftuser.ecoframework.CommandFrame;
import jp.minecraftuser.ecoframework.ConfigFrame;
import jp.minecraftuser.setuden.command.*;
import jp.minecraftuser.setuden.config.SetudenConfig;
import jp.minecraftuser.setuden.config.PluginConfigUpdate;
import jp.minecraftuser.setuden.config.SetudenPermissionConfig;
import jp.minecraftuser.setuden.db.WhoisDB;
import jp.minecraftuser.setuden.listener.*;
import jp.minecraftuser.setuden.scheduler.ForumScheduler;
import jp.minecraftuser.setuden.trie.Trie;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

/**
 *
 * @author ecolight
 */
public class Setuden extends PluginFrame {

    public static Setuden instance;

    public static String OUTGOING_PLUGIN_CHANNEL = "bsuite:warps-in";
    public static String INCOMING_PLUGIN_CHANNEL = "bsuite:warps-out";

    //オフラインプレイヤー名のリストをTrie形式で保存するリスト
    public Trie offline_player_name_list = new Trie();

    /**
     * プラグイン開始処理
     */
    @Override
    public void onEnable() {
        initialize();
        registerChannels();
        instance = this;

        OfflinePlayer[] offlinePlayers = this.getServer().getOfflinePlayers();
        for (OfflinePlayer offlinePlayer : offlinePlayers) {
            String name = offlinePlayer.getName();
            if(name != null) {
                offline_player_name_list.insert(offlinePlayer.getName());
            }
        }
    }

    /**
     * プラグイン終了処理
     */
    @Override
    public void onDisable()
    {
        disable();
    }

    /**
     * 設定ファイル処理の初期化と登録
     */
    @Override
    public void initializeConfig() {
        ConfigFrame conf;

        // デフォルトコンフィグ
        conf = new SetudenConfig(this);
        conf.registerInt("newestresource");
        conf.registerSectionString("test");
        conf.registerInt("sever-open-interval");
        conf.registerBoolean("setuden-scheduler");
        conf.registerString("level-name");
        registerPluginConfig(conf);

        // 節電パーミッションコンフィグ
        conf = new SetudenPermissionConfig(this, "permissions.yml", "permissions");
        conf.registerSectionString("");
        registerPluginConfig(conf);
        
        // こんふぃぐあっぷでーた
        registerPluginConfig(new PluginConfigUpdate(this, "config.yml", "updater"));
    }

    /**
     * コマンド処理の初期化と登録
     */
    @Override
    public void initializeCommand() {
        CommandFrame cmd = new SetudenCommand(this, "setuden");
        cmd.addCommand(new SetudenReloadCommand(this, "reload"));
        cmd.addCommand(new SetudenMkbaseCommand(this, "mkbase"));
        cmd.addCommand(new SetudenMkbaseendCommand(this, "mkbaseend"));
        CommandFrame perm = new SetudenPermissionCommand(this, "permission");
        perm.addCommand(new SetudenPermissionSetCommand(this, "set"));
        perm.addCommand(new SetudenPermissionUnsetCommand(this, "unset"));
        cmd.addCommand(perm);
        registerPluginCommand(cmd);
        registerPluginCommand(new WhoisCommand(this, "whois"));
        registerPluginCommand(new SetmsgCommand(this, "setmsg"));
        registerPluginCommand(new EntCommand(this, "ent"));
        registerPluginCommand(new EntCommand(this, "entrance"));
        registerPluginCommand(new SetEntCommand(this, "setent"));
        registerPluginCommand(new SetEntCommand(this, "setentrance"));
        registerPluginCommand(new PlzombieCommand(this, "plzombie"));
        registerPluginCommand(new TestCommand(this, "test"));
    }

    /**
     * イベントリスナ―処理の初期化と登録
     */
    @Override
    public void initializeListener() {
        registerPluginListener(new PlayerListener(this, "player"));
        registerPluginListener(new DeathListener(this, "death"));
        registerPluginListener(new GuardListener(this, "guard"));
        registerPluginListener(new TestListener(this, "test"));
        registerPluginListener(new WarpListener(this, "warp"));
    }

    private void registerChannels() {
        Bukkit.getMessenger().registerIncomingPluginChannel(this,
                INCOMING_PLUGIN_CHANNEL, new WarpMessageListener());
        Bukkit.getMessenger().registerOutgoingPluginChannel(this,
                OUTGOING_PLUGIN_CHANNEL);
    }
    /**
     * タイマー処理の初期化と登録
     */
    @Override
    public void initializeTimer() {
        registerPluginTimer(new ForumScheduler(this, "scheduler"));

        // 起動
        runTaskTimer("scheduler", 0L, 8L);              // 現在から400msec間隔
    }

    /**
     * DB初期化処理
     */
    @Override
    protected void initializeDB() {
        try {
            registerPluginDB(new WhoisDB(this, "whois"));
        } catch (ClassNotFoundException | SQLException ex) {
            log.log(Level.SEVERE, null, ex);
        }
    }
}
