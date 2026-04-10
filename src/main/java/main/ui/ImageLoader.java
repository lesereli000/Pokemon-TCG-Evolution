package main.ui;

import java.awt.Component;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.SwingUtilities;

public class ImageLoader {
    private static final Map<String, BufferedImage> imageCache = Collections.synchronizedMap(new HashMap<>());
    private static final Set<String> loading = Collections.synchronizedSet(new HashSet<>());

    /**
     * Get image from cache or start background load.
     * Returns null if not in cache.
     */
    public static BufferedImage getImage(String urlString, Component componentToRepaint) {
        if (urlString == null || urlString.isEmpty()) {
            return null;
        }

        BufferedImage cached = imageCache.get(urlString);
        if (cached != null) {
            return cached;
        }

        if (loading.contains(urlString)) {
            return null;
        }

        startBackgroundLoad(urlString, componentToRepaint, null);
        return null;
    }

    /**
     * Loads image into button icon.
     */
    public static void loadIntoButton(String urlString, JButton button, int width, int height) {
        if (urlString == null || urlString.isEmpty()) {
            return;
        }

        BufferedImage cached = imageCache.get(urlString);
        if (cached != null) {
            setIcon(button, cached, width, height);
            return;
        }

        if (loading.contains(urlString)) {
            return;
        }

        startBackgroundLoad(urlString, button, (image) -> {
            setIcon(button, image, width, height);
        });
    }

    private static void startBackgroundLoad(String urlString, Component componentToRepaint, java.util.function.Consumer<BufferedImage> callback) {
        loading.add(urlString);
        new Thread(() -> {
            try {
                BufferedImage image = ImageIO.read(new URL(urlString));
                if (image != null) {
                    imageCache.put(urlString, image);
                    SwingUtilities.invokeLater(() -> {
                        if (callback != null) {
                            callback.accept(image);
                        }
                        if (componentToRepaint != null) {
                            componentToRepaint.repaint();
                        }
                    });
                }
            } catch (IOException e) {
                System.err.println("Failed to load image from: " + urlString);
            } finally {
                loading.remove(urlString);
            }
        }).start();
    }

    private static void setIcon(JButton button, BufferedImage image, int width, int height) {
        Image scaled = image.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        button.setIcon(new ImageIcon(scaled));
        button.setText("");
    }
}
