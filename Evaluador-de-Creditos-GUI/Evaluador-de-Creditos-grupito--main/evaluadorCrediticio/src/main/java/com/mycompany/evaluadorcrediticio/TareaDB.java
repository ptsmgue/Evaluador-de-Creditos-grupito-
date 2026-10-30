package com.mycompany.evaluadorcrediticio;

import java.awt.Component;
import java.awt.Cursor;
import java.sql.SQLException;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.SwingWorker;

/**
 * Ejecuta una operación (normalmente contra la BD) fuera del hilo de la
 * interfaz para que la ventana no se congele, y vuelve al hilo de la
 * interfaz para mostrar el resultado o el error.
 */
final class TareaDB {

    private TareaDB() {
    }

    /**
     * @param padre    ventana sobre la que se muestran los errores
     * @param trabajo  operación a ejecutar en segundo plano
     * @param alExito  se ejecuta en el hilo de la interfaz con el valor devuelto
     * @param alFallar se ejecuta en el hilo de la interfaz con el mensaje de error;
     *                 si es null se muestra un cuadro de diálogo
     * @param barra    barra de progreso indeterminada que se muestra mientras corre
     *                 la operación y se oculta al terminar; null la ignora
     * @param bloquear controles que se deshabilitan mientras la operación corre
     */
    static <T> void ejecutar(Component padre, Callable<T> trabajo, Consumer<T> alExito,
            Consumer<String> alFallar, JProgressBar barra, JComponent... bloquear) {
        for (JComponent c : bloquear) {
            c.setEnabled(false);
        }
        if (barra != null) {
            barra.setIndeterminate(true);
            barra.setVisible(true);
        }
        padre.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

        new SwingWorker<T, Void>() {
            @Override
            protected T doInBackground() throws Exception {
                return trabajo.call();
            }

            @Override
            protected void done() {
                for (JComponent c : bloquear) {
                    c.setEnabled(true);
                }
                if (barra != null) {
                    barra.setIndeterminate(false);
                    barra.setVisible(false);
                }
                padre.setCursor(Cursor.getDefaultCursor());

                T valor;
                try {
                    valor = get();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                } catch (ExecutionException e) {
                    informar(padre, alFallar, e.getCause());
                    return;
                }
                alExito.accept(valor);
            }
        }.execute();
    }

    /** Variante sin barra de progreso, para ventanas que no la necesitan. */
    static <T> void ejecutar(Component padre, Callable<T> trabajo, Consumer<T> alExito,
            Consumer<String> alFallar, JComponent... bloquear) {
        ejecutar(padre, trabajo, alExito, alFallar, null, bloquear);
    }

    private static void informar(Component padre, Consumer<String> alFallar, Throwable causa) {
        boolean datosInvalidos = causa instanceof IllegalArgumentException;
        String mensaje;
        if (datosInvalidos) {
            mensaje = causa.getMessage();
        } else if (causa instanceof SQLException) {
            mensaje = "Error de base de datos: " + causa.getMessage();
        } else {
            mensaje = "Error inesperado: " + causa;
        }
        if (alFallar != null) {
            alFallar.accept(mensaje);
        } else {
            JOptionPane.showMessageDialog(padre, mensaje,
                    datosInvalidos ? "Datos inválidos" : "Error",
                    datosInvalidos ? JOptionPane.WARNING_MESSAGE : JOptionPane.ERROR_MESSAGE);
        }
    }
}
