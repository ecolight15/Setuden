
package jp.minecraftuser.setuden.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import jp.minecraftuser.ecoframework.PluginFrame;
import jp.minecraftuser.ecoframework.DatabaseFrame;
import jp.minecraftuser.ecomqttserverlog.EcoMQTTServerLog;
import org.bukkit.ChatColor;
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
    String total_play_time_format = new String("%d日%02d時間%02d分%02d秒");

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

    public List<String> getWhois(String name) {
        // UUID問い合わせプラグインの検出
        EcoMQTTServerLog logp = (EcoMQTTServerLog) plg.getServer().getPluginManager().getPlugin("EcoMQTTServerLog");
        List<String> whoisTextList = new ArrayList<>();
        if (logp != null) {
            UUID uuid = logp.latestUUID(name);
            OfflinePlayer pl = plg.getServer().getOfflinePlayer(uuid);
            if (pl.isBanned()) {
                whoisTextList.add("Whois[" + name + "] *** Banned Player *** ");
                whoisTextList.addAll(getWhoisDate(uuid));
                return whoisTextList;
            } else {
                whoisTextList.add("Whois[" + name + "]");
                whoisTextList.addAll(getWhoisDate(uuid));
                return whoisTextList;
            }
        } else {
            whoisTextList.add("Whois[" + name + "]");
            whoisTextList.addAll(getWhoisDate(plg.getServer().getOfflinePlayer(name).getUniqueId()));
            return whoisTextList;
            //return "UUID Plugin Not Ready.";
        }

    }

    private List<String> getWhoisDate(UUID uuid) {
        PreparedStatement prep = null;
        ResultSet rs = null;
        String msg = null;
        Date login = null;
        Date quit = null;
        Connection con = null;
        OfflinePlayer player = plg.getServer().getOfflinePlayer(uuid);
        List<String> whoisTextList = new ArrayList<>();
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
                whoisTextList.add("database read error.");
                return whoisTextList;

            }
        }


        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss ");

        if ((login == null) || (login.getTime() == 0)) {
            whoisTextList.add("最終ログイン　:" + ChatColor.GOLD + "No record.");
        } else {
            whoisTextList.add("最終ログイン　:" + ChatColor.GOLD + sdf1.format(login));
        }
        if ((quit == null) || (quit.getTime() == 0)) {
            whoisTextList.add("最終ログアウト:" + ChatColor.GOLD + "No record.");
        } else {
            whoisTextList.add("最終ログアウト:" + ChatColor.GOLD + sdf1.format(quit));
        }

        if (player.getFirstPlayed() == 0) {
            whoisTextList.add("初回ログイン　:" + ChatColor.GOLD + "No record.");
        } else {
            Date first_login = new Date(player.getFirstPlayed());

            whoisTextList.add("初回ログイン　:" + ChatColor.GOLD + sdf1.format(first_login));
        }

        if (player.getStatistic(Statistic.TOTAL_WORLD_TIME) == 0) {
            whoisTextList.add("合計プレイ時間:" + ChatColor.GOLD + "No record.");
        } else {
            long total_play_second = player.getStatistic(Statistic.TOTAL_WORLD_TIME) / 20;
            long play_day = total_play_second / 86400;
            long play_hour = total_play_second % 3600;
            long play_minute = (total_play_second % 3600) / 60;
            long play_second = total_play_second % 60;
            StringBuilder b = new StringBuilder();
            b.append("合計プレイ時間:").append(ChatColor.GOLD).append(String.format(total_play_time_format, play_day, play_hour, play_minute, play_second));
            whoisTextList.add(b.toString());
        }


        if (msg != null) {
            whoisTextList.add("コメント　　　:" + ChatColor.GOLD + msg);
        }
        return whoisTextList;
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
