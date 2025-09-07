package pl.thinkdata.droptop.baselinker.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Unmarshaller;

public interface BaselinkerSendable<REQ, D, RES> {
    RES sendRequest(REQ req, D date) throws JsonProcessingException;

    default <RES> RES deserialize(String json, Class<RES> clazz) {
        try {
            JAXBContext context = jakarta.xml.bind.JAXBContext.newInstance(clazz);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            return clazz.cast(unmarshaller.unmarshal(new java.io.StringReader(json)));
        } catch (Exception e) {
            throw new RuntimeException("Błąd deserializacji XML", e);
        }
    }

}
