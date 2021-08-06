package model;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
public class FileDownload extends AbstractCommand {

    private final String name;
    private final long size;
    private final byte[] data;

    public FileDownload(Path path) throws IOException {
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
        return CommandType.FILE_DOWNLOAD;
    }
}
