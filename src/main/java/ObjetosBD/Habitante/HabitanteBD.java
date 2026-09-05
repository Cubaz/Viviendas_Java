package ObjetosBD.Habitante;

public class HabitanteBD {
    private int idPersona;
    private int idVivienda;
    private String rol;

    public HabitanteBD(int idPersona, int idVivienda, String rol){
        this.idPersona = idPersona;
        this.idVivienda = idVivienda;
        this.rol = rol;
    }

    public int getIdPersona(){return idPersona;}
    public int getIdVivienda(){return idVivienda;}
    public String getRol(){return rol;}

    public void setIdPersona(int idPersona){this.idPersona = idPersona;}
    public void setIdVivienda(int idVivienda){this.idVivienda = idVivienda;}
    public void setRol(String rol){this.rol = rol;}
}
