
package jp.minecraftuser.setuden;

import java.sql.SQLException;
import java.util.logging.Level;
import jp.minecraftuser.ecoframework.PluginFrame;
import jp.minecraftuser.ecoframework.CommandFrame;
import jp.minecraftuser.ecoframework.ConfigFrame;
import jp.minecraftuser.setuden.command.EntCommand;
import jp.minecraftuser.setuden.command.PlzombieCommand;
import jp.minecraftuser.setuden.command.SetmsgCommand;
import jp.minecraftuser.setuden.command.TestCommand;
import jp.minecraftuser.setuden.command.SetudenCommand;
import jp.minecraftuser.setuden.command.SetudenMkbaseCommand;
import jp.minecraftuser.setuden.command.SetudenMkbaseendCommand;
import jp.minecraftuser.setuden.command.SetudenPermissionCommand;
import jp.minecraftuser.setuden.command.SetudenPermissionSetCommand;
import jp.minecraftuser.setuden.command.SetudenPermissionUnsetCommand;
import jp.minecraftuser.setuden.command.SetudenReloadCommand;
import jp.minecraftuser.setuden.command.WhoisCommand;
import jp.minecraftuser.setuden.config.SetudenConfig;
import jp.minecraftuser.setuden.config.PluginConfigUpdate;
import jp.minecraftuser.setuden.config.SetudenPermissionConfig;
import jp.minecraftuser.setuden.db.WhoisDB;
import jp.minecraftuser.setuden.listener.DeathListener;
import jp.minecraftuser.setuden.listener.GuardListener;
import jp.minecraftuser.setuden.listener.PlayerListener;
import jp.minecraftuser.setuden.listener.TestListener;
import jp.minecraftuser.setuden.scheduler.ForumScheduler;

/**
 *
 * @author ecolight
 */
public class Setuden  extends PluginFrame {

    /**
     * プラグイン開始処理
     */
    @Override
    public void onEnable() {
        initialize();
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
