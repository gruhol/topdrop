package pl.thinkdata.droptop.baselinker.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;

public interface BaselinkerSendable<RES, REQ> {
    Logger log = LoggerFactory.getLogger(BaselinkerSendable.class);
    ObjectMapper MAPPER = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    RES sendRequest(REQ req) throws JsonProcessingException;

    default RES mapToResponse(ResponseEntity<String> response, Class<RES> clazz) {
        try {
            return MAPPER.readValue(response.getBody(), clazz);
        } catch (Exception e) {
            log.error("Deserialization Error: {} | Raw response: {}", clazz.getName(), response.getBody(), e);
            throw new RuntimeException("Deserialization Error: " + clazz.getName(), e);
        }
    }

}
