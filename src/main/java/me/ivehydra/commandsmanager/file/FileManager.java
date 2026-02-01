package me.ivehydra.commandsmanager.file;

import java.util.HashMap;
import java.util.Map;

public class FileManager {

    private final Map<String, CustomFile> files = new HashMap<>();

    public void createFile(String name) { createFile(null, name); }

    public void createFile(String folder, String name) {
        String key = folder == null ? name : folder + "/" + name;

        CustomFile file = new CustomFile(folder, name);
        files.put(key, file);
    }

    public CustomFile getFile(String name) { return files.get(name); }

}
