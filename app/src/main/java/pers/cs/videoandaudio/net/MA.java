package pers.cs.videoandaudio.net;

/**
 * @author chensen
 * @time 2020/4/2  22:00
 * @desc Music Address
 */
public class MA {

    public static final String FORMATE = "json";
    public static final String BASE = "http://tingapi.ting.baidu.com/v1/restserver/ting?from=android&version=5.6.5.6&format=" + FORMATE;

    /**
     * 搜索
     */
    public static class Search{
        /**
         * 热门关键字
         * @return
         */
        public static String hotWord() {
            StringBuffer sb = new StringBuffer(BASE);
            sb.append("&method=").append("baidu.ting.search.hot");
            return sb.toString();
        }
    }

}
