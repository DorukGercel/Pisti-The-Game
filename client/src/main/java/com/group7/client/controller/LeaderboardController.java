package com.group7.client.controller;

import com.group7.client.controller.common.BaseNetworkController;
import com.group7.client.definitions.leaderboard.RecordEntry;
import com.group7.client.definitions.screen.ScreenManager;
import com.group7.client.definitions.common.StatusCode;
import com.group7.client.definitions.network.NetworkManager;
import com.group7.client.dto.common.CommonResponse;
import com.group7.client.dto.leaderboard.ListRecordsResponse;
import com.group7.client.model.Leaderboard;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.event.EventListener;
import org.springframework.http.*;
import org.springframework.stereotype.Component;

import java.util.List;

/** Controller for the leaderboard table*/
@Component
public class LeaderboardController extends BaseNetworkController {
    /** Common api address of the back-end for controller requests*/
    @Value("${spring.application.apiAddress.leaderboard}") private String mApiAddress;

    /** FXML fields*/
    @FXML private TableView<Leaderboard> table;
    @FXML private TableColumn<Leaderboard, Integer> rank;
    @FXML private TableColumn<Leaderboard, String> username;
    @FXML private TableColumn<Leaderboard, Integer> score;
    @FXML private ComboBox<String> period_combobox;

    /** Setter injection method*/
    @Autowired
    public void setManagers(@Lazy ScreenManager screenManager, NetworkManager networkManager) {
        this.mScreenManager = screenManager;
        this.mNetworkManager = networkManager;
    }

    /** Init Leaderboard Event listener, sets username*/
    @EventListener({UserMenuController.InitLeaderboardEvent.class})
    public void onInitLeaderboardEvent() {
        rank.setCellValueFactory(
                new PropertyValueFactory<Leaderboard, Integer>("rank"));
        username.setCellValueFactory(
                new PropertyValueFactory<Leaderboard, String>("username"));
        score.setCellValueFactory(
                new PropertyValueFactory<Leaderboard, Integer>("score"));

        table.getItems().clear();

        period_combobox.getItems().clear();
        period_combobox.getItems().addAll( "Last 7 days", "Last 30 days", "All times");
        // Default display time is the most recent week
        period_combobox.getSelectionModel().select("Last 7 days");

        loadLeaderboardTable("weekly");
    }

    /** Choose time period of leaderboard records to be displayed */
    @FXML
    private void selectFromComboBox() {
        String period = period_combobox.getValue();
        loadLeaderboardTable(periodConverter(period));
    }

    /** Function which sends the get request to leaderboard table*/
    private void loadLeaderboardTable(String period) {
        CommonResponse[] commonResponse = new ListRecordsResponse[1];

        // TODO: Remove print
        System.out.println("Period " + period);

        StatusCode networkStatusCode = mNetworkManager.exchange(
        mApiAddress + "/get/" + period,
                HttpMethod.GET,
                null,
                commonResponse,
                ListRecordsResponse.class);

        if (isOperationSuccess(commonResponse[0], networkStatusCode, ListRecordsResponse.class, "Leaderboard")) {
            ListRecordsResponse listRecordsResponse = (ListRecordsResponse) commonResponse[0];
            putLeaderboardRecords(listRecordsResponse);
        }
    }

    /** Function which displays leaderboard records on the table*/
    private void putLeaderboardRecords(ListRecordsResponse listRecordsResponse) {
        table.getItems().clear();
        List<RecordEntry> recordEntryList = listRecordsResponse.getRecordEntryList();
        for(int i = 0; i < recordEntryList.size(); i++) {
            RecordEntry record = recordEntryList.get(i);
            // TODO: Remove print
            System.out.println("Record " + record);
            table.getItems().add(new Leaderboard(i+1, record.getPlayerName(), record.getScore()));
        }
    }

    /** Helper function to change leaderboard table according to time period*/
    private String periodConverter(String tablePeriodValue) {
        if (tablePeriodValue == null || tablePeriodValue.equals("All times")) {
            return "allTimes";
        } else if(tablePeriodValue.equals("Last 30 days")) {
            return "monthly";
        } else {
            return "weekly";
        }
    }
}
