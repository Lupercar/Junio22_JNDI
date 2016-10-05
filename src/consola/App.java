package consola;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import oracle.jdbc.OracleDriver;
import oracle.jdbc.pool.OracleDataSource;

public class App {

	private static final String DATASOURCE_NAME = "DataSource"; 
	private static Properties prop = new Properties();
	
	public static void main(String[] args) throws SQLException, IOException {
		//Cargar el driver a la MVJ
        DriverManager.registerDriver(new OracleDriver());
        
//        conectarnos al JNDI
        Properties propiedadesJNDI = new Properties(); 
        propiedadesJNDI.setProperty(Context.INITIAL_CONTEXT_FACTORY, 
        							"com.sun.jndi.fscontext.RefFSContextFactory"); 
 
        propiedadesJNDI.setProperty(Context.PROVIDER_URL, 
        							"file:///c:/fscontext"); 
        
        InitialContext jndi = null; 
        DataSource ds = null; 
        
        try{
        	
        	jndi = new InitialContext(propiedadesJNDI); 
//       	 obtener el DataSource de JNDI
        	ds = (DataSource) jndi.lookup(DATASOURCE_NAME); //Obtenemos el DataSource
        
        }catch(NamingException ex){
//        	no lo ha encontrado
        	ds = crearDataSource(); //cremos el DataSource
        	try {
        		
				jndi.bind(DATASOURCE_NAME, ds);
			
        	} catch (NamingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} //guardamos en JNDI
        }
        
        try {
        	
			jndi.close(); //nos desconectamos del JNDI
	
        } catch (NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
        mostrarCliente(ds);
	}
	
	private static DataSource crearDataSource() throws SQLException, IOException {
		//Leemos configuración de un fichero de propiedades
        prop.load(App.class.getResourceAsStream("../configuracion/oracle.properties"));
        
//      creamos el DataSource
        OracleDataSource ds = new OracleDataSource();
        
//        Configuramos el DataSource
        ds.setURL(prop.getProperty("url"));
        ds.setUser(prop.getProperty("usuario"));
        ds.setPassword(prop.getProperty("pass"));
		return ds;
	}

	private static void mostrarCliente(DataSource ds) throws SQLException{
//      Hacemos una conexion 
      Connection conn = ds.getConnection(); 
      
//      Lo que queremos hacer
      Statement stm = conn.createStatement();  
      String sql = "SELECT * FROM cliente"; 
      ResultSet resultado = stm.executeQuery(sql); 
      
//      muestro por pantalla
      while(resultado.next()){
      	int id = resultado.getInt("ID"); 
      	String nombre = resultado.getString("NOMBRE"); 
      	System.out.println("ID: " + id + ", NOMBRE: " + nombre); 
      }
      
//      cerramos ResultSet
      resultado.close();
//      cerramos Statement
      stm.close();
//      cerramos la conexion y el DataSource
      conn.close(); //se la devuelve al DataSource
	}

}//fin class consola.App
