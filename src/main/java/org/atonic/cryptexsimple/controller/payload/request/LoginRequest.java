package org.atonic.cryptexsimple.controller.payload.request;

import lombok.Data;

@Data
public class LoginRequest {
    private String username;
    private String password;
}
