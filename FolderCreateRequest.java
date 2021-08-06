package model;

import java.nio.file.Path;

public class FolderCreateRequest extends AbstractCommand{
    private String path;

    public FolderCreateRequest(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }

    @Override
    public CommandType getType() {
        return CommandType.FOLDER_CREATE_REQUEST;
    }
}
