/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package lab7binarios.ui;

/**
 *
 * @author palma
 */
import java.awt.*;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import lab7binarios.logic.Archivo;
import lab7binarios.logic.ReproductorUniversal;
import lab7binarios.model.Cancion;

public class ReproductorFrame extends JFrame {

    private DefaultListModel<Cancion> modeloLista = new DefaultListModel<>();
    private JList<Cancion> listaCanciones = new JList<>(modeloLista);

    private JLabel imagenAlbum = new JLabel();
    private JLabel lblTitulo = new JLabel("Título de la canción", SwingConstants.CENTER);
    private JLabel lblArtista = new JLabel("Artista", SwingConstants.CENTER);
    private JLabel lblTiempoActual = new JLabel("0:00");
    private JLabel lblTiempoTotal = new JLabel("0:00");
    
    private JProgressBar progressBar = new JProgressBar(0, 100);

    private JButton btnPlayPause = new JButton();
    private JButton btnStop = new JButton();
    private JButton btnAdd = new JButton();
    private JButton btnRemove = new JButton();

    private ArrayList<Cancion> playlist = new ArrayList<>();

    private ReproductorUniversal reproductor = new ReproductorUniversal();
    private Archivo archivo;
    private Timer progressTimer;
    private boolean isPlaying = false;
    
    // Iconos
    private ImageIcon iconPlay;
    private ImageIcon iconPause;
    private ImageIcon iconStop;
    private ImageIcon iconAdd;
    private ImageIcon iconRemove;
    private ImageIcon iconSpotify;

    public ReproductorFrame() throws Exception {

        archivo = new Archivo("canciones.dat");
        
        cargarIconos();
        configurarVentana();
        configurarComponentes();
        cargarCanciones();
        eventos();

        setVisible(true);
    }
    
    private void cargarIconos() {
        iconPlay = cargarIcono("play.png");
        iconPause = cargarIcono("pause.png");
        iconStop = cargarIcono("stop.png");
        iconAdd = cargarIcono("add.png");
        iconRemove = cargarIcono("remove.png");
        iconSpotify = cargarIcono("spotify.png");
        
        btnPlayPause.setIcon(iconPlay);
        btnStop.setIcon(iconStop);
        btnAdd.setIcon(iconAdd);
        btnAdd.setText("Agregar");
        btnRemove.setIcon(iconRemove);
        btnRemove.setText("Eliminar");
    }
    
    private ImageIcon cargarIcono(String nombre) {
        try {
            String ruta = "src/resources/icons/" + nombre;
            java.io.File file = new java.io.File(ruta);
            if (file.exists()) {
                ImageIcon icon = new ImageIcon(ruta);
                Image img = icon.getImage().getScaledInstance(32, 32, Image.SCALE_SMOOTH);
                return new ImageIcon(img);
            }
        } catch (Exception e) {
            System.err.println("No se pudo cargar el icono: " + nombre);
        }
        return null;
    }

    private void configurarVentana() {
        setTitle("Spotify - Reproductor de Música");
        setSize(1200, 700);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(new Color(18, 18, 18));
    }

    private void configurarComponentes() {
        // Panel izquierdo - Playlist
        JPanel panelIzquierdo = crearPanelPlaylist();
        add(panelIzquierdo, BorderLayout.WEST);

        // Panel central - Reproductor
        JPanel panelCentro = crearPanelReproductor();
        add(panelCentro, BorderLayout.CENTER);

        // Panel inferior - Controles
        JPanel panelControles = crearPanelControles();
        add(panelControles, BorderLayout.SOUTH);
    }

    private JPanel crearPanelPlaylist() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBackground(new Color(30, 30, 30));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        panel.setPreferredSize(new Dimension(350, 0));

        JLabel titulo = new JLabel("Tu Biblioteca", SwingConstants.CENTER);
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titulo.setForeground(Color.WHITE);
        titulo.setBorder(new EmptyBorder(0, 0, 10, 0));
        
        // Agregar icono de Spotify si existe
        if (iconSpotify != null) {
            titulo.setIcon(iconSpotify);
            titulo.setHorizontalTextPosition(SwingConstants.RIGHT);
            titulo.setIconTextGap(10);
        }

