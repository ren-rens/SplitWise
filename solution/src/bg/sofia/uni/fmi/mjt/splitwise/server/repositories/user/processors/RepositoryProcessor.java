package bg.sofia.uni.fmi.mjt.splitwise.server.repositories.user.processors;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

public class RepositoryProcessor {

    public static <T> void loadFromFile(String filePath, Map<String, T> dataMap) {
        try {
            Path path = getPath(filePath);
            if (path == null) {
                return;
            }

            Map<String, T> loadedData = readFromFile(path);
            dataMap.putAll(loadedData);

        } catch (IOException e) {
            throw new RuntimeException("Problem with loading data occurred from file: " + filePath, e);
        }
    }

    private static <T> Map<String, T> readFromFile(Path path) {
        try (ObjectInputStream reader = new ObjectInputStream(Files.newInputStream(path))) {
            return (Map<String, T>) reader.readObject();

        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Problem with reading data occurred", e);
        } catch (IOException e) {
            throw new RuntimeException("Problem with reading from file occurred", e);
        }
    }

    public static <T> void saveToFile(String filePath, Map<String, T> dataMap) {
        try (ObjectOutputStream writer = new ObjectOutputStream(new FileOutputStream(filePath))) {
            writer.writeObject(dataMap);
            writer.flush();

        } catch (IOException e) {
            throw new RuntimeException("Problem with saving data to file: " + filePath, e);
        }
    }

    public static <T> void updateInFile(String filePath, Map<String, T> dataMap, String key, T value) {
        dataMap.put(key, value);
        saveToFile(filePath, dataMap);
    }

    public static <T> T getFromMap(Map<String, T> dataMap, String key) {
        return dataMap.get(key);
    }

    public static <T> boolean existsInMap(Map<String, T> dataMap, String key) {
        return dataMap.containsKey(key);
    }

    private static Path getPath(String filePath) throws IOException {
        Path path = Path.of(filePath);
        if (Files.notExists(path)) {
            Files.createFile(path);
            return null;
        }
        if (path.toFile().length() == 0) {
            return null;
        }
        return path;
    }

}