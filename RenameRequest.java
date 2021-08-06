package model;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class RenameRequest extends AbstractCommand{

    String oldName;
    String newName;

//    public RenameRequest(String newName) {
//        serverView.setOnMouseClicked(event -> {
//            String currentDirectory = serverPath.getText();
//            Path oldPath = Paths.get(serverPath.getText() + "/" + fileName);
//            renameButtonServer.setVisible(true);
//            renameButtonServer.setOnMouseClicked(e -> {
//                serverPath.setText("Renaming " + fileName + " to:");
//                serverPath.setEditable(true);
//                serverPath.setOnMouseClicked(d -> {
//                    serverSubmitButton.setVisible(true);
//                    serverPath.setText("");
//                });
//            });
//            serverSubmitButton.setOnAction(e -> {
//                String newName = serverPath.getCharacters().toString();
//                try {
//                    FileMessage oldName = new FileMessage (Paths.get(String.valueOf(oldPath)));
//                    Path newPath = Paths.get(oldName.changeName(newName));
//                    Files.move(oldPath,newPath);
//                    refreshServerView();
//                } catch (IOException ioException) {
//                    ioException.printStackTrace();
//                }
//                serverPath.setEditable(false);
//                serverPath.setText(currentDirectory);
//                serverSubmitButton.setVisible(false);
//                renameButtonServer.setVisible(false);
//            });
//        });
//    }


    public RenameRequest(String oldName, String newName) {
        this.oldName = oldName;
        this.newName = newName;

    }

    public String getOldName(){
        return this.oldName;
    }

    public String getNewName(){
        return this.newName;
    }

    @Override
    public CommandType getType() {
        return CommandType.RENAME_REQUEST;
    }
}
