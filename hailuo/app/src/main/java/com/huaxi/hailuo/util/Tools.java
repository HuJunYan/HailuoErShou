package com.huaxi.hailuo.util;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.text.TextUtils;
import android.text.format.Formatter;
import android.util.Log;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;

import static android.text.format.Formatter.formatFileSize;

/**
 * Created by hjy on 2018/1/25.
 * 获取手机内存大小封装工具类
 */

public class Tools {

    public static final String COMMAND_SU = "su";
    public static final String COMMAND_SH = "sh";
    public static final String COMMAND_EXIT = "exit\n";
    public static final String COMMAND_LINE_END = "\n";

    private static final String MEM_INFO_PATH = "/proc/meminfo";
    public static final String MEMTOTAL = "MemTotal";
    public static final String MEMFREE = "MemFree";

    /**
     * 得到中内存大小
     *
     * @param context
     * @return
     */
    public static String getTotalMemory(Context context) {
        return getMemInfoIype(context, MEMTOTAL);
    }

    /**
     * 得到可用内存大小
     *
     * @param context
     * @param memfree
     * @return
     */
    public static String getMemoryFree(Context context, String memfree) {
        return getMemInfoIype(context, MEMFREE);
    }

    /**
     * 得到type info
     *
     * @param context
     * @param type
     * @return
     */
    public static String getMemInfoIype(Context context, String type) {
        try {
            FileReader fileReader = new FileReader(MEM_INFO_PATH);
            BufferedReader bufferedReader = new BufferedReader(fileReader, 4 * 1024);
            String str = null;
            while ((str = bufferedReader.readLine()) != null) {
                if (str.contains(type)) {
                    break;
                }
            }
            bufferedReader.close();
            /* \\s表示   空格,回车,换行等空白符,
            +号表示一个或多个的意思     */
            String[] array = str.split("\\s+");
            // 获得系统总内存，单位是KB，乘以1024转换为Byte
            int length = Integer.valueOf(array[1]).intValue() * 1024;
            return formatFileSize(context, length);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 得到内置存储空间的总容量，可能为null,
     * 最大256G,最小1G
     *
     * @param context
     * @return 手机内存
     */
    public static String getInternalToatalSpace(Context context) {
        long G1 = (long) (1 * Math.pow(1024, 3));
        long G2 = (long) (2 * Math.pow(1024, 3));
        long G4 = (long) (4 * Math.pow(1024, 3));
        long G8 = (long) (8 * Math.pow(1024, 3));
        long G16 = (long) (16 * Math.pow(1024, 3));
        long G32 = (long) (32 * Math.pow(1024, 3));
        long G64 = (long) (64 * Math.pow(1024, 3));
        long G128 = (long) (128 * Math.pow(1024, 3));
        long G256 = (long) (256 * Math.pow(1024, 3));
        try {
            String path = Environment.getDataDirectory().getPath();
            Log.d("memory", "root path is " + path);
            if (TextUtils.isEmpty(path)) {
                return null;
            }
            StatFs statFs = new StatFs(path);
            long blockSize = statFs.getBlockSize();
            long totalBlocks = statFs.getBlockCount();
            long rom_length = totalBlocks * blockSize;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                if (rom_length > 1024) {
                    if (Long.compare(rom_length, G2) != 1) {
                        rom_length = G2;
                    } else if (Long.compare(rom_length, G4) != 1) {
                        rom_length = G4;
                    } else if (Long.compare(rom_length, G8) != 1) {
                        rom_length = G8;
                    } else if (Long.compare(rom_length, G16) != 1) {
                        rom_length = G16;
                    } else if (Long.compare(rom_length, G32) != 1) {
                        rom_length = G32;
                    } else if (Long.compare(rom_length, G64) != 1) {
                        rom_length = G64;
                    } else if (Long.compare(rom_length, G128) != 1) {
                        rom_length = G128;
                    } else if (Long.compare(rom_length, G256) != 1) {
                        rom_length = G256;
                    } else {
                        rom_length = G256;
                    }
                } else {
                    rom_length = G1;
                }
            }
            String totalSize = Formatter.formatFileSize(context, rom_length);
            return totalSize.replace(".00", "").replace(" ", "").replace("B","");
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 获取手机名称
     *
     * @return 手机型号
     */
    public static String getPhoneName() {
        return Build.MODEL;
    }


    /**
     * 是否是在root下执行命令
     *
     * @param command         命令
     * @param isRoot          是否root
     * @param isNeedResultMsg 是否需要结果消息
     * @return CommandResult
     */
    public static CommandResult execCmd(String command, boolean isRoot, boolean isNeedResultMsg) {
        return execCmd(new String[]{command}, isRoot, isNeedResultMsg);
    }

    /**
     * 判断设备是否root
     *
     * @return {@code true}: root<br>{@code false}: 没root
     */
    public static boolean isRoot() {
        return execCmd("echo root", true, false).result == 0;
    }

    /**
     * 是否是在root下执行命令
     *
     * @param command 命令
     * @param isRoot  是否root
     * @return CommandResult
     */
    public static CommandResult execCmd(String command, boolean isRoot) {
        return execCmd(new String[]{command}, isRoot, true);
    }

    /**
     * 是否是在root下执行命令
     *
     * @param commands        命令数组
     * @param isRoot          是否root
     * @param isNeedResultMsg 是否需要结果消息
     * @return CommandResult
     */
    public static CommandResult execCmd(String[] commands, boolean isRoot, boolean isNeedResultMsg) {
        int result = -1;
        if (commands == null || commands.length == 0) {
            return new CommandResult(result, null, null);
        }
        Process process = null;
        BufferedReader successResult = null;
        BufferedReader errorResult = null;
        StringBuilder successMsg = null;
        StringBuilder errorMsg = null;
        DataOutputStream os = null;
        try {
            process = Runtime.getRuntime().exec(isRoot ? COMMAND_SU : COMMAND_SH);
            os = new DataOutputStream(process.getOutputStream());
            for (String command : commands) {
                if (command == null) {
                    continue;
                }
                os.write(command.getBytes());
                os.writeBytes(COMMAND_LINE_END);
                os.flush();
            }
            os.writeBytes(COMMAND_EXIT);
            os.flush();

            result = process.waitFor();
            if (isNeedResultMsg) {
                successMsg = new StringBuilder();
                errorMsg = new StringBuilder();
                successResult = new BufferedReader(new InputStreamReader(process.getInputStream()));
                errorResult = new BufferedReader(new InputStreamReader(process.getErrorStream()));
                String s;
                while ((s = successResult.readLine()) != null) {
                    successMsg.append(s);
                }
                while ((s = errorResult.readLine()) != null) {
                    errorMsg.append(s);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (os != null) {
                    os.close();
                }
                if (successResult != null) {
                    successResult.close();
                }
                if (errorResult != null) {
                    errorResult.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (process != null) {
                process.destroy();
            }
        }
        return new CommandResult(result, successMsg == null ? null : successMsg.toString(), errorMsg == null ? null
                : errorMsg.toString());
    }

    /**
     * 返回的命令结果
     */
    public static class CommandResult {

        /**
         * 结果码
         **/
        public int result;
        /**
         * 成功的信息
         **/
        public String successMsg;
        /**
         * 错误信息
         **/
        public String errorMsg;

        public CommandResult(int result) {
            this.result = result;
        }

        public CommandResult(int result, String successMsg, String errorMsg) {
            this.result = result;
            this.successMsg = successMsg;
            this.errorMsg = errorMsg;
        }
    }

    /**
     * 得到内置存储空间的总容量，可能为null,
     * 最大256G,最小1G
     *
     * @param context
     * @return 手机内存
     */
    public static String getStringToatalSpace(Context context) {
        long G1 = (long) (1 * Math.pow(1024, 3));
        long G2 = (long) (2 * Math.pow(1024, 3));
        long G4 = (long) (4 * Math.pow(1024, 3));
        long G8 = (long) (8 * Math.pow(1024, 3));
        long G16 = (long) (16 * Math.pow(1024, 3));
        long G32 = (long) (32 * Math.pow(1024, 3));
        long G64 = (long) (64 * Math.pow(1024, 3));
        long G128 = (long) (128 * Math.pow(1024, 3));
        long G256 = (long) (256 * Math.pow(1024, 3));
        String memory=  "2G";
        try {
            String path = Environment.getDataDirectory().getPath();
            Log.d("memory", "root path is " + path);
            if (TextUtils.isEmpty(path)) {
                return null;
            }
            StatFs statFs = new StatFs(path);
            long blockSize = statFs.getBlockSize();
            long totalBlocks = statFs.getBlockCount();
            long rom_length = totalBlocks * blockSize;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                if (rom_length > 1024) {
                    if (Long.compare(rom_length, G2) != 1) {
                        memory = "2G";
                    } else if (Long.compare(rom_length, G4) != 1) {
                        memory = "4G";
                    } else if (Long.compare(rom_length, G8) != 1) {
                        memory = "8G";
                    } else if (Long.compare(rom_length, G16) != 1) {
                        memory = "16G";
                    } else if (Long.compare(rom_length, G32) != 1) {
                        memory = "32G";
                    } else if (Long.compare(rom_length, G64) != 1) {
                        memory = "64G";
                    } else if (Long.compare(rom_length, G128) != 1) {
                        memory = "128G";
                    } else if (Long.compare(rom_length, G256) != 1) {
                        memory = "256G";
                    } else {
                        memory = "256G";
                    }
                } else {
                    rom_length = G1;
                }
            }

            return memory;
        } catch (Exception e) {
            return memory;
        }
    }
    public static void getAppDetailSettingIntent(Context context) {
        Intent localIntent = new Intent();
        localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (Build.VERSION.SDK_INT >= 9) {
            localIntent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
            localIntent.setData(Uri.fromParts("package", context.getPackageName(), null));
        } else if (Build.VERSION.SDK_INT <= 8) {
            localIntent.setAction(Intent.ACTION_VIEW);
            localIntent.setClassName("com.android.settings","com.android.settings.InstalledAppDetails");
            localIntent.putExtra("com.android.settings.ApplicationPkgName", context.getPackageName());
        }
        context.startActivity(localIntent);
    }

    //跳转到自己应用的设置界面
    public static void toSelfSetting(Context context) {
        Intent mIntent = new Intent();
        mIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (Build.VERSION.SDK_INT >= 9) {
            mIntent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
            mIntent.setData(Uri.fromParts("package", context.getPackageName(), null));
        } else if (Build.VERSION.SDK_INT <= 8) {
            mIntent.setAction(Intent.ACTION_VIEW);
            mIntent.setClassName("com.android.settings", "com.android.setting.InstalledAppDetails");
            mIntent.putExtra("com.android.settings.ApplicationPkgName", context.getPackageName());
        }
        context.startActivity(mIntent);
    }

    public static byte[] bmpToByteArray(final Bitmap bmp, final boolean needRecycle) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, output);
        if (needRecycle) {
            bmp.recycle();
        }

        byte[] result = output.toByteArray();
        try {
            output.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}
