
package jp.minecraftuser.setuden.command;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import jp.minecraftuser.setuden.scheduler.ForumSchedulerEventKick;
import jp.minecraftuser.setuden.test.DB;
import jp.minecraftuser.setuden.test.TestMessageThread;
import jp.minecraftuser.setuden.test.TestPayload;
import jp.minecraftuser.ecoframework.PluginFrame;
import jp.minecraftuser.ecoframework.CommandFrame;
import static jp.minecraftuser.ecoframework.Utl.sendPluginMessage;
import jp.minecraftuser.ecoframework.bungee.BungeeController;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

/**
 * リロードコマンドクラス
 * @author ecolight
 */
public class TestCommand extends CommandFrame {
    private static long timeout;
    private static BukkitRunnable runner = null;
    private static Entity ent = null;
    private TestMessageThread thread = null;
    BungeeController bc = new BungeeController(plg) {
        @Override
        protected void receiveForward(Player p, String subChannel, DataInputStream msgin) {
            sendPluginMessage(plg, p, "Forward受信："+p.getName()+":"+subChannel+":"+msgin.toString());
        }

        @Override
        protected void receiveGetServer(Player p, String servername) {
            sendPluginMessage(plg, p, "GetServer受信："+p.getName()+":"+servername);
        }

        @Override
        protected void receiveGetServers(Player p, String[] serverList) {
            for (String s : serverList) {
                sendPluginMessage(plg, p, "GetServers受信："+p.getName()+":"+s);
            }
        }

        @Override
        protected void receiveIP(Player p, String ip, int port) {
            sendPluginMessage(plg, p, "IP受信："+p.getName()+":"+ip+":"+port);
        }

        @Override
        protected void receivePlayerCount(Player p, String server, int playercount) {
            sendPluginMessage(plg, p, "PlayerCount受信："+p.getName()+":"+server+":"+playercount);
        }

        @Override
        protected void receivePlayerList(Player p, String server, String[] playerList) {
            for (String s : playerList) {
                sendPluginMessage(plg, p, "PlayerList受信："+p.getName()+":"+server+":"+s);
            }
        }

        @Override
        protected void receiveServerIP(Player p, String serverName, String ip, int port) {
            sendPluginMessage(plg, p, "ServerIP受信："+p.getName()+":"+serverName+":"+ip+":"+port);
        }

        @Override
        protected void receiveUUID(Player p, String uuid) {
            sendPluginMessage(plg, p, "UUID受信："+p.getName()+":"+uuid);
        }

        @Override
        protected void receiveUUIDOther(Player p, String playerName, String uuid) {
            sendPluginMessage(plg, p, "UUID受信："+p.getName()+":"+playerName+":"+uuid);
        }

    };
    BungeeController bc2 = new BungeeController(plg) {
        @Override
        protected void receiveForward(Player p, String subChannel, DataInputStream msgin) {
            try {
                sendPluginMessage(plg, p, "Forward2受信："+p.getName()+":"+subChannel+":"+msgin.toString());
                if (subChannel.equals("redirect")) {
                    long most = msgin.readLong();
                    long least = msgin.readLong();
                    timeout = msgin.readLong();
                    String target = msgin.readUTF();
                    UUID uid = new UUID(most, least);
                    if (uid.equals(p.getUniqueId())) {
                        if (runner != null) {
                            runner.cancel();
                        }
                        runner = new BukkitRunnable() {
                            @Override
                            public void run() {
                                if ((!p.isOnline()) || (timeout == 0)) {
                                    this.cancel();
                                    runner = null;
                                } else {
                                    connectServer(p, target);
                                    log.log(Level.INFO, "依頼を受けてプレイヤーを転送しました。 Player:{0} Server:{1}", new Object[]{p.getName(), target});
                                    timeout--;
                                }
                            }
                        };
                        runner.runTaskTimer(plg, 20, 20);
                    }
                }
            } catch (IOException ex) {
                Logger.getLogger(TestCommand.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        @Override
        protected void receiveGetServer(Player p, String servername) {
            sendPluginMessage(plg, p, "GetServer2受信："+p.getName()+":"+servername);
        }

        @Override
        protected void receiveGetServers(Player p, String[] serverList) {
            for (String s : serverList) {
                sendPluginMessage(plg, p, "GetServers2受信："+p.getName()+":"+s);
            }
        }

        @Override
        protected void receiveIP(Player p, String ip, int port) {
            sendPluginMessage(plg, p, "IP2受信："+p.getName()+":"+ip+":"+port);
        }

        @Override
        protected void receivePlayerCount(Player p, String server, int playercount) {
            sendPluginMessage(plg, p, "PlayerCount2受信："+p.getName()+":"+server+":"+playercount);
        }

        @Override
        protected void receivePlayerList(Player p, String server, String[] playerList) {
            for (String s : playerList) {
                sendPluginMessage(plg, p, "PlayerList2受信："+p.getName()+":"+server+":"+s);
            }
        }

        @Override
        protected void receiveServerIP(Player p, String serverName, String ip, int port) {
            sendPluginMessage(plg, p, "ServerIP2受信："+p.getName()+":"+serverName+":"+ip+":"+port);
        }

        @Override
        protected void receiveUUID(Player p, String uuid) {
            sendPluginMessage(plg, p, "UUID2受信："+p.getName()+":"+uuid);
        }

        @Override
        protected void receiveUUIDOther(Player p, String playerName, String uuid) {
            sendPluginMessage(plg, p, "UUIDOther2受信："+p.getName()+":"+playerName+":"+uuid);
        }

    };
    /**
     * コンストラクタ
     * @param plg_ プラグインインスタンス
     * @param name_ コマンド名
     */
    public TestCommand(PluginFrame plg_, String name_) {
        super(plg_, name_);
        setAuthBlock(true);
        setAuthConsole(true);
        bc.registerBungeeListener();
        bc2.registerBungeeListener();
    }

    /**
     * コマンド権限文字列設定
     * @return 権限文字列
     */
    @Override
    public String getPermissionString() {
        return "setuden.test";
    }

    /**
     * 処理実行部
     * @param sender コマンド送信者
     * @param args パラメタ
     * @return コマンド処理成否
     */
    @Override
    public boolean worker(CommandSender sender, String[] args) {
//        Set<String> str = conf.getSectionList("test");
//        for (String s : str) {
//            plg.getServer().broadcastMessage(s);
//        }
//        sender.sendMessage("test コマンド完了");
//        for (int i = 1000; i != 0; i--) {
//            sender.sendMessage("line:"+i);
//        }

//        TestTimer t = new TestTimer(plg);
//        t.runTaskTimer(plg, 0, 40);

        if (args.length == 0) return false;
        if (args[0].equalsIgnoreCase("save")) {
            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(plg.getDataFolder().getPath() + "/data.txt"))) {
                TestPayload payload = new TestPayload(plg);
                oos.writeObject(payload);
            } catch (IOException e) {
                log.info(e.getMessage());
            }
        } else if (args[0].equalsIgnoreCase("load")) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(plg.getDataFolder().getPath() + "/data.txt"))) {
                TestPayload dePayload = (TestPayload) ois.readObject();

                sender.sendMessage("name:" + dePayload.getName());
                sender.sendMessage("version:" + dePayload.getVersion());
            } catch (IOException | ClassNotFoundException e) {
                log.info(e.getMessage());
            }
        } else if (args[0].equalsIgnoreCase("start")) {
            if (thread != null) {
                thread.cancel();
                sender.sendMessage("Current parent thread cancel call");
            }
            thread = new TestMessageThread(plg, "test");
            thread.runTaskTimer(plg, 0, 5);
            sender.sendMessage("Thread start call");
        } else if (args[0].equalsIgnoreCase("stop")) {
            if (args.length >= 2) {
                if (args[1].equalsIgnoreCase("parent")) {
                    if (thread != null) {
                        thread.cancel();
                        thread = null;
                        sender.sendMessage("Parent thread cancel call");
                    } else {
                        sender.sendMessage("メッセージ送信スレッドが存在しません");
                    }
                } else if (args[1].equalsIgnoreCase("child")) {
                    if (thread != null) {
                        thread.childStop();
                        sender.sendMessage("Child thread cancel call");
                    } else {
                        sender.sendMessage("メッセージ送信スレッドが存在しません");
                    }
                }
            } else if (thread != null) {
                thread.cancel();
                thread = null;
                sender.sendMessage("Parent thread cancel call");
            } else {
                sender.sendMessage("メッセージ送信スレッドが存在しません");
            }
        } else if (args[0].equalsIgnoreCase("send")) {
            if (thread == null) {
                sender.sendMessage("メッセージ送信スレッドが存在しません");
            } else {
                StringBuilder sb = new StringBuilder();
                int count = 0;
                for (String s : args) {
                    if (count == 0) { count++; continue; }
                    if (count >= 2) sb.append(" ");
                    sb.append(s);
                    count++;
                }
                Player pl = (Player) sender;
                List<UUID> uids = new ArrayList<>();
                uids.add(pl.getUniqueId());
//                thread.sendMessage(new MessagePayload(plg, pl.getUniqueId(), uids.toArray(new UUID[0]), sb.toString()));
            }
        } else if (args[0].equalsIgnoreCase("sender")) {
            if (sender instanceof Player) {
                sender.sendMessage("プレイヤー");
            }
            if (sender instanceof ConsoleCommandSender) {
                sender.sendMessage("コンソール");
            }
            if (sender instanceof BlockCommandSender) {
                sender.sendMessage("ブロック");
            }
        } else if (args[0].equalsIgnoreCase("db")) {
            try {
                DB db = new DB(plg, "test");
                sender.sendMessage("DBオープン");
                db.close();
                sender.sendMessage("DBクローズ");
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(TestCommand.class.getName()).log(Level.SEVERE, null, ex);
            } catch (SQLException ex) {
                Logger.getLogger(TestCommand.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else if (args[0].equalsIgnoreCase("server")) {
             for (World w: plg.getServer().getWorlds()) {
                 sender.sendMessage(w.getName());
             }
        } else if (args[0].equalsIgnoreCase("bungee")) {
            if (args.length == 1) {
                sendPluginMessage(plg, sender, "パラメータ不足");
                return true;
            }

            
            switch (args[1].toLowerCase()) {
                case "connect":
                    bc.connectServer((Player) sender, args[2]);
                    break;
                case "connectother":
                    bc.connectOtherPlayerServer((Player) sender, args[2], args[3]);
                    break;
                case "ip":
                    bc.getPlayerIP((Player) sender);
                    break;
                case "playercount":
                    bc.getPlayerCount((Player) sender, args[2]);
                    break;
                case "playerlist":
                    bc.getPlayerList((Player) sender, args[2]);
                    break;
                case "getservers":
                    bc.getServers((Player) sender);
                    break;
                case "message":
                    bc.sendMessage((Player) sender, args[2], args[3]);
                    break;
                case "getserver":
                    bc.getServer((Player) sender);
                    break;
                case "forward":
                    ByteArrayOutputStream msgbytes = new ByteArrayOutputStream();
                    DataOutputStream msgout = new DataOutputStream(msgbytes);
                    try {
                        msgout.writeUTF("Some kind of data here");
                        msgout.writeShort(123);
                    } catch (IOException ex) {
                        Logger.getLogger(TestCommand.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    bc.forward((Player) sender, args[2], msgbytes);
                    break;
                case "redirect":
                    Player p = plg.getServer().getPlayerExact(args[2]);
                    if (p == null) {
                        sendPluginMessage(plg, sender, "指定したプレイヤーが見つかりませんでした");
                        break;
                    }
                    // プレイヤーが居なくなると送信できないタイミングが
                    bc.connectOtherPlayerServer((Player) sender, args[2], "lobby");
                    ByteArrayOutputStream msgbytes2 = new ByteArrayOutputStream();
                    DataOutputStream msgout2 = new DataOutputStream(msgbytes2);
                    try {
                        msgout2.writeLong(p.getUniqueId().getMostSignificantBits());
                        msgout2.writeLong(p.getUniqueId().getLeastSignificantBits());
                        msgout2.writeLong(10);
                        msgout2.writeUTF(args[3]);
                    } catch (IOException ex) {
                        Logger.getLogger(TestCommand.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    bc.forward((Player) sender, "ALL", "redirect", msgbytes2);
                    break;
                case "forwardtoplayer":
                    ByteArrayOutputStream msgbytes3 = new ByteArrayOutputStream();
                    DataOutputStream msgout3 = new DataOutputStream(msgbytes3);
                    try {
                        msgout3.writeUTF("Some kind of data here");
                        msgout3.writeShort(123);
                    } catch (IOException ex) {
                        Logger.getLogger(TestCommand.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    bc.forwardPlayer((Player) sender, args[2], args[3], msgbytes3);
                    break;
                case "uuid":
                    bc.getUUID((Player) sender);
                    break;
                case "uuidother":
                    bc.getOtherPlayerUUID((Player) sender, args[2]);
                    break;
                case "serverip":
                    bc.getServerIP((Player) sender, args[2]);
                    break;
                case "kick":
                    bc.kickPlayer((Player) sender, args[2], args[3]);
                    break;
            }
            log.info("send bungee");
        } else if (args[0].equalsIgnoreCase("mob")) {
            if (args[1].equals("spawn")) {
                Player p = (Player) sender;
                ent = p.getWorld().spawnEntity(p.getLocation(), EntityType.ZOMBIE);
                Zombie z = (Zombie) ent;
                z.setRemoveWhenFarAway(false);
            } else if (args[1].equalsIgnoreCase("z")) {
                Zombie z = (Zombie) ent;
                Vector v = z.getVelocity();
                v.setZ(Integer.parseInt(args[2]));
                z.setVelocity(v);
            } else if (args[1].equalsIgnoreCase("y")) {
                Zombie z = (Zombie) ent;
                Vector v = z.getVelocity();
                v.setY(Integer.parseInt(args[2]));
                z.setVelocity(v);
            } else if (args[1].equalsIgnoreCase("x")) {
                Zombie z = (Zombie) ent;
                Vector v = z.getVelocity();
                v.setX(Integer.parseInt(args[2]));
                z.setVelocity(v);
            } else if (args[1].equalsIgnoreCase("ai")) {
                Zombie z = (Zombie) ent;
                if (z.hasAI()) {
                    z.setAI(false);
                    sendPluginMessage(plg, sender, "AI OFF");
                } else {
                    z.setAI(true);
                    sendPluginMessage(plg, sender, "AI ON");
                }
            } else if (args[1].equalsIgnoreCase("info")) {
                Zombie z = (Zombie) ent;
                sendPluginMessage(plg, sender, "getName:{0}", z.getName());
                sendPluginMessage(plg, sender, "getCustomName:{0}", z.getCustomName());
                // sendPluginMessage(plg, sender, "getMaxHealth:{0}", Double.toString(z.getMaxHealth()));
                sendPluginMessage(plg, sender, "getHealth:{0}", Double.toString(z.getHealth()));
                sendPluginMessage(plg, sender, "getFireTicks:{0}", Integer.toString(z.getFireTicks()));
                sendPluginMessage(plg, sender, "getMaxFireTicks:{0}", Integer.toString(z.getMaxFireTicks()));
                sendPluginMessage(plg, sender, "getFallDistance:{0}", Float.toString(z.getFallDistance()));
                sendPluginMessage(plg, sender, "getLastDamage:{0}", Double.toString(z.getLastDamage()));
                sendPluginMessage(plg, sender, "getLastDamageCause:{0}", z.getLastDamageCause().getCause().name());
                sendPluginMessage(plg, sender, "getTicksLived:{0}", Integer.toString(z.getTicksLived()));
                sendPluginMessage(plg, sender, "getMaximumAir:{0}", Integer.toString(z.getMaximumAir()));
                sendPluginMessage(plg, sender, "getRemainingAir:{0}", Integer.toString(z.getRemainingAir()));
                sendPluginMessage(plg, sender, "getMaximumNoDamageTicks:{0}", Integer.toString(z.getMaximumNoDamageTicks()));
                sendPluginMessage(plg, sender, "getNoDamageTicks:{0}", Integer.toString(z.getNoDamageTicks()));
                sendPluginMessage(plg, sender, "getRemoveWhenFarAway:{0}", Boolean.toString(z.getRemoveWhenFarAway()));
                sendPluginMessage(plg, sender, "getCanPickupItems:{0}", Boolean.toString(z.getCanPickupItems()));
            } else if (args[1].equalsIgnoreCase("vel")) {
                Zombie z = (Zombie) ent;
                Location loc = z.getLocation();
                Vector v = z.getVelocity();
                sendPluginMessage(plg, sender, "Velocity:x={0} y={1} z={2}",
                        Double.toString(v.getX()),
                        Double.toString(v.getY()),
                        Double.toString(v.getZ())
                        );
                sendPluginMessage(plg, sender, "Loction: x={0} y={1} z={2} Pitch={3} Yaw={4}",
                        Double.toString(loc.getX()),
                        Double.toString(loc.getY()),
                        Double.toString(loc.getZ()),
                        Float.toString(loc.getPitch()),
                        Float.toString(loc.getYaw())
                        );
            } else if (args[1].equalsIgnoreCase("mov1")) {
                Zombie z = (Zombie) ent;
                if (runner != null) runner.cancel();
                z.setAI(false);
                runner = new BukkitRunnable() {
                    @Override
                    public void run() {
                        Location loc = z.getLocation();
                        loc.setYaw(306.03027F);
                        setYawAndPitch(ent, loc.getYaw(), loc.getPitch());
//                        Vector v = loc.toVector();
                        Vector v = z.getVelocity();
                        v.setX(0.1134510715886404D);
                        v.setY(-0.0784000015258789D);
                        v.setZ(0.0825085080617184D);
                        z.setVelocity(v);
                    }
                };
                runner.runTaskTimer(plg, 0, 5);
                sendPluginMessage(plg, sender, "mov1 登録");
            } else if (args[1].equalsIgnoreCase("mov2")) {
                Zombie z = (Zombie) ent;
                if (runner != null) runner.cancel();
                z.setAI(false);
                runner = new BukkitRunnable() {
                    @Override
                    public void run() {
                        Location loc = z.getLocation();
                        loc.setYaw(126.03027F);
                        setYawAndPitch(ent, loc.getYaw(), loc.getPitch());
//                        Vector v = loc.toVector();
                        Vector v = z.getVelocity();
                        v.setX(-0.1134510715886404D);
                        v.setY(-0.0784000015258789D);
                        v.setZ(-0.0825085080617184D);
                        z.setVelocity(v);
                        
                    }
                };
                runner.runTaskTimer(plg, 0, 5);
                sendPluginMessage(plg, sender, "mov2 登録");
            } else if (args[1].equalsIgnoreCase("mov3")) {
                Zombie z = (Zombie) ent;
                if (runner != null) runner.cancel();
                z.setAI(false);
                runner = new BukkitRunnable() {
                    @Override
                    public void run() {
                        Vector v = z.getVelocity();
                        v.setX(0.1134510715886404D);
                        v.setY(-0.0784000015258789D);
                        v.setZ(0.0825085080617184D);
                        z.setVelocity(v);
                    }
                };
                runner.runTaskTimer(plg, 0, 5);
                sendPluginMessage(plg, sender, "mov3 登録");
            } else if (args[1].equalsIgnoreCase("mov4")) {
                
                Zombie z = (Zombie) ent;
                if (runner != null) runner.cancel();
                z.setAI(false);
                runner = new BukkitRunnable() {
                    @Override
                    public void run() {
                        Vector v = z.getVelocity();
                        v.setX(-0.1134510715886404D);
                        v.setY(-0.0784000015258789D);
                        v.setZ(-0.0825085080617184D);
                        z.setVelocity(v);
                        
                    }
                };
                runner.runTaskTimer(plg, 0, 5);
                sendPluginMessage(plg, sender, "mov4 登録");
            } else if (args[1].equalsIgnoreCase("mov")) {
                if (runner != null) runner.cancel();
                sendPluginMessage(plg, sender, "mov 削除");
            }
        } else if (args[0].equalsIgnoreCase("plsave")) {
            Bukkit.savePlayers();
        } else if (args[0].equalsIgnoreCase("kicksave")) {
            if (args.length == 2) {
                plg.getServer().dispatchCommand(plg.getServer().getConsoleSender(), "ecoadmin:lock "+args[1]);
            } else {
                plg.getServer().dispatchCommand(plg.getServer().getConsoleSender(), "ecoadmin:lock test");
            }
        }

        return true;
    }
    public Entity getEnt() {
        return ent;
    }
    public static void setYawAndPitch(Entity en, float newYaw, float newPitch){
        try{
           String path = Bukkit.getServer().getClass().getPackage().getName();
           String version = path.substring(path.lastIndexOf(".") + 1, path.length());
           Class<?> EntityLivingClass = Class.forName("net.minecraft.server." + version +".EntityLiving");
           Class<?> CraftEntityClass = Class.forName("org.bukkit.craftbukkit." + version +".entity.CraftEntity");
           Method getHandle = CraftEntityClass.getMethod("getHandle", new Class<?>[0]);
           Object craftEntityObject = CraftEntityClass.cast(en);
           Object entityLivingObject = EntityLivingClass.cast(getHandle.invoke(craftEntityObject, new Object[0]));
           Field yawField = EntityLivingClass.getField("yaw");
           yawField.setFloat(entityLivingObject, newYaw);
           Field pitchField = EntityLivingClass.getField("pitch");
           pitchField.setFloat(entityLivingObject, newPitch);
        }catch(Exception ex){
            ex.printStackTrace();
        }
    }
}
