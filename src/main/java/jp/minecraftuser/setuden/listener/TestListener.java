
package jp.minecraftuser.setuden.listener;


import jp.minecraftuser.setuden.command.TestCommand;
import jp.minecraftuser.ecoframework.PluginFrame;
import jp.minecraftuser.ecoframework.ListenerFrame;

/**
 * プレイヤーイベント処理リスナークラス
 * @author ecolight
 */
public class TestListener extends ListenerFrame {
    private static TestCommand cmd = null;
    /**
     * コンストラクタ
     * @param plg_ プラグインインスタンス
     * @param name_ 名前
     */
    public TestListener(PluginFrame plg_, String name_) {
        super(plg_, name_);
        cmd = (TestCommand) plg.getPluginCommand("test");
        if (cmd == null) log.info("コマンド登録失敗");
    }
    
}
