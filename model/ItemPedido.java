package model;

public class ItemPedido {
  private int id;
  private int productoId;
  private String productoNombre;
  private int cantidad;
  private double precio;

  public ItemPedido() {}

  public ItemPedido(int productoId, String productoNombre, int cantidad, double precio) {
    this.productoId = productoId;
    this.productoNombre = productoNombre;
    this.cantidad = cantidad;
    this.precio = precio;
  }

  public int getId() { return id; }
  public void setId(int id) { this.id = id; }

  public int getProductoId() { return productoId; }
  public void setProductoId(int productoId) { this.productoId = productoId; }

  public String getProductoNombre() { return productoNombre; }
  public void setProductoNombre(String productoNombre) { this.productoNombre = productoNombre; }

  public int getCantidad() { return cantidad; }
  public void setCantidad(int cantidad) { this.cantidad = cantidad; }

  public double getPrecio() { return precio; }
  public void setPrecio(double precio) { this.precio = precio; }

  public double getSubtotal() { return cantidad * precio; }

  @Override
  public String toString() {
    return cantidad + "x " + productoNombre + " - S/" + getSubtotal();
  }
}
