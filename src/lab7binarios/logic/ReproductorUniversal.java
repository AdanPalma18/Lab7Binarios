package lab7binarios.logic;

import java.io.File;
import java.io.FileInputStream;
import javax.sound.sampled.*;
import javazoom.jl.player.Player;

public class ReproductorUniversal {
    
    private Clip clip;
    private Player mp3Player;
    private Thread mp3Thread;
    private Long posicion;
    private boolean isPaused = false;
    private boolean isMP3 = false;
    private String currentFile;
    
    public void play(String ruta) throws Exception {
        stop(); // Detener cualquier reproducción anterior
        
        File file = new File(ruta);
        if (!file.exists()) {
            throw new Exception("El archivo no existe: " + ruta);
        }
        
        currentFile = ruta;
        
        // Detectar si es MP3 o WAV
        if (ruta.toLowerCase().endsWith(".mp3")) {
            playMP3(ruta);
        } else {
            playWAV(ruta);
        }
    }
    
    private void playWAV(String ruta) throws Exception {
        isMP3 = false;
        AudioInputStream audio = AudioSystem.getAudioInputStream(new File(ruta));
        clip = AudioSystem.getClip();
        clip.open(audio);
        clip.start();
        isPaused = false;
    }
    
    private void playMP3(String ruta) throws Exception {
        isMP3 = true;
        isPaused = false;
        
        mp3Thread = new Thread(() -> {
            try {
                FileInputStream fis = new FileInputStream(ruta);
                mp3Player = new Player(fis);
                mp3Player.play();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        mp3Thread.start();
    }
    
    public void pause() {
        if (isMP3) {
            // MP3: detener y guardar posición (JLayer no soporta pause nativo)
            if (mp3Player != null) {
                mp3Player.close();
                isPaused = true;
            }
        } else {
            // WAV: pause nativo
            if (clip != null && clip.isRunning()) {
                posicion = clip.getMicrosecondPosition();
                clip.stop();
                isPaused = true;
            }
        }
    }
    
    public void resume() {
        if (isMP3) {
            // MP3: reiniciar desde el principio (limitación de JLayer)
            try {
                if (isPaused && currentFile != null) {
                    playMP3(currentFile);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            // WAV: resume desde posición guardada
            if (clip != null && isPaused) {
                clip.setMicrosecondPosition(posicion);
                clip.start();
                isPaused = false;
            }
        }
    }
    
    public void stop() {
        if (isMP3) {
            if (mp3Player != null) {
                mp3Player.close();
                mp3Player = null;
            }
            if (mp3Thread != null) {
                mp3Thread.interrupt();
                mp3Thread = null;
            }
        } else {
            if (clip != null) {
                clip.stop();
                clip.setFramePosition(0);
                if (clip.isOpen()) {
                    clip.close();
                }
                clip = null;
            }
        }
        isPaused = false;
    }
    
    public boolean isPlaying() {
        if (isMP3) {
            return mp3Thread != null && mp3Thread.isAlive() && !isPaused;
        } else {
            return clip != null && clip.isRunning();
        }
    }
    
    public boolean isPaused() {
        return isPaused;
    }
}
