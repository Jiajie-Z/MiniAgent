package com.jagent.rag;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class KnowledgeBaseLoader {
    private final String resourceDirectory;

    public KnowledgeBaseLoader(String resourceDirectory) {
        this.resourceDirectory = resourceDirectory;
    }

    public List<Document> load() {
        URL resource = Thread.currentThread()
                .getContextClassLoader()
                .getResource(resourceDirectory);

        if (resource == null) {
            return List.of();
        }

        try {
            URI uri = resource.toURI();
            if ("jar".equals(uri.getScheme())) {
                return loadFromJar(uri);
            }
            return loadFromPath(Path.of(uri));
        } catch (IOException | URISyntaxException exception) {
            throw new IllegalStateException("Failed to load knowledge base: " + resourceDirectory, exception);
        }
    }

    private List<Document> loadFromJar(URI uri) throws IOException {
        try (FileSystem fileSystem = FileSystems.newFileSystem(uri, Map.of())) {
            return loadFromPath(fileSystem.getPath(resourceDirectory));
        }
    }

    private List<Document> loadFromPath(Path directory) throws IOException {
        if (!Files.exists(directory)) {
            return List.of();
        }

        List<Document> documents = new ArrayList<>();
        try (Stream<Path> paths = Files.walk(directory)) {
            paths.filter(Files::isRegularFile)
                    .filter(path -> path.getFileName().toString().endsWith(".md"))
                    .sorted()
                    .forEach(path -> documents.add(toDocument(path)));
        }

        return Collections.unmodifiableList(documents);
    }

    private Document toDocument(Path path) {
        try {
            String fileName = path.getFileName().toString();
            String id = fileName.substring(0, fileName.length() - 3);
            return new Document(id, Files.readString(path, StandardCharsets.UTF_8));
        } catch (IOException exception) {
            throw new IllegalStateException("Failed to read knowledge document: " + path, exception);
        }
    }
}
