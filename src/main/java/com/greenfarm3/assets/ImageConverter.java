package com.greenfarm3.assets;

import javafx.scene.image.Image;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import javax.imageio.ImageIO;

/**
 * Utility class for converting between BufferedImage (AWT) and JavaFX Image.
 * Used to bridge ImageIO (which returns BufferedImage) and JavaFX (which uses Image).
 */
public class ImageConverter {
    
    /**
     * Convert BufferedImage (from ImageIO) to JavaFX Image.
     * Uses PNG encoding for lossless conversion.
     * 
     * @param bufferedImage BufferedImage to convert
     * @return JavaFX Image, or null if conversion failed
     */
    public static Image bufferedImageToJavaFX(BufferedImage bufferedImage) {
        if (bufferedImage == null) {
            return null;
        }
        
        return bufferedImageToJavaFXViaPNG(bufferedImage);
    }
    
    /**
     * Convert BufferedImage to JavaFX Image via PNG encoding.
     * This method uses PNG encoding (lossless) to convert BufferedImage to JavaFX Image.
     * 
     * @param bufferedImage BufferedImage to convert
     * @return JavaFX Image, or null if conversion failed
     */
    private static Image bufferedImageToJavaFXViaPNG(BufferedImage bufferedImage) {
        try {
            // Write BufferedImage to PNG bytes
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(bufferedImage, "png", baos);
            byte[] pngBytes = baos.toByteArray();
            
            // Load PNG bytes as JavaFX Image
            Image javafxImage = new Image(new ByteArrayInputStream(pngBytes));
            
            if (javafxImage.isError()) {
                return null;
            }
            
            return javafxImage;
        } catch (IOException e) {
            System.err.println("[ImageConverter] Failed to convert via PNG: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Convert JavaFX Image to BufferedImage (if needed for processing).
     * Uses PNG encoding for conversion.
     * 
     * @param javafxImage JavaFX Image to convert
     * @return BufferedImage, or null if conversion failed
     */
    public static BufferedImage javaFXImageToBufferedImage(Image javafxImage) {
        if (javafxImage == null) {
            return null;
        }
        
        try {
            // Read pixel data from JavaFX Image and create BufferedImage
            // This is a simplified approach - for full implementation, would need to read pixels
            // For now, return null as this is not critical for image loading
            System.out.println("[ImageConverter] javaFXImageToBufferedImage not fully implemented (not needed for loading)");
            return null;
        } catch (Exception e) {
            System.err.println("[ImageConverter] Failed to convert JavaFX Image to BufferedImage: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Validate that a BufferedImage is valid (has valid dimensions).
     * 
     * @param bufferedImage Image to validate
     * @return true if image is valid
     */
    public static boolean isValidBufferedImage(BufferedImage bufferedImage) {
        if (bufferedImage == null) {
            return false;
        }
        
        int width = bufferedImage.getWidth();
        int height = bufferedImage.getHeight();
        
        return width > 0 && height > 0;
    }
}
