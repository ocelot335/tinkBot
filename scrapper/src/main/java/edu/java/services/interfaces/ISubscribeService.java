package edu.java.services.interfaces;

import edu.java.controller.dto.LinkResponse;
import java.util.List;

public interface ISubscribeService {
    List<LinkResponse> getTrackedURLs(Long chatId);

    Long addTrackedURLs(Long chatId, String providedURL);

    Long removeTrackedURLs(Long chatId, String providedURL);
}
