/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Common;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 *
 * @author nico
 */
public class Utils {
    
    public static String byteArrayToString(byte[] array){
        StringBuilder stringB = new StringBuilder();
        for (byte b : array) {
                stringB.append(String.format("%02x", b));
        }

        return stringB.toString();
    }
    
  
     public static String applySHA1(String password) throws NoSuchAlgorithmException, UnsupportedEncodingException{
        byte[] passUTF8 = password.getBytes("UTF-8");
        //Se le aplica SHA1
        MessageDigest messageD = MessageDigest.getInstance("SHA-1");
        messageD.update(passUTF8);
        byte[] bytePassSHA1 = messageD.digest();
        String passSHA1 = Utils.byteArrayToString(bytePassSHA1);
        return passSHA1.toUpperCase();
    }
}
