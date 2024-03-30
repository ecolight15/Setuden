
package jp.minecraftuser.setuden.scheduler;

import java.util.ArrayList;

public enum TypeDayOfWeek {
    GETSU(0x01),
    KA(0x02),
    SUI(0x04),
    MOKU(0x08),
    KIN(0x10),
    DO(0x20),
    NICHI(0x40),
    OTHER(0x80);
    private final int num;
    
    private TypeDayOfWeek(int num) {
        this.num = num;
    }
    
    public int getValue() {
        return num;
    }
    
    public static ArrayList<TypeDayOfWeek> toDayOfWeek(int num) {
        ArrayList<TypeDayOfWeek> result = new ArrayList<>();
        for (TypeDayOfWeek no : values()) {
            
            if (no.getValue() == (num & no.getValue())) {
                result.add(no);
            }
        }
        return result;
    }
}
