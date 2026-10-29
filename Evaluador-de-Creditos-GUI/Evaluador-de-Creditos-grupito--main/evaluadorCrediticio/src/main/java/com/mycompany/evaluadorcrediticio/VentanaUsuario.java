package com.mycompany.evaluadorcrediticio;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.util.Arrays;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

/**
 * Gestión de usuarios: añadir, eliminar y listar. Todo pasa por {@link Usuario};
 * esta ventana solo toma datos, llama a la lógica y muestra lo que cambió.
 */
public class VentanaUsuario extends JFrame {

    private static final Color VERDE = new Color(0x1B7F3B);
    private static final Color ROJO = new Color(0xB00020);

    private final JTextField txtUsuario = new JTextField(16);
    private final JPasswordField txtContrasena = new JPasswordField(16);
    private final JButton btnAnadir = new JButton("Añadir");
    private final JButton btnEliminar = new JButton("Eliminar");
    private final JButton btnListar = new JButton("Listar");
    private final JLabel lblEstado = new JLabel(" ");

    private final DefaultTableModel modelo = new DefaultTableModel(new Object[] { "Usuario", "Estado" }, 0) {
        @Override
        public boolean isCellEditable(int fila, int columna) {
            return false;
        }
    };
    private final JTable tabla = new JTable(modelo);

    public VentanaUsuario() {
        super("Gestión de usuarios");
        // Es una ventana secundaria: cerrarla no debe cerrar todo el programa.
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        construirInterfaz();

        btnAnadir.addActionListener(e -> anadir());
        btnEliminar.addActionListener(e -> eliminar());
        btnListar.addActionListener(e -> listar());

        listar();
        pack();
        setLocationRelativeTo(null);
    }

    private void construirInterfaz() {
        JPanel raiz = new JPanel(new BorderLayout(12, 12));
        raiz.setBorder(new EmptyBorder(16, 16, 8, 16));
        setContentPane(raiz);

        // Formulario (arriba)
        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(5, 6, 5, 6);
        c.gridy = 0;
        c.gridx = 0;
        c.anchor = GridBagConstraints.LINE_END;
        form.add(new JLabel("Nombre de usuario"), c);
        c.gridx = 1;
        c.anchor = GridBagConstraints.LINE_START;
        form.add(txtUsuario, c);
        c.gridy = 1;
        c.gridx = 0;
        c.anchor = GridBagConstraints.LINE_END;
        form.add(new JLabel("Contraseña"), c);
        c.gridx = 1;
        c.anchor = GridBagConstraints.LINE_START;
        form.add(txtContrasena, c);
        raiz.add(form, BorderLayout.NORTH);

        // Tabla (centro)
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabla.setFillsViewportHeight(true);
        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setPreferredSize(new Dimension(380, 220));
        raiz.add(scroll, BorderLayout.CENTER);

        // Botones (derecha)
        JPanel botones = new JPanel(new GridLayout(3, 1, 0, 14));
        botones.add(btnAnadir);
        botones.add(btnEliminar);
        botones.add(btnListar);
        JPanel contenedorBotones = new JPanel(new BorderLayout());
        contenedorBotones.add(botones, BorderLayout.NORTH);
        raiz.add(contenedorBotones, BorderLayout.EAST);

        // Estado (abajo)
        lblEstado.setBorder(new EmptyBorder(4, 2, 4, 2));
        raiz.add(lblEstado, BorderLayout.SOUTH);
    }

    // ------------------------------------------------------------------ acciones

    private void anadir() {
        char[] clave = txtContrasena.getPassword();
        try {
            Usuario.registrarUsuario(txtUsuario.getText(), new String(clave));
            String nombre = txtUsuario.getText().trim();
            txtUsuario.setText("");
            txtContrasena.setText("");
            listar();
            estado("Usuario \"" + nombre + "\" añadido.", false);
        } catch (IllegalArgumentException ex) {
            estado(ex.getMessage(), true);
        } finally {
            Arrays.fill(clave, '\0');
        }
    }

    private void eliminar() {
        int fila = tabla.getSelectedRow();
        String nombre = fila >= 0 ? (String) modelo.getValueAt(fila, 0) : txtUsuario.getText().trim();
        if (nombre.isEmpty()) {
            estado("Seleccione un usuario de la tabla (o escriba su nombre) para eliminarlo.", true);
            return;
        }
        int r = JOptionPane.showConfirmDialog(this, "¿Eliminar al usuario \"" + nombre + "\"?",
                "Confirmar eliminación", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (r != JOptionPane.YES_OPTION) {
            return;
        }
        try {
            Usuario.eliminarUsuario(nombre);
            txtUsuario.setText("");
            listar();
            estado("Usuario \"" + nombre + "\" eliminado.", false);
        } catch (IllegalArgumentException ex) {
            estado(ex.getMessage(), true);
        }
    }

    /** Vuelve a cargar la tabla con los usuarios que hay en {@link Usuario}. */
    private void listar() {
        modelo.setRowCount(0);
        Usuario actual = Usuario.getUsuarioActual();
        for (Usuario u : Usuario.listarUsuarios()) {
            // Nunca se muestran las contraseñas.
            modelo.addRow(new Object[] { u.nombreUsuario, u == actual ? "Sesión actual" : "" });
        }
    }

    private void estado(String texto, boolean error) {
        lblEstado.setForeground(error ? ROJO : VERDE);
        lblEstado.setText(texto);
    }
}
