package pl.thinkdata.droptop.baselinker.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.ResponseEntity;

public interface BaselinkerSendable<RES, REQ> {
    RES sendRequest(REQ req) throws JsonProcessingException;

    default RES mapToResponse(ResponseEntity<String> response, Class<RES> clazz) {
        try {
            return new ObjectMapper().readValue(response.getBody(), clazz);
        } catch (Exception e) {
            throw new RuntimeException("Deserialization Error", e);
        }
    }

}
