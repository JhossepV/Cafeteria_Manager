package model;

public class Mozo {
  private int id;
  private String nombre;
  private String turno;

  public Mozo() {}

  public Mozo(String nombre, String turno) {
    this.nombre = nombre;
    this.turno = turno;
  }

  public int getId() { return id; }
  public void setId(int id) { this.id = id; }

  public String getNombre() { return nombre; }
  public void setNombre(String nombre) { this.nombre = nombre; }

  public String getTurno() { return turno; }
  public void setTurno(String turno) { this.turno = turno; }

  @Override
  public String toString() {
    return nombre + " - Turno: " + turno;
  }
}
