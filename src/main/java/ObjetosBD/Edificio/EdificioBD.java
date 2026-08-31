package ObjetosBD.Edificio;

public class EdificioBD {

    private int IdEdificio;
    private String nombre;

    public EdificioBD(int id_edificio, String nombre) {
        this.IdEdificio = id_edificio;
        this.nombre = nombre;
    }

    public int getIdEdificio(){ return IdEdificio;}
    public void setIdEdificio(int IdEdificio) {this.IdEdificio = IdEdificio;}

    public String getNombre(){ return nombre;}
    public void setNombre(String nombre){this.nombre = nombre;}
}
