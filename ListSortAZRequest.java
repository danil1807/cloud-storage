package model;


import java.nio.file.Path;

public class ListSortAZRequest extends AbstractCommand{

    private boolean firstFolders;

    public ListSortAZRequest(boolean firstFolders) {
        this.firstFolders = firstFolders;
    }

    @Override
    public CommandType getType() {

        if (!firstFolders){
            return CommandType.LIST_SORT_AZ_REQUEST;
        } else{
            return CommandType.LIST_SORT_FIRST_FOLDERS_REQUEST;
        }
    }


}
