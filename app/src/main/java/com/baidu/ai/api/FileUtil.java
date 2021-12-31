package com.baidu.ai.api;

import java.io.*;

/**
 * 文件读取工具类
 */
public class FileUtil {

    /**
     * 读取文件内容，作为字符串返回
     */
    public static String readFileAsString(String filePath) throws IOException {
        File file = new File(filePath);
        if (!file.exists()) {
            throw new FileNotFoundException(filePath);
        } 

        if (file.length() > 1024 * 1024 * 1024) {
            throw new IOException("File is too large");
        } 

        StringBuilder sb = new StringBuilder((int) (file.length()));
        // 创建字节输入流  
        FileInputStream fis = new FileInputStream(filePath);  
        // 创建一个长度为10240的Buffer
        byte[] bbuf = new byte[10240];  
        // 用于保存实际读取的字节数  
        int hasRead = 0;  
        while ( (hasRead = fis.read(bbuf)) > 0 ) {  
            sb.append(new String(bbuf, 0, hasRead));  
        }  
        fis.close();  
        return sb.toString();
    }

    /**
     * 根据文件路径读取byte[] 数组
     */
    public static byte[] readFileByBytes(String filePath) throws IOException {
        File file = new File(filePath);
        if (!file.exists()) {
            throw new FileNotFoundException(filePath);
        } else {
            ByteArrayOutputStream bos = new ByteArrayOutputStream((int) file.length());
            BufferedInputStream in = null;

            try {
                in = new BufferedInputStream(new FileInputStream(file));
                short bufSize = 1024;
                byte[] buffer = new byte[bufSize];
                int len1;
                while (-1 != (len1 = in.read(buffer, 0, bufSize))) {
                    bos.write(buffer, 0, len1);
                }

                byte[] var7 = bos.toByteArray();
                return var7;
            } finally {
                try {
                    if (in != null) {
                        in.close();
                    }
                } catch (IOException var14) {
                    var14.printStackTrace();
                }
                bos.close();
            }
        }
    }
    public static String myspilt(){
        String[] name=new String[]{"left_parts","top_head","right_mouth_corner","neck","left_shoulder","left_knee","left_ankle",
                "left_mouth_corner","right_elbow","right_ear","nose","left_eye","right_eye","right_hip","left_wrist","left_ear",
                "left_elbow","right_shoulder","right_ankle","right_knee","right_wrist"};
        System.out.println(name.length);

        String str="result:{\"person_num\": 1, \"person_info\": [{\"body_parts\": {\"left_hip\": {\"y\": 527.44140625, \"x\": 355.04296875, \"score\": 0.651006281375885}, \"top_head\": {\"y\": 25.13674163818359, \"x\": 263.71484375, \"score\": 0.8308587074279785}, \"right_mouth_corner\": {\"y\": 162.1289215087891, \"x\": 224.57421875, \"score\": 0.8779381513595581}, \"neck\": {\"y\": 188.2226715087891, \"x\": 276.76171875, \"score\": 0.7961496710777283}, \"left_shoulder\": {\"y\": 214.3164215087891, \"x\": 355.04296875, \"score\": 0.8395131230354309}, \"left_knee\": {\"y\": 631.81640625, \"x\": 165.8632965087891, \"score\": 0.7670961618423462}, \"left_ankle\": {\"y\": 618.76953125, \"x\": 361.56640625, \"score\": 0.4249553680419922}, \"left_mouth_corner\": {\"y\": 155.6054840087891, \"x\": 257.19140625, \"score\": 0.883640706539154}, \"right_elbow\": {\"y\": 377.40234375, \"x\": 211.52734375, \"score\": 0.8536253571510315}, \"right_ear\": {\"y\": 109.9414215087891, \"x\": 205.00390625, \"score\": 0.7496447563171387}, \"nose\": {\"y\": 136.0351715087891, \"x\": 231.09765625, \"score\": 0.8832266926765442}, \"left_eye\": {\"y\": 103.4179840087891, \"x\": 263.71484375, \"score\": 0.8677999973297119}, \"right_eye\": {\"y\": 109.9414215087891, \"x\": 224.57421875, \"score\": 0.8945008516311646}, \"right_hip\": {\"y\": 507.87109375, \"x\": 270.23828125, \"score\": 0.6680450439453125}, \"left_wrist\": {\"y\": 579.62890625, \"x\": 361.56640625, \"score\": 0.8698246479034424}, \"left_ear\": {\"y\": 116.4648590087891, \"x\": 315.90234375, \"score\": 0.8849414587020874}, \"left_elbow\": {\"y\": 403.49609375, \"x\": 335.47265625, \"score\": 0.8294979929924011}, \"right_shoulder\": {\"y\": 214.3164215087891, \"x\": 205.00390625, \"score\": 0.793376088142395}, \"right_ankle\": {\"y\": 592.67578125, \"x\": 315.90234375, \"score\": 0.2949504852294922}, \"right_knee\": {\"y\": 592.67578125, \"x\": 87.58204650878906, \"score\": 0.837354302406311}, \"right_wrist\": {\"y\": 520.91796875, \"x\": 185.43359375, \"score\": 0.7566587924957275}}, \"location\": {\"height\": 667.8069458007812, \"width\": 450.2604370117188, \"top\": 1.19305419921875, \"score\": 0.9987338185310364, \"left\": 48.73957824707031}}], \"log_id\": 1823195431547637945}\n";
        String[] array=str.replaceAll("\\s+","")
                .split("\\[");
        System.out.println(array[1]);
        String[] array1=array[1].split("[\\{\\}]");
        Parts[] parts=new Parts[21];
        int j=0;
        for(int i=2;i<44;i=i+2){
            String[] Pointxy=array1[i+1].split("((,?\\\"[a-z]+\\\"\\:))");
            System.out.println(name[j]);
            parts[j]=new Parts();
            parts[j].name=name[j];
            parts[j].x=Double.parseDouble(Pointxy[1]);
            parts[j].y=Double.parseDouble(Pointxy[2]);
            parts[j].socre=Double.parseDouble(Pointxy[3]);
            //System.out.println(parts[j].socre);
            // System.out.println(array1[i]);
            //System.out.println(array1[i+1]);
            j++;
        }

        return "";
    }
}
