package com.group7.client.definitions.network;

import com.group7.client.definitions.common.StatusCode;
import com.group7.client.dto.authentication.LoginResponse;
import com.group7.client.dto.common.CommonRequest;
import com.group7.client.dto.common.CommonResponse;
import org.springframework.stereotype.Component;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

@Component
public class NetworkManager {

    private final RestTemplate mRestTemplate;
    private final HttpHeaders mHttpHeaders;

    public NetworkManager() {
        mRestTemplate = new RestTemplate();
        mHttpHeaders = new HttpHeaders();
        mHttpHeaders.setContentType(MediaType.APPLICATION_JSON);
    }

    public StatusCode exchange(String apiAddress, HttpMethod httpMethod, CommonRequest request, CommonResponse[] response, Class<? extends CommonResponse> responseType) {
        var httpEntity = new HttpEntity<>(request, mHttpHeaders);
        ResponseEntity<? extends CommonResponse> responseEntity = mRestTemplate.exchange(
                apiAddress,
                httpMethod,
                httpEntity,
                responseType);

        if(isNetworkOperationSuccess(responseEntity)) {
            response[0] = responseEntity.getBody();
            if (responseType.equals(LoginResponse.class) && response[0] != null) {
                setToken((LoginResponse) response[0]);
            }
            return StatusCode.SUCCESS;
        }
        return StatusCode.FAIL;
    }

    private boolean isNetworkOperationSuccess(HttpEntity<?> responseHttpEntity) {
        return responseHttpEntity != null && responseHttpEntity.hasBody();
    }

    private void setToken(LoginResponse loginResponse) {
        if (loginResponse.getStatusCode().equals(StatusCode.SUCCESS)) {
            String token = loginResponse.getToken();
            mHttpHeaders.setBearerAuth(token);
        }
    }
}
