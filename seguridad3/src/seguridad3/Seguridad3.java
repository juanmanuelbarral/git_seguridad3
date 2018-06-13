/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package seguridad3;

import Controllers.UsersController;

/**
 *
 * @author manuelbarral
 */
public class Seguridad3 {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        UsersController uc = new UsersController();
        uc.newUser("manuelbarral02@gmail.com", "estaseguronoesta1357", "Manuel", "Barral");
    }
    
}
