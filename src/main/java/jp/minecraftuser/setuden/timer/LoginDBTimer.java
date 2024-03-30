package jp.minecraftuser.setuden.timer;

import java.util.Date;
import java.util.UUID;
import jp.minecraftuser.ecoframework.PluginFrame;
import jp.minecraftuser.ecoframework.TimerFrame;
import jp.minecraftuser.setuden.db.WhoisDB;
import org.bukkit.entity.Player;

public class LoginDBTimer extends TimerFrame {
    UUID uid = null;
    Boolean login = null;

    public LoginDBTimer(PluginFrame plg_, String name_, Player pl_, Boolean login_) {
        super(plg_, name_);
        uid = pl_.getUniqueId();
        login = login_;
    }

    @Override
    public void run()
    {
        if (login) {
            // ログイン時間更新
            ((WhoisDB)plg.getDB("whois")).update(uid, new Date(), null);
        } else {
            // ログアウト時間更新
            ((WhoisDB)plg.getDB("whois")).update(uid, null, new Date());
        }
    }
}
