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
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

public class Crypto {

    public static boolean fileProcessor(int cipherMode,String key,File inputFile,File outputFile){
        try {
            Key secretKey = crearLlave(key);
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(cipherMode, secretKey);

            FileInputStream inputStream = new FileInputStream(inputFile);
            byte[] inputBytes = new byte[(int) inputFile.length()];
            inputStream.read(inputBytes);

            byte[] outputBytes = cipher.doFinal(inputBytes);

            FileOutputStream outputStream = new FileOutputStream(outputFile);
            outputStream.write(outputBytes);

            inputStream.close();
            outputStream.close();

        } catch (NoSuchPaddingException | NoSuchAlgorithmException | BadPaddingException | IllegalBlockSizeException | IOException e) {
            return false;
        } catch(InvalidKeyException e){
            System.out.println("Excepción: clave inválida");
            return false;
        }
        return true;
    }
    
    public static SecretKeySpec crearLlave(String frase) throws UnsupportedEncodingException, NoSuchAlgorithmException
    {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
	digest.update(frase.getBytes("UTF-8"));
        SecretKeySpec key = new SecretKeySpec(digest.digest(), 0, 16, "AES");
        return key;
    
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
