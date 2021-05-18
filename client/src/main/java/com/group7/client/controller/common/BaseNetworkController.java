package com.group7.client.controller.common;

import com.group7.client.definitions.common.StatusCode;
import com.group7.client.dto.common.CommonResponse;
import javafx.fxml.Initializable;

import java.net.URL;
import java.util.ResourceBundle;

/** Abstract network controller which implements common network controller methods*/
public abstract class BaseNetworkController extends BaseController
        implements Initializable {

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {}

    /** Checks whether the http response is present*/
    protected boolean isNetworkOperationSuccess(CommonResponse commonResponse, StatusCode networkStatusCode) {
        return commonResponse != null && networkStatusCode.equals(StatusCode.SUCCESS);
    }

    /** Checks whether the response status code is success*/
    protected boolean isOperationSuccess(CommonResponse commonResponse) {
        return commonResponse != null && commonResponse.getStatusCode().equals(StatusCode.SUCCESS);
    }
}

