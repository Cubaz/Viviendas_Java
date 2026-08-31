package ObjetosBD;
import java.sql.Connection;
import java.sql.DriverManager;
import javax.swing.JOptionPane;
import java.sql.SQLException;

public class Conexion {

    private static Connection cnx;
    private static final String DRIVER = "com.mysql.cj.jdbc.Driver";
    private static final String BD = "viviendas";
    private static final String USER = "root";
    //Cambios de Said (prometo que sin esto no corría xd)
    private static final String PASS = "admin";

    //private static final String PASS = "root";
    private static final String URL = "jdbc:mysql://localhost:3306/" + BD + "?useSSL=false&useLegacyDatetimeCode=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";


    public Conexion(){
        cnx = null;
    }

    /// MÉTODO QUE VERIFICA LA CONEXIÓN A LA BD
    public Connection getConexion(){
        cnx=null;
        try{
            Class.forName(DRIVER);
            cnx =(Connection)DriverManager.getConnection(URL, USER, PASS);
        }catch(ClassNotFoundException e){
            JOptionPane.showMessageDialog(null, "NO SE PUDO REALIZAR LA CONVERSION DE CLASE" + e.getMessage(), "ERROR", JOptionPane.ERROR_MESSAGE);
            System.exit(0);
        }catch(SQLException e){
            JOptionPane.showMessageDialog(null, "ERROR DE CONEXION" + e.getMessage(), "ERROR", JOptionPane.ERROR_MESSAGE);
            System.exit(0);
        }
        return cnx;
    }

    /// MÉTODO QUE CIERRA LA CONEXIÓN A LA BD
    public void close(){
        try{
            cnx.close();
        }catch(SQLException e){
            JOptionPane.showMessageDialog(null, "Error al cerrar la conexion\n"+ e.getMessage(), "ERROR", 2);
        }
    }

    /// MÉTODO QUE EJECUTA LA CLASE Y QUE IMPRIME CUANDO LA CONEXIÓN ES O NO EXITOSA
    public static void main(String[] args){
        Conexion cnx = new Conexion();
        if(cnx.getConexion() != null){
            System.out.println("Conexion exitosa");
        }else{
            System.out.println("No se pudo conectar a la BD");
        }
    }
}