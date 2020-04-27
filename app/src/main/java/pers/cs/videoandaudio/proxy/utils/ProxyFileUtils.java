package pers.cs.videoandaudio.proxy.utils;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.URI;

import pers.cs.videoandaudio.MyApplication;
import pers.cs.videoandaudio.proxy.db.CacheFileInfoDao;

/**
 * @author chensen
 *
 * @time 2020/4/20  21:56
 *
 * @desc 缓存文件读写，及预缓存
 *
 */


public class ProxyFileUtils {

    private static final String TAG = ProxyFileUtils.class.getSimpleName();

    private static ProxyFileUtils fileUtilsByMediaPlayer;
    private static ProxyFileUtils fileUtilsByPreLoader;

    private boolean isEnable;

    private FileOutputStream outputStream;
    private RandomAccessFile randomAccessFile;
    private File file;

    public static ProxyFileUtils getInstance(Context context, URI uri, boolean isUseByMediaPlayer) {
        // 如果文件名有错误，返回空Utils
        String name = getValidFileName(uri);
        if (name == null || name.length() <= 0) {
            ProxyFileUtils utils = new ProxyFileUtils(context, null);
            //上述构造中已设置
            utils.setEnableFalse();
            return utils;
        }
        // 如果是预加载，如果MediaPlayer正在使用文件，则不预加载，返回空Utils
        if (!isUseByMediaPlayer) {
            if (fileUtilsByMediaPlayer != null && fileUtilsByMediaPlayer.getFileName().equals(name)) {
                ProxyFileUtils utils = new ProxyFileUtils(context, null);
                utils.setEnableFalse();
                return utils;
            }
        }
        // 创建Utils，关闭之前Util。如果是MediaPlayer使用文件Preloader也在使用，则关闭Preloader的Utils
        if (isUseByMediaPlayer) {
            //此时isUseByMediaPlayer为true，之前使用false过一次
            if (fileUtilsByPreLoader != null && fileUtilsByPreLoader.getFileName().equals(name)) {
                //预加载的和此次加载的一样
                close(fileUtilsByPreLoader, false);
            }
            close(fileUtilsByMediaPlayer, true);
            fileUtilsByMediaPlayer = new ProxyFileUtils(context, name);
            return fileUtilsByMediaPlayer;
        } else {
            close(fileUtilsByPreLoader, false);
            fileUtilsByPreLoader = new ProxyFileUtils(context, name);
            return fileUtilsByPreLoader;
        }
    }



    /**
     * 判断存储是否可用
     * 删除多余缓存文件
     *
     * @return
     */
    private boolean isSdAvaliable() {
        // 判断外部存储器是否可用
        File dir = new File(Constants.DOWNLOAD_PATH);
        if (!dir.exists()) {
            dir.mkdirs();
            if (!dir.exists()) {
                return false;
            }
        }
        // 删除部分缓存文件
        ProxyUtils.asynRemoveBufferFile(Constants.CACHE_FILE_NUMBER);
        // 可用空间大小是否大于SD卡预留最小值
        long freeSize = ProxyUtils.getAvailaleSize(Constants.DOWNLOAD_PATH);
        if(MyApplication.DEBUG){
            Log.d(TAG,"isSdAvaliable:"+freeSize);
        }
        if (freeSize > Constants.SD_REMAIN_SIZE) {
            return true;
        } else {

            return false;
        }
    }

    private ProxyFileUtils(Context context, String name) {
        if (name == null || name.length() <= 0) {
            isEnable = false;
            return;
        }

        if (!isSdAvaliable()) {
            isEnable = false;
            return;
        }
        //name不为空，sd卡可用
        try {
            //Log.e("path","/");
//            file = new File(context.getCacheDir().getPath() + name);
            //注：此处通过删除缓存删不掉
            file = new File(Constants.DOWNLOAD_PATH + name);
            if (!file.exists()) {
                File dir = new File(Constants.DOWNLOAD_PATH);
                dir.mkdirs();
                file.createNewFile();
            }
            randomAccessFile = new RandomAccessFile(file, "r");
            outputStream = new FileOutputStream(file, true);
            isEnable = true;
        } catch (IOException e) {
            isEnable = false;
            Log.e(TAG, "文件操作失败，无SD？", e);
        }
    }

