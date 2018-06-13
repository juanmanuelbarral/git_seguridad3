/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controllers;

import Common.Utils;
import Logic.UsersLogic;
import Models.Users;

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
    
    public Users newUser(String email, String password, String nombre, String apellido){
        try{
            if(UsersLogic.checkPasswordWithPwned(password)){
                String passSHA1 = Utils.applySHA1(password);
                
                Users usuario = new Users(email, passSHA1,nombre,apellido);
                
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
        catch(Exception e){
            return null;
        }
    }
    
    public Users login(String email, String password){
        UsersJpaController ujc = new UsersJpaController();
        Users usuarioEncontrado = ujc.findUsers(email);
        if(usuarioEncontrado!=null){
            try{
                String passSHA1 = Utils.applySHA1(password);
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
    
}
