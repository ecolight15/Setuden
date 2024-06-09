
package jp.minecraftuser.setuden.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import jp.minecraftuser.ecoframework.DatabaseFrame;
import jp.minecraftuser.ecoframework.PluginFrame;
import jp.minecraftuser.ecomqttserverlog.EcoMQTTServerLog;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.Statistic;
/**
 * @author ecolight
 */
public class WhoisDB extends DatabaseFrame {

    /**
     * コンストラクタ
     *
     * @param plg_ プラグインインスタンス
     * @param name_ 名前
     * @throws java.lang.ClassNotFoundException
     * @throws java.sql.SQLException
     */
    private String total_play_time_format = new String("%d時間(%d日%02d時間%02d分%02d秒)");
    private String location_format = "world[%s] X[%d] Y[%d] Z[%d]";

    public WhoisDB(PluginFrame plg_, String name_) throws ClassNotFoundException, SQLException {
        super(plg_, "whois.db", name_);
    }

    /**
     * データベース移行処理
     * 内部処理からトランザクション開始済みの状態で呼ばれる
     *
     * @throws SQLException
     */
    @Override
    protected void migrationData(Connection con) throws SQLException {
        // version 1 の場合、新規作成もしくは旧バージョンのデータベース引き継ぎの場合を検討する
        if (dbversion == 1) {
            if (justCreated) {
                // 新規作成の場合、テーブル定義のみ作成して終わり
                executeStatement(con, "CREATE TABLE IF NOT EXISTS WHOISTABLE(MOSTUUID INTEGER NOT NULL, LEASTUUID INTEGER NOT NULL, LOGIN INTEGER, QUIT INTEGER, MSG STRING, UNIQUE(MOSTUUID, LEASTUUID));");
                log.info("DataBase WHOISTABLE table checked.");
                // データベースバージョンは最新版数に設定する
                log.info("create " + name + " version 2");
                updateSettingsVersion(con, 2);
                return;
            } else {
                // 既存DB引き継ぎの場合は新規作成と同レベルの状態にする必要がある
                // 1 -> 2版の変更内容
                // - (1) CHANNELテーブルにACTIVATEカラム追加
                // - (2) USNGCONFテーブル新規作成 : USUSCONFからNG設定分離
                // - (3) USUSCONFからNG削除, TARGETのDELETE CASCADE指定を追加
                // - (4) CHUSERSのユーザー対応情報にUUID保持していたのをUSERSテーブルキーのUSERIDを保持するようにカラム変更
                log.info("Start " + plg.getName() + "database migration " + name + " version 1 -> 2 start");

                //-----------------------------------------------------------------------------------
                // データベースバージョンは次版にする
                //-----------------------------------------------------------------------------------
                updateSettingsVersion(con);

                log.info(plg.getName() + " database migration " + name + " version 1 -> 2 completed.");
            }
        }
    }

    public boolean isExist(UUID uuid) {
        boolean exist = false;
        PreparedStatement prep = null;
        Connection con = null;
        ResultSet rs = null;
        try {
            con = connect();
            prep = con.prepareStatement("SELECT * FROM WHOISTABLE WHERE MOSTUUID = ? AND LEASTUUID = ?");
            prep.setLong(1, uuid.getMostSignificantBits());
            prep.setLong(2, uuid.getLeastSignificantBits());
            rs = prep.executeQuery();
            exist = rs.next();
            rs.close();
            prep.close();
            con.close();
        } catch (SQLException ex) {
            log.info(ex.getLocalizedMessage());
            log.info(ex.getMessage());
            log.info(ex.getSQLState());
            if (rs != null) try {
                rs.close();
            } catch (SQLException ex1) {
                Logger.getLogger(WhoisDB.class.getName()).log(Level.SEVERE, null, ex1);
            }
            if (prep != null) try {
                prep.close();
            } catch (SQLException ex1) {
                Logger.getLogger(WhoisDB.class.getName()).log(Level.SEVERE, null, ex1);
            }
            if (con != null) try {
                con.close();
            } catch (SQLException ex1) {
                Logger.getLogger(WhoisDB.class.getName()).log(Level.SEVERE, null, ex1);
            }
        }

        return exist;
    }

