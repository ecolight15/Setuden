
package jp.minecraftuser.setuden.test;

import java.sql.Connection;
import java.sql.SQLException;
import jp.minecraftuser.ecoframework.DatabaseFrame;
import jp.minecraftuser.ecoframework.PluginFrame;

/**
 *
 * @author ecolight
 */
public class DB extends DatabaseFrame {

    public DB(PluginFrame plg_, String name_) throws ClassNotFoundException, SQLException {
        super(plg_, "test.db", name_);
    }

    /**
     * データベース移行処理
     * 内部処理からトランザクション開始済みの状態で呼ばれる
     * @throws SQLException
     */
    @Override
    protected void migrationData(Connection con) throws SQLException {

    }
    

}
