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
import java.io.File;
import javax.swing.*;

public class CancionFormPanel extends JPanel {
    
    private JTextField txtNombre;
    private JTextField txtArtista;
    private JTextField txtGenero;
    private JLabel lblImagenPreview;
    private JLabel lblArchivoAudio;
    private JLabel lblDuracion;
    
    private File archivoAudio;
    private File archivoImagen;
    
    public CancionFormPanel() {
        setLayout(new BorderLayout(10, 10));
        
        // Panel principal con los campos
        JPanel panelCampos = new JPanel(new GridLayout(6, 2, 10, 10));
        
        txtNombre = new JTextField(20);
        txtArtista = new JTextField(20);
        txtGenero = new JTextField(20);
        lblArchivoAudio = new JLabel("No seleccionado");
        lblDuracion = new JLabel("--:--");
        
        JButton btnSeleccionarAudio = new JButton("Seleccionar Audio");
        JButton btnSeleccionarImagen = new JButton("Seleccionar Imagen");
        
        panelCampos.add(new JLabel("Archivo de Audio:"));
        panelCampos.add(btnSeleccionarAudio);
        
        panelCampos.add(new JLabel("Audio seleccionado:"));
        panelCampos.add(lblArchivoAudio);
        
        panelCampos.add(new JLabel("Duración:"));
        panelCampos.add(lblDuracion);
        
        panelCampos.add(new JLabel("Nombre:"));
        panelCampos.add(txtNombre);
        
        panelCampos.add(new JLabel("Artista:"));
        panelCampos.add(txtArtista);
        
        panelCampos.add(new JLabel("Género:"));
        panelCampos.add(txtGenero);
        
        add(panelCampos, BorderLayout.CENTER);
        
        // Panel derecho para la imagen
        JPanel panelImagen = new JPanel(new BorderLayout(5, 5));
        panelImagen.setBorder(BorderFactory.createTitledBorder("Imagen del álbum"));
        
        lblImagenPreview = new JLabel("Sin imagen", SwingConstants.CENTER);
        lblImagenPreview.setPreferredSize(new Dimension(200, 200));
        lblImagenPreview.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        
        panelImagen.add(lblImagenPreview, BorderLayout.CENTER);
        panelImagen.add(btnSeleccionarImagen, BorderLayout.SOUTH);
        
        add(panelImagen, BorderLayout.EAST);
        
        // Eventos
        btnSeleccionarAudio.addActionListener(e -> seleccionarAudio());
        btnSeleccionarImagen.addActionListener(e -> seleccionarImagen());
    }
    
    private void seleccionarAudio() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Seleccionar archivo de audio");
        chooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
                "Archivos de audio", "mp3", "wav"));
        
        if(chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            archivoAudio = chooser.getSelectedFile();
            lblArchivoAudio.setText(archivoAudio.getName());
            
            // Calcular duración
            String duracion = calcularDuracion(archivoAudio);
            lblDuracion.setText(duracion);
        }
    }
    
    private void seleccionarImagen() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Seleccionar imagen");
        chooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
                "Imágenes", "jpg", "jpeg", "png", "gif"));
        
        if(chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            archivoImagen = chooser.getSelectedFile();
            
            // Mostrar preview
            ImageIcon icon = new ImageIcon(archivoImagen.getAbsolutePath());
            Image img = icon.getImage().getScaledInstance(200, 200, Image.SCALE_SMOOTH);
            lblImagenPreview.setIcon(new ImageIcon(img));
            lblImagenPreview.setText("");
        }
    }
    
    private String calcularDuracion(File audioFile) {
        try {
            // Intentar con AudioSystem (funciona para WAV)
            javax.sound.sampled.AudioInputStream audioInputStream = 
                javax.sound.sampled.AudioSystem.getAudioInputStream(audioFile);
            javax.sound.sampled.AudioFormat format = audioInputStream.getFormat();
            long frames = audioInputStream.getFrameLength();
            double durationInSeconds = (frames / format.getFrameRate());
            
            int minutes = (int) (durationInSeconds / 60);
            int seconds = (int) (durationInSeconds % 60);
            
            audioInputStream.close();
            
            return String.format("%d:%02d", minutes, seconds);
        } catch (Exception e) {
            // Para MP3, usar JavaFX Media
            if (audioFile.getName().toLowerCase().endsWith(".mp3")) {
                return calcularDuracionMP3(audioFile);
            }
            return calcularDuracionAproximada(audioFile);
        }
    }
    
    private String calcularDuracionMP3(File audioFile) {
        try {
            // Inicializar JavaFX si no está inicializado
            try {
                javafx.embed.swing.JFXPanel panel = new javafx.embed.swing.JFXPanel();
            } catch (Exception e) {
                // Ya está inicializado
            }
            
            final String[] resultado = {null};
            final Object lock = new Object();
            
            // Ejecutar en el hilo de JavaFX
            javafx.application.Platform.runLater(() -> {
                try {
                    javafx.scene.media.Media media = new javafx.scene.media.Media(audioFile.toURI().toString());
                    javafx.scene.media.MediaPlayer player = new javafx.scene.media.MediaPlayer(media);
                    
                    player.setOnReady(() -> {
                        synchronized (lock) {
                            javafx.util.Duration duration = media.getDuration();
                            if (duration != null && !duration.isUnknown() && !duration.isIndefinite()) {
                                double duracionSegundos = duration.toSeconds();
                                int minutos = (int) (duracionSegundos / 60);
                                int segundos = (int) (duracionSegundos % 60);
                                resultado[0] = String.format("%d:%02d", minutos, segundos);
                            }
                            player.dispose();
                            lock.notify();
                        }
                    });
                    
                    player.setOnError(() -> {
                        synchronized (lock) {
                            player.dispose();
                            lock.notify();
                        }
                    });
                } catch (Exception e) {
                    synchronized (lock) {
                        lock.notify();
                    }
                }
            });
            
            // Esperar hasta 5 segundos
            synchronized (lock) {
                lock.wait(5000);
            }
            
            if (resultado[0] != null) {
                return resultado[0];
            }
        } catch (Exception e) {
            System.err.println("Error calculando duración MP3: " + e.getMessage());
        }
        return calcularDuracionAproximada(audioFile);
    }
    
    private String calcularDuracionAproximada(File audioFile) {
        long fileSizeInBytes = audioFile.length();
        long fileSizeInKB = fileSizeInBytes / 1024;
        
        // Bitrate promedio MP3: 128 kbps = 16 KB/s
        int durationInSeconds = (int) (fileSizeInKB / 16);
        
        int minutes = durationInSeconds / 60;
        int seconds = durationInSeconds % 60;
        
        return String.format("%d:%02d (aprox)", minutes, seconds);
    }
    
    public String getNombre() {
        return txtNombre.getText().trim();
    }
    
    public String getArtista() {
        return txtArtista.getText().trim();
    }
    
    public String getGenero() {
        return txtGenero.getText().trim();
    }
    
    public String getDuracion() {
        return lblDuracion.getText();
    }
    
    public File getArchivoAudio() {
        return archivoAudio;
    }
    
    public File getArchivoImagen() {
        return archivoImagen;
    }
    
    public boolean validarCampos() {
        return archivoAudio != null && 
               archivoImagen != null &&
               !getNombre().isEmpty() && 
               !getArtista().isEmpty() && 
               !getGenero().isEmpty();
    }
}
