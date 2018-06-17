/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package seguridad3;

import Controllers.UsersController;
import Controllers.exceptions.ContrasenaIncorrectaException;
import Models.Users;

/**
 *
 * @author manuelbarral
 */
public class Seguridad3 {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args){
        // TODO code application logic here
        UsersController uc = new UsersController();
        Users usuario = uc.newUser("12345678", "clavesupersegura1357", "Administrador", "Lopez", 1);
        try{
            uc.modificarContrasena(usuario, "clavesupersegura1234", "clavesupersegura1357");
        } catch(Exception e){
            System.out.println("excepción");
        }

    }
    
}
