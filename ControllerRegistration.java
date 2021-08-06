package controller;

import db.DBHandler;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.sql.SQLException;

public class ControllerRegistration {
    public PasswordField passwordField;
    public TextField loginField;
    public Button registerButton;
    public Button loginButton;
    DBHandler dbHandler;

    public void doRegister(ActionEvent actionEvent) throws SQLException {
        dbHandler.createUser(loginField.getText(), passwordField.getText(), null);
    }

    public void doLogin(ActionEvent actionEvent) {

    }
}
