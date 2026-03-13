package lab7binarios.logic;

import javafx.embed.swing.JFXPanel;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;
import java.io.File;
import javax.sound.sampled.*;

public class ReproductorUniversal {
    
    private Clip clip;
    private MediaPlayer mediaPlayer;
    private Long posicion;
    private boolean isPaused = false;
    private boolean isMP3 = false;
    private static boolean javafxInitialized = false;
    
    public ReproductorUniversal() {
        // Inicializar JavaFX (solo una vez)
        if (!javafxInitialized) {
            new JFXPanel();
            javafxInitialized = true;
        }
    }
    
    public void play(String ruta) throws Exception {
        stop(); // Detener cualquier reproducción anterior
        
        File file = new File(ruta);
        if (!file.exists()) {
            throw new Exception("El archivo no existe: " + ruta);
        }
        
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
        
        File file = new File(ruta);
        Media media = new Media(file.toURI().toString());
        mediaPlayer = new MediaPlayer(media);
        
        mediaPlayer.setOnError(() -> {
            System.err.println("Error al reproducir: " + mediaPlayer.getError().getMessage());
        });
        
        mediaPlayer.play();
    }
    
    public void pause() {
        if (isMP3) {
            if (mediaPlayer != null && mediaPlayer.getStatus() == MediaPlayer.Status.PLAYING) {
                mediaPlayer.pause();
                isPaused = true;
            }
        } else {
            if (clip != null && clip.isRunning()) {
                posicion = clip.getMicrosecondPosition();
                clip.stop();
                isPaused = true;
            }
        }
    }
    
    public void resume() {
        if (isMP3) {
            if (mediaPlayer != null && mediaPlayer.getStatus() == MediaPlayer.Status.PAUSED) {
                mediaPlayer.play();
                isPaused = false;
            }
        } else {
            if (clip != null && isPaused) {
                clip.setMicrosecondPosition(posicion);
                clip.start();
                isPaused = false;
            }
        }
    }
    
    public void stop() {
        if (isMP3) {
            if (mediaPlayer != null) {
                mediaPlayer.stop();
                mediaPlayer.dispose();
                mediaPlayer = null;
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
            return mediaPlayer != null && 
                   mediaPlayer.getStatus() == MediaPlayer.Status.PLAYING;
        } else {
            return clip != null && clip.isRunning();
        }
    }
    
    public boolean isPaused() {
        return isPaused;
    }
    
    public double getCurrentTime() {
        if (isMP3 && mediaPlayer != null) {
            return mediaPlayer.getCurrentTime().toSeconds();
        } else if (!isMP3 && clip != null) {
            return clip.getMicrosecondPosition() / 1_000_000.0;
        }
        return 0;
    }
    
    public double getDuration() {
        if (isMP3 && mediaPlayer != null && mediaPlayer.getTotalDuration() != null) {
            return mediaPlayer.getTotalDuration().toSeconds();
        } else if (!isMP3 && clip != null) {
            return clip.getMicrosecondLength() / 1_000_000.0;
        }
        return 0;
    }
    
    public void seek(int segundos) {
        if (isMP3 && mediaPlayer != null) {
            mediaPlayer.seek(Duration.seconds(segundos));
        } else if (!isMP3 && clip != null) {
            long microsegundos = segundos * 1_000_000L;
            clip.setMicrosecondPosition(microsegundos);
        }
    }
}
