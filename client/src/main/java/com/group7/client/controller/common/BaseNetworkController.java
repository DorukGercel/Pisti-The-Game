package com.group7.client.controller.common;

import com.group7.client.definitions.common.StatusCode;
import com.group7.client.definitions.network.NetworkManager;
import com.group7.client.dto.authentication.LoginResponse;
import com.group7.client.dto.common.CommonResponse;
import javafx.application.Platform;

import java.util.Objects;

/** Abstract network controller which implements common network controller methods*/
public abstract class BaseNetworkController extends BaseController {
    /** Reference to common network manager*/
    protected NetworkManager mNetworkManager;

    protected boolean isOperationSuccess(CommonResponse commonResponse, StatusCode networkStatusCode, Class<? extends CommonResponse> responseType, String headerText) {
        // Check if network operation is successful
        if(isNetworkOperationSuccess(commonResponse, networkStatusCode, responseType)) {
            // Check if operation is successful
            if (isRequestedOperationSuccess(commonResponse)) {
                return true;
            } else {
                displayError(headerText,
                        Objects.requireNonNull(commonResponse).getErrorMessage());
                return false;
            }
        } else {
            displayError(headerText,
                    "Network connection error occurred!");
            return false;
        }
    }

    /** Checks whether the http response is present*/
    private boolean isNetworkOperationSuccess(CommonResponse commonResponse, StatusCode networkStatusCode, Class<? extends CommonResponse> responseType) {
        return responseType.isInstance(commonResponse) && networkStatusCode.equals(StatusCode.SUCCESS);
    }

    /** Checks whether the response status code is success*/
    private boolean isRequestedOperationSuccess(CommonResponse commonResponse) {
        return commonResponse != null && commonResponse.getStatusCode().equals(StatusCode.SUCCESS);
    }
}

