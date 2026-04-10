package main;

import org.junit.Before;
import org.junit.Test;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.Set;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

public class ImageLoaderTest {

    @Before
    public void setUp() throws Exception {
        // Clear caches using reflection
        Field cacheField = ImageLoader.class.getDeclaredField("imageCache");
        cacheField.setAccessible(true);
        Map<?, ?> cache = (Map<?, ?>) cacheField.get(null);
        cache.clear();

        Field loadingField = ImageLoader.class.getDeclaredField("loading");
        loadingField.setAccessible(true);
        Set<?> loading = (Set<?>) loadingField.get(null);
        loading.clear();
    }

    @Test
    public void testGetImageNullUrl() {
        assertNull(ImageLoader.getImage(null, null));
    }

    @Test
    public void testGetImageEmptyUrl() {
        assertNull(ImageLoader.getImage("", null));
    }

    @Test
    public void testGetImageCacheHit() throws Exception {
        String url = "http://example.com/test.png";
        BufferedImage mockImage = new BufferedImage(10, 10, BufferedImage.TYPE_INT_ARGB);
        
        // Manually put in cache
        Field cacheField = ImageLoader.class.getDeclaredField("imageCache");
        cacheField.setAccessible(true);
        Map<String, BufferedImage> cache = (Map<String, BufferedImage>) cacheField.get(null);
        cache.put(url, mockImage);

        assertEquals(mockImage, ImageLoader.getImage(url, null));
    }

    @Test
    public void testGetImageAlreadyLoading() throws Exception {
        String url = "http://example.com/loading.png";
        
        // Manually put in loading set
        Field loadingField = ImageLoader.class.getDeclaredField("loading");
        loadingField.setAccessible(true);
        Set<String> loading = (Set<String>) loadingField.get(null);
        loading.add(url);

        assertNull(ImageLoader.getImage(url, null));
    }

    @Test
    public void testLoadIntoButtonNullUrl() {
        JButton button = createMock(JButton.class);
        replay(button);
        ImageLoader.loadIntoButton(null, button, 10, 10);
        verify(button);
    }

    @Test
    public void testLoadIntoButtonEmptyUrl() {
        JButton button = createMock(JButton.class);
        replay(button);
        ImageLoader.loadIntoButton("", button, 10, 10);
        verify(button);
    }

    @Test
    public void testLoadIntoButtonCacheHit() throws Exception {
        String url = "http://example.com/button.png";
        BufferedImage mockImage = new BufferedImage(10, 10, BufferedImage.TYPE_INT_ARGB);
        JButton button = createMock(JButton.class);

        // Manually put in cache
        Field cacheField = ImageLoader.class.getDeclaredField("imageCache");
        cacheField.setAccessible(true);
        Map<String, BufferedImage> cache = (Map<String, BufferedImage>) cacheField.get(null);
        cache.put(url, mockImage);

        button.setIcon(anyObject(ImageIcon.class));
        button.setText("");
        replay(button);

        ImageLoader.loadIntoButton(url, button, 10, 10);
        verify(button);
    }

    @Test
    public void testLoadIntoButtonAlreadyLoading() throws Exception {
        String url = "http://example.com/button_loading.png";
        JButton button = createMock(JButton.class);

        // Manually put in loading set
        Field loadingField = ImageLoader.class.getDeclaredField("loading");
        loadingField.setAccessible(true);
        Set<String> loading = (Set<String>) loadingField.get(null);
        loading.add(url);

        replay(button);
        ImageLoader.loadIntoButton(url, button, 10, 10);
        verify(button);
    }

    @Test(timeout = 10000)
    public void testGetImageStartBackgroundLoad() throws Exception {
        String url = "invalid_url"; // Will cause IOException in thread
        Component comp = createMock(Component.class);
        replay(comp);

        BufferedImage result = ImageLoader.getImage(url, comp);
        assertNull(result);
        
        // Wait for thread to complete the catch block
        Thread.sleep(500);
        
        verify(comp);
    }
}