    public String getFileName() {
        return file.getName();
    }

    public boolean isEnable() {
        return isEnable;
    }

    public void setEnableFalse() {
        isEnable = false;
    }

    /**
     *
     * @return 文件内容长度
     */
    public int getLength() {
        if (isEnable) {
            return (int) file.length();
        } else {
            return -1;
        }
    }

    /**
     * 删除文件
     *
     * @return
     */
    public boolean delete() {
        return file.delete();
    }

    public byte[] read(int startPos) {
        if (isEnable) {
            int byteCount = (int) (getLength() - startPos);
            byte[] tmp = new byte[byteCount];
            try {
                randomAccessFile.seek(startPos);
                randomAccessFile.read(tmp);
                return tmp;
            } catch (IOException e) {
                Log.e(TAG, "缓存读取失败", e);
                return null;
            }
        } else {
            return null;
        }
    }

    public byte[] read(int startPos, int length) {
        if (isEnable) {
            int byteCount = (int) (getLength() - startPos);
            if (byteCount > length) {
                byteCount = length;
            }
            byte[] tmp = new byte[byteCount];
            try {
                randomAccessFile.seek(startPos);
                randomAccessFile.read(tmp);
                return tmp;
            } catch (IOException e) {
                Log.e(TAG, "缓存读取失败", e);
                return null;
            }
        } else {
            return null;
        }
    }

    public void write(byte[] buffer, int byteCount) {
        if (isEnable) {
            try {
                outputStream.write(buffer, 0, byteCount);
                outputStream.flush();
            } catch (IOException e) {
                e.printStackTrace();
                Log.e(TAG, "缓存写入失败", e);
            }
        }
    }

    /**
     * 将ProxyFileUtils设置不可用，并置空
     *
     * @param fileUtils
     * @param isUseByMediaPlayer
     */
    public static void close(ProxyFileUtils fileUtils, boolean isUseByMediaPlayer) {
        if (isUseByMediaPlayer) {
            if (fileUtilsByMediaPlayer != null && fileUtilsByMediaPlayer == fileUtils) {
                fileUtilsByMediaPlayer.setEnableFalse();
                fileUtilsByMediaPlayer = null;
            }
        } else {
            if (fileUtilsByPreLoader != null && fileUtilsByPreLoader == fileUtils) {
                fileUtilsByPreLoader.setEnableFalse();
                fileUtilsByPreLoader = null;
            }
        }
    }

    /**
     * 删除缓存文件，以及缓存数据库中此文件信息
     *
     * @param url
     */
    public static void deleteFile(String url) {
        URI uri = URI.create(url);
        String name = getValidFileName(uri);
        boolean isSuccess = new File(name).delete();
        if (isSuccess) {
            CacheFileInfoDao.getInstance().delete(name);
        }
    }

    /**
     * 是否完成缓存
     *
     * @param url
     * @return
     */
    public static boolean isFinishCache(String url) {
        URI uri = URI.create(url);
        String name = getValidFileName(uri);
        File f = new File(name);

        if (!f.exists()) {
            return false;
        }
        if (f.length() != CacheFileInfoDao.getInstance().getFileSize(name)) {
            return false;
        }
        return true;
    }

    /**
     * 获取有效的文件名
     *
     * @param uri
     * @return
     */
    static protected String getValidFileName(URI uri) {
        //不对转移后的字符解码，如%E2
        String path = uri.getRawPath();
        String name = path.substring(path.lastIndexOf("/"));
        name = name.replace("\\", "");
        name = name.replace("/", "");
        name = name.replace(":", "");
        name = name.replace("*", "");
        name = name.replace("?", "");
        name = name.replace("\"", "");
        name = name.replace("<", "");
        name = name.replace(">", "");
        name = name.replace("|", "");
        name = name.replace(" ", "_"); // 前面的替换会产生空格,最后将其一并替换掉
        return name;
    }
}
