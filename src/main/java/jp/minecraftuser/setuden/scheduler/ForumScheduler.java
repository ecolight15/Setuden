
package jp.minecraftuser.setuden.scheduler;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import jp.minecraftuser.ecoframework.TimerFrame;
import jp.minecraftuser.setuden.Setuden;
import org.bukkit.scheduler.BukkitRunnable;

/**
 *
 * @author ecolight
 */
public class ForumScheduler extends TimerFrame{
    private static long counter = 0;
    private static ArrayList<ForumSchedulerEvent> schedule;
    private static int prevminofweek = -1;
    SimpleDateFormat sdf_you = null;
    SimpleDateFormat sdf_hour = null;
    SimpleDateFormat sdf_min = null;
    
    /**
     * コンストラクタ
     * @param plg_ プラグインインスタンス
     * @param name_ 名前
     */
    public ForumScheduler(Setuden plg_, String name_) {
        super(plg_, name_);
        schedule = new ArrayList<>();
        loadSchedule();
    
        String path = plg.getDataFolder().getPath() + "/ignoreLock";
        File file = new File(path);
        sdf_you = new SimpleDateFormat("E");
        sdf_hour = new SimpleDateFormat("HH");
        sdf_min = new SimpleDateFormat("mm");
        if (!file.exists()) {
            if (plg.getPluginFrame("EcoAdmin") != null) {
                plg.getServer().dispatchCommand(plg.getServer().getConsoleSender(), "ecoadmin:lock プラグインの起動シーケンス中です。少々お待ちください。");
            }
        }
    }
    
    private void addSchedule(int youbi, int hour, int min, TypeEvent type, String... params) {
        switch (type) {
            case MESSAGE:
                schedule.add(new ForumSchedulerEventMessage(plg, youbi, hour, min, type, params));
                break;
            case KICK:
                schedule.add(new ForumSchedulerEventKick(plg, youbi, hour, min, type, params));
                break;
            case SAVE:
                schedule.add(new ForumSchedulerEventSave(plg, youbi, hour, min, type, params));
                break;
            case GEN_SHIGEN:
                schedule.add(new ForumSchedulerEventShigen(plg, youbi, hour, min, type, params));
                break;
            case GEN_END:
                schedule.add(new ForumSchedulerEventShigenEnd(plg, youbi, hour, min, type, params));
                break;
            case SHUTDOWN:
                schedule.add(new ForumSchedulerEventShutdown(plg, youbi, hour, min, type, params));
                break;
            case SIGNAL:
                schedule.add(new ForumSchedulerEventSignal(plg, youbi, hour, min, type, params));
                break;
        }
    }
        
