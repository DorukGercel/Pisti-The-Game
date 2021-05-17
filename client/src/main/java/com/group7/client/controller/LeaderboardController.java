package com.group7.client.controller;

import com.group7.client.ScreenManager;
import com.group7.client.definitions.common.StatusCode;
import com.group7.client.definitions.network.NetworkManager;
import com.group7.client.dto.common.CommonRequest;
import com.group7.client.dto.common.CommonResponse;
import com.group7.client.dto.leaderboard.LeaderboardResponse;
import com.group7.client.model.Leaderboard;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.*;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

@Component
public class LeaderboardController implements Initializable {

    private ScreenManager mScreenManager;
    private NetworkManager mNetworkManager;
    @Value("http://localhost:8080/api/leaderboard") private String apiAddress;

    @FXML public TableView<Leaderboard> table;
    @FXML public TableColumn<Leaderboard, String> username_column;
    @FXML public TableColumn<Leaderboard, Integer> rank_column;
    @FXML public TableColumn<Leaderboard, Integer> score_column;
    @FXML public ComboBox<String> time_selection_combobox;
    @FXML public Label informationLabel;

    public ObservableList<Leaderboard> list = FXCollections.observableArrayList();

    @Autowired
    public void setScreenManager(@Lazy ScreenManager screenManager, NetworkManager networkManager) {
        this.mScreenManager = screenManager;
        this.mNetworkManager = networkManager;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        rank_column.setCellValueFactory(
                new PropertyValueFactory<Leaderboard, Integer>("rank"));
        username_column.setCellValueFactory(
                new PropertyValueFactory<Leaderboard, String>("username"));
        score_column.setCellValueFactory(
                new PropertyValueFactory<Leaderboard, Integer>("score"));

        time_selection_combobox.getItems().addAll( "Last 7 days", "Last 30 days", "All times");
        time_selection_combobox.getSelectionModel().select("Last 7 days");
    }

    public void loadLeaderboardTable(String period) {
        list.clear();
        CommonRequest commonRequest = new CommonRequest();
        CommonResponse[] commonResponse = new LeaderboardResponse[1];

        StatusCode networkStatusCode = mNetworkManager.exchange(
        apiAddress + "/get/" + period,
                HttpMethod.GET,
                commonRequest,
                commonResponse,
                LeaderboardResponse.class);

        if(isNetworkOperationSuccess(commonResponse[0], networkStatusCode)) {
            LeaderboardResponse leaderboardResponse = (LeaderboardResponse) commonResponse[0];
            if(isOperationSuccess(leaderboardResponse)) {
                putLeaderboardRecords(leaderboardResponse);
                mScreenManager.activatePane("leaderboard_table");
            } else {
                displayAlert(Objects.requireNonNull(leaderboardResponse.getErrorMessage()));
            }
        } else {
            displayAlert("Network connection error occurred!");
        }
    }
    // TODO implement this functionality
    public void putLeaderboardRecords(LeaderboardResponse leaderboardResponse) {

    }

    @FXML
    public void selectFromComboBox(ActionEvent actionEvent) {
        String period = time_selection_combobox.getValue();
        loadLeaderboardTable(period);
    }

    private void displayAlert(String errorMessage) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("Display Leaderboard");
        alert.setContentText(Objects.requireNonNull(errorMessage));
        alert.showAndWait();
        loseFocus();
    }

    private void loseFocus() {
        informationLabel.requestFocus();
    }

    private boolean isOperationSuccess(LeaderboardResponse leaderboardResponse) {
        return leaderboardResponse != null && leaderboardResponse.getStatusCode().equals(StatusCode.SUCCESS);
    }

    private boolean isNetworkOperationSuccess(CommonResponse commonResponse, StatusCode networkStatusCode) {
        return commonResponse != null && networkStatusCode.equals(StatusCode.SUCCESS);
    }

}
