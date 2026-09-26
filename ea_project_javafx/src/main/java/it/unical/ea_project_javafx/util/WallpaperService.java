package it.unical.ea_project_javafx.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.time.LocalDate;

public class WallpaperService {

    private static final String BING_API_URL = "https://www.bing.com/HPImageArchive.aspx?format=js&idx=0&n=1&mkt=it-IT";
    private static final String LOCAL_FALLBACK_RESOURCE = "/it/unical/ea_project_javafx/images/fallback_bg.avif";

    public static String getDailyWallpaperUrl() {

        String todayStr = LocalDate.now().toString();
        Path cacheDir = getSystemCacheDir();
        Path targetFile = cacheDir.resolve("bing_wallpaper_" + todayStr + ".jpg");

        try {
            // Se l'immagine odierna esiste già in cache, usiamo quella locale
            if (Files.exists(targetFile)) {
                return targetFile.toUri().toString();
            }

            // Controllo che la cartella di cache esista
            if (!Files.exists(cacheDir)) {
                Files.createDirectories(cacheDir);
            }

            URL apiUrl = URI.create(BING_API_URL).toURL();
            try (InputStream is = apiUrl.openStream();
                 InputStreamReader reader = new InputStreamReader(is, StandardCharsets.UTF_8)) {

                JsonObject jsonObject = JsonParser.parseReader(reader).getAsJsonObject();
                JsonArray imagesArray = jsonObject.getAsJsonArray("images");

                if (imagesArray != null && imagesArray.size() > 0) {
                    JsonObject firstImage = imagesArray.get(0).getAsJsonObject();
                    String relativeUrl = firstImage.get("url").getAsString();
                    String bingImageUrl = "https://www.bing.com" + relativeUrl;

                    // 3. Scarica l'immagine e la salva nella cache
                    try (InputStream imageStream = URI.create(bingImageUrl).toURL().openStream()) {
                        Files.copy(imageStream, targetFile, StandardCopyOption.REPLACE_EXISTING);
                    }

                    // Pulizia dalla cache le vecchie immagini per liberare spazio sul disco
                    cleanOldCacheFiles(cacheDir, todayStr);

                    return targetFile.toUri().toString();
                }
            }
        } catch (Exception e) {
            System.err.println("Impossibile scaricare lo sfondo da Bing, uso il fallback locale: " + e.getMessage());
        }

        // 4. Fallback locale in assenza di connessione o in caso di errori
        URL resourceUrl = WallpaperService.class.getResource(LOCAL_FALLBACK_RESOURCE);
        if (resourceUrl != null) {
            return resourceUrl.toExternalForm();
        }

        // Chiamata dall'URL in caso l'immagine non viene trovata in locale
        return "https://images.unsplash.com/photo-1464822759023-fed622ff2c3b?auto=format&fit=crop&w=1920&q=80";

    }

    /**
     * Elimina i vecchi file di sfondo salvati in precedenza.
     */
    private static void cleanOldCacheFiles(Path cacheDir, String todayStr) {

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(cacheDir, "bing_wallpaper_*.jpg")) {
            for (Path entry : stream) {
                if (!entry.getFileName().toString().contains(todayStr)) {
                    Files.deleteIfExists(entry);
                }
            }
        } catch (Exception ignored) {
        }

    }

    /**
     * Ottiene la directory temporanea corretta in base al sistema operativo in uso.
     */
    private static Path getSystemCacheDir() {

        String os = System.getProperty("os.name").toLowerCase();
        String userHome = System.getProperty("user.home");

        if (os.contains("win")) {

            // Windows usa %LOCALAPPDATA%
            String localAppData = System.getenv("LOCALAPPDATA");
            if (localAppData != null) {
                return Paths.get(localAppData, "ea_project", "cache");
            }
            return Paths.get(userHome, "AppData", "Local", "ea_project", "cache");

        } else if (os.contains("mac")) {

            // macOS usa ~/Library/Caches/
            return Paths.get(userHome, "Library", "Caches", "ea_project");

        } else {

            // Linux/Unix usa ~/.cache/
            String xdgCache = System.getenv("XDG_CACHE_HOME");
            if (xdgCache != null) {
                return Paths.get(xdgCache, "ea_project");
            }
            return Paths.get(userHome, ".cache", "ea_project");

        }

    }
}