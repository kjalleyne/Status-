package com.example.statusplusplus.Classes;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.FocusModel;
import javafx.scene.control.TextField;
import com.example.statusplusplus.DatabaseModels.*;
import java.sql.SQLException;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class LoginGUI {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Button createAccount;

    @FXML
    private TextField email;

    @FXML
    private Button login;

    @FXML
    private TextField password;

    @FXML
    private TextField username;

    private final Database databaseManager = new Database();

    @FXML
    void initialize() {
        assert createAccount != null : "fx:id=\"createAccount\" was not injected: check your FXML file 'Status++Login.fxml'.";
        assert email != null : "fx:id=\"email\" was not injected: check your FXML file 'Status++Login.fxml'.";
        assert login != null : "fx:id=\"login\" was not injected: check your FXML file 'Status++Login.fxml'.";
        assert password != null : "fx:id=\"password\" was not injected: check your FXML file 'Status++Login.fxml'.";
        assert username != null : "fx:id=\"username\" was not injected: check your FXML file 'Status++Login.fxml'.";

        login.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                try {
                    System.out.println("Login button clicked");
                    handleLogin();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        createAccount.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                try {
                    System.out.println("Create Account button clicked");
                    handleAccountCreation();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        });

    }

    private void handleLogin() throws SQLException {
        //String usernameInput = username.getText();
        String passwordInput = password.getText();
        String emailInput = email.getText();
        boolean validUser = databaseManager.checkCredentials(emailInput, passwordInput);
        if (validUser) {
            // Close the current stage
            Stage stage = (Stage) login.getScene().getWindow();
            stage.close();

            // Load and show the new GUI
            openMainGUI();
        } else {
            System.out.println("Login failed");
        }
    }

    private void handleAccountCreation() throws SQLException {
        String usernameInput = username.getText();
        String emailInput = email.getText();
        String passwordInput = password.getText();

        boolean validUser = databaseManager.checkCredentials(emailInput, passwordInput);
        if (validUser) {
            // Close the current stage
            Stage stage = (Stage) login.getScene().getWindow();
            stage.close();

            // Load and show the new GUI
            openMainGUI();
        } else {
            System.out.println("Creating account");
            databaseManager.addUser(usernameInput, emailInput, passwordInput);

            // Close the current stage
            Stage stage = (Stage) login.getScene().getWindow();
            stage.close();

            // Load and show the new GUI
            openMainGUI();
        }
    }
    public static User appUser;
    /**
     * A function to open the main gui screen. Will also pass the userID of the logged-in user.
     */
    private void openMainGUI() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/com/example/statusplusplus/Status++GUI.fxml"));
            Parent mainParent = loader.load();

            mainGUI controller = loader.getController();
            //controller.initialize();
            controller.setUser(databaseManager.getUserByEmail(email.getText()));

            Stage newStage = new Stage();
            newStage.setScene(new Scene(mainParent));
            newStage.setTitle("Status++");
            newStage.setResizable(false);
            newStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
