/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controllers;

import Common.GenerateKeys;
import Common.Utils;
import Logic.Crypto;
import Models.Users;
import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PublicKey;
import javax.crypto.Cipher;

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
    
    public static File getNuevoArchivo(File archivo){
        String[] filePath = archivo.getAbsolutePath().split("\\.");
        String newPath = filePath[0] + "_firma";  

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
    
    public static boolean firmado(File archivoAFirmar, File Key){
        File archivoFirmado = getNuevoArchivo(archivoAFirmar);
        return Crypto.firmar(archivoAFirmar, archivoFirmado, Key);
    }
    
    public static boolean verificar(File archivoAVerificar, File firma, String ci){
        UsersController uc = new UsersController();
        PublicKey pk = uc.getPublicKey(ci);
        return Crypto.verificar(archivoAVerificar,firma, pk);
        
    }
    
    public static boolean generarLlaves(Users usuario,String path){
        GenerateKeys gk;
        try {
                gk = new GenerateKeys(1024);
                gk.createKeys();
                Utils.writeToFile(path + CipherController.getBarra() + "privateKey", gk.getPrivateKey().getEncoded());
                usuario.setPublicKey(gk.getPublicKey().getEncoded());
                
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
