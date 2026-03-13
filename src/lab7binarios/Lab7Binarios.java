/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package lab7binarios;

import lab7binarios.ui.ReproductorFrame;
/**
 *
 * @author palma
 */
public class Lab7Binarios {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try { 
            new ReproductorFrame().setVisible(true);
        } catch(Exception ex) {
            System.out.println("Error: " + ex.getMessage());
        }
    }
    
}
