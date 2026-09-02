package ObjetosBD.Calle;

public class CalleBD {
    private int id_calle;
    private String nombre;
    private int id_colonia;

    public CalleBD(int idCalle, String nombre, int idColonia){
        this.id_calle = idCalle;
        this.nombre = nombre;
        this.id_colonia = idColonia;
    }

    public int getId_calle(){return id_calle;}
    public void setId_calle(int id_calle){this.id_calle = id_calle;}

    public String getNombre(){return nombre;}
    public void setNombre(String nombre){this.nombre = nombre;}

    public int getId_colonia(){return id_colonia;}
    public void setId_colonia(int id_colonia){this.id_colonia = id_colonia;}

    @Override
    public String toString() {
        return nombre; // 👈 Esto hará que el ComboBox muestre el nombre de la calle
    }
}
