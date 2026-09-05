package ObjetosBD.Departamento;

public class DepartamentoBD {
    private int id_departamento;
    private int id_edificio;
    private int id_vivienda;
    private int piso;

    public DepartamentoBD(int idDepartamento, int idEdficio, int idVivienda, int num_piso){
        this.id_departamento = idDepartamento;
        this.id_edificio = idEdficio;
        this.id_vivienda = idVivienda;
        this.piso = num_piso;
    }

    public int getId_vivienda(){return id_vivienda;}

    public void setId_vivienda(int id_vivienda) {
        this.id_vivienda = id_vivienda;
    }

    public int getId_edificio() {
        return id_edificio;
    }

    public void setId_edificio(int id_edificio) {
        this.id_edificio = id_edificio;
    }

    public int getPiso() {
        return piso;
    }

    public void setPiso(int piso) {
        this.piso = piso;
    }
}
