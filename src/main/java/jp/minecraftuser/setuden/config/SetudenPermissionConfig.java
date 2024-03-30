package jp.minecraftuser.setuden.config;

import jp.minecraftuser.ecoframework.PluginFrame;
import jp.minecraftuser.ecoframework.ConfigFrame;

/**
 *
 * @author ecolight
 */
public class SetudenPermissionConfig extends ConfigFrame {

    public SetudenPermissionConfig(PluginFrame plg_, String file_, String name_) {
        super(plg_, file_, name_);
        plg.registerNotifiable(this);
    }
}
