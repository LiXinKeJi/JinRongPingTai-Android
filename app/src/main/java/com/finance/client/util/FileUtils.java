package com.finance.client.util;

import android.content.res.AssetManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class FileUtils {
    /**
     * 从Assert中读取一个文件并且转换为一个字符串
     *
     * @param manager
     * @return
     */
    public static final String readFromAssert(AssetManager manager,
                                              String filepath) {
        String string = null;
        InputStream in = null;
        try {
            in = manager.open(filepath);
            byte[] bytes = new byte[in.available()];
            in.read(bytes);
            string = new String(bytes);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return string;
    }

    /**
     * 从Assert中读取一个文件，并将其转换为一个byte[]数组
     *
     * @param manager
     * @param filepath
     * @return
     */
    public static final byte[] readFromAssertToByte(AssetManager manager,
                                                    String filepath) {
        InputStream in = null;
        byte[] bytes = null;
        try {
            in = manager.open(filepath);
            bytes = new byte[in.available()];
            in.read(bytes);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return bytes;
    }

    public static final byte[] readFromSdcardToByte(String filepath) {
        InputStream in = null;
        byte[] bytes = null;
        File file = new File(filepath);
        if (!file.exists()) {
            return null;
        }
        try {
            in = new FileInputStream(file);
            bytes = new byte[in.available()];
            in.read(bytes);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return bytes;
    }

    /**
     * 写出文件
     *
     * @param dir_path
     * @param file_path
     * @param to_write
     * @return
     */
    public static final boolean writeToSdcard(String dir_path,
                                              String file_path, byte[] to_write) {
        try {
            File filePath = new File(dir_path);
            if (!filePath.exists()) {
                filePath.mkdirs();
            }

            File outFile = new File(file_path);
            if (outFile.exists()) {
                outFile.delete();
            }

            if (!outFile.exists()) {
                outFile.createNewFile();
            }

            FileOutputStream out = new FileOutputStream(outFile);
            out.write(to_write);
            out.flush();
            out.close();
        } catch (Exception e) {
        }
        if (new File(file_path).exists()) {
            return true;
        }
        return false;
    }

    /**
     * 从Assert中读取一个文件并且转换为一个字符串
     *
     * @param manager
     * @return
     */
    public static final String readFromSdcard(String filepath) {
        InputStream in = null;
        String string = null;
        File file = new File(filepath);
        if (!file.exists()) {
            return string;
        }
        try {
            in = new FileInputStream(file);
            byte[] bytes = new byte[in.available()];
            in.read(bytes);
            string = new String(bytes);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return string;
    }
}
