package org.atonic.cryptexsimple.controller.payload.response;

import lombok.Data;
import lombok.NonNull;

@Data
public class MessageResponse {
    @NonNull
    private String message;
}
