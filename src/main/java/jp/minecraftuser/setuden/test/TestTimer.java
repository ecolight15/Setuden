package jp.minecraftuser.setuden.test;

import jp.minecraftuser.ecoframework.async.AsyncFrame;
import jp.minecraftuser.ecoframework.PluginFrame;

public class TestTimer extends AsyncFrame {
    long parentNum = 0;
    long childNum = 0;
    public TestTimer(PluginFrame plg_, String name_) {
        super(plg_, name_);
    }

    @Override
    protected AsyncFrame clone() {
        return new TestTimer(plg, name);
    }

    @Override
    protected void parentRun() {
        //log.info("parent:"+parentNum);
        parentNum++;
    }

    @Override
    protected void childRun() {
        for (childNum = 0; ; childNum++) {
//            try {
//                Thread.sleep(1000);
//            } catch (InterruptedException ex) {
//                Logger.getLogger(TestTimer.class.getName()).log(Level.SEVERE, null, ex);
//            }
            //log.info("child:"+childNum);
        }
    }
}
