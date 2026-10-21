package com.mycompany.sorteogrupos;

import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.FlatLightLaf;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Random;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;

public class SorteoGrupos {

    private static final int TOTAL = 10;
    private static final int ALTO_TARJETA = 60;
    private static final int SALTO_CLIC_MS = 150;
    private static final Color ACENTO = new Color(99, 102, 241);
    private static final Color[] PALETA = {
        new Color(239, 68, 68), new Color(249, 115, 22), new Color(202, 138, 4),
        new Color(34, 197, 94), new Color(20, 184, 166), new Color(14, 165, 233),
        new Color(99, 102, 241), new Color(168, 85, 247), new Color(236, 72, 153),
        new Color(244, 63, 94)
    };

    private final Sorteador sorteador = new Sorteador(TOTAL);
    private final Random azar = new Random();
    private int turno = 0;
    private boolean oscuro = true;

    private JLabel lblGrupo;
    private JLabel lblQuedan;
    private JPanel panelTarjetas;
    private JButton btnSacar;
    private JButton btnReiniciar;
    private JButton btnTema;

    public static void main(String[] args) {
        FlatDarkLaf.setup();
        UIManager.put("Button.arc", 20);
        Sonido.precargar("/sonidos/clic.wav", "/sonidos/tick.wav", "/sonidos/ganador.wav");
        SwingUtilities.invokeLater(() -> new SorteoGrupos().construirVentana());
    }

