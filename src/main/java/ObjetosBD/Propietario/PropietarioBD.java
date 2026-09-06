package ObjetosBD.Propietario;

public class PropietarioBD {
    private int id_vivienda;
    private int id_persona;

    public PropietarioBD(int vivienda, int persona){
        this.id_vivienda = vivienda;
        this.id_persona = persona;
    }

    public int getId_persona() {
        return id_persona;
    }

    public int getId_vivienda() {
        return id_vivienda;
    }

    public void setId_persona(int id_persona) {
        this.id_persona = id_persona;
    }

    public void setId_vivienda(int id_vivienda) {
        this.id_vivienda = id_vivienda;
    }
}
