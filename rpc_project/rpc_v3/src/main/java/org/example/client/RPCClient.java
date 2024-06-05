package org.example.client;

import org.example.com.Request;
import org.example.com.Response;

public interface RPCClient {
    Response sendRequest(Request request);
}
