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
    
    public static boolean cifradoSimetrico(File archivoACifrar, String password, String newName){
        String[] filePath = archivoACifrar.getAbsolutePath().split("\\.");
        String fileParent = archivoACifrar.getParent();
        String newPath = fileParent + "\\" + newName + "." + filePath[1];  

        File archivoEncriptado = new File(newPath);
        return Crypto.fileProcessor(Cipher.ENCRYPT_MODE, password, archivoACifrar, archivoEncriptado);
        
    }
    
    public static boolean descifradoSimetrico(File archivoADescifrar, String password, String newName){
        String[] filePath = archivoADescifrar.getAbsolutePath().split("\\.");
        String fileParent = archivoADescifrar.getParent();
        String newPath = fileParent + "\\" + newName + "." + filePath[1];
        
        File archivoDescifrado = new File(newPath);
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
    
}
