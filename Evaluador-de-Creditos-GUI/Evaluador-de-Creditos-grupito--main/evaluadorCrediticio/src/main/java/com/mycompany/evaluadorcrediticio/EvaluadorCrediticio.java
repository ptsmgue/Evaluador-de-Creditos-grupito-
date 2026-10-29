package com.mycompany.evaluadorcrediticio;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

/**
 * Punto de entrada. Ya no hay menú por consola: se abre la ventana de login y,
 * desde ahí, el resto de la interfaz gráfica.
 *
 * @author Miguel
 */
public class EvaluadorCrediticio {

    private static final Logger logger = Logger.getLogger(EvaluadorCrediticio.class.getName());

    public static void main(String[] args) {
        // Lee conexion.txt una sola vez; si falla, el error se informa recién al usar la BD.
        ConexionBDD conexion = new ConexionBDD();

        SwingUtilities.invokeLater(() -> {
            instalarNimbus();
            new VentanaLogin(conexion).setVisible(true);
        });
    }

    private static void instalarNimbus() {
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ReflectiveOperationException | UnsupportedLookAndFeelException ex) {
            logger.log(Level.WARNING, "No se pudo aplicar Nimbus, se usa el aspecto por defecto", ex);
        }
    }
}
