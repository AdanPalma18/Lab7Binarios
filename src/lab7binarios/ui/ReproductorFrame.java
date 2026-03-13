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
import lab7binarios.logic.Archivo;
import lab7binarios.logic.ReproductorUniversal;
import lab7binarios.model.Cancion;
public class ReproductorFrame extends JFrame {

    private DefaultListModel<Cancion> modeloLista = new DefaultListModel<>();
    private JList<Cancion> listaCanciones = new JList<>(modeloLista);

    private JLabel imagenAlbum = new JLabel();
    private JLabel info = new JLabel("Selecciona una canción");

    private JButton play = new JButton("Play");
    private JButton pause = new JButton("Pause");
    private JButton stop = new JButton("Stop");

    private JButton add = new JButton("Add");
    private JButton remove = new JButton("Remove");

    private ArrayList<Cancion> playlist = new ArrayList<>();

    private ReproductorUniversal reproductor = new ReproductorUniversal();
    private Archivo archivo;

    public ReproductorFrame() throws Exception {

        archivo = new Archivo("canciones.dat");

        setTitle("Reproductor");
        setSize(900,500);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        listaCanciones.setBackground(Color.black);
        listaCanciones.setForeground(Color.green);

        JScrollPane scroll = new JScrollPane(listaCanciones);

        add(scroll, BorderLayout.WEST);

        JPanel centro = new JPanel(new BorderLayout());

        imagenAlbum.setHorizontalAlignment(JLabel.CENTER);

        centro.add(imagenAlbum, BorderLayout.CENTER);
        centro.add(info, BorderLayout.SOUTH);

        add(centro, BorderLayout.CENTER);

        JPanel controles = new JPanel();

        controles.add(play);
        controles.add(pause);
        controles.add(stop);
        controles.add(add);
        controles.add(remove);

        add(controles, BorderLayout.SOUTH);

        cargarCanciones();

        eventos();

        setVisible(true);
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

            Cancion c = listaCanciones.getSelectedValue();

            if(c != null){

                info.setText(c.getNombre()+" - "+c.getArtista());

                ImageIcon img = new ImageIcon(c.getRutaImagen());
                Image imagenEscalada = img.getImage().getScaledInstance(250,250,Image.SCALE_SMOOTH);
                imagenAlbum.setIcon(new ImageIcon(imagenEscalada));
            }
        });

        play.addActionListener(e -> {

            try {

                Cancion c = listaCanciones.getSelectedValue();

                if(c != null) {
                    reproductor.play(c.getRutaAudio());
                }

            } catch(Exception ex){
                JOptionPane.showMessageDialog(this, 
                    "Error al reproducir: " + ex.getMessage(), 
                    "Error de reproducción", 
                    JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        });

        pause.addActionListener(e -> {
            if(reproductor.isPlaying()) {
                reproductor.pause();
                pause.setText("Resume");
            } else if(reproductor.isPaused()) {
                reproductor.resume();
                pause.setText("Pause");
            }
        });

        stop.addActionListener(e -> {
            reproductor.stop();
            pause.setText("Pause");
        });

        add.addActionListener(e -> agregarCancion());

        remove.addActionListener(e -> eliminarCancion());
    }

    private void agregarCancion(){

        try{

            // Crear panel de formulario
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
            JOptionPane.showMessageDialog(this, "Error al agregar canción: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void eliminarCancion(){

        int index = listaCanciones.getSelectedIndex();

        if(index != -1){

            reproductor.stop();

            playlist.remove(index);
            modeloLista.remove(index);
        }
    }
}
