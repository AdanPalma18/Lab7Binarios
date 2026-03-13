/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package lab7binarios.logic;

/**
 *
 * @author palma
 */
import java.io.RandomAccessFile;
import java.util.ArrayList;
import lab7binarios.model.Cancion;

public class Archivo {

    private RandomAccessFile archivo;

    public Archivo(String ruta) throws Exception {
        archivo = new RandomAccessFile(ruta, "rw");
    }

    public void agregarCancion(Cancion c) throws Exception {

        archivo.seek(archivo.length());

        archivo.writeUTF(c.getNombre());
        archivo.writeUTF(c.getArtista());
        archivo.writeUTF(c.getDuracion());
        archivo.writeUTF(c.getGenero());
        archivo.writeUTF(c.getRutaImagen());
        archivo.writeUTF(c.getRutaAudio());
    }

    public ArrayList<Cancion> leerCanciones() throws Exception {

        ArrayList<Cancion> lista = new ArrayList<>();

        archivo.seek(0);

        while (archivo.getFilePointer() < archivo.length()) {

            String nombre = archivo.readUTF();
            String artista = archivo.readUTF();
            String duracion = archivo.readUTF();
            String genero = archivo.readUTF();
            String imagen = archivo.readUTF();
            String audio = archivo.readUTF();

            lista.add(new Cancion(nombre, artista, duracion, genero, imagen, audio));
        }

        return lista;
    }
    
    public void reescribirArchivo(ArrayList<Cancion> canciones) throws Exception {
        // Limpiar el archivo
        archivo.setLength(0);
        archivo.seek(0);
        
        // Escribir todas las canciones
        for(Cancion c : canciones) {
            archivo.writeUTF(c.getNombre());
            archivo.writeUTF(c.getArtista());
            archivo.writeUTF(c.getDuracion());
            archivo.writeUTF(c.getGenero());
            archivo.writeUTF(c.getRutaImagen());
            archivo.writeUTF(c.getRutaAudio());
        }
    }
}