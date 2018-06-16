/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controllers;

import Logic.Crypto;
import java.io.File;
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
    
    public static boolean cifradoSimetrico(File archivoACifrar, String password, String newName){
        File archivoEncriptado = getNuevoArchivo(archivoACifrar, newName);
        return Crypto.fileProcessor(Cipher.ENCRYPT_MODE, password, archivoACifrar, archivoEncriptado);
        
    }
    
    public static boolean descifradoSimetrico(File archivoADescifrar, String password, String newName){
        File archivoDescifrado = getNuevoArchivo(archivoADescifrar, newName);
        return Crypto.fileProcessor(Cipher.DECRYPT_MODE, password , archivoADescifrar, archivoDescifrado);
    }
    
    public static boolean cifradoAsimetrico(File archivoACifrar){
        return false;
        //IMPLEMENTAR
    }
    
    public static boolean descifradoAsimetrico(File archivoADescifar){
        return false;
        //IMPLEMENTAR
    }
    
    public static String getBarra(){
        switch(System.getProperty("os.name")){
            case "Linux": return "/";
            case "Windows" : return "\\";
            default: return "/";
        }
    }
    
}
