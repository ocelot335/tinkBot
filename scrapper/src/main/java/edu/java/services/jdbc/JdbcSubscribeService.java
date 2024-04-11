package edu.java.services.jdbc;

import edu.java.clients.apiclients.IAPIClient;
import edu.java.controller.dto.LinkResponse;
import edu.java.controller.exception.CantHandleURLException;
import edu.java.controller.exception.ChatNotFoundException;
import edu.java.controller.exception.LinkNotFoundException;
import edu.java.controller.exception.LinkReAddingException;
import edu.java.domain.dto.LinkDTO;
import edu.java.domain.jdbc.JdbcChatsDAO;
import edu.java.domain.jdbc.JdbcLinksDAO;
import edu.java.domain.jdbc.JdbcSubscribesDAO;
import edu.java.services.interfaces.ISubscribeService;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

public class JdbcSubscribeService implements ISubscribeService {

    JdbcChatsDAO chatRepository;
    JdbcLinksDAO linkRepository;
    JdbcSubscribesDAO subscribesRepository;
    IAPIClient[] clients;

    public JdbcSubscribeService(
        JdbcChatsDAO chatRepository,
        JdbcLinksDAO linkRepository,
        JdbcSubscribesDAO subscribesRepository,
        IAPIClient[] clients
    ) {
        this.chatRepository = chatRepository;
        this.linkRepository = linkRepository;
        this.subscribesRepository = subscribesRepository;
        this.clients = clients;
    }

    @Override
    public List<LinkResponse> getTrackedURLs(Long chatId) {
        checkChatInSystem(chatId);
        List<LinkDTO> subscribesOfUser = subscribesRepository.findAllLinksByChatId(chatId);
        return subscribesOfUser.stream().map(subscribe -> {
            try {
                return new LinkResponse(subscribe.getId(), new URI(subscribe.getUrl()));
            } catch (URISyntaxException e) {
                throw new RuntimeException(e);
            }
        }).toList();
    }

    @Override
    public Long addTrackedURLs(Long chatId, String providedURL) {
        checkURL(providedURL);
        checkChatInSystem(chatId);
        if (!linkRepository.contains(providedURL)) {
            linkRepository.add(providedURL);
        }

        Long linkId = linkRepository.getId(providedURL);
        if (subscribesRepository.contains(chatId, linkId)) {
            throw new LinkReAddingException(
                "У чата со следующим id: " + chatId + " уже есть подпись на сайт: " + providedURL);
        }
        subscribesRepository.add(chatId, linkId);
        return linkId;
    }

    @Override
    public Long removeTrackedURLs(Long chatId, String providedURL) {
        checkURL(providedURL);
        checkChatInSystem(chatId);
        if (!linkRepository.contains(providedURL)) {
            linkRepository.add(providedURL);
        }
        Long linkId = linkRepository.getId(providedURL);
        if (!subscribesRepository.contains(chatId, linkId)) {
            throw new LinkNotFoundException("У чата с id: " + chatId + " нет подписи на сайт: " + providedURL);

        }
        subscribesRepository.remove(chatId, linkId);
        return linkId;
    }

    private void checkChatInSystem(Long chatId) {
        if (!chatRepository.contains(chatId)) {
            throw new ChatNotFoundException("Нет чата с id: " + chatId);
        }
    }

    private void throwBadURLException(String url) {
        throw new CantHandleURLException("Сервис не может обработать ссылку: " + url);
    }

    private void checkURL(String providedURL) {
        URI url = null;
        try {
            url = new URI(providedURL);
        } catch (URISyntaxException ex) {
            throwBadURLException(providedURL);
        }

        for (IAPIClient client : clients) {
            if (client.isCorrectURL(providedURL)) {
                return;
            }
        }
        throw new CantHandleURLException("Сервис не работает с данным сайтом: " + url);
    }
}
