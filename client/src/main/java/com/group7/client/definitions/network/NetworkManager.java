package com.group7.client.definitions.network;

import com.group7.client.definitions.common.StatusCode;
import com.group7.client.definitions.player.PlayerManager;
import com.group7.client.dto.authentication.LoginResponse;
import com.group7.client.dto.authentication.LogoutResponse;
import com.group7.client.dto.common.CommonRequest;
import com.group7.client.dto.common.CommonResponse;
import org.springframework.stereotype.Component;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

/**
 * Responsible for dealing with the connections to the back-end
 */
@Component
public class NetworkManager {

    /** Deals with exchange of request and response*/
    private final RestTemplate mRestTemplate;
    /** Request header*/
    private final HttpHeaders mHttpHeaders;

    /** No args constructor*/
    public NetworkManager() {
        mRestTemplate = new RestTemplate();
        mHttpHeaders = new HttpHeaders();
        mHttpHeaders.setContentType(MediaType.APPLICATION_JSON);
    }

    /***
     * Responsible for exchanging requests and responses with back-end.
     *
     * @param apiAddress address to send the request
     * @param httpMethod type of request method
     * @param request is the request object
     * @param response the response sent from back-end; initially empty, set in the method
     * @param responseType type of the response
     * @return the status code indicates whether network operation is completed;
     *                         just checks for connection and presence of response
     */
    public StatusCode exchange(String apiAddress,
                               HttpMethod httpMethod,
                               CommonRequest request,
                               CommonResponse[] response,
                               Class<? extends CommonResponse> responseType) {
        try {
            // Entity sent as request
            var httpEntity = new HttpEntity<>(request, mHttpHeaders);
            // Perform rest exchange
            ResponseEntity<? extends CommonResponse> responseEntity = mRestTemplate.exchange(
                    apiAddress,
                    httpMethod,
                    httpEntity,
                    responseType);

            // Check if response body is present; if present status success
            if(isNetworkOperationSuccess(responseEntity)) {
                response[0] = responseEntity.getBody();
                if (responseType.equals(LoginResponse.class) && response[0] != null) {
                    // In login operation set token
                    setToken((LoginResponse) response[0]);
                } else if (responseType.equals(LogoutResponse.class) && response[0] != null) {
                    // In logout operation remove token
                    removeToken((LogoutResponse) response[0]);
                }
                return StatusCode.SUCCESS;
            }
            return StatusCode.FAIL;
        } catch (Exception e) {
            return StatusCode.FAIL;
        }
    }

    /** Check whether response entity is present*/
    private boolean isNetworkOperationSuccess(HttpEntity<?> responseHttpEntity) {
        return responseHttpEntity != null && responseHttpEntity.hasBody();
    }

    /** Set token and set session id*/
    private void setToken(LoginResponse loginResponse) {
        if (loginResponse.getStatusCode().equals(StatusCode.SUCCESS)) {
            String token = loginResponse.getToken();
            mHttpHeaders.setBearerAuth(token);
        }
    }

    /** Remove token and remove session id*/
    private void removeToken(LogoutResponse logoutResponse) {
        if (logoutResponse.getStatusCode().equals(StatusCode.SUCCESS)) {
            mHttpHeaders.setBearerAuth("");
        }
    }
}
