package com.CommonUtils;

import android.content.Context;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Comparator;

public class FileUtils {


    /**
     * 以行为单位读取文件内容，一次读一整行，常用于读面向行的格式化文件
     *
     * @param filePath
     *            文件路径
     * @param encoding
     *            写文件编码
     */
    public static String readFileByLines(String filePath, String encoding)
    throws IOException {
        BufferedReader reader = null;
        StringBuffer sb = new StringBuffer();
        try {
            reader = new BufferedReader(new InputStreamReader(
                                            new FileInputStream(filePath), encoding));
            String tempString = null;
            int i = 1;
            while ((tempString = reader.readLine()) != null) {
                if (i == 1) {
                    sb.append(tempString);
                    i++;
                } else {
                    sb.append("\n");
                    sb.append(tempString);
                }
            }

            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                reader.close();
            }
        }

        return sb.toString();
    }





    /**
     * 指定编码保存内容
     *
     * @param filePath
     *            文件路径
     * @param content
     *            保存的内容
     * @param encoding
     *            写文件编码
     * @throws IOException
     */
    public static void saveToFile(String filePath, String content,
                                  String encoding) throws IOException {
        BufferedWriter writer = null;
        File file = new File(filePath);
        try {
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            if (!file.exists()) {
                file.createNewFile();
            }
            writer = new BufferedWriter(new OutputStreamWriter(
                                            new FileOutputStream(file, false), encoding));
            writer.write(content);

        } finally {
            if (writer != null) {
                writer.close();
            }
        }
    }


    //删除文件夹
    public static boolean deleteFolder(String folderPath) {
        deleteAllFile(folderPath); //删除里面所有文件（夹）
        File file = new File(folderPath);
        return file.delete();//删除空文件夹，同时返回布尔值
    }


    //删除指定文件夹下的所有文件
    public static boolean deleteAllFile(String path) {
        boolean flag = false;
        File file = new File(path);
        if (!file.exists()) {
            return flag;
        }
        if (!file.isDirectory()) {
            return flag;
        }
        String[] tempList = file.list();
        File temp = null;
        for (int i = 0; i < tempList.length; i++) {
            if (path.endsWith(File.separator)) {
                temp = new File(path + tempList[i]);
            } else {
                temp = new File(path + File.separator + tempList[i]);
            }
            if (temp.isFile()) {
                temp.delete();
            }
            if (temp.isDirectory()) {
                deleteAllFile(path + "/" + tempList[i]);//先删除文件夹里面的文件
                deleteFolder(path + "/" + tempList[i]);//再删除空文件夹
                flag = true;
            }
        }
        return flag;
    }



    /**
     * 文件排序  越新越靠前
     *
     * @param filePath 文件路径
     */
    public static File[] SortByDate(String filePath) {
        File file = new File(filePath);
        File[] files = file.listFiles();
        // 排序
        Arrays.sort(files, new Comparator<File>() {
                @Override
                public int compare(File f1, File f2) {
                    long diff = f1.lastModified() - f2.lastModified();
                    if (diff > 0) {
                        return -1;
                    } else if (diff == 0) {
                        return 0;
                    } else {
                        return 1;//如果 if 中修改为 返回-1 同时此处修改为返回 1  排序就会是递减
                    }
                }
            });
        return files;
    }



    /**
     * 读取assets下的txt文件，返回utf-8 String
     * @param context
     * @param fileName 不包括后缀
     * @return
     */
    public static String readAssetsTxt(Context context, String fileName, String encoding) {
        try {
            InputStream is = context.getAssets().open(fileName);
            int size = is.available();

            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();

            String text = new String(buffer, "utf-8");
            return text;
        } catch (IOException e) { }
        return "";
    }



    //获取文件后缀名
    public static String getSuffix(String filePath) {
        String fileName=new File(filePath).getName();    
        String fileSuffix=fileName.substring(fileName.lastIndexOf(".") + 1, fileName.length()); 
        if (fileSuffix == null) {
            fileSuffix = "";
        }
        return fileSuffix;  
    }


    //获取文件最后修改时间
    public static String getLastModTime(String filePath) {
        String result = "";
        File file = new File(filePath);

        long time=file.lastModified(); //文件最后一次修改时间
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  
        result = formatter.format(time);

        return result;
    }



    /**
     * 文件名是否有效
     * 自创
     *
     * @fileName 文件名
     * @fileFolder 文件夹名，判断是否有重名文件
     **/
    public static boolean isValid(String fileName, String fileFolder) {
        if (fileName.equals("") || new File(fileFolder + File.separator + fileName).exists()) {
            return false;
        }
        return fileName.matches("[^/\\\\<>*?|\"]+");
    }



    /**
     * 获取单个文件的MD5值！

     * @param file
     * @return
     */
    public static String getFileMD5(String path) {
        File file = new File(path);
        if (!file.isFile()) {
            return null;
        }
        MessageDigest digest = null;
        FileInputStream in = null;
        byte buffer[] = new byte[1024];
        int len;
        try {
            digest = MessageDigest.getInstance("MD5");
            in = new FileInputStream(file);
            while ((len = in.read(buffer, 0, 1024)) != -1) {
                digest.update(buffer, 0, len);
            }
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        BigInteger bigInt = new BigInteger(1, digest.digest());
        return bigInt.toString(16);
    }




}
