/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Logic;
import Common.Utils;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

     
    
/**
 *
 * @author nico
 */
public class UsersLogic {
    /*
    private final String password;
    
    public UsersLogic(String pass){
        this.password=pass;
    }
    */
    
    public UsersLogic(){}
    
    /**
     *
     * Metodo que verifica si la contrasena esta en la api de haveibeenpawned
     * 
     * @param password contraseña a ser verificada contra haveibeenpwned
     * @return true si la contrasena no esta en la BD de la api, false en caso contrario
     * @throws UnsupportedEncodingException
     * @throws NoSuchAlgorithmException
     * @throws MalformedURLException
     * @throws IOException
     */
    public static boolean checkPasswordWithPwned(String password) throws UnsupportedEncodingException, NoSuchAlgorithmException, MalformedURLException, IOException{
        //Se convierte la contrasena a UTF8
        byte[] passUTF8 = password.getBytes("UTF-8");
        //Se le aplica SHA1
        MessageDigest messageD = MessageDigest.getInstance("SHA-1");
        messageD.update(passUTF8);
        byte[] bytePassSHA1 = messageD.digest();
        String passSHA1 = Utils.byteArrayToString(bytePassSHA1);
        System.out.println(passSHA1);
        
        //A la api de haveibeenpwned se le pasan los primeros 5 caracteres y responde con los sufijo
        //por lo cual separamos prefijo y sufijo
        String prefijo = passSHA1.substring(0, 5);
        String sufijo = passSHA1.substring(5);
        
        //Se envia el prefijo a la api
        String urlConsulta = "https://api.pwnedpasswords.com/range/" + prefijo;
        
        URL url = new URL(urlConsulta);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("User-Agent","java-Seguridad");
        
        //FALTA CHEQUEAR STATUS
        int status = con.getResponseCode();
        
        //Se lee la respuesta, se agrega "|" entre cada linea
        BufferedReader in = new BufferedReader(
        new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer content = new StringBuffer();
        while ((inputLine = in.readLine()) != null) {
            content.append(inputLine);
            content.append("|");
        }
        in.close();
        con.disconnect();
        
        //posibles tiene los sufijos de las respuestas dadas por la api
        String[] posibles = content.toString().split("\\|");
        
        //chequeamos si algun sufijo de los mandamos es el que buscamos, si lo encontramos se devuelve false, si no true
        for(String s:posibles)
        {  
            String[] linea = s.split(":");
            String ocurrencias = linea[1];
            String sufijoPwned = linea [0];
            if (sufijoPwned.equals(sufijo.toUpperCase()))
            {
                System.out.println("Se encontro el sufijo con "+ ocurrencias + " ocurrencias");
                return false;
            }
        }
        System.out.println("Su clave es bastante buena");
        return true;
    }
    
    
}
