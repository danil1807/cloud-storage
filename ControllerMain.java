package controller;

import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import io.netty.handler.codec.serialization.ObjectDecoderInputStream;
import io.netty.handler.codec.serialization.ObjectEncoderOutputStream;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import lombok.extern.slf4j.Slf4j;
import model.*;

@Slf4j
public class ControllerMain implements Initializable {


    public ListView<String> clientView;
    public ListView<String> serverView;
    public TextField clientPath;
    public TextField serverPath;
    public Button buttonPathUp;
    public Button clientSubmitButton;
    public Button serverSubmitButton;
    public Button renameButton;
    public Button sortA_ZButtonServer;
    public Button sortFirstFoldersServerButton;
    public Button renameButtonServer;
    public Button deleteServerButton;
    public Button createFolderClient;
    public Button createFolderServer;
    public Button deleteClientButton;
    private Path currentDir;
    private ObjectEncoderOutputStream os;
    private ObjectDecoderInputStream is;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            String userDir = System.getProperty("user.name");
            currentDir = Paths.get("/Users", userDir).toAbsolutePath();
            //log.info("Current user: {}", System.getProperty("user.name"));
            Socket socket = new Socket("localhost", 8189);
            os = new ObjectEncoderOutputStream(socket.getOutputStream());
            is = new ObjectDecoderInputStream(socket.getInputStream());

            refreshClientView();
            addNavigationListeners();

