package model;

import java.nio.file.Path;

public class FolderCreateResponse extends AbstractCommand{
    public FolderCreateResponse(Path path) {

    }

    @Override
    public CommandType getType() {
        return CommandType.FOLDER_CREATE_RESPONSE;
    }
}
