
package jp.minecraftuser.setuden.scheduler;

import java.util.ArrayList;

public enum TypeEvent {
    MESSAGE(1),
    SIGNAL(2),
    SHUTDOWN(3),
    KICK(4),
    SAVE(5),
    GEN_SHIGEN(6),
    GEN_END(7),
    ;
    private final int num;
    
    private TypeEvent(int num) {
        this.num = num;
    }
    
    public int getValue() {
        return num;
    }
    
    public static ArrayList<TypeEvent> toEvent(int num) {
        ArrayList<TypeEvent> result = new ArrayList<>();
        for (TypeEvent no : values()) {
            
            if (no.getValue() == (num & no.getValue())) {
                result.add(no);
            }
        }
        return result;
    }
}
