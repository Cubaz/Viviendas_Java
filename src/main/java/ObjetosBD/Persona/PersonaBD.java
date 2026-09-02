package ObjetosBD.Persona;

public class PersonaBD {

    private int IdPersona;
    private String Nombre;
    public int IdFamilia;
    public int edadPersona;

    public PersonaBD(int IdPersona, String Nombre, int IdFamilia, int edadPersona){
        this.IdPersona = IdPersona;
        this.Nombre = Nombre;
        this.IdFamilia = IdFamilia;
        this.edadPersona = edadPersona;
    }

    public int getIdPersona(){return IdPersona;}
    public  void  setIdPersona(int IdPersona){this.IdPersona = IdPersona;}

    public String getNombre(){return Nombre;}
    public void setNombre(String Nombre){this.Nombre = Nombre;}

    public int getIdFamilia(){return IdFamilia;}
    public void setIdFamilia(int IdFamilia){this.IdFamilia = IdFamilia;}

    public int getEdadPersona(){return edadPersona;}
    public void setEdadPersona(int edadPersona){this.edadPersona = edadPersona;}

    @Override
    public String toString() {
        return Nombre;
    }

}