    public String getWhois(String name, Boolean isOp) {
        // UUID問い合わせプラグインの検出
        EcoMQTTServerLog logp = (EcoMQTTServerLog) plg.getServer().getPluginManager().getPlugin("EcoMQTTServerLog");
        StringBuilder whoisText = new StringBuilder();
        if (logp != null) {
            UUID uuid = logp.latestUUID(name);
            if(uuid == null){
                return "Player UUID not Found";
            }
            OfflinePlayer pl = plg.getServer().getOfflinePlayer(uuid);
            if (pl.isBanned()) {
                whoisText.append("Whois[").append(name).append("] *** Banned Player *** ").append("\n");
                whoisText.append(getWhoisDate(uuid, isOp));
                return whoisText.toString();
            } else {
                whoisText.append("Whois[").append(name).append("]").append("\n");
                whoisText.append(getWhoisDate(uuid, isOp));
                return whoisText.toString();
            }
        } else {
            whoisText.append("Whois[").append(name).append("]").append("\n");
            whoisText.append(getWhoisDate(plg.getServer().getOfflinePlayer(name).getUniqueId(), isOp));
            return whoisText.toString();
            //return "UUID Plugin Not Ready.";
        }

    }

    private String getWhoisDate(UUID uuid, Boolean isOp) {
        PreparedStatement prep = null;
        ResultSet rs = null;
        String msg = null;
        Date login = null;
        Date quit = null;
        Connection con = null;
        OfflinePlayer player = plg.getServer().getOfflinePlayer(uuid);

        StringBuilder whoisText = new StringBuilder();
        if (uuid != null) {
            try {
                con = connect();
                prep = con.prepareStatement("SELECT * FROM WHOISTABLE WHERE MOSTUUID = ? AND LEASTUUID = ?");
                prep.setLong(1, uuid.getMostSignificantBits());
                prep.setLong(2, uuid.getLeastSignificantBits());
                rs = prep.executeQuery();
                boolean result = rs.next();
                if (result) {
                    msg = rs.getString("MSG");
                    login = new Date(rs.getLong("LOGIN"));
                    quit = new Date(rs.getLong("QUIT"));
                }
                rs.close();
                prep.close();
                con.close();
            } catch (SQLException ex) {
                log.info(ex.getLocalizedMessage());
                log.info(ex.getMessage());
                log.info(ex.getSQLState());
                if (rs != null) try {
                    rs.close();
                } catch (SQLException ex1) {
                    Logger.getLogger(WhoisDB.class.getName()).log(Level.SEVERE, null, ex1);
                }
                if (prep != null) try {
                    prep.close();
                } catch (SQLException ex1) {
                    Logger.getLogger(WhoisDB.class.getName()).log(Level.SEVERE, null, ex1);
                }
                if (con != null) try {
                    con.close();
                } catch (SQLException ex1) {
                    Logger.getLogger(WhoisDB.class.getName()).log(Level.SEVERE, null, ex1);
                }

                whoisText.append("database read error.");
                return whoisText.toString();

            }
        }

        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss ");

        if ((login == null) || (login.getTime() == 0)) {
            whoisText.append("最終ログイン　:").append(ChatColor.GOLD).append("No record.").append(ChatColor.RESET).append("\n");
        } else {
            whoisText.append("最終ログイン　:").append(ChatColor.GOLD).append(sdf1.format(login)).append(ChatColor.RESET).append("\n");
        }
        if ((quit == null) || (quit.getTime() == 0)) {
            whoisText.append("最終ログアウト:").append(ChatColor.GOLD).append("No record.").append(ChatColor.RESET).append("\n");
        } else {
            whoisText.append("最終ログアウト:").append(ChatColor.GOLD).append(sdf1.format(quit)).append(ChatColor.RESET).append("\n");
        }

        if (player.getFirstPlayed() == 0) {
            whoisText.append("初回ログイン　:").append(ChatColor.GOLD).append("No record.").append(ChatColor.RESET).append("\n");
        } else {
            Date first_login = new Date(player.getFirstPlayed());
            whoisText.append("初回ログイン　:").append(ChatColor.GOLD).append(sdf1.format(first_login)).append(ChatColor.RESET).append("\n");
        }

        if (player.getStatistic(Statistic.PLAY_ONE_MINUTE) == 0) {
            whoisText.append("合計プレイ時間:").append(ChatColor.GOLD).append("No record.").append(ChatColor.RESET).append("\n");
        } else {
            long total_play_second = player.getStatistic(Statistic.PLAY_ONE_MINUTE) / 20;
            long play_hours = total_play_second / 3600;
            long play_day = total_play_second / 86400;
            long play_hour = (total_play_second % 86400) / 3600;
            long play_minute = (total_play_second % 3600) / 60;
            long play_second = total_play_second % 60;
            whoisText.append("合計プレイ時間:").append(ChatColor.GOLD).append(String.format(total_play_time_format, play_hours, play_day, play_hour, play_minute, play_second)).append(ChatColor.RESET).append("\n");
        }

        if (msg != null) {
            whoisText.append("コメント　　　:").append(ChatColor.GOLD).append(msg).append(ChatColor.RESET);
        } else {
            whoisText.append("コメント　　　:").append(ChatColor.GOLD).append("No record.").append(ChatColor.RESET);
        }
        //OPならロケーションも表示
        if (isOp) {
            Location loc = player.getLocation();
            if (loc != null &&loc.getWorld() != null) {
                whoisText.append("\n").append("ロケーション　:").append(ChatColor.GOLD).append(String.format(location_format, loc.getWorld().getName(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ())).append(ChatColor.RESET);
            } else {
                whoisText.append("\n").append("ロケーション　:").append(ChatColor.GOLD).append("No record.").append(ChatColor.RESET);
            }
        }

        return whoisText.toString();
    }

    public void update(UUID uuid, Date login, Date quit) {
        PreparedStatement prep = null;
        Connection con = null;
        if (isExist(uuid)) {
            try {
                con = connect();
                // UUIDテーブルに現在の名前を登録
                if ((login != null) && (quit != null)) {
                    prep = con.prepareStatement("UPDATE WHOISTABLE SET LOGIN = ?, QUIT = ? WHERE MOSTUUID = ? AND LEASTUUID = ?");
                    prep.setLong(1, login.getTime());
                    prep.setLong(2, quit.getTime());
                    prep.setLong(3, uuid.getMostSignificantBits());
                    prep.setLong(4, uuid.getLeastSignificantBits());
                } else if ((login != null) && (quit == null)) {
                    prep = con.prepareStatement("UPDATE WHOISTABLE SET LOGIN = ? WHERE MOSTUUID = ? AND LEASTUUID = ?");
                    prep.setLong(1, login.getTime());
                    prep.setLong(2, uuid.getMostSignificantBits());
                    prep.setLong(3, uuid.getLeastSignificantBits());
                } else if ((login == null) && (quit != null)) {
                    prep = con.prepareStatement("UPDATE WHOISTABLE SET QUIT = ? WHERE MOSTUUID = ? AND LEASTUUID = ?");
                    prep.setLong(1, quit.getTime());
                    prep.setLong(2, uuid.getMostSignificantBits());
                    prep.setLong(3, uuid.getLeastSignificantBits());
                } else {

                }
                prep.executeUpdate();
                con.commit();
                prep.close();
                con.close();
            } catch (SQLException ex) {
                log.info(ex.getLocalizedMessage());
                log.info(ex.getMessage());
                log.info(ex.getSQLState());
                if (prep != null) try {
                    prep.close();
                } catch (SQLException ex1) {
                    Logger.getLogger(WhoisDB.class.getName()).log(Level.SEVERE, null, ex1);
                }
                if (con != null) try {
                    con.close();
                } catch (SQLException ex1) {
                    Logger.getLogger(WhoisDB.class.getName()).log(Level.SEVERE, null, ex1);
                }
            }
        } else {
            try {
                con = connect();
                // UUIDテーブルに現在の名前を登録
                if ((login != null) && (quit != null)) {
                    prep = con.prepareStatement("INSERT INTO WHOISTABLE(MOSTUUID, LEASTUUID, LOGIN, QUIT) VALUES (?, ?, ?, ?);");
                    prep.setLong(1, uuid.getMostSignificantBits());
                    prep.setLong(2, uuid.getLeastSignificantBits());
                    prep.setLong(3, login.getTime());
                    prep.setLong(4, quit.getTime());
                } else if ((login != null) && (quit == null)) {
                    prep = con.prepareStatement("INSERT INTO WHOISTABLE(MOSTUUID, LEASTUUID, LOGIN, QUIT) VALUES (?, ?, ?, 0);");
                    prep.setLong(1, uuid.getMostSignificantBits());
                    prep.setLong(2, uuid.getLeastSignificantBits());
                    prep.setLong(3, login.getTime());
                } else if ((login == null) && (quit != null)) {
                    prep = con.prepareStatement("INSERT INTO WHOISTABLE(MOSTUUID, LEASTUUID, LOGIN, QUIT) VALUES (?, ?, 0, ?);");
                    prep.setLong(1, uuid.getMostSignificantBits());
                    prep.setLong(2, uuid.getLeastSignificantBits());
                    prep.setLong(3, quit.getTime());
                } else {

                }
                prep.executeUpdate();
                con.commit();
                prep.close();
                con.close();
            } catch (SQLException ex) {
                log.info(ex.getLocalizedMessage());
                log.info(ex.getMessage());
                log.info(ex.getSQLState());
                if (prep != null) try {
                    prep.close();
                } catch (SQLException ex1) {
                    Logger.getLogger(WhoisDB.class.getName()).log(Level.SEVERE, null, ex1);
                }
                if (con != null) try {
                    con.close();
                } catch (SQLException ex1) {
                    Logger.getLogger(WhoisDB.class.getName()).log(Level.SEVERE, null, ex1);
                }
            }
        }
    }

    public void updateMassage(OfflinePlayer player, String message) {
        UUID uuid = player.getUniqueId();
        PreparedStatement prep = null;
        Connection con = null;
        if (isExist(uuid)) {
            try {
                con = connect();
                // UUIDテーブルに現在の名前を登録
                prep = con.prepareStatement("UPDATE WHOISTABLE SET MSG = ? WHERE MOSTUUID = ? AND LEASTUUID = ?");
                prep.setString(1, message);
                prep.setLong(2, uuid.getMostSignificantBits());
                prep.setLong(3, uuid.getLeastSignificantBits());
                prep.executeUpdate();
                con.commit();
                prep.close();
                con.close();
                log.info("UpdateMessage[" + player.getName() + "]:" + message);
            } catch (SQLException ex) {
                log.info(ex.getLocalizedMessage());
                log.info(ex.getMessage());
                log.info(ex.getSQLState());
                if (prep != null) try {
                    prep.close();
                } catch (SQLException ex1) {
                    Logger.getLogger(WhoisDB.class.getName()).log(Level.SEVERE, null, ex1);
                }
                if (con != null) try {
                    con.close();
                } catch (SQLException ex1) {
                    Logger.getLogger(WhoisDB.class.getName()).log(Level.SEVERE, null, ex1);
                }
            }
        }
    }
}
