
package jp.minecraftuser.setuden.scheduler;

import java.util.ArrayList;
import java.util.logging.Logger;
import jp.minecraftuser.ecoframework.PluginFrame;

/**
 *
 * @author ecolight
 */
abstract class ForumSchedulerEvent{
    protected final PluginFrame plg;
    protected final Logger log;
    protected final ArrayList<Integer> schedule;
    protected final TypeEvent type;
    protected final String[] params;
    public PluginFrame getPlg() { return plg; }
    public ArrayList<Integer> getSchedule() { return schedule; }
    public TypeEvent getType() { return type; }
    public String[] getParams() { return params; }
    
    // コンストラクタ、指定曜日のスケジュールを登録する。hourが0～23の範囲外の場合は毎時イベントとする。
    public ForumSchedulerEvent(PluginFrame plg_, int youbi, int hour, int min, TypeEvent type_, String... params_) {
        plg = plg_;
        log = plg.getLogger();
        schedule = new ArrayList<>();
        for (TypeDayOfWeek w : TypeDayOfWeek.toDayOfWeek(youbi)) {
            if (hour < 0 || hour >= 24) {
                for (int i = 0; i < 24; i++) {
                    schedule.add(convMOW(w, i, min));
                }
            } else {
                schedule.add(convMOW(w, hour, min));
            }
        }
        type = type_;
        params = params_;
    }
    
    // 1週間のうち何分目か算出する
    public static int convMOW(TypeDayOfWeek youbi, int hour, int min) {
        int hourbuf = 0;
        switch(youbi) {
            case GETSU: hourbuf = 24; break;
            case KA: hourbuf = 24 * 2; break;
            case SUI: hourbuf = 24 * 3; break;
            case MOKU: hourbuf = 24 * 4; break;
            case KIN: hourbuf = 24 * 5; break;
            case DO: hourbuf = 24 * 6; break;
            case NICHI: hourbuf = 24 * 7; break;
            default: break;
        }
        hourbuf += hour;
        return (hourbuf * 60 + min);
    }
    // 1週間のうち何分目か算出する
    public static int convMOW2(String youbi, int hour, int min) {
        int hourbuf = 0;
        switch(youbi) {
            case "月": hourbuf = 24; break;
            case "火": hourbuf = 24 * 2; break;
            case "水": hourbuf = 24 * 3; break;
            case "木": hourbuf = 24 * 4; break;
            case "金": hourbuf = 24 * 5; break;
            case "土": hourbuf = 24 * 6; break;
            case "日": hourbuf = 24 * 7; break;
        }
        hourbuf += hour;
        return (hourbuf * 60 + min);
    }
    
    // 登録件数を返却する
    public int size() {
        return schedule.size();
    }
    // 一致するMinOfWeekがあるか
    public boolean contains(int mow) {
        return schedule.contains(mow);
    }
    
    abstract public void run();
}
