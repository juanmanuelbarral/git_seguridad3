/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Common;

/**
 *
 * @author manuelbarral
 */
public class Roles {
    
    
    /**
     * Rol para un usuario inactivo (sin permisos).
     * Es el equivalente a estar eliminado.
     */
    public static int INACTIVO = 0;
    
    /**
     * Rol de administrador.
     * Puede crear y administrar usuarios. Asigna los roles de usuario.
     * Puede cifrar/descifrar archivos simétricamente.
     * Puede firmar archivos.
     */
    public static int ADMINISTRADOR = 1;
    
    /**
     * Rol de usuario tipo 1.
     * Puede cifrar/descifrar archivos simétricamente.
     * Puede firmar y verificar archivos.
     */
    public static int USUARIO_TIPO1 = 2;
    
    /**
     * Rol de usuario tipo 2.
     * Puede cifrar/descifrar archivos simétricamente.
     */
    public static int USUARIO_TIPO2 = 3;
    
}
