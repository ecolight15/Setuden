
package jp.minecraftuser.setuden.config;

import java.util.HashMap;
import jp.minecraftuser.ecoframework.PluginFrame;
import jp.minecraftuser.ecoframework.ConfigFrame;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;

/**
 *
 * @author ecolight
 */
public class SetudenConfig extends ConfigFrame {
    private static boolean eventflag = false;
    private static boolean plzombie = true; 
    private static String plzombie_player = ""; 
    private static HashMap<Player, Zombie> plzombies = null;

    public SetudenConfig(PluginFrame plg_) {
        super(plg_);
        plg.registerNotifiable(this);
    }

    @Override
    public void reloadNotify(boolean base) {
        if (base) {
            super.reload();
        } else {
            plzombies = new HashMap<>();
        }
    }
    
    public void setPlayerZombie(String player, boolean flag) {
        plzombie = flag;
        plzombie_player = player;
    }
    public String getPlayerZombieLastSettingUser() {
        return plzombie_player;
    }
    public boolean getPlayerZombieFlag() {
        return plzombie;
    }

    public HashMap<Player, Zombie> getPlayerZombies() {
        return plzombies;
    }

    public void setEventFlag(boolean b) {
        eventWorld = null;
        eventflag = b;
    }
    private static World eventWorld = null;
    public void setEventFlag(boolean b, World w) {
        if (b) {
            eventWorld = w;
        } else {
            eventWorld = null;
        }
        eventflag = b;
    }
    public boolean getEventFlag() {
        return eventflag;
    }
    public World getEventWorld() {
        return eventWorld;
    }
    
}
