package com.cpic.barsms.bpm.common.utils;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class RandomUtils {

    private static double random() {
        try {
            return SecureRandom.getInstanceStrong().nextDouble();
        } catch (NoSuchAlgorithmException e) {
            return 0.33772051716679785;
        }
    }

    public static int getRandom1() {
        int round = (int) Math.round(random() * 10);
        return round;
    }

    public static String getRandom5() {
        long round = Math.round((random() + 1) * 10000);
        return String.valueOf(round);
    }

    public static String getRandom6() {
        long round = Math.round((random() + 1) * 100000);
        return String.valueOf(round);
    }

    public static String getRandom10() {
        long round = Math.round((random() + 1) * 1000000000);
        return String.valueOf(round);
    }

    public static String getRandom16() {
        return getRandom5() + getRandom5() + getRandom6();
    }

    @Deprecated
    public static String createDbLogicId20() {
        return DateFormatUtils.getDateId() + getRandom5();
    }

    public static String createDbLogicId() {
        return DateFormatUtils.getDateId() + getRandom10();
    }

    public static String createDbLogicIdPlus() {
        return DateFormatUtils.getDateId() + getRandom10() + getRandom5();
    }
}
