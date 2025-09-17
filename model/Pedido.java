package model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class Pedido {
  private int id;
  private int clienteId;
  private String clienteNombre;
  private int mozoId;
  private String mozoNombre;
  private LocalDateTime fecha;
  private String estado;
  private double total;
  // private boolean favorito; // Eliminado
  private List<ItemPedido> items;

  public Pedido() {
    this.items = new ArrayList<>();
    this.fecha = LocalDateTime.now();
    this.estado = "PENDIENTE";
  }

  public Pedido(int clienteId, String clienteNombre, int mozoId, String mozoNombre) {
    this();
    this.clienteId = clienteId;
    this.clienteNombre = clienteNombre;
    this.mozoId = mozoId;
    this.mozoNombre = mozoNombre;
  }

  public int getId() { return id; }
  public void setId(int id) { this.id = id; }

  public int getClienteId() { return clienteId; }
  public void setClienteId(int clienteId) { this.clienteId = clienteId; }

  public String getClienteNombre() { return clienteNombre; }
  public void setClienteNombre(String clienteNombre) { this.clienteNombre = clienteNombre; }

  public int getMozoId() { return mozoId; }
  public void setMozoId(int mozoId) { this.mozoId = mozoId; }

  public String getMozoNombre() { return mozoNombre; }
  public void setMozoNombre(String mozoNombre) { this.mozoNombre = mozoNombre; }

  public LocalDateTime getFecha() { return fecha; }
  public void setFecha(LocalDateTime fecha) { this.fecha = fecha; }

  public String getEstado() { return estado; }
  public void setEstado(String estado) { this.estado = estado; }

  public double getTotal() { return total; }
  public void setTotal(double total) { this.total = total; }

  // public boolean isFavorito() { return favorito; } // Eliminado
  // public void setFavorito(boolean favorito) { this.favorito = favorito; } // Eliminado

  public List<ItemPedido> getItems() { return items; }
  public void setItems(List<ItemPedido> items) { this.items = items; }

  public void agregarItem(ItemPedido item) {
    items.add(item);
    total += item.getSubtotal();
  }

  public void eliminarItem(int index) {
    if (index >= 0 && index < items.size()) {
      ItemPedido item = items.remove(index);
      total -= item.getSubtotal();
    }
  }

  @Override
  public String toString() {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    return "Pedido #" + id + " - " + clienteNombre + " - " + fecha.format(formatter) + " - S/" + total + " - " + estado;
  }

  public String toStringDetallado() {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    StringBuilder sb = new StringBuilder();
    sb.append("Pedido #").append(id).append("\n");
    sb.append("Cliente: ").append(clienteNombre).append("\n");
    sb.append("Mozo: ").append(mozoNombre).append("\n");
    sb.append("Fecha: ").append(fecha.format(formatter)).append("\n");
    sb.append("Estado: ").append(estado).append("\n");
  // sb.append("Favorito: ").append(favorito ? "Sí" : "No").append("\n"); // Eliminado
    sb.append("Items:\n");

    for (ItemPedido item : items) {
      sb.append("  - ").append(item.toString()).append("\n");
    }

    sb.append("Total: S/").append(total);
    return sb.toString();
  }
}
