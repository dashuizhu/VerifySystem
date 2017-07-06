package verifysystem.company.com.verifysystem.utils;

import android.os.Environment;
import android.util.Log;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Administrator on 2016/5/13.
 */
public class LogUtils {

    private static final String PARENT_DIR = "VerifySystem";
    private static final String LOG_DIR = "CmdLog";

    private final static String Prefix = "verify_";

    public static void d(String TAG ,String log) {
        Log.d(Prefix+TAG, log);
    }

    public static void i(String TAG ,String log) {
        Log.i(Prefix+TAG, log);
    }

    public static void v(String TAG ,String log) {
        Log.v(Prefix+TAG, log);
    }

    public static void e(String tag, String s) {
        Log.e(Prefix+tag, s);
    }


    public static void e(String tag, String s, IOException e2) {
        Log.v(Prefix+tag, s);
    }

    public static void logD(String TAG, String log) {
        Log.d(Prefix+TAG, log);
        //writeLogToFile(TAG, log);
    }

    private static FileOutputStream fos = null;
    private static String mFileName = null;

    private static SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
    private static SimpleDateFormat timelogFormatter = new SimpleDateFormat("HH:mm:ss");

    public static void writeLogToFile(final String tag,final String msg)
    {
        if(msg == null || msg.equals(""))
        {
            Log.v(tag, "writeLogToFile failed1");
            return;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Log.e(tag, tag + "写入文件" + msg);
                    if(fos != null)
                    {
                        File tmpFile = new File(mFileName);
                        if (!tmpFile.exists()) {
                            closeLogFile();
                            openLogFile();
                        }
                        String time = timelogFormatter.format(new Date())+"   ";
                        StringBuffer sb = new StringBuffer();
                        sb.append(time);
                        sb.append(tag);
                        int len = tag.length();
                        int timelen = time.length();
                        sb.setLength(timelen+32);
                        for(int i=0;i<32-len;i++)
                        {
                            sb.setCharAt(timelen+len+i, ' ');
                        }
                        sb.append(msg);
                        sb.append("\r\n");
                        fos.write(sb.toString().getBytes());

                    }
                    else
                    {
                        openLogFile();
                        writeLogToFile(tag, msg);
                    }
                } catch (IOException e) {
                    Log.v(tag, "writeLogToFile err111");
                    e.printStackTrace();
                }
            }
        }).start();
    }


    private static void openLogFile()
    {
        try {
            //long timestamp = System.currentTimeMillis();
            String time = formatter.format(new Date());
            String fileName = "log_" + time + ".txt";
            File parentFile = null;
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                File file = Environment.getExternalStorageDirectory();
                parentFile = new File(file, PARENT_DIR+"/"+LOG_DIR);
                if(!parentFile.exists())
                {
                    parentFile.mkdirs();
                }
                if(parentFile != null)
                {
                    mFileName = parentFile.getAbsolutePath() +"/"+ fileName;
                    Log.e(Prefix, "写入文件" + mFileName);
                    fos = new FileOutputStream(mFileName,true);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void closeLogFile()
    {
        try {
            if(fos != null)
            {
                fos.flush();
                fos.close();
                fos = null;
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
