package model;

public class PathResponse extends AbstractCommand {

    private final String path;

    public PathResponse(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }

    @Override
    public CommandType getType() {
        return CommandType.PATH_RESPONSE;
    }
}
