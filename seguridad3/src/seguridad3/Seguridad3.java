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
        Users usuario = uc.newUser("53007976", "clavesupersegura1357", "Manuel", "Barral", 1);
        try{
            uc.modificarContrasena(usuario, "esmuyfacil1234", "clavesupersegura1357");
        } catch(Exception e){
            System.out.println("excepción");
        }

    }
    
}
