
package jp.minecraftuser.setuden.scheduler;

import org.bukkit.Location;
import org.bukkit.Material;

/**
 *
 * @author ecolight
 */
public class Builder {
    
    public static void mkbase(Location l) {
        Location loc = l.clone();
        double baseX = l.getX();
        double baseY = l.getY();
        double baseZ = l.getZ();

        // ベース作成
        for (loc.setY(baseY - 0.5); loc.getY() < baseY - 0.5 + 10; loc.setY(loc.getY()+1)) {
            for (loc.setX(baseX-16.5); loc.getX() < baseX+17; loc.setX(loc.getX()+1)) {
                for (loc.setZ(baseZ-16.5); loc.getZ() < baseZ+17; loc.setZ(loc.getZ()+1)) {
                    // 土台の高さ
                    if (loc.getBlockY() == baseY-1) {
                        // 4ブロック間隔でグローストーン埋め込み
                        if ((loc.getBlockX()%4==0) && (loc.getBlockZ()%4==0)) loc.getBlock().setType(Material.OCHRE_FROGLIGHT);
                        else loc.getBlock().setType(Material.BRICKS);
                    }
                    // 72の高さは屋根
                    else if(loc.getBlockY() == baseY+8) {
                        loc.getBlock().setType(Material.LIGHT_GRAY_STAINED_GLASS);
                    }
                    // それ以外は空気
                    else {
                        loc.getBlock().setType(Material.AIR);
                    }
                }
            }
        }
        // リスポン設定
        l.getWorld().setSpawnLocation((int)baseX,(int)baseY,(int)baseZ);

        // ゲート位置設定
        loc.setX(baseX);
        loc.setY(baseY-0.5);
        loc.setZ(baseZ-4.5);
        loc.getBlock().setType(Material.OBSIDIAN);
        loc.setY(baseY+2.5);
        loc.getBlock().setType(Material.OBSIDIAN);
        loc.setY(baseY + 0.5);
        loc.getBlock().setType(Material.END_GATEWAY);
        loc.setY(baseY + 1.5);
        loc.getBlock().setType(Material.END_GATEWAY);

        // 埋め込みビーコン用glass穴
        loc.setX(baseX);
        loc.setY(baseY-0.5);
        loc.setZ(baseZ+4.5);
        loc.getBlock().setType(Material.LIGHT_BLUE_STAINED_GLASS);

        // ついでに埋め込み作業台とかまどとチェスト
        loc.setX(baseX-4.5);
        loc.setY(baseY+0.5);
        loc.setZ(baseZ);
        loc.getBlock().setType(Material.CRAFTING_TABLE);
        loc.setZ(baseZ+1.5);
        loc.getBlock().setType(Material.CHEST);
        loc.setZ(baseZ+2.5);
        loc.getBlock().setType(Material.STONECUTTER);
        loc.setZ(baseZ+3.5);
        loc.getBlock().setType(Material.FURNACE);
        loc.setZ(baseZ+4.5);
        loc.getBlock().setType(Material.BLAST_FURNACE);
        loc.setZ(baseZ+5.5);
        loc.getBlock().setType(Material.SMOKER);

        return;
    }
    public static void mkbaseend(Location l) {
        Location loc = l.clone();
        double baseX = l.getX();
        double baseY = l.getY();
        double baseZ = l.getZ();
        // ベース作成
        for (loc.setY(baseY - 0.5); loc.getY() < baseY - 0.5 + 10; loc.setY(loc.getY()+1)) {
            for (loc.setX(baseX-16.5); loc.getX() < baseX+17; loc.setX(loc.getX()+1)) {
                for (loc.setZ(baseZ-16.5); loc.getZ() < baseZ+17; loc.setZ(loc.getZ()+1)) {
                    // 土台の高さ
                    if (loc.getBlockY() == baseY-1) {
                        // 4ブロック間隔でグローストーン埋め込み
                        if ((loc.getBlockX()%4==0) && (loc.getBlockZ()%4==0)) loc.getBlock().setType(Material.PEARLESCENT_FROGLIGHT);
                        else loc.getBlock().setType(Material.BEDROCK);
                    }
                    // 72の高さは屋根
                    else if(loc.getBlockY() == baseY+8) {
                        loc.getBlock().setType(Material.BEDROCK);
                    }
                    // それ以外は空気
                    else {
                        loc.getBlock().setType(Material.AIR);
                    }
                }
            }
        }
        // リスポン設定
        l.getWorld().setSpawnLocation((int)baseX + 5,(int)baseY,(int)baseZ);

        // ゲート位置設定
        loc.setX(baseX);
        loc.setY(baseY);
        loc.setZ(baseZ);
        loc.getBlock().setType(Material.OBSIDIAN);
        loc.setY(baseY+2.5);
        loc.getBlock().setType(Material.OBSIDIAN);
        loc.setY(baseY + 0.5);
        loc.getBlock().setType(Material.END_GATEWAY);
        loc.setY(baseY + 1.5);
        loc.getBlock().setType(Material.END_GATEWAY);

        // ついでに埋め込み作業台とかまどとチェスト
        loc.setX(baseX-4.5);
        loc.setY(baseY+0.5);
        loc.setZ(baseZ);
        loc.getBlock().setType(Material.CRAFTING_TABLE);
        loc.setZ(baseZ+1.5);
        loc.getBlock().setType(Material.CHEST);
        loc.setZ(baseZ+2.5);
        loc.getBlock().setType(Material.STONECUTTER);
        loc.setZ(baseZ+3.5);
        loc.getBlock().setType(Material.FURNACE);
        loc.setZ(baseZ+4.5);
        loc.getBlock().setType(Material.BLAST_FURNACE);
        loc.setZ(baseZ+5.5);
        loc.getBlock().setType(Material.SMOKER);

        return;
    }
}