        listaCanciones.setBackground(new Color(18, 18, 18));
        listaCanciones.setForeground(new Color(200, 200, 200));
        listaCanciones.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        listaCanciones.setSelectionBackground(new Color(30, 215, 96));
        listaCanciones.setSelectionForeground(Color.WHITE);
        listaCanciones.setBorder(new EmptyBorder(5, 5, 5, 5));
        listaCanciones.setFixedCellHeight(60);
        
        // Renderer personalizado para mostrar canciones con estilo Spotify
        listaCanciones.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, 
                    int index, boolean isSelected, boolean cellHasFocus) {
                
                JPanel panel = new JPanel(new BorderLayout(10, 5));
                panel.setBorder(new EmptyBorder(8, 10, 8, 10));
                
                if (isSelected) {
                    panel.setBackground(new Color(40, 40, 40));
                } else {
                    panel.setBackground(new Color(18, 18, 18));
                }
                
                Cancion c = (Cancion) value;
                
                // Miniatura de la imagen
                JLabel lblImagen = new JLabel();
                try {
                    ImageIcon img = new ImageIcon(c.getRutaImagen());
                    Image imagenEscalada = img.getImage().getScaledInstance(45, 45, Image.SCALE_SMOOTH);
                    lblImagen.setIcon(new ImageIcon(imagenEscalada));
                } catch (Exception e) {
                    // Sin icono fallback
                }
                lblImagen.setPreferredSize(new Dimension(45, 45));
                
                // Info de la canción
                JPanel panelInfo = new JPanel();
                panelInfo.setLayout(new BoxLayout(panelInfo, BoxLayout.Y_AXIS));
                panelInfo.setOpaque(false);
                
                JLabel lblNombre = new JLabel(c.getNombre());
                lblNombre.setFont(new Font("Segoe UI", Font.BOLD, 13));
                lblNombre.setForeground(Color.WHITE);
                
                JLabel lblArtista = new JLabel(c.getArtista() + " • " + c.getDuracion());
                lblArtista.setFont(new Font("Segoe UI", Font.PLAIN, 11));
                lblArtista.setForeground(new Color(180, 180, 180));
                
                panelInfo.add(lblNombre);
                panelInfo.add(Box.createVerticalStrut(3));
                panelInfo.add(lblArtista);
                
                panel.add(lblImagen, BorderLayout.WEST);
                panel.add(panelInfo, BorderLayout.CENTER);
                
                return panel;
            }
        });

        JScrollPane scroll = new JScrollPane(listaCanciones);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(40, 40, 40)));
        scroll.getViewport().setBackground(new Color(18, 18, 18));

        JPanel panelBotones = new JPanel(new GridLayout(1, 2, 5, 0));
        panelBotones.setBackground(new Color(30, 30, 30));
        panelBotones.setBorder(new EmptyBorder(10, 0, 0, 0));

        estilizarBoton(btnAdd, new Color(30, 215, 96));
        estilizarBoton(btnRemove, new Color(220, 53, 69));

        panelBotones.add(btnAdd);
        panelBotones.add(btnRemove);

        panel.add(titulo, BorderLayout.NORTH);
        panel.add(scroll, BorderLayout.CENTER);
        panel.add(panelBotones, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel crearPanelReproductor() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(new Color(18, 18, 18));
        panel.setBorder(new EmptyBorder(30, 30, 30, 30));

        // Imagen del álbum con sombra
        JPanel panelImagen = new JPanel(new GridBagLayout());
        panelImagen.setBackground(new Color(18, 18, 18));
        
        imagenAlbum.setHorizontalAlignment(JLabel.CENTER);
        imagenAlbum.setPreferredSize(new Dimension(400, 400));
        imagenAlbum.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(40, 40, 40), 3),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        imagenAlbum.setOpaque(true);
        imagenAlbum.setBackground(new Color(40, 40, 40));
        
        panelImagen.add(imagenAlbum);

        // Info de la canción
        JPanel panelInfo = new JPanel();
        panelInfo.setLayout(new BoxLayout(panelInfo, BoxLayout.Y_AXIS));
        panelInfo.setBackground(new Color(18, 18, 18));
        panelInfo.setBorder(new EmptyBorder(25, 0, 0, 0));

        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblTitulo.setForeground(Color.WHITE);
        lblTitulo.setAlignmentX(Component.CENTER_ALIGNMENT);

        lblArtista.setFont(new Font("Segoe UI", Font.PLAIN, 20));
        lblArtista.setForeground(new Color(180, 180, 180));
        lblArtista.setAlignmentX(Component.CENTER_ALIGNMENT);

        panelInfo.add(lblTitulo);
        panelInfo.add(Box.createVerticalStrut(8));
        panelInfo.add(lblArtista);

        panel.add(panelImagen, BorderLayout.CENTER);
        panel.add(panelInfo, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel crearPanelControles() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(new Color(30, 30, 30));
        panel.setBorder(new EmptyBorder(15, 20, 15, 20));

        // Progress bar con tiempos
        JPanel panelProgress = new JPanel(new BorderLayout(10, 5));
        panelProgress.setBackground(new Color(30, 30, 30));

        progressBar.setValue(0);
        progressBar.setStringPainted(false);
        progressBar.setBackground(new Color(50, 50, 50));
        progressBar.setForeground(new Color(30, 215, 96));
        progressBar.setBorder(BorderFactory.createLineBorder(new Color(60, 60, 60)));
        progressBar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Hacer el progressBar clickeable para saltar en la canción
        progressBar.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (isPlaying || reproductor.isPaused()) {
                    int mouseX = evt.getX();
                    int width = progressBar.getWidth();
                    int maxValue = progressBar.getMaximum();
                    int newValue = (int) ((double) mouseX / width * maxValue);
                    saltarATiempo(newValue);
                }
            }
        });

        lblTiempoActual.setForeground(Color.WHITE);
        lblTiempoTotal.setForeground(Color.WHITE);

        panelProgress.add(lblTiempoActual, BorderLayout.WEST);
        panelProgress.add(progressBar, BorderLayout.CENTER);
        panelProgress.add(lblTiempoTotal, BorderLayout.EAST);

        // Botones de control
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        panelBotones.setBackground(new Color(30, 30, 30));

        btnPlayPause.setFont(new Font("Segoe UI", Font.BOLD, 20));
        btnStop.setText("⏹");
        btnStop.setFont(new Font("Segoe UI", Font.BOLD, 20));

        estilizarBotonControl(btnPlayPause);
        estilizarBotonControl(btnStop);

        panelBotones.add(btnPlayPause);
        panelBotones.add(btnStop);

        panel.add(panelProgress, BorderLayout.NORTH);
        panel.add(panelBotones, BorderLayout.CENTER);

        return panel;
    }

    private void estilizarBoton(JButton btn, Color color) {
        btn.setBackground(color);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    private void estilizarBotonControl(JButton btn) {
        btn.setPreferredSize(new Dimension(60, 60));
        btn.setBackground(new Color(50, 50, 50));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createLineBorder(new Color(80, 80, 80), 2));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    private void cargarCanciones() {
        try {
            playlist = archivo.leerCanciones();
            for(Cancion c : playlist)
                modeloLista.addElement(c);
        } catch(Exception e) {}
    }

    private void eventos() {
        listaCanciones.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                Cancion c = listaCanciones.getSelectedValue();
                if(c != null){
                    actualizarInfoCancion(c);
                }
            }
        });

        btnPlayPause.addActionListener(e -> togglePlayPause());
        btnStop.addActionListener(e -> detener());
        btnAdd.addActionListener(e -> agregarCancion());
        btnRemove.addActionListener(e -> eliminarCancion());
        
        // Doble click en la lista para reproducir
        listaCanciones.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    reproducir();
                }
            }
        });
    }

    private void actualizarInfoCancion(Cancion c) {
        lblTitulo.setText(c.getNombre());
        lblArtista.setText(c.getArtista());
        lblTiempoTotal.setText(c.getDuracion());

        ImageIcon img = new ImageIcon(c.getRutaImagen());
        Image imagenEscalada = img.getImage().getScaledInstance(400, 400, Image.SCALE_SMOOTH);
        imagenAlbum.setIcon(new ImageIcon(imagenEscalada));
    }

    private void togglePlayPause() {
        if (isPlaying) {
            // Pausar
            reproductor.pause();
            if(progressTimer != null) progressTimer.stop();
            btnPlayPause.setIcon(iconPlay);
            isPlaying = false;
        } else if (reproductor.isPaused()) {
            // Resume
            reproductor.resume();
            if(progressTimer != null) progressTimer.start();
            btnPlayPause.setIcon(iconPause);
            isPlaying = true;
        } else {
            // Play desde el inicio
            reproducir();
        }
    }

    private void reproducir() {
        try {
            Cancion c = listaCanciones.getSelectedValue();
            if(c != null) {
                reproductor.play(c.getRutaAudio());
                iniciarProgressBar(c.getDuracion());
                btnPlayPause.setIcon(iconPause);
                isPlaying = true;
            }
        } catch(Exception ex){
            JOptionPane.showMessageDialog(this, 
                "Error al reproducir: " + ex.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void detener() {
        reproductor.stop();
        if(progressTimer != null) {
            progressTimer.stop();
        }
        progressBar.setValue(0);
        lblTiempoActual.setText("0:00");
        btnPlayPause.setIcon(iconPlay);
        isPlaying = false;
    }

    private void iniciarProgressBar(String duracionStr) {
        if(progressTimer != null) {
            progressTimer.stop();
        }

        // Convertir duración a segundos
        String[] partes = duracionStr.replace(" (aprox)", "").split(":");
        int duracionTotal = Integer.parseInt(partes[0]) * 60 + Integer.parseInt(partes[1]);

        progressBar.setMaximum(duracionTotal);
        progressBar.setValue(0);

        progressTimer = new Timer(1000, e -> {
            int valor = progressBar.getValue() + 1;
            if(valor <= duracionTotal) {
                progressBar.setValue(valor);
                int min = valor / 60;
                int seg = valor % 60;
                lblTiempoActual.setText(String.format("%d:%02d", min, seg));
            } else {
                progressTimer.stop();
            }
        });
        progressTimer.start();
    }

    private void agregarCancion(){
        try{
            CancionFormPanel formPanel = new CancionFormPanel();

            int result = JOptionPane.showConfirmDialog(this, formPanel, 
                    "Agregar nueva canción", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

            if(result != JOptionPane.OK_OPTION) return;

            if(!formPanel.validarCampos()) {
                JOptionPane.showMessageDialog(this, "Todos los campos son obligatorios", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Cancion c = new Cancion(
                    formPanel.getNombre(),
                    formPanel.getArtista(),
                    formPanel.getDuracion(),
                    formPanel.getGenero(),
                    formPanel.getArchivoImagen().getAbsolutePath(),
                    formPanel.getArchivoAudio().getAbsolutePath()
            );

            playlist.add(c);
            modeloLista.addElement(c);
            archivo.agregarCancion(c);
            
            JOptionPane.showMessageDialog(this, "Canción agregada exitosamente");

        }catch(Exception ex){
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void eliminarCancion(){
        int index = listaCanciones.getSelectedIndex();

        if(index != -1){
            reproductor.stop();
            if(progressTimer != null) progressTimer.stop();
            
            playlist.remove(index);
            modeloLista.remove(index);
            
            // Reescribir el archivo sin la canción eliminada
            try {
                archivo.reescribirArchivo(playlist);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, 
                    "Error al actualizar el archivo: " + ex.getMessage(), 
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
            
            // Limpiar selección y UI
            listaCanciones.clearSelection();
            lblTitulo.setText("Título de la canción");
            lblArtista.setText("Artista");
            imagenAlbum.setIcon(null);
            progressBar.setValue(0);
            lblTiempoActual.setText("0:00");
            lblTiempoTotal.setText("0:00");
        }
    }
    
    private void saltarATiempo(int segundos) {
        try {
            Cancion c = listaCanciones.getSelectedValue();
            if (c != null) {
                // Usar el método seek del reproductor
                reproductor.seek(segundos);
                
                // Actualizar la barra de progreso
                progressBar.setValue(segundos);
                int min = segundos / 60;
                int seg = segundos % 60;
                lblTiempoActual.setText(String.format("%d:%02d", min, seg));
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, 
                "Error al saltar en la canción: " + ex.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