            Thread readThread = new Thread(() -> {
                try {
                    while (true) {
                        AbstractCommand command = (AbstractCommand) is.readObject();
                        switch (command.getType()) {
                            case LIST_MESSAGE:
                                ListResponse response = (ListResponse) command;
                                List<String> names = response.getNames();
                                refreshServerView(names);
                                break;
                            case LIST_SORT_AZ_RESPONSE:
                                ListSortAZResponse sortAZResponse = (ListSortAZResponse) command;
                                List <String> namesSortedAZ = sortAZResponse.getNames();
                                refreshServerViewSortedAZ(namesSortedAZ);
                                break;
                            case PATH_RESPONSE:
                                PathResponse pathResponse = (PathResponse) command;
                                String path = pathResponse.getPath();
                                Platform.runLater(() -> serverPath.setText(path));
                                serverPath.setEditable(false);
                                serverSubmitButton.setVisible(false);
                                break;
                            case FILE_DOWNLOAD:
                                FileDownload message = (FileDownload) command;
                                Files.write(currentDir.resolve(message.getName()), message.getData());
                                refreshClientView();
                                break;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
            readThread.setDaemon(true);
            readThread.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void refreshClientView() throws IOException {
        clientPath.setText(currentDir.toString());
        List<String> names = Files.list(currentDir)
                .map(p -> p.getFileName().toString())
                .collect(Collectors.toList());
        Platform.runLater(() -> {
            clientView.getItems().clear();
            clientView.getItems().addAll(names);
        });
    }

    private void refreshServerView(List<String> names) {
        Platform.runLater(() -> {
            serverView.getItems().clear();
            serverView.getItems().addAll(names);
        });
    }
    private void refreshServerViewSortedAZ(List<String> names) {
        Platform.runLater(() -> {
            serverView.getItems().clear();
            serverView.getItems().addAll(names);
        });
    }

    public void upload(ActionEvent actionEvent) throws IOException {
        String fileName = clientView.getSelectionModel().getSelectedItem();
        if (!Files.isDirectory(currentDir.resolve(fileName))) {
            os.writeObject(new FileUpload(currentDir.resolve(fileName)));
            os.flush();
        }
    }

    public void downLoad(ActionEvent actionEvent) throws IOException {
        String fileName = serverView.getSelectionModel().getSelectedItem();
        os.writeObject(new FileRequest(fileName));
        os.flush();
    }

    private void addNavigationListeners() {
        clientView.setOnMouseClicked(e -> {
            renameButtonServer.setVisible(false);
            sortA_ZButtonServer.setVisible(false);
            sortFirstFoldersServerButton.setVisible(false);
            createFolderServer.setVisible(false);
            deleteServerButton.setVisible(false);
            if (e.getClickCount() == 2) {
                String item = clientView.getSelectionModel().getSelectedItem();
                Path newPath = currentDir.resolve(item);
                if (Files.isDirectory(newPath)) {
                    currentDir = newPath;
                    try {
                        refreshClientView();
                    } catch (IOException ioException) {
                        ioException.printStackTrace();
                    }
                }
            }
        });


        serverView.setOnMouseClicked(e -> {
                renameButtonServer.setVisible(true);
                sortA_ZButtonServer.setVisible(true);
                sortFirstFoldersServerButton.setVisible(true);
                createFolderServer.setVisible(true);
                deleteServerButton.setVisible(true);
            if (e.getClickCount() == 2) {
                String item = serverView.getSelectionModel().getSelectedItem();
                try {
                    os.writeObject(new PathInRequest(item));
                    os.flush();
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            }
        });
    }

    public void clientPathUp(ActionEvent actionEvent) throws IOException {
        currentDir = currentDir.getParent();
        clientPath.setText(currentDir.toString());
        refreshClientView();
    }

    public void serverPathUp(ActionEvent actionEvent) throws IOException {
        os.writeObject(new PathUpRequest());
        os.flush();
    }

    public void renameClient(ActionEvent actionEvent) throws IOException {
            String fileName = clientView.getSelectionModel().getSelectedItem();
            renameButton.setOnMouseClicked(e -> {
                clientPath.setText("Renaming " + fileName + " to:");
                clientPath.setEditable(true);
                clientPath.setOnMouseClicked(d -> {
                    clientSubmitButton.setVisible(true);
                    clientPath.setText("");
                });
            });
            clientSubmitButton.setOnAction(e -> {
                String newName = clientPath.getCharacters().toString();
                try {
                    Files.move(currentDir.resolve(fileName), currentDir.resolve(newName));
                    refreshClientView();
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
                clientPath.setEditable(false);
                clientPath.setText(currentDir.toString());
                clientSubmitButton.setVisible(false);
            });
    }
    public void renameServer(ActionEvent actionEvent) throws IOException{
        String fileName = serverView.getSelectionModel().getSelectedItem();
        renameButtonServer.setOnMouseClicked(e -> {
            serverPath.setText("Renaming " + fileName + " to:");
            serverPath.setEditable(true);
            serverPath.setOnMouseClicked(d -> {
                serverSubmitButton.setVisible(true);
                serverPath.setText("");
            });
        });
        serverSubmitButton.setOnAction(e -> {
            try {
                String newName = serverPath.getText();
                os.writeObject(new RenameRequest(fileName, newName));
                os.flush();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        });
    }

    public void deleteClient(ActionEvent actionEvent) throws IOException {
        String fileName = clientView.getSelectionModel().getSelectedItem();
        deleteClientButton.setOnMouseClicked(e -> {
            clientSubmitButton.setVisible(true);
        });
        clientSubmitButton.setOnAction(e -> {
            try {
                if (!Files.isDirectory(currentDir.resolve(fileName))) {
                    Files.delete(currentDir.resolve(fileName));
                    clientPath.setText(fileName + " deleted successfully.");
                    refreshClientView();
                }
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        });
    }

    public void deleteServer(ActionEvent actionEvent) {
        String fileName = serverView.getSelectionModel().getSelectedItem();
        deleteServerButton.setOnMouseClicked(e -> {
            serverSubmitButton.setVisible(true);
        });
        serverSubmitButton.setOnAction(e -> {
            try {
                String pathToDelete = serverPath.getText() + "/" + fileName;
                os.writeObject(new DeleteRequest(pathToDelete));
                os.flush();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        });
    }

   public void sortA_ZClient(ActionEvent actionEvent) throws IOException {
       List<String> names = Files.list(currentDir)
               .map(p -> p.getFileName().toString())
               .sorted(Comparator.naturalOrder())
               .collect(Collectors.toList());
       Platform.runLater(() -> {
           clientView.getItems().clear();
           clientView.getItems().addAll(names);
       });
   }

    public void sortA_ZServer (ActionEvent actionEvent) throws IOException {
        ListSortAZRequest request = new ListSortAZRequest(false);
        os.writeObject(request);
        os.flush();
    }

    public void sortA_Z_FirstFoldersClient (ActionEvent actionEvent) throws IOException {
        List<String> names = Files.list(currentDir)
                .map(p -> p.getFileName().toString())
                .sorted(Comparator.naturalOrder())
                .collect(Collectors.toList());
        List <String> filesList = new ArrayList<>();
        List <String> foldersList = new ArrayList<>();;
        for (String name : names) {
            if (Files.isDirectory(currentDir.resolve(name))){
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
        Platform.runLater(() -> {
        clientView.getItems().clear();
        clientView.getItems().addAll(names);
    });
    }

    public void sortA_Z_FirstFoldersServer (ActionEvent actionEvent) throws IOException {
        ListSortAZRequest request = new ListSortAZRequest(true);
        os.writeObject(request);
        os.flush();
    }

    public void createFolderClient(ActionEvent actionEvent) {
        createFolderClient.setOnMouseClicked(e -> {
            clientPath.setText("Name new folder:");
            clientPath.setEditable(true);
            clientPath.setOnMouseClicked(d -> {
                clientSubmitButton.setVisible(true);
                clientPath.setText(currentDir.toString());
            });
        });
        clientSubmitButton.setOnAction(e -> {
            String newFolder = clientPath.getCharacters().toString();
            try {
                Files.createDirectories(Paths.get(newFolder));
                refreshClientView();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
            clientPath.setEditable(false);
            clientPath.setText(currentDir.toString());
            clientSubmitButton.setVisible(false);
        });
    }

    public void createFolderServer(ActionEvent actionEvent) {
        Path currentDirectory = Paths.get(serverPath.getText());
        createFolderServer.setOnMouseClicked(e -> {
            serverPath.setText("Name new folder:");
            serverPath.setEditable(true);
            serverPath.setOnMouseClicked(d -> {
                serverSubmitButton.setVisible(true);
                serverPath.setText("");
            });
        });
        serverSubmitButton.setOnAction(e -> {
            try {
                String folderPath = currentDirectory + "/" + serverPath.getText();
                FolderCreateRequest request = new FolderCreateRequest(folderPath);
                os.writeObject(request);
                os.flush();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        });
    }


}

