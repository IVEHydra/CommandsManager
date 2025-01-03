package me.ivehydra.commandsmanager.delay;

import org.apache.commons.lang.StringUtils;

public class LoadingBar {

    public static String getLoadingBar(int currentTime, int time, int length, String completedColor, String notCompletedColor, String symbol) {
        float percent = (float) currentTime / time;
        int progress = (int) (length * percent);

        return StringUtils.repeat(completedColor + symbol, progress) + StringUtils.repeat(notCompletedColor + symbol, length - progress);
    }

}
