package ObjetosBD.Familia;

public class FamiliaBD {

    public FamiliaBD(int id_familia, String apellidos) {
        this.id_familia = id_familia;
        this.apellidos = apellidos;
    }

    private int id_familia;
    private String apellidos;

    public int getId(){
        return id_familia;
    }
    public void setId(int id_familia){this.id_familia = id_familia;}

    public String getApellidos(){return apellidos;}
    public void setApellidos(String apellidos){this.apellidos = apellidos;}

    @Override
    public String toString() {
        return apellidos; // Muestra el apellido real en lugar de la memoria
    }
}
