package com.group7.client.controller;

import com.group7.client.controller.common.BaseNetworkController;
import com.group7.client.definitions.game.MoveType;
import com.group7.client.definitions.leaderboard.RecordEntry;
import com.group7.client.definitions.screen.ScreenManager;
import com.group7.client.definitions.common.StatusCode;
import com.group7.client.definitions.network.NetworkManager;
import com.group7.client.dto.common.CommonRequest;
import com.group7.client.dto.common.CommonResponse;
import com.group7.client.dto.game.InitGameResponse;
import com.group7.client.dto.leaderboard.LeaderboardResponse;
import com.group7.client.dto.leaderboard.ListRecordsResponse;
import com.group7.client.model.Leaderboard;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.event.EventListener;
import org.springframework.http.*;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.concurrent.Executors;

@Component
public class LeaderboardController extends BaseNetworkController {

    @Value("${spring.application.apiAddress.leaderboard}") private String mApiAddress;

    @FXML private TableView<Leaderboard> paginationTableView;
    @FXML private TableColumn<Leaderboard, Integer> rank;
    @FXML private TableColumn<Leaderboard, String> username;
    @FXML private TableColumn<Leaderboard, Integer> score;
    @FXML private ComboBox<String> time_selection_combobox;
    @FXML private Pagination pagination;

    private static int rowsPerPage = 15;
    public ObservableList<Leaderboard> tableRecordsList = FXCollections.observableArrayList();

    @Autowired
    public void setManagers(@Lazy ScreenManager screenManager, NetworkManager networkManager) {
        this.mScreenManager = screenManager;
        this.mNetworkManager = networkManager;
    }

    /** Init Leaderboard Event listener, sets username*/
    @EventListener({UserMenuController.InitLeaderboardEvent.class})
    public void onInitLeaderboardEvent(UserMenuController.InitLeaderboardEvent initLeaderboardEvent) {
        rank.setCellValueFactory(
                new PropertyValueFactory<Leaderboard, Integer>("rank"));
        username.setCellValueFactory(
                new PropertyValueFactory<Leaderboard, String>("username"));
        score.setCellValueFactory(
                new PropertyValueFactory<Leaderboard, Integer>("score"));

        time_selection_combobox.getItems().addAll( "Last 7 days", "Last 30 days", "All times");
        // Default display time is the most recent week
        time_selection_combobox.getSelectionModel().select("Last 7 days");

        // TODO: This part creates network error look at the back-end api
        // TODO: Uncomment to fix
        loadLeaderboardTable("weekly");
    }

    private void loadLeaderboardTable(String period) {
        tableRecordsList.clear();
        CommonResponse[] commonResponse = new ListRecordsResponse[1];

        StatusCode networkStatusCode = mNetworkManager.exchange(
        mApiAddress + "/get/" + period,
                HttpMethod.GET,
                null,
                commonResponse,
                ListRecordsResponse.class);

        if (isOperationSuccess(commonResponse[0], networkStatusCode, ListRecordsResponse.class, "Leaderboard")) {
            ListRecordsResponse listRecordsResponse = (ListRecordsResponse) commonResponse[0];
            putLeaderboardRecords(listRecordsResponse);
            mScreenManager.activatePane("leaderboard_table", null);
        }
    }

    private void putLeaderboardRecords(ListRecordsResponse listRecordsResponse) {
        List<RecordEntry> recordEntryList = listRecordsResponse.getMLeaderboardRecordEntryList();
        for(int i = 0; i < recordEntryList.size(); i++) {
            RecordEntry record = recordEntryList.get(i);
            paginationTableView.getItems().add(new Leaderboard(i+1, record.getPlayerName(), record.getScore()));
            tableRecordsList.add(new Leaderboard(i+1, record.getPlayerName(), record.getScore()));
        }
        int pageCount = (recordEntryList.size() / rowsPerPage) + 1;
        pagination.setPageCount(pageCount);
        pagination.setPageFactory(this::createPage);
    }

    private Node createPage(int pageIndex) {
        int from = pageIndex * rowsPerPage;
        int to = rowsPerPage;
        paginationTableView.setItems(FXCollections.observableArrayList(tableRecordsList));
        return paginationTableView;
    }
    @FXML
    public void selectFromComboBox(ActionEvent actionEvent) {
        String period = time_selection_combobox.getValue();
        loadLeaderboardTable(period);
    }
}
