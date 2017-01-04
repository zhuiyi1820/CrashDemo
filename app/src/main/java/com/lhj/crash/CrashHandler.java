package com.lhj.crash;


import android.content.Context;
import android.os.Build;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * ┴┬┴┬／￣＼＿／￣＼
 * ┬┴┬┴▏　　▏▔▔▔▔＼ 这
 * ┴┬┴／＼　／　　　　　　﹨ 地
 * ┬┴∕　　　　　　　／　　　） 方
 * ┴┬▏　　　　　　　　●　　▏ 不
 * ┬┴▏　　　　　　　　　　　▔█◤ 错，
 * ┴◢██◣　　　　　　 ＼＿＿／
 * ┬█████◣　　　　　　　／　 让我用PP踩踩！
 * ┴█████████████◣
 * ◢██████████████▆▄
 * █◤◢██◣◥█████████◤＼
 * ◥◢████　████████◤　　 ＼
 * ┴█████　██████◤　　　　　 ﹨
 * ┬│　　　│█████◤　　　　　　　　▏
 * ┴│　　　│ PP熊在此！借贵宝地一踩！ 　▏
 * ┬∕　　　∕　　　　／▔▔▔＼　　　　 ∕
 * *∕＿＿_／﹨　　　∕　　　　　 ＼　　／＼
 * ┴┬┴┬┴┬┴ ＼＿＿＿＼　　　　 ﹨／▔＼﹨／▔＼ ╃天天开心╃
 * ▲△▲▲╓╥╥╥╥╥╥╥╥＼　　 ∕　 ／▔﹨　／▔
 * 　＊＊＊╠╬╬╬╬╬╬╬╬＊﹨　　／　　／／ ╃事事顺心╃整和不错
 * <p>
 * 作者：linhongjie
 * 时间：2016/7/27 14:32
 * 描述：UncaughtException处理类,当程序发生Uncaught异常的时候,有该类来接管程序,并记录发送错误报告.
 */
public class CrashHandler implements UncaughtExceptionHandler {

    public static final String TAG = "CrashHandler";

    //系统默认的UncaughtException处理类
    private UncaughtExceptionHandler mDefaultHandler;
    //CrashHandler实例
    private static CrashHandler INSTANCE = new CrashHandler();
    //程序的Context对象
    private Context mContext;

    //用于格式化日期,作为日志文件名的一部分
    private DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");

    /**
     * 保证只有一个CrashHandler实例
     */
    private CrashHandler() {
    }

    /**
     * 获取CrashHandler实例 ,单例模式
     */
    public static CrashHandler getInstance() {
        return INSTANCE;
    }

    /**
     * 初始化
     *
     * @param context
     */
    public void init(Context context) {
        mContext = context;
        //获取系统默认的UncaughtException处理器
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        //设置该CrashHandler为程序的默认处理器
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    /**
     * 当UncaughtException发生时会转入该函数来处理
     */
    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        if (!handleException(ex) && mDefaultHandler != null) {
            //如果用户没有处理则让系统默认的异常处理器来处理
            mDefaultHandler.uncaughtException(thread, ex);
        } else {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                Log.e(TAG, "error : ", e);
            }
            //退出程序
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(1);
        }
    }

    /**
     * 自定义错误处理,收集错误信息 发送错误报告等操作均在此完成.
     *
     * @param ex
     * @return true:如果处理了该异常信息;否则返回false.
     */
    private boolean handleException(final Throwable ex) {
        if (ex == null) {
            return false;
        }
        final String strhh = saveCrashInfo2File(ex);
        Log.e(TAG, strhh);
        //使用Toast来显示异常信息
        new Thread() {
            @Override
            public void run() {
                Looper.prepare();
                Toast.makeText(mContext, strhh, Toast.LENGTH_LONG).show();
//                Toast.makeText(mContext, "很抱歉,程序出现异常,即将退出.", Toast.LENGTH_LONG).show();
                Looper.loop();
            }
        }.start();
        //收集设备参数信息,保存日志文件
        writeFileSdcardFile(FileUtils.SDPATH, "Crash_" + System.currentTimeMillis() + ".txt", saveCrashInfo2File(ex), ex.getMessage());
        return true;
    }

    /**
     * 收集设备信息与错误日志
     *
     * @param e
     */
    public String saveCrashInfo2File(Throwable e) {
        StringBuilder sb = new StringBuilder();
        sb.append("生产厂商：\n");
        sb.append(Build.MANUFACTURER).append("\n\n");
        sb.append("手机型号：\n");
        sb.append(Build.MODEL).append("\n\n");
        sb.append("系统版本：\n");
        sb.append(Build.VERSION.RELEASE).append("\n\n");
        sb.append("异常时间：\n");
        sb.append(formatter.format(new Date())).append("\n\n");
        sb.append("异常类型：\n");
        sb.append(e.getClass().getName()).append("\n\n");
        sb.append("异常信息：\n");
        sb.append(e.getMessage()).append("\n\n");
        sb.append("异常堆栈：\n");
        Writer writer = new StringWriter();
        PrintWriter printWriter = new PrintWriter(writer);
        e.printStackTrace(printWriter);
        Throwable cause = e.getCause();
        while (cause != null) {
            cause.printStackTrace(printWriter);
            cause = cause.getCause();
        }
        printWriter.close();
        String result = writer.toString();
        sb.append(result);
        return sb.toString();
    }

    /**
     * 保存错误信息到文件中
     *
     * @param path
     * @param fileName  文件名
     * @param write_str 错误日志
     * @param ex        错误信息
     */
    public void writeFileSdcardFile(String path, String fileName, String write_str, String ex) {
        if (!FileUtils.file.exists()) {
            FileUtils.CreateDir();
        }
        try {
            FileOutputStream fout = new FileOutputStream(path + fileName);
            byte[] bytes = write_str.getBytes();
            fout.write(bytes);
            fout.close();
            Log.e(TAG, "保存成功" + path + fileName);
            //此地做上传错误日志代码
//            uploadLogFile(new File(path + fileName), ex);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "保存失败");
        }
    }

}
