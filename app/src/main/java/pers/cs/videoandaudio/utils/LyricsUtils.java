package pers.cs.videoandaudio.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import pers.cs.videoandaudio.bean.Lyrics;

/**
 * @author chensen
 * @time 2020/2/8  13:37
 * @desc 解析歌词工具类
 */
public class LyricsUtils {

    private List<Lyrics> lyricsList;

    public List<Lyrics> readLyricFile(File file){

        if(file == null || !file.exists()){

            lyricsList = null;

        }else{
            try {

                lyricsList = new ArrayList<>();

                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(file),FileUtil.charset(file)));

                String line = "";
                while ((line = bufferedReader.readLine()) != null){
                    try{
                        parseLyric(line);
                    }catch (Exception e){
                        return lyricsList;
                    }

                }

                bufferedReader.close();

                Collections.sort(lyricsList, new Comparator<Lyrics>() {
                    @Override
                    public int compare(Lyrics o1, Lyrics o2) {
                        if(o1.getTimePoint() > o2.getTimePoint()){
                            return 1;
                        }else if(o1.getTimePoint() <o2.getTimePoint()){
                            return -1;
                        }else {
                            return 0;
                        }
                    }
                });

                //计算高亮事件
                for (int i = 0; i < lyricsList.size() - 1; i++) {
                    long time1 = lyricsList.get(i).getTimePoint();
                    long time2 = lyricsList.get(i+1).getTimePoint();
                    lyricsList.get(i).setSleepTime(time2 - time1);
                }




            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        return lyricsList;

    }

    private void parseLyric(String line) {

        if(getCount(line) != 0){
            int lb = line.indexOf("[");
            int rb = line.indexOf("]");
            if(lb == 0 && rb != -1){
                long timeA[] = new long[getCount(line)];

                String timeS = line.substring(lb+1,rb);
                long time = stringTime2IntTime(timeS);
                timeA[0] = time;

                String content = "";

                int i = 1;
                while (lb == 0 && rb != -1){
                    content = line.substring(rb+1);
                    lb = content.indexOf("[");
                    rb = content.indexOf("]");

                    if(rb != -1){
                        timeS = content.substring(lb+1,rb);
                        time = stringTime2IntTime(timeS);
                        timeA[i] = time;

                        if(time == -1){
                            break;
                        }

                        i++;
                    }else{
                        timeA[i] = -1;
                    }

                }

                for (int j = 0;j <timeA.length;j++){

                    if(timeA[j] != -1){
                        Lyrics lyrics = new Lyrics();
                        lyrics.setTimePoint(timeA[j]);
                        lyrics.setContent(content);
                        lyricsList.add(lyrics);
                    }

                }
            }
        }





    }

    /**
     * 一行有几个时间点
     * @param line
     * @return
     */
    private int getCount(String line) {
        int result = 0;

        String lb[] = line.split("\\[");

        String rb[] = line.split("\\]");

        String content = line.substring(line.indexOf("[")+1,line.indexOf("]"));

        if(stringTime2IntTime(content) != -1){
            if(lb.length == rb.length){
                result = lb.length;
            }else if(lb.length > rb.length){
                result = lb.length;
            }else{
                result = rb.length;
            }
        }

        return result;
    }

    /**
     * 将规定字符串转为long型
     * [02:23.06]会不会稍嫌狼狈
     * @param timeS 02:23.06
     * @return 若字符串格式错误或内容错误返回-1
     */
    private long stringTime2IntTime(String timeS) {
        long result = -1;
        try {
            int index = timeS.indexOf(":");
            int dotIndex = timeS.indexOf(".");

            if(index!=-1 && dotIndex != -1){
                long minute = Long.parseLong(timeS.substring(0,index));

                long second = Long.parseLong(timeS.substring(index+1,dotIndex));

                long millisecond = Long.parseLong(timeS.substring(dotIndex+1)) * 10;

                result =  minute * 60 * 1000 + second * 1000 + millisecond;
            }

        }catch (Exception e){
            e.printStackTrace();
            result = -1;
        }

        return result;
    }

}
