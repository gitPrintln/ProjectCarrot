package com.carrot.nara.domain;

public enum TimeFormattingType {
    NOW("방금 전"),
    SEC("초 전"),
    MIN("분 전"),
    HOUR("시간 전"),
    DAY("일 전"),
    MONTH("달 전");
    
    private String timeAgo;
    
    private TimeFormattingType(String timeAgo) {
        this.timeAgo = timeAgo;
    }
    
    public String getTimeAgo() {
        return timeAgo;
    }
    
    /**
     * 0: NOW("방금 전"),
       1: SEC("초 전"),
       2: MIN("분 전"),
       3: HOUR("시간 전"),
       4: DAY("일 전"),
       5: MONTH("달 전")
     * @param n 0~5
     * @return (*시간)+ 전
     */
    public static String getValue(int n) {
        TimeFormattingType[] type = values();
        return type[n].getTimeAgo();
    }
}
