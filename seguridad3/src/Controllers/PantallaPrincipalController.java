/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controllers;

import Common.Roles;
import Models.Users;

/**
 *
 * @author manuelbarral
 */
public class PantallaPrincipalController {
    
    public static boolean habilitarCifradoSimetrico(Users usuario){
        int rol = usuario.getRol();
        if(rol != Roles.INACTIVO){
            return true;
        }
        return false;
    }
    
    public static boolean habilitarFirma(Users usuario){
        int rol = usuario.getRol();
        if(rol == Roles.ADMINISTRADOR || rol == Roles.USUARIO_TIPO1){
            return true;
        }
        return false;
    }
    
    public static boolean habilitarRegistrar(Users usuario){
        int rol = usuario.getRol();
        if(rol == Roles.ADMINISTRADOR){
            return true;
        }
        return false;
    }
    
    public static boolean habilitarGenerarLlaves(Users usuario){
        int rol = usuario.getRol();
        boolean primeraContra = usuario.getPrimeraContra();
        if(primeraContra && (rol == Roles.ADMINISTRADOR || rol == Roles.USUARIO_TIPO1)){
            return true;
        }
        return false;
    }
    
    
}