    private void loadSchedule() {
        if (conf.getBoolean("setuden-scheduler")) {
            addSchedule(TypeDayOfWeek.DO.getValue(), 19, 30, TypeEvent.MESSAGE, "本日20:00に新資源マップの再生成及びサーバ再起動が行われます");
            addSchedule(TypeDayOfWeek.DO.getValue(), 19, 59, TypeEvent.MESSAGE, "本日20:00に新資源マップの再生成及びサーバ再起動が行われます");
            addSchedule(TypeDayOfWeek.DO.getValue(), 21, 00, TypeEvent.MESSAGE, "本日21:30から資源END再生成とサーバ再起動、及びエンドラ戦が行われます");
            addSchedule(TypeDayOfWeek.DO.getValue(), 21, 29, TypeEvent.MESSAGE, "本日21:30から資源END再生成とサーバ再起動、及びエンドラ戦が行われます");
        }
        addSchedule(TypeDayOfWeek.NICHI.getValue() |
                    TypeDayOfWeek.GETSU.getValue() |
                    TypeDayOfWeek.KA.getValue() |
                    TypeDayOfWeek.SUI.getValue() |
                    TypeDayOfWeek.MOKU.getValue() |
                    TypeDayOfWeek.KIN.getValue() |
                    TypeDayOfWeek.DO.getValue() , 4, 30, TypeEvent.MESSAGE, "5:00より定時サーバー再起動に入ります(30分前)");
        addSchedule(TypeDayOfWeek.NICHI.getValue() |
                    TypeDayOfWeek.GETSU.getValue() |
                    TypeDayOfWeek.KA.getValue() |
                    TypeDayOfWeek.SUI.getValue() |
                    TypeDayOfWeek.MOKU.getValue() |
                    TypeDayOfWeek.KIN.getValue() |
                    TypeDayOfWeek.DO.getValue() , 4, 45, TypeEvent.MESSAGE, "5:00より定時サーバー再起動に入ります(15分前)");
        addSchedule(TypeDayOfWeek.NICHI.getValue() |
                    TypeDayOfWeek.GETSU.getValue() |
                    TypeDayOfWeek.KA.getValue() |
                    TypeDayOfWeek.SUI.getValue() |
                    TypeDayOfWeek.MOKU.getValue() |
                    TypeDayOfWeek.KIN.getValue() |
                    TypeDayOfWeek.DO.getValue() , 4, 55, TypeEvent.MESSAGE, "5:00より定時サーバー再起動に入ります(5分前)");
        addSchedule(TypeDayOfWeek.NICHI.getValue() |
                    TypeDayOfWeek.GETSU.getValue() |
                    TypeDayOfWeek.KA.getValue() |
                    TypeDayOfWeek.SUI.getValue() |
                    TypeDayOfWeek.MOKU.getValue() |
                    TypeDayOfWeek.KIN.getValue() |
                    TypeDayOfWeek.DO.getValue() , 4, 59, TypeEvent.MESSAGE, "5:00より定時サーバー再起動に入ります(1分前)");
        addSchedule(TypeDayOfWeek.NICHI.getValue() |
                    TypeDayOfWeek.GETSU.getValue() |
                    TypeDayOfWeek.KA.getValue() |
                    TypeDayOfWeek.SUI.getValue() |
                    TypeDayOfWeek.MOKU.getValue() |
                    TypeDayOfWeek.KIN.getValue() |
                    TypeDayOfWeek.DO.getValue() , 5, 0, TypeEvent.KICK, "毎日5時の定例サーバ再起動", "サーバーの定例再起動中のため一時的にログインできません");

        addSchedule(TypeDayOfWeek.NICHI.getValue() |
                TypeDayOfWeek.GETSU.getValue() |
                TypeDayOfWeek.KA.getValue() |
                TypeDayOfWeek.SUI.getValue() |
                TypeDayOfWeek.MOKU.getValue() |
                TypeDayOfWeek.KIN.getValue() |
                TypeDayOfWeek.DO.getValue() , 5, 1, TypeEvent.SAVE);
//        addSchedule(TypeDayOfWeek.NICHI.getValue() |
//                    TypeDayOfWeek.GETSU.getValue() |
//                    TypeDayOfWeek.KA.getValue() |
//                    TypeDayOfWeek.SUI.getValue() |
//                    TypeDayOfWeek.MOKU.getValue() |
//                    TypeDayOfWeek.KIN.getValue() |
//                    TypeDayOfWeek.DO.getValue() , 5, 0, TypeEvent.KICK, "毎日5時の定例サーバ再起動", "サーバーの定例再起動中のため一時的にログインできません");
//        addSchedule(TypeDayOfWeek.NICHI.getValue() |
//                    TypeDayOfWeek.GETSU.getValue() |
//                    TypeDayOfWeek.KA.getValue() |
//                    TypeDayOfWeek.SUI.getValue() |
//                    TypeDayOfWeek.MOKU.getValue() |
//                    TypeDayOfWeek.KIN.getValue() |
//                    TypeDayOfWeek.DO.getValue() , 5, 1, TypeEvent.SHUTDOWN, "毎日定例のサーバ再起動", "サーバーの定例再起動中のため一時的にログインできません");
        addSchedule(TypeDayOfWeek.NICHI.getValue() |
                    TypeDayOfWeek.GETSU.getValue() |
                    TypeDayOfWeek.KA.getValue() |
                    TypeDayOfWeek.SUI.getValue() |
                    TypeDayOfWeek.MOKU.getValue() |
                    TypeDayOfWeek.KIN.getValue() |
                    TypeDayOfWeek.DO.getValue() , -1, 0, TypeEvent.SIGNAL);

        if (conf.getBoolean("setuden-scheduler")) {
            addSchedule(TypeDayOfWeek.DO.getValue() , 20, 0, TypeEvent.KICK, "資源再生成に伴うサーバ再起動", "資源マップ再生成に伴うサーバー再起動中のため一時的にログインできません");
            addSchedule(TypeDayOfWeek.DO.getValue() , 20, 1, TypeEvent.GEN_SHIGEN);
            addSchedule(TypeDayOfWeek.DO.getValue() , 20, 2, TypeEvent.SHUTDOWN);

            addSchedule(TypeDayOfWeek.DO.getValue() , 21, 30, TypeEvent.KICK, "資源END再生成に伴うサーバ再起動", "資源エンド再生成に伴うサーバー再起動中のため一時的にログインできません");
            addSchedule(TypeDayOfWeek.DO.getValue() , 21, 31, TypeEvent.GEN_END);
            addSchedule(TypeDayOfWeek.DO.getValue() , 21, 32, TypeEvent.SHUTDOWN);
        }
    }
    
    @Override
    public void run()
    {
        // 起動してから10回(5tick*40 = 200tick = 10秒)は動作を行わない
        int interval = conf.getInt("sever-open-interval");
        if (counter < interval) { counter++; return; }
        if (counter == interval) {
            counter++;
            if (plg.getPluginFrame("EcoAdmin") != null) {
                plg.getServer().dispatchCommand(plg.getServer().getConsoleSender(), "ecoadmin:unlock");
                log.info("サーバーの起動後安定待ちロックダウンを解除しました");
            }
        } 

        // 現在の曜日＋時間＋分 のシリアル数値算出
        Date date = new Date();
        int minofweek = (int) (date.getTime() / 60000);
        
        // 直前の実行回と分に変化があれば走査する
        if (prevminofweek != minofweek) {
            int num = ForumSchedulerEvent.convMOW2(sdf_you.format(date), Integer.parseInt(sdf_hour.format(date)), Integer.parseInt(sdf_min.format(date)));

            for (final ForumSchedulerEvent e: schedule) {
                if (e.contains(num)) {
                    // 即時実行
                    log.info("start:"+e.toString());
                    try {
                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                e.run();
                            }
                        }.runTaskLater(plg, 0);
                    } catch (Exception ex) {
                        log.warning(ex.getLocalizedMessage());
                    }
                }
            }
            prevminofweek = minofweek;
        }
    }
}
