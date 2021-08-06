package model;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileUpload extends AbstractCommand {

    private final String name;
    private final long size;
    private final byte[] data;

    public FileUpload (Path path) throws IOException {
        name = path.getFileName().toString();
        size = Files.size(path);
        data = Files.readAllBytes(path);
    }

    public String getName() {
        return name;
    }

    public byte[] getData() {
        return data;
    }

    @Override
    public CommandType getType() {
        return CommandType.FILE_UPLOAD;
    }
}

