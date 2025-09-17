public class Cafeteria {
  public static void main(String[] args) {
    BD.DatabaseConfig.initializeDatabase();
    javax.swing.SwingUtilities.invokeLater(() -> {
      String[] opciones = {"Mozo", "Chef"};
      int sel = javax.swing.JOptionPane.showOptionDialog(
          null,
          "Elija su rol",
          "Seleccionar",
          javax.swing.JOptionPane.DEFAULT_OPTION,
          javax.swing.JOptionPane.QUESTION_MESSAGE,
          null,
          opciones,
          opciones[0]
      );
      ui.AppFrame.Role rol = (sel == 1) ? ui.AppFrame.Role.CHEF : ui.AppFrame.Role.MOZO;
      new ui.AppFrame(rol).setVisible(true);
    });
  }
}
