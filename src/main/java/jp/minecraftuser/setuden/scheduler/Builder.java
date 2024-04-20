
package jp.minecraftuser.setuden.scheduler;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Directional;
import org.bukkit.block.data.type.Chest;
import org.bukkit.block.data.type.Chest.Type;

/**
 *
 * @author ecolight
 */
public class Builder {
    
    public static void setFace(Block block, BlockFace face) {
        BlockData blockData = block.getBlockData();
        if (blockData instanceof Directional) {
            ((Directional) blockData).setFacing(face);
            block.setBlockData(blockData);
        }
    }

    public static void mergeChest(Block left, Block right) {
        BlockData leftBlockData = left.getBlockData();
        BlockData rightBlockData = right.getBlockData();
        if ((leftBlockData instanceof Chest) && (rightBlockData instanceof Chest)) {
            ((Chest) leftBlockData).setType(Type.LEFT);
            ((Chest) rightBlockData).setType(Type.RIGHT);
            left.setBlockData(leftBlockData);
            right.setBlockData(rightBlockData);
        }
    }

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
        loc.getBlock().setType(Material.BEDROCK);
        loc.setY(baseY+2.5);
        loc.getBlock().setType(Material.BEDROCK);
        loc.setY(baseY + 0.5);
        loc.getBlock().setType(Material.END_GATEWAY);
        loc.setY(baseY + 1.5);
        loc.getBlock().setType(Material.END_GATEWAY);

        // 埋め込みビーコン用glass穴
        loc.setX(baseX);
        loc.setY(baseY-0.5);
        loc.setZ(baseZ+4.5);
        loc.getBlock().setType(Material.LIGHT_BLUE_STAINED_GLASS);
        loc.setZ(baseZ+3.5);
        loc.getBlock().setType(Material.LIME_STAINED_GLASS);
        loc.setZ(baseZ+2.5);
        loc.getBlock().setType(Material.YELLOW_STAINED_GLASS);

        // ついでに埋め込み作業台とかまどとチェスト
        loc.setX(baseX-4.5);
        loc.setY(baseY+0.5);
        loc.setZ(baseZ+0.5);
        loc.getBlock().setType(Material.CRAFTING_TABLE);
        setFace(loc.getBlock(), BlockFace.EAST);

        loc.setZ(baseZ+1.5);
        Block leftChest = loc.getBlock();
        leftChest.setType(Material.CHEST);
        setFace(leftChest, BlockFace.EAST);
        loc.setZ(baseZ+2.5);
        Block rightChest = loc.getBlock();
        rightChest.setType(Material.CHEST);
        setFace(rightChest, BlockFace.EAST);
        mergeChest(leftChest, rightChest);

        loc.setY(baseY+1.5);
        loc.setZ(baseZ+1.5);
        leftChest = loc.getBlock();
        leftChest.setType(Material.CHEST);
        setFace(leftChest, BlockFace.EAST);
        loc.setZ(baseZ+2.5);
        rightChest = loc.getBlock();
        rightChest.setType(Material.CHEST);
        setFace(rightChest, BlockFace.EAST);
        mergeChest(leftChest, rightChest);

        loc.setY(baseY+0.5);
        loc.setZ(baseZ+3.5);
        loc.getBlock().setType(Material.STONECUTTER);
        setFace(loc.getBlock(), BlockFace.EAST);
        loc.setZ(baseZ+4.5);
        loc.getBlock().setType(Material.FURNACE);
        setFace(loc.getBlock(), BlockFace.EAST);
        loc.setZ(baseZ+5.5);
        loc.getBlock().setType(Material.BLAST_FURNACE);
        setFace(loc.getBlock(), BlockFace.EAST);
        loc.setZ(baseZ+6.5);
        loc.getBlock().setType(Material.SMOKER);
        setFace(loc.getBlock(), BlockFace.EAST);
        loc.setY(baseY+1.5);
        loc.setZ(baseZ+0.5);
        loc.getBlock().setType(Material.ENDER_CHEST);
        setFace(loc.getBlock(), BlockFace.EAST);
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
        loc.setY(baseY - 0.5);
        loc.setZ(baseZ);
        loc.getBlock().setType(Material.BEDROCK);
        loc.setY(baseY + 2.5);
        loc.getBlock().setType(Material.BEDROCK);
        // loc.setY(baseY + 0.5);
        // loc.getBlock().setType(Material.END_GATEWAY);
        // loc.setY(baseY + 1.5);
        // loc.getBlock().setType(Material.END_GATEWAY);

        // ついでに埋め込み作業台とかまどとチェスト
        loc.setX(baseX-4.5);
        loc.setY(baseY+0.5);
        loc.setZ(baseZ+0.5);
        loc.getBlock().setType(Material.CRAFTING_TABLE);
        setFace(loc.getBlock(), BlockFace.EAST);

        loc.setZ(baseZ+1.5);
        Block leftChest = loc.getBlock();
        leftChest.setType(Material.CHEST);
        setFace(leftChest, BlockFace.EAST);
        loc.setZ(baseZ+2.5);
        Block rightChest = loc.getBlock();
        rightChest.setType(Material.CHEST);
        setFace(rightChest, BlockFace.EAST);
        mergeChest(leftChest, rightChest);

        loc.setY(baseY+1.5);
        loc.setZ(baseZ+1.5);
        leftChest = loc.getBlock();
        leftChest.setType(Material.CHEST);
        setFace(leftChest, BlockFace.EAST);
        loc.setZ(baseZ+2.5);
        rightChest = loc.getBlock();
        rightChest.setType(Material.CHEST);
        setFace(rightChest, BlockFace.EAST);
        mergeChest(leftChest, rightChest);

        loc.setY(baseY+0.5);
        loc.setZ(baseZ+3.5);

        loc.getBlock().setType(Material.STONECUTTER);
        setFace(loc.getBlock(), BlockFace.EAST);
        loc.setZ(baseZ+4.5);
        loc.getBlock().setType(Material.FURNACE);
        setFace(loc.getBlock(), BlockFace.EAST);
        loc.setZ(baseZ+5.5);
        loc.getBlock().setType(Material.BLAST_FURNACE);
        setFace(loc.getBlock(), BlockFace.EAST);
        loc.setZ(baseZ+6.5);
        loc.getBlock().setType(Material.SMOKER);
        setFace(loc.getBlock(), BlockFace.EAST);
        loc.setY(baseY+1.5);
        loc.setZ(baseZ+0.5);
        loc.getBlock().setType(Material.ENDER_CHEST);
        setFace(loc.getBlock(), BlockFace.EAST);

        return;
    }
}
