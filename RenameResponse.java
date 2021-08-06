package model;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class RenameResponse extends AbstractCommand{
    String oldName;
    String newName;


    public RenameResponse() throws IOException {

    }

    @Override
    public CommandType getType() {
        return CommandType.RENAME_RESPONSE;
    }
}
