/**
 * 死亡関連イベントリスナ―
 * 
 * PlayerDeath
 *   事由なし死亡メッセージの出力抑止
 *   プレイヤーゾンビ―
 * PlayerMove
 *   奈落落下遅延機能 (-100 -> 0 へ)
 * EntranceDamage
 *   エントランスでダメージなしにする処理
 */
package jp.minecraftuser.setuden.listener;

import jp.minecraftuser.ecoframework.PluginFrame;
import jp.minecraftuser.ecoframework.ListenerFrame;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerMoveEvent;

/**
 * プレイヤーイベント処理リスナークラス
 * @author ecolight
 */
public class DeathListener extends ListenerFrame {
    /**
     * コンストラクタ
     * @param plg_ プラグインインスタンス
     * @param name_ 名前
     */
    public DeathListener(PluginFrame plg_, String name_) {
        super(plg_, name_);
    }
    @EventHandler(priority=EventPriority.LOWEST)
    public void PlayerDeath(PlayerDeathEvent event)
    {
//        if (ecaConf.getPlayerZombieFlag()) {
//            PlayerZombie(event);
//        }
        Player p = event.getEntity();
        if (event.getDeathMessage() == null) return;
        if (event.getDeathMessage().equals(p.getName()+" died")) {
            log.info(event.getDeathMessage());
            event.setDeathMessage(null);
        }
    }
    /*
    public void PlayerZombie(PlayerDeathEvent event) {
        Player pl = event.getEntity();
        if (pl == null) return;
        World w = pl.getWorld();
        if (!w.getName().startsWith("pvp")) return;

        if (ecaConf.getPlayerZombies().containsKey(pl)) {
            ((Zombie)ecaConf.getPlayerZombies().get(pl)).remove();
            ecaConf.getPlayerZombies().remove(pl);
        }

        Zombie z = (Zombie)w.spawnEntity(pl.getLocation(), EntityType.ZOMBIE);
        z.setCanPickupItems(false);
        z.setBaby(false);
        z.setCustomName(pl.getDisplayName());
        z.setCustomNameVisible(true);
        z.setVillager(false);
        z.setRemoveWhenFarAway(false);
        PotionEffect p = new PotionEffect(PotionEffectType.SPEED, 60480000, 5);
        z.addPotionEffect(p);
        Player killer = pl.getKiller();
        if (killer != null) z.setTarget(pl.getKiller());

        ecaConf.getPlayerZombies().put(pl, z);

        EntityEquipment eq = z.getEquipment();
        EntityEquipment peq = pl.getEquipment();

        eq.setBoots(peq.getBoots());
        eq.setLeggings(peq.getLeggings());
        eq.setChestplate(peq.getChestplate());
        eq.setItemInHand(peq.getItemInHand());

        ItemStack head = new ItemStack(397, 1);
        SkullMeta hmeta = (SkullMeta)head.getItemMeta();

        if (!hmeta.setOwner(pl.getName())) {
            return;
        }
        head.setItemMeta(hmeta);
        head.setDurability((short)3);
        eq.setHelmet(head);

        eq.setBootsDropChance(0.0F);
        eq.setLeggingsDropChance(0.0F);
        eq.setChestplateDropChance(0.0F);
        eq.setItemInHandDropChance(0.0F);
        eq.setHelmetDropChance(0.0F);
    }
*/
    // 奈落落下ダメージ軽減
    @EventHandler(priority=EventPriority.LOWEST)
    public void PlayerMove(PlayerMoveEvent event) {
        if (event.getPlayer().getHealth() >= 2) {
            if (event.getTo().getBlockY() < -100) {
                Location l = event.getTo();
                l.setY(0);
                event.setTo(l);
                event.getPlayer().damage(1);
            }
        }
    }
    
    @EventHandler(priority=EventPriority.LOWEST)
    public void EntranceDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player ) {
            Player p = (Player) event.getEntity();
            if (p.getWorld().getName().equals("world")) {
                if (p.getHealth() - event.getDamage() < 1) {
                    p.setHealth(20);
                    event.setDamage(19);
                }
            }
        }
    }
}
