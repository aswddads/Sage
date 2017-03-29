package tj.com.safe_project.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by Jun on 17/3/29.
 */

public class MD5Utils {
    public static String encoder(String psd){
        try {
//            密码加盐
            psd=psd+"mobilesafe";
//            单例模式   指定加密算法类型
            MessageDigest digest=MessageDigest.getInstance("MD5");
//            将需要加密的字符串转换为byte类型数组，然后进行随机哈西过程
            byte[] bs=digest.digest(psd.getBytes());
//            循环遍历bs，生成32位字符串  拼接字符串过程
            StringBuffer stringBuffer=new StringBuffer();
            for (byte b:bs) {
                int i=b & 0xff;
//                int类型转换为16进制字符
                String hexstring=Integer.toHexString(i);
                if(hexstring.length()<2){
                    hexstring='0'+hexstring;
                }
                stringBuffer.append(hexstring);
            }
            return stringBuffer.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }
}
