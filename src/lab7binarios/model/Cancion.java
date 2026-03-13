/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package lab7binarios.model;

/**
 *
 * @author palma
 */
public class Cancion {

    private String nombre;
    private String artista;
    private String duracion;
    private String genero;
    private String rutaImagen;
    private String rutaAudio;

    public Cancion(String nombre, String artista, String duracion,
                   String genero, String rutaImagen, String rutaAudio) {
        this.nombre = nombre;
        this.artista = artista;
        this.duracion = duracion;
        this.genero = genero;
        this.rutaImagen = rutaImagen;
        this.rutaAudio = rutaAudio;
    }

    public String getNombre() { return nombre; }
    public String getArtista() { return artista; }
    public String getDuracion() { return duracion; }
    public String getGenero() { return genero; }
    public String getRutaImagen() { return rutaImagen; }
    public String getRutaAudio() { return rutaAudio; }

    @Override
    public String toString() {
        return nombre + " - " + artista;
    }
}