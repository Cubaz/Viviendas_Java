package ObjetosBD.Vivienda;

public class ViviendaBD {
    private int id_vivienda;
    private String tipo;
    private int id_propietario;
    private int num_habitantes;
    private int num_ext;
    private int num_int;
    private float mts_cuadrados;

    public ViviendaBD(int idVivienda, String tipoVivienda, int idPropietario, int habitantes, int numExterior, int numInterior, float metrosCuadrados){
        this.id_vivienda = idVivienda;
        this.tipo = tipoVivienda;
        this.id_propietario = idPropietario;
        this.num_habitantes = habitantes;
        this.num_ext = numExterior;
        this.num_int = numInterior;
        this.mts_cuadrados = metrosCuadrados;
    }

    public int getId_vivienda(){return id_vivienda;}
    public void setId_vivienda(int id_vivienda){this.id_vivienda = id_vivienda;}

    public String getTipo(){return tipo;}
    public void  setTipo(String tipo){this.tipo = tipo;}

    public int getId_propietario(){return id_propietario;}
    public void setId_propietario(int id_propietario){this.id_propietario = id_propietario;}

    public int getNum_habitantes(){return num_habitantes;}
    public void setNum_habitantes(int num_habitantes){this.num_habitantes = num_habitantes;}

    public  int getNum_ext(){return num_ext;}
    public void setNum_ext(int num_ext){this.num_ext = num_ext;}

    public  int getNum_int(){return num_int;}
    public void setNum_int(int num_int){this.num_int = num_int;}

    public float getMts_cuadrados(){return mts_cuadrados;}
    public void setMts_cuadrados(float mts_cuadrados){this.mts_cuadrados = mts_cuadrados;}

}
