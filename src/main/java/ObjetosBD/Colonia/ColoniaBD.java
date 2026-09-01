package ObjetosBD.Colonia;

public class ColoniaBD {

    private int id_colonia;
    private String nombre;
    public float sup_construida;

    public ColoniaBD(int id_colonia, String nombre){
        this.id_colonia = id_colonia;
        this.nombre = nombre;
    }

    public int getId_colonia(){return id_colonia;}
    public  void  setId_colonia(int id_colonia){this.id_colonia = id_colonia;}

    public String getNombre(){return nombre;}
    public void setNombre(String nombre){this.nombre = nombre;}

    public float getSup_construida(){return sup_construida;}
    public void setSup_construida(float sup_construida){this.sup_construida = sup_construida;}

    @Override
    public String toString(){
        return nombre;
    }
}
