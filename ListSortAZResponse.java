package model;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class ListSortAZResponse extends AbstractCommand{
    private final List<String> names;

    public ListSortAZResponse (Path path, boolean firstFolders) throws IOException {
        names = Files.list(path)
                .map(p -> p.getFileName().toString())
                .sorted(Comparator.naturalOrder())
                .collect(Collectors.toList());
        if (firstFolders){
            List <String> filesList = new ArrayList<>();
            List <String> foldersList = new ArrayList<>();;
            for (String name : names) {
                if (Files.isDirectory(path.resolve(name))){
                    foldersList.add(name);
                }else{
                    filesList.add(name);
                }
            }
            foldersList.sort(Comparator.naturalOrder());
            filesList.sort(Comparator.naturalOrder());
            names.clear();
            names.addAll(foldersList);
            names.addAll(filesList);
        }
    }



    public List<String> getNames() {
        return names;
    }

    @Override
    public CommandType getType() {
        return CommandType.LIST_SORT_AZ_RESPONSE;
    }
}
