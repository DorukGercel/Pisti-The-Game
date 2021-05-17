package com.group7.client.controller;

import com.group7.client.ScreenManager;
import com.group7.client.definitions.common.StatusCode;
import com.group7.client.definitions.network.NetworkManager;
import com.group7.client.dto.authentication.AuthRequest;
import com.group7.client.dto.authentication.LoginResponse;
import com.group7.client.dto.common.CommonResponse;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

@Component
public class LoginController implements Initializable {

    private ScreenManager mScreenManager;
    private NetworkManager mNetworkManager;
    @Value("${spring.application.apiAddress.player}") private String apiAddress;

    @FXML public GridPane login_form;
    @FXML public VBox login_form_container;
    @FXML public TextField username_field;
    @FXML public TextField email_field;
    @FXML public PasswordField password_field;
    @FXML public Button login_form_return_button;
    @FXML public Label informationLabel;

    @Autowired
    public void setScreenManager(@Lazy ScreenManager screenManager, NetworkManager networkManager) {
        this.mScreenManager = screenManager;
        this.mNetworkManager = networkManager;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {}

    @FXML
    public void clickReturnButton() {
        mScreenManager.activatePane("main_menu");
    }

    @FXML
    public void clickLoginButton() {
        String username = username_field.getText();
        String email = email_field.getText();
        String password = password_field.getText();
        System.out.println(password);

        if(!areFieldsValid(username, email, password)) {
            displayAlert("Some fields are missing.");
            return;
        }

        AuthRequest authRequest = new AuthRequest(username, password, email);
        CommonResponse[] commonResponse = new LoginResponse[1];

        StatusCode networkStatusCode = mNetworkManager.exchange(
                apiAddress + "/login",
                HttpMethod.POST,
                authRequest,
                commonResponse,
                LoginResponse.class);

        if(isNetworkOperationSuccess(commonResponse[0], networkStatusCode)) {
            LoginResponse loginResponse = (LoginResponse) commonResponse[0];
            if (isOperationSuccess(loginResponse)) {
                mScreenManager.activatePane("user_menu");
            } else {
                displayAlert(Objects.requireNonNull(loginResponse).getErrorMessage());
            }
        } else {
            displayAlert("Network connection error occurred!");
        }
        clearFields();
    }

    private void displayAlert(String errorMessage) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("Login Player");
        alert.setContentText(Objects.requireNonNull(errorMessage));
        alert.showAndWait();
        loseFocus();
    }

    private void loseFocus() {
        informationLabel.requestFocus();
    }

    private void clearFields() {
        username_field.clear();
        email_field.clear();
        password_field.clear();
    }

    private boolean areFieldsValid(String username, String email, String password) {
        return !username.isEmpty() && !email.isEmpty() && !password.isEmpty();
    }

    private boolean isOperationSuccess(LoginResponse loginResponse) {
        return loginResponse != null && loginResponse.getStatusCode().equals(StatusCode.SUCCESS);
    }

    private boolean isNetworkOperationSuccess(CommonResponse commonResponse, StatusCode networkStatusCode) {
        return commonResponse != null && networkStatusCode.equals(StatusCode.SUCCESS);
    }
}
