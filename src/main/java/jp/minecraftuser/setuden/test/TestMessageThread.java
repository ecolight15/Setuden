
package jp.minecraftuser.setuden.test;

import jp.minecraftuser.ecoframework.PluginFrame;
import jp.minecraftuser.ecoframework.async.AsyncFrame;
import jp.minecraftuser.ecoframework.async.MessageAsyncFrame;
import jp.minecraftuser.ecoframework.async.MessagePayload;

/**
 *
 * @author ecolight
 */
public class TestMessageThread extends MessageAsyncFrame{
    static long num = 0;
    public TestMessageThread(PluginFrame plg_, String name_) {
        super(plg_, name_);
    }

    /**
     * 子スレッドインスタンス生成処理
     * @return 子スレッドインスタンス
     */
    @Override
    protected AsyncFrame clone() {
        return new TestMessageThread(plg, name);
    }

    /**
     * 子スレッドにおけるメッセージ加工処理
     * @param msg メッセージペイロード
     */
    @Override
    protected void executeProcess(MessagePayload msg) {
   
    }

    
}
