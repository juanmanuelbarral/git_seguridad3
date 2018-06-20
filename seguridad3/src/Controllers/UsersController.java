/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controllers;

import Common.Utils;
import Controllers.exceptions.ContrasenaIncorrectaException;
import Logic.Crypto;
import Logic.UsersLogic;
import Models.Users;
import java.io.File;
import java.security.PublicKey;

/**
 *
 * @author manuelbarral
 */
public class UsersController {
    
    /**
     * Constructor de la clase UsersController
     * Constructor vacío
     */
    public UsersController(){}
    
    public Users newUser(String ci, String password, String nombre, String apellido, int rol){
        try{
            if(UsersLogic.checkPasswordWithPwned(password)){
                String passSHA1 = Utils.applySHA256(password);
                Integer.valueOf(ci);
                
                Users usuario = new Users(ci, passSHA1, nombre, apellido, rol, false, false);
                
                UsersJpaController ujc = new UsersJpaController();
                try
                {
                    ujc.create(usuario);
                    return usuario;
                }
                catch(Exception e){
                    return null;
                }
                
            }
            return null;
        }
        catch(NumberFormatException nf){
            throw nf;
        }
        catch(Exception e){
            return null;
        }
    }
    
    public Users login(String ci, String password){
        UsersJpaController ujc = new UsersJpaController();
        Users usuarioEncontrado = ujc.findUsers(ci);
        if(usuarioEncontrado!=null){
            try{
                String passSHA1 = Utils.applySHA256(password);
                if(passSHA1.equals(usuarioEncontrado.getPassword())){
                    return usuarioEncontrado;
                }
            }
            catch(Exception e){
                return null;
            }
        }
        return null;
    }
    
    public Users loginConCIVerificada(String ci){
        UsersJpaController ujc = new UsersJpaController();
        Users usuarioEncontrado = ujc.findUsers(ci);
        if(usuarioEncontrado!=null){
            try{
                return usuarioEncontrado;
            }
            catch(Exception e){
                return null;
            }
        }
        return null;
    }
    
    public boolean modificarContrasena(Users usuario, String nuevaContra, String contraActual) throws ContrasenaIncorrectaException{
        try{
            String passSHA1 = Utils.applySHA256(contraActual);
            if(usuario.getPassword().equals(passSHA1)){
                if(UsersLogic.checkPasswordWithPwned(nuevaContra)){
                    String passNuevaSHA1 = Utils.applySHA256(nuevaContra);
                    usuario.setPassword(passNuevaSHA1);
                    usuario.seCambioContra();
                    UsersJpaController ujc = new UsersJpaController();
                    ujc.edit(usuario);
                    return true;
                }
            }
            else{
                throw new ContrasenaIncorrectaException();
            }
        }
        catch(ContrasenaIncorrectaException cIncorrecta){
            throw cIncorrecta;
        }
        catch (Exception e){
            return false;
        }
        return false;
    }
    
    public PublicKey getPublicKey(String ci){
        UsersJpaController ujc = new UsersJpaController();
        Users usuario = ujc.findUsers(ci);
        if(!usuario.getTieneClaves()) return null;
        byte[] publicByte = usuario.getPublicKey();
        if (publicByte!=null){
            try{
                return Crypto.getPublic(publicByte);
            }
            catch(Exception e){
                return null;
            }
        }
        return null;
        
    }
    
    public boolean generarClaves(Users usuario, String path){
        if(usuario.getTieneClaves()) return false;
        try{
            usuario.setTieneClaves(true);
            boolean res = CipherController.generarLlaves(usuario,path);
            UsersJpaController ujc = new UsersJpaController();
            ujc.edit(usuario);
            return res;
        }
        catch(Exception e){
            return false;
        }
        
    }
    
}
