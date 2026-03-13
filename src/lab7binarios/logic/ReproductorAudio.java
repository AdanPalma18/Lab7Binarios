/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package lab7binarios.logic;

/**
 *
 * @author palma
 */
import java.io.File;
import javax.sound.sampled.*;

public class ReproductorAudio {

    private Clip clip;
    private Long posicion;

    public void play(String ruta) throws Exception {

        File file = new File(ruta);
        AudioInputStream audio = AudioSystem.getAudioInputStream(file);

        clip = AudioSystem.getClip();
        clip.open(audio);
        clip.start();
    }

    public void pause() {

        if (clip != null) {
            posicion = clip.getMicrosecondPosition();
            clip.stop();
        }
    }

    public void resume() {

        if (clip != null) {
            clip.setMicrosecondPosition(posicion);
            clip.start();
        }
    }

    public void stop() {

        if (clip != null) {
            clip.stop();
            clip.setFramePosition(0);
        }
    }
}