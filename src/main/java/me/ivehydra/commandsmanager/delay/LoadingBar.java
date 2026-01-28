package me.ivehydra.commandsmanager.delay;

import org.apache.commons.lang.StringUtils;

public class LoadingBar {

    public static float getPercent(int currentTime, int time) { return Math.min(1F, (float) currentTime / time); }

    public static String getLoadingBar(int currentTime, int time, int length, String completedColor, String notCompletedColor, String symbol) {
        float percent = getPercent(currentTime, time);
        int progress = (int) (length * percent);

        return StringUtils.repeat(completedColor + symbol, progress) + StringUtils.repeat(notCompletedColor + symbol, length - progress);
    }

}