    private void construirVentana() {
        JFrame ventana = new JFrame("Sorteo de Exposición");
        ventana.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        ventana.setSize(900, 600);
        ventana.setMinimumSize(new Dimension(700, 450));
        ventana.setLocationRelativeTo(null);

        JPanel raiz = new JPanel(new BorderLayout(20, 20));
        raiz.setBorder(new EmptyBorder(20, 20, 20, 20));
        ventana.setContentPane(raiz);

        JLabel titulo = new JLabel("Sorteo de exposición");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 28));

        btnTema = new JButton("Modo claro");
        btnTema.addActionListener(e -> cambiarTema());

        JPanel encabezado = new JPanel(new BorderLayout());
        encabezado.add(titulo, BorderLayout.WEST);
        encabezado.add(btnTema, BorderLayout.EAST);
        raiz.add(encabezado, BorderLayout.NORTH);

        JPanel centro = new JPanel(new BorderLayout());
        JLabel subtitulo = new JLabel("Le toca exponer a:", SwingConstants.CENTER);
        subtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 20));
        lblGrupo = new JLabel("?", SwingConstants.CENTER);
        lblGrupo.setFont(new Font("Segoe UI", Font.BOLD, 80));
        lblGrupo.setForeground(ACENTO);
        lblQuedan = new JLabel("Quedan " + TOTAL + " grupos", SwingConstants.CENTER);
        lblQuedan.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        centro.add(subtitulo, BorderLayout.NORTH);
        centro.add(lblGrupo, BorderLayout.CENTER);
        centro.add(lblQuedan, BorderLayout.SOUTH);
        raiz.add(centro, BorderLayout.CENTER);

        panelTarjetas = new JPanel();
        panelTarjetas.setLayout(new BoxLayout(panelTarjetas, BoxLayout.Y_AXIS));
        JScrollPane scroll = new JScrollPane(panelTarjetas);
        scroll.setPreferredSize(new Dimension(280, 0));
        scroll.setBorder(null);
        raiz.add(scroll, BorderLayout.EAST);

        btnSacar = new JButton("Sacar grupo");
        btnSacar.setFont(new Font("Segoe UI", Font.BOLD, 18));
        btnSacar.setBackground(ACENTO);
        btnSacar.setForeground(Color.WHITE);
        btnSacar.addActionListener(e -> sacarGrupo());
        btnSacar.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (btnSacar.isEnabled()) {
                    Sonido.reproducirDesde("/sonidos/clic.wav", SALTO_CLIC_MS);
                }
            }
        });

        btnReiniciar = new JButton("Reiniciar");
        btnReiniciar.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        btnReiniciar.addActionListener(e -> reiniciar());

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        botones.add(btnSacar);
        botones.add(btnReiniciar);
        raiz.add(botones, BorderLayout.SOUTH);

        ventana.getRootPane().setDefaultButton(btnSacar);
        ventana.setVisible(true);
    }

    private void sacarGrupo() {
        String resultado = sorteador.sacarSiguiente();
        if (resultado == null) {
            return;
        }
        btnSacar.setEnabled(false);
        btnReiniciar.setEnabled(false);

        Sonido.reproducir("/sonidos/tick.wav");

        int[] ticks = {0};
        Timer timer = new Timer(80, null);
        timer.addActionListener(e -> {
            ticks[0]++;
            if (ticks[0] < 20) {
                lblGrupo.setText("Grupo " + (azar.nextInt(TOTAL) + 1));
            } else {
                timer.stop();
                Sonido.detener("/sonidos/tick.wav");
                Sonido.reproducir("/sonidos/ganador.wav");
                turno++;
                Color color = PALETA[(turno - 1) % PALETA.length];
                lblGrupo.setText(resultado);
                lblGrupo.setForeground(color);
                agregarTarjeta(turno, resultado, color);
                actualizarContador();
                btnSacar.setEnabled(sorteador.hayDisponibles());
                btnReiniciar.setEnabled(true);
                if (!sorteador.hayDisponibles()) {
                    JOptionPane.showMessageDialog(null,
                            "Ya salieron los " + TOTAL + " grupos.",
                            "Sorteo terminado", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        });
        timer.start();
    }

    private void agregarTarjeta(int numTurno, String grupo, Color color) {
        Tarjeta tarjeta = new Tarjeta(color);
        tarjeta.setBorder(new EmptyBorder(0, 16, 0, 16));

        JLabel lblNumero = new JLabel(String.valueOf(numTurno));
        lblNumero.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblNumero.setForeground(Color.WHITE);

        JLabel lblNombre = new JLabel(grupo);
        lblNombre.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        lblNombre.setForeground(Color.WHITE);

        tarjeta.add(lblNumero, BorderLayout.WEST);
        tarjeta.add(lblNombre, BorderLayout.CENTER);

        panelTarjetas.add(tarjeta);
        panelTarjetas.add(Box.createVerticalStrut(8));

        int[] altura = {0};
        Timer aparicion = new Timer(15, null);
        aparicion.addActionListener(e -> {
            altura[0] += 6;
            if (altura[0] >= ALTO_TARJETA) {
                altura[0] = ALTO_TARJETA;
                aparicion.stop();
            }
            tarjeta.setPreferredSize(new Dimension(250, altura[0]));
            tarjeta.setMaximumSize(new Dimension(Integer.MAX_VALUE, altura[0]));
            panelTarjetas.revalidate();
            panelTarjetas.repaint();
            if (!aparicion.isRunning()) {
                SwingUtilities.invokeLater(()
                        -> tarjeta.scrollRectToVisible(new Rectangle(tarjeta.getSize())));
            }
        });
        aparicion.start();
    }

    private void actualizarContador() {
        int quedan = sorteador.getRestantes();
        if (quedan == 0) {
            lblQuedan.setText("Ya salieron todos los grupos");
        } else {
            lblQuedan.setText("Quedan " + quedan + (quedan == 1 ? " grupo" : " grupos"));
        }
    }

    private void reiniciar() {
        if (turno > 0) {
            int respuesta = JOptionPane.showConfirmDialog(null,
                    "¿Reiniciar el sorteo? Se borrará el orden actual.",
                    "Confirmar", JOptionPane.YES_NO_OPTION);
            if (respuesta != JOptionPane.YES_OPTION) {
                return;
            }
        }
        sorteador.reiniciar();
        turno = 0;
        panelTarjetas.removeAll();
        panelTarjetas.revalidate();
        panelTarjetas.repaint();
        lblGrupo.setText("?");
        lblGrupo.setForeground(ACENTO);
        btnSacar.setEnabled(true);
        actualizarContador();
    }

    private void cambiarTema() {
        oscuro = !oscuro;
        if (oscuro) {
            FlatDarkLaf.setup();
        } else {
            FlatLightLaf.setup();
        }
        FlatLaf.updateUI();
        btnTema.setText(oscuro ? "Modo claro" : "Modo oscuro");
    }

    private static class Tarjeta extends JPanel {

        private final Color color;

        Tarjeta(Color color) {
            super(new BorderLayout(15, 0));
            this.color = color;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 24, 24);
            g2.dispose();
            super.paintComponent(g);
        }
    }
}