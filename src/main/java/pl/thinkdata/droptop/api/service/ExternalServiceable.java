package pl.thinkdata.droptop.api.service;

import pl.thinkdata.droptop.api.dto.GetPublicationsDto;
import pl.thinkdata.droptop.api.dto.PlatonResponse;

public interface ExternalServiceable<T> {
    PlatonResponse get(T request);
}
