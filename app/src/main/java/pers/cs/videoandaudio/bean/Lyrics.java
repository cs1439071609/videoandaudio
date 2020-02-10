package pers.cs.videoandaudio.bean;

/**
 * @author chensen
 * @time 2020/2/8  12:30
 * @desc
 */
public class Lyrics {

    private long timePoint;
    private String content;
    private long sleepTime;

    public long getTimePoint() {
        return timePoint;
    }

    public void setTimePoint(long timePoint) {
        this.timePoint = timePoint;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getSleepTime() {
        return sleepTime;
    }

    public void setSleepTime(long sleepTime) {
        this.sleepTime = sleepTime;
    }

    @Override
    public String toString() {
        return "Lyrics{" +
                "timePoint='" + timePoint + '\'' +
                ", content='" + content + '\'' +
                ", sleepTime=" + sleepTime +
                '}';
    }
}
