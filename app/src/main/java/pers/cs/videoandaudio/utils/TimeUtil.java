package pers.cs.videoandaudio.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Formatter;
import java.util.Locale;

/**
 * @author chensen
 * @time 2020/1/10  19:54
 * @desc
 */
public class TimeUtil {

    private StringBuffer mStringBuffer;
    private Formatter mFormatter;


    public TimeUtil(){
        mStringBuffer = new StringBuffer();
        mFormatter = new Formatter(mStringBuffer, Locale.getDefault());
    }

    public String formatTime(int timeMs) {

        int totalSeconds = timeMs / 1000;
        int seconds = totalSeconds % 60;

        int minutes = totalSeconds / 60 % 60;

        int hours = totalSeconds / 3600;

        mStringBuffer.setLength(0);
        if (hours > 0) {
            return mFormatter.format("%02d:%02d:%02d", hours, minutes, seconds).toString();
        } else {
            return mFormatter.format("%02d:%02d", minutes, seconds).toString();

        }
    }

    public String getSystemTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        Calendar calendar = Calendar.getInstance();
        return sdf.format(calendar.getTime());
    }
}
