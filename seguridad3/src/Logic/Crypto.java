/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Logic;

import Common.Utils;
import Controllers.CipherController;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

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
                byte[] decifrado = cipher.doFinal(textToDecipher);
                FileOutputStream outputStream = new FileOutputStream(outputFile,true);
                outputStream.write(decifrado);
                outputStream.close();
            }
            
        }catch (NoSuchPaddingException | BadPaddingException |NoSuchAlgorithmException | InvalidAlgorithmParameterException | IllegalBlockSizeException | InvalidKeyException|  IOException e) {
            System.out.println(e.getMessage());
            return false;
        } 
        return true;
    }
    
//    
//Ver si sirve o se saca
//    
    public static boolean AsymmetricFileProcessor(int cipherMode, File key, File inputFile, File outputFile) {
        try{
            if(cipherMode == Cipher.ENCRYPT_MODE){
                PrivateKey privKey = Crypto.getPrivate(key);
                Cipher cipher = Cipher.getInstance("DSA");
                cipher.init(Cipher.ENCRYPT_MODE, privKey);
                Utils.writeToFile(inputFile.getAbsolutePath(), cipher.doFinal(Files.readAllBytes(outputFile.toPath())));
      
            }
            else{
                //Si se va a desencriptar
                PublicKey pubKey = Crypto.getPublic(key);
                Cipher cipher = Cipher.getInstance("DSA");
                cipher.init(Cipher.DECRYPT_MODE, pubKey);
                Utils.writeToFile(outputFile.getAbsolutePath(), cipher.doFinal(Files.readAllBytes(inputFile.toPath())));
      
            }
            
            return true;
        } catch (NoSuchAlgorithmException ex) {
            return false;
        } catch (NoSuchPaddingException ex) {
            return false;
        } catch (Exception ex) {
            return false;
        }
    }
    
    private static PrivateKey getPrivate(File file) throws Exception {
        byte[] keyBytes = Files.readAllBytes(file.toPath());
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory kf = KeyFactory.getInstance("DSA");
        return kf.generatePrivate(spec);
    }
    
    
    // https://docs.oracle.com/javase/8/docs/api/java/security/spec/X509EncodedKeySpec.html
    public static PublicKey getPublic(File file) throws Exception {
            byte[] keyBytes = Files.readAllBytes(file.toPath());
            X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
            KeyFactory kf = KeyFactory.getInstance("DSA");
            return kf.generatePublic(spec);
    }
    
    public static PublicKey getPublic(byte[] bytes)throws Exception{
        X509EncodedKeySpec spec = new X509EncodedKeySpec(bytes);
        KeyFactory kf = KeyFactory.getInstance("DSA");
        return kf.generatePublic(spec);
    }
    
    
    public static boolean firmar(File archivoAFirmar, File archivoFirma, File privateKey){
        try{
            Signature dsa = Signature.getInstance("SHA256withDSA", "SUN"); 
            PrivateKey priv = Crypto.getPrivate(privateKey);
            dsa.initSign(priv);
            
            byte[] buf = new byte[(int)archivoAFirmar.length()];
            FileInputStream fis = new FileInputStream(archivoAFirmar);
            fis.read(buf);
            dsa.update(buf);
            fis.close();
            
            byte[] firma = dsa.sign();
            
            FileOutputStream sigfos = new FileOutputStream(archivoFirma);

            
            sigfos.write(firma);
            
            sigfos.close();
            return true;
        
        } catch (Exception ex) {
            return false;
        }
    }
    
    public static boolean verificar(File archivo, File firma, PublicKey publicKey) {
        try {
            byte[] bufArchivo = new byte[(int) archivo.length()];
            byte[] bufFirma = new byte[(int) firma.length()];
            
            FileInputStream fisArchivo = new FileInputStream(archivo);
            fisArchivo.read(bufArchivo);
            fisArchivo.close();
            
            FileInputStream fisFirma = new FileInputStream(firma);
            fisFirma.read(bufFirma);
            fisFirma.close();
            
            Signature sig = Signature.getInstance("SHA256withDSA", "SUN");
            sig.initVerify(publicKey);
            
            sig.update(bufArchivo, 0, bufArchivo.length);
            
            
            return sig.verify(bufFirma);
            
        } catch (Exception e) {
            return false;
        }

    }
    
	
}
