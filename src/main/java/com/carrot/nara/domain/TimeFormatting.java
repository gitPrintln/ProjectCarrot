package com.carrot.nara.domain;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class TimeFormatting {
    
    private static class TIME_STANDARD {
        public static final int SEC = 60;
        public static final int MIN = 60;
        public static final int HOUR = 24;
        public static final int DAY = 30;
        public static final int MONTH = 12;
    }
    
    /**
     * LocalDateTime 시간을 입력하면 n분 전, n초 전을 리턴함.
     * @param writtenTime 대상 시간
     * @return ~시간 전의 문자열
     */
    public static String formatting(LocalDateTime writtenTime) {
        String msg = "";
        
        // 현재 시간
        LocalDateTime nowTime = LocalDateTime.now();
        
        // 현재시간 - 비교시간
        long diff = ChronoUnit.SECONDS.between(writtenTime, nowTime);
        
        if(diff == 0) {
            msg = TimeFormattingType.getValue(0);
        } else if(diff < TIME_STANDARD.SEC) {
            msg = diff + TimeFormattingType.getValue(1);
        } else if((diff /= TIME_STANDARD.SEC) < TIME_STANDARD.MIN) {
            msg = diff + TimeFormattingType.getValue(2);
        } else if((diff /= TIME_STANDARD.MIN) < TIME_STANDARD.HOUR) {
            msg = diff + TimeFormattingType.getValue(3);
        } else if((diff /= TIME_STANDARD.HOUR) < TIME_STANDARD.DAY) {
            msg = diff + TimeFormattingType.getValue(4);
        } else if((diff /= TIME_STANDARD.MONTH) < TIME_STANDARD.MONTH) {
            msg = diff + TimeFormattingType.getValue(5);
        } else {
            msg = writtenTime.format(DateTimeFormatter.ofPattern("yy.MM.dd"));
        }
        
        return msg;
    }
}
