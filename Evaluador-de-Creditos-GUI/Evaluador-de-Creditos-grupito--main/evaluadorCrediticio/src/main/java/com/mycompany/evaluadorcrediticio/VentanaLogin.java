package com.mycompany.evaluadorcrediticio;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.Arrays;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

/**
 * Ventana de inicio de sesión. Valida contra {@link Usuario} y, si las
 * credenciales son correctas, abre la ventana principal.
 */
public class VentanaLogin extends JFrame {

    private final ConexionBDD conexion;

    private final JTextField txtUsuario = new JTextField(16);
    private final JPasswordField txtContrasena = new JPasswordField(16);
    private final JButton btnIngresar = new JButton("Ingresar");
    private final JLabel lblMensaje = new JLabel(" ", SwingConstants.CENTER);

    public VentanaLogin(ConexionBDD conexion) {
        super("Evaluador crediticio - Iniciar sesión");
        this.conexion = conexion;
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        construirInterfaz();
        btnIngresar.addActionListener(e -> ingresar());
        getRootPane().setDefaultButton(btnIngresar); // Enter = Ingresar
        pack();
        setResizable(false);
        setLocationRelativeTo(null);
    }

    private void construirInterfaz() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(new EmptyBorder(30, 40, 30, 40));
        setContentPane(panel);

        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(8, 8, 8, 8);

        c.gridx = 0;
        c.gridy = 0;
        c.anchor = GridBagConstraints.LINE_END;
        panel.add(new JLabel("Nombre de usuario"), c);
        c.gridx = 1;
        c.anchor = GridBagConstraints.LINE_START;
        c.fill = GridBagConstraints.HORIZONTAL;
        panel.add(txtUsuario, c);

        c.gridx = 0;
        c.gridy = 1;
        c.fill = GridBagConstraints.NONE;
        c.anchor = GridBagConstraints.LINE_END;
        panel.add(new JLabel("Contraseña"), c);
        c.gridx = 1;
        c.anchor = GridBagConstraints.LINE_START;
        c.fill = GridBagConstraints.HORIZONTAL;
        panel.add(txtContrasena, c);

        c.gridx = 0;
        c.gridy = 2;
        c.gridwidth = 2;
        c.fill = GridBagConstraints.NONE;
        c.anchor = GridBagConstraints.CENTER;
        c.insets = new Insets(20, 8, 8, 8);
        btnIngresar.setPreferredSize(new java.awt.Dimension(129, 36));
        panel.add(btnIngresar, c);

        c.gridy = 3;
        c.insets = new Insets(4, 8, 0, 8);
        lblMensaje.setForeground(new Color(0xB00020));
        panel.add(lblMensaje, c);
    }

    private void ingresar() {
        String usuario = txtUsuario.getText().trim();
        char[] clave = txtContrasena.getPassword();
        try {
            if (usuario.isEmpty() || clave.length == 0) {
                lblMensaje.setText("Ingrese usuario y contraseña.");
                return;
            }
            if (Usuario.iniciarSesion(usuario, new String(clave))) {
                new VentanaPrincipal(conexion).setVisible(true);
                dispose();
            } else {
                lblMensaje.setText("Usuario o contraseña incorrectos.");
                txtContrasena.setText("");
                txtContrasena.requestFocusInWindow();
            }
        } finally {
            Arrays.fill(clave, '\0'); // no dejar la contraseña en memoria más de lo necesario
        }
    }
}
