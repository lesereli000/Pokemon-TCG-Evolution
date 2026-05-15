package main.ui;

import java.awt.Color;
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
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.SwingUtilities;

public class ImageLoader {
    private static final Map<String, BufferedImage> imageCache = Collections.synchronizedMap(new HashMap<>());
    private static final Set<String> loading = Collections.synchronizedSet(new HashSet<>());
    private static final Logger logger = Logger.getLogger(ImageLoader.class.getName());

    private static final String PLACEHOLDER_URL = "https://via.placeholder.com/150?text=Card+Image+Missing";

    /**
     * Get image from cache or start background load.
     * Returns null if not in cache (starts loading).
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
            setPlaceholder(button, width, height);
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
                } else {
                    handleLoadFailure(urlString, componentToRepaint, callback);
                }
            } catch (IOException e) {
                logger.log(Level.SEVERE, "Failed to load image from: " + urlString, e);
                handleLoadFailure(urlString, componentToRepaint, callback);
            } finally {
                loading.remove(urlString);
            }
        }).start();
    }

    private static void handleLoadFailure(String urlString, Component componentToRepaint, java.util.function.Consumer<BufferedImage> callback) {
        // You could put a default locally stored image here if the internet is down
        // For now, we'll just log and potentially use a placeholder if we had one locally.
    }

    private static void setIcon(JButton button, BufferedImage image, int width, int height) {
        Image scaled = image.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        button.setIcon(new ImageIcon(scaled));
        button.setText("");
    }

    private static void setPlaceholder(JButton button, int width, int height) {
        button.setBackground(Color.LIGHT_GRAY);
        button.setText("No Image");
    }
}
