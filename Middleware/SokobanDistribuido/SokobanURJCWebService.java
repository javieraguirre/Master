/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package servJugadores;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import javax.jws.WebService;
import javax.jws.WebMethod;
import javax.jws.WebParam;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;


/**
 *
 * @author Javi
 */
@WebService(serviceName = "SokobanURJCWebService")
public class SokobanURJCWebService {
    
    
    @WebMethod(operationName = "recuperarJugadorSQL")
    public String recuperarJugador(@WebParam(name = "usuario") String usuario, @WebParam(name = "password") String password)
        {

        String customerInfo = "";
        
        try
          {
          Class.forName("com.mysql.jdbc.Driver").newInstance();
          Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/mydb", "root", "Manolo12");

          PreparedStatement statement
                =  con.prepareStatement("select nombre, password, nivel from sokobanurjc where nombre=\"" + usuario + "\";");
          ResultSet result = statement.executeQuery();

          while(result.next())
            {
            customerInfo = customerInfo
                         + result.getString("nombre")
                         + "&" + result.getString("password")
                         + "&"+result.getString("nivel");
            }
          }
        catch(Exception exc){ System.out.println(exc.getMessage()); }        
        
        return customerInfo;
        }
    
    
    
    @WebMethod(operationName = "crearJugadorSQL")
    public String crearJugador(@WebParam(name = "usuario") String usuario, @WebParam(name = "password") String password)
        {
        try
          {
          Class.forName("com.mysql.jdbc.Driver").newInstance();
          Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/mydb", "root", "Manolo12");
          
          PreparedStatement statement
                =  con.prepareStatement("insert into sokobanurjc (nombre, password, nivel) values ('" + usuario + "','" + password + "','0.1');");
          statement.executeUpdate();

          }
        catch(Exception exc){ System.out.println(exc.getMessage()); }
        
        return recuperarJugador(usuario, password);
        }
    
    
    @WebMethod(operationName = "actualizarJugadorSQL")
    public String actualizarJugador(@WebParam(name = "usuario") String usuario, @WebParam(name = "password") String password, @WebParam(name = "nivel") String nivel)
        {
        try
          {
          Class.forName("com.mysql.jdbc.Driver").newInstance();
          Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/mydb", "root", "Manolo12");

          PreparedStatement statement
                =  con.prepareStatement("update sokobanurjc set nivel = '" + nivel + "' where nombre='" + usuario + "';");
          statement.executeUpdate();
          }
        catch(Exception exc){ System.out.println(exc.getMessage()); }
        
        return recuperarJugador(usuario, password);
        }
    
    
    
    
    
    
    
    // LOS METODOS SIGUIENTES SON ANTiGUOS Y NO TIENEN USO:
    
    /**
     * This is a sample web service operation
     */
    @WebMethod(operationName = "hello")
    public String hello(@WebParam(name = "name") String txt) {
        return "Hola, " + txt;
    }

    /**
     * Web service operation
     */
    @WebMethod(operationName = "addition")
    public String addition(@WebParam(name = "parameter1") double parameter1, @WebParam(name = "parameter2") double parameter2) {

        return Double.toString((parameter1 + parameter2));
    }
    
    public void CrearFichaJugador(String[] usuario)
    {
        try
        {            
            PrintWriter writer = new PrintWriter("c:\\" + usuario[0] + ".txt", "UTF-8");    // nombre del fichero = nombre del usuario
            writer.println(usuario[0]);                                           // primera linea del fichero = usuario
            writer.println(usuario[1]);                     // segunda linea = password
            writer.println(usuario[2]);                     // segunda linea del fichero = [coleccionNiveles],[nivel]
            writer.close();
        }
        catch(Exception e) { e.printStackTrace(); }
    }
    
    public void actualizarNivel(String usuario, String nivel)
    {
        String password = LeerFichaJugadorIndexado(usuario, 1); // obtener password del fichero original del usuario.
        try
        {
            PrintWriter writer = new PrintWriter("c:\\" + usuario + ".txt", "UTF-8");
            writer.println(usuario);
            writer.println(password);
            writer.println(nivel);
            writer.close();
        }
        catch(Exception e) { e.printStackTrace(); }

    }
    
    
    public String[] LeerFichaJugador(String usuario)
    {
                int celdas = 3;
                String[] fichaJugador = new String[celdas];
		try
                {
			BufferedReader br = new BufferedReader(new FileReader(new File("c:\\" + usuario + ".txt")));
			for (int i=0; i<celdas; i++) { fichaJugador[i] = br.readLine(); }                                
			br.close();
                }        
		catch (FileNotFoundException e) { e.printStackTrace(); }
		catch (IOException e) { e.printStackTrace(); }
                
    return fichaJugador;
    }
    
    
    public String LeerFichaJugadorIndexado(String usuario, int indice)
    {
                int celdas = 3;
                String[] fichaJugador = new String[celdas];
		try
                {
			BufferedReader br = new BufferedReader(new FileReader(new File("c:\\" + usuario + ".txt")));
			for (int i=0; i<celdas; i++) { fichaJugador[i] = br.readLine(); }
			br.close();
                }        
		catch (FileNotFoundException e) { e.printStackTrace(); }
		catch (IOException e) { e.printStackTrace(); }
                
    return fichaJugador[indice];
    }
}