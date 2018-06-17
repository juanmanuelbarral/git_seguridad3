/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Logic;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class Crypto {
    
    public static SecureRandom sr = new SecureRandom();

    public static SecretKeySpec crearLlave(String frase) throws UnsupportedEncodingException, NoSuchAlgorithmException
    {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
	digest.update(frase.getBytes("UTF-8"));
        SecretKeySpec key = new SecretKeySpec(digest.digest(), 0, 16, "AES");
        return key;
    
    }
    
    public static boolean fileProcessor(int cipherMode,String key,File inputFile,File outputFile){
        try {
            Key secretKey = crearLlave(key);
            //Cipher cipher = Cipher.getInstance("AES");
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            
             

            if(cipherMode == Cipher.ENCRYPT_MODE){
                byte[] ivbytes = new byte[16];
                sr.nextBytes(ivbytes);
                IvParameterSpec spec = new IvParameterSpec(ivbytes);
                cipher.init(cipherMode, secretKey, spec);
                FileInputStream inputStream = new FileInputStream(inputFile);
                byte[] inputBytes = new byte[(int) inputFile.length()];
                inputStream.read(inputBytes);
                byte[] outputBytes = cipher.doFinal(inputBytes); 
                byte[] ciphertextWithIVBytes = new byte[spec.getIV().length + outputBytes.length];
                System.arraycopy(spec.getIV(), 0, ciphertextWithIVBytes, 0, 16);
                System.arraycopy(outputBytes, 0, ciphertextWithIVBytes, 16, outputBytes.length);
                FileOutputStream outputStream = new FileOutputStream(outputFile,true);
                outputStream.write(ciphertextWithIVBytes);
                outputStream.close();
            }
            else{
                //Si se va a descifrar
                FileInputStream inputStream = new FileInputStream(inputFile);
                byte[] ciphertextWithIVBytes = new byte[(int) inputFile.length()];
                byte[] decipherIV = new byte[16];
                byte[] textToDecipher = new byte[ciphertextWithIVBytes.length - 16];
                inputStream.read(ciphertextWithIVBytes);
                System.arraycopy(ciphertextWithIVBytes, 0, decipherIV, 0, 16);
                System.arraycopy(ciphertextWithIVBytes, 16, textToDecipher, 0, textToDecipher.length);
                cipher.init(cipherMode, secretKey, new IvParameterSpec(decipherIV));
                byte[] cifrado = cipher.doFinal(textToDecipher);
                FileOutputStream outputStream = new FileOutputStream(outputFile,true);
                outputStream.write(cifrado);
                outputStream.close();
            }
            
        }catch (NoSuchPaddingException | BadPaddingException |NoSuchAlgorithmException | InvalidAlgorithmParameterException | IllegalBlockSizeException | InvalidKeyException|  IOException e) {
            System.out.println(e.getMessage());
            return false;
        } 
        return true;
    }

    /**
    public static void main(String[] args) {
       String key = "This is a secret";
       File inputFile = new File("text.txt");
       File encryptedFile = new File("text.encrypted");
       File decryptedFile = new File("decrypted-text.txt");

       try {
            Crypto.fileProcessor(Cipher.ENCRYPT_MODE,key,inputFile,encryptedFile);
            Crypto.fileProcessor(Cipher.DECRYPT_MODE,key,encryptedFile,decryptedFile);
            System.out.println("Sucess");
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            ex.printStackTrace();
        }
    }
    */
	
}
