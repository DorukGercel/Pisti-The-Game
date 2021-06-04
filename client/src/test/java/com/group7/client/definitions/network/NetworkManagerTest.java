package com.group7.client.definitions.network;

import com.group7.client.definitions.common.StatusCode;
import com.group7.client.definitions.network.NetworkManager;
import com.group7.client.dto.common.CommonResponse;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {
        NetworkManager.class
})
public class NetworkManagerTest {

    private NetworkManager mNetworkManager;

    @Autowired
    public void setManager(NetworkManager networkManager){
        this.mNetworkManager = networkManager;
    }

    @Test
    public void testExchange_Fail_NoApiAddress() {
        StatusCode statusCode = mNetworkManager.exchange("", HttpMethod.GET, null, null, CommonResponse.class);
        assertEquals(StatusCode.FAIL, statusCode);
    }

    @Test
    public void testExchange_Fail_NoMethod() {
        StatusCode statusCode = mNetworkManager.exchange("localhost:8080", null, null, null, CommonResponse.class);
        assertEquals(StatusCode.FAIL, statusCode);
    }

    @Test
    public void testExchange_Fail_NoRequest() {
        StatusCode statusCode = mNetworkManager.exchange("localhost:8080", HttpMethod.POST, null, null, CommonResponse.class);
        assertEquals(StatusCode.FAIL, statusCode);
    }
}
