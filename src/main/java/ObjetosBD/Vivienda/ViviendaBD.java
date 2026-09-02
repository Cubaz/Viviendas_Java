package ObjetosBD.Vivienda;

public class ViviendaBD {
    private int id_vivienda;
    private String tipo;
    private int num_habitantes;
    private int num_ext;
    private int num_int;
    private int id_calle;
    private float mts_cuadrados;

    ViviendaBD(int id_vivienda, String tipo, int num_habitantes, int num_ext, int num_int, int id_calle, float mts_cuadrados){
        this.id_vivienda = id_vivienda;
        this.tipo = tipo;
        this.num_habitantes = num_habitantes;
        this.num_ext = num_ext;
        this.num_int = num_int;
        this.id_calle = id_calle;
        this.mts_cuadrados = mts_cuadrados;
    }

    public int getId_vivienda(){return id_vivienda;}
    public void setId_vivienda(int id_vivienda){this.id_vivienda = id_vivienda;}

    public String getTipo(){return tipo;}
    public void  setTipo(String tipo){this.tipo = tipo;}

    public int getNum_habitantes(){return num_habitantes;}
    public void setNum_habitantes(int num_habitantes){this.num_habitantes = num_habitantes;}

    public  int getNum_ext(){return num_ext;}
    public void setNum_ext(int num_ext){this.num_ext = num_ext;}

    public  int getNum_int(){return num_int;}
    public void setNum_int(int num_int){this.num_int = num_int;}

    public int getId_calle(){return id_calle;}
    public void setId_calle(int id_calle){this.id_calle = id_calle;}

    public float getMts_cuadrados(){return mts_cuadrados;}
    public void setMts_cuadrados(float mts_cuadrados){this.mts_cuadrados = mts_cuadrados;}

}
