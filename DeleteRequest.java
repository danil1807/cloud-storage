package model;

public class DeleteRequest extends AbstractCommand{

    private String filePath;

    public DeleteRequest(String filePath) {
        this.filePath = filePath;
    }

    public String getFilePath() {
        return filePath;
    }

    @Override
    public CommandType getType() {
        return CommandType.DELETE_REQUEST;
    }
}
