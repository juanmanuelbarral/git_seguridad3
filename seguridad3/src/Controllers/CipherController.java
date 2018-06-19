/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controllers;

import Common.GenerateKeys;
import Common.Utils;
import Logic.Crypto;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;

/**
 *
 * @author manuelbarral
 */
public class CipherController {
    
    public static File getNuevoArchivo(File archivo, String newName){
        String[] filePath = archivo.getAbsolutePath().split("\\.");
        String fileParent = archivo.getParent();
        String newPath = fileParent + getBarra() + newName + "." + filePath[1];  

        File archivoNuevo = new File(newPath);
        return archivoNuevo;
    }
    
    public static boolean cifradoSimetrico(File archivoACifrar, String password, String newName){
        File archivoEncriptado = getNuevoArchivo(archivoACifrar, newName);
        return Crypto.fileProcessor(Cipher.ENCRYPT_MODE, password, archivoACifrar, archivoEncriptado);
        
    }
    
    public static boolean descifradoSimetrico(File archivoADescifrar, String password, String newName){
        File archivoDescifrado = getNuevoArchivo(archivoADescifrar, newName);
        return Crypto.fileProcessor(Cipher.DECRYPT_MODE, password , archivoADescifrar, archivoDescifrado);
    }
    
    public static boolean cifradoAsimetrico(File archivoACifrar,String newName, File key){
            File archivoEncriptado = getNuevoArchivo(archivoACifrar,newName);
            return Crypto.AsymmetricFileProcessor(Cipher.ENCRYPT_MODE,key,archivoACifrar,archivoEncriptado);
            
    }
    
    public static boolean descifradoAsimetrico(File archivoADescifrar, String newName, File key) {
	File archivoDesencriptado = getNuevoArchivo(archivoADescifrar,newName);
        return Crypto.AsymmetricFileProcessor(Cipher.ENCRYPT_MODE,key,archivoADescifrar,archivoDesencriptado);
            
    }
    
    public static boolean firmado(File archivoAFirmar, String newName, File Key){
        ///Ni idea que hacer (?)
        File archivoFirmado = getNuevoArchivo(archivoAFirmar,newName);
        return Crypto.firmar(archivoAFirmar, archivoFirmado, Key);
    }
    
    public static boolean generarLlaves(String path){
        GenerateKeys gk;
        try {
                gk = new GenerateKeys(1024);
                gk.createKeys();
                Utils.writeToFile(path + CipherController.getBarra() + "publicKey", gk.getPublicKey().getEncoded());
                Utils.writeToFile(path + CipherController.getBarra() + "privateKey", gk.getPrivateKey().getEncoded());
                
                //
                //Aca se actualizaria en la BD, por ahora lo dejo local
                //
                
                return true;
        } catch (NoSuchAlgorithmException | NoSuchProviderException e) {
                return false;
        } catch (IOException e) {
                return false;
        }
    }
    
    public static String getBarra(){
        switch(System.getProperty("os.name")){
            case "Linux": return "/";
            case "Windows" : return "\\";
            default: return "/";
        }
    }
    
    
    
}
