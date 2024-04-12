package edu.java.services.jpa;

import edu.java.clients.apiclients.IAPIClient;
import edu.java.controller.dto.LinkResponse;
import edu.java.controller.exception.CantHandleURLException;
import edu.java.controller.exception.ChatNotFoundException;
import edu.java.controller.exception.LinkNotFoundException;
import edu.java.controller.exception.LinkReAddingException;
import edu.java.domain.jpa.JpaChatsDAO;
import edu.java.domain.jpa.JpaLinksDAO;
import edu.java.domain.jpa.entities.ChatEntity;
import edu.java.domain.jpa.entities.LinkEntity;
import edu.java.services.interfaces.ISubscribeService;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import org.springframework.transaction.annotation.Transactional;

public class JpaSubscribeService implements ISubscribeService {
    JpaChatsDAO chatRepository;
    JpaLinksDAO linkRepository;
    IAPIClient[] clients;

    public JpaSubscribeService(
        JpaChatsDAO chatRepository,
        JpaLinksDAO linkRepository,
        IAPIClient[] clients
    ) {
        this.chatRepository = chatRepository;
        this.linkRepository = linkRepository;
        this.clients = clients;
    }

    //А это ок делать проверки в транзакции?
    @Override
    @Transactional
    public List<LinkResponse> getTrackedURLs(Long chatId) {
        checkChatInSystem(chatId);
        List<LinkEntity> subscribesOfUser = chatRepository.findById(chatId).get().getSubscribes();
        return subscribesOfUser.stream().map(subscribe -> {
            try {
                return new LinkResponse(subscribe.getId(), new URI(subscribe.getUrl()));
            } catch (URISyntaxException e) {
                throw new RuntimeException(e);
            }
        }).toList();
    }

    //А это ок делать проверки в транзакции?
    @Override
    @Transactional
    public Long addTrackedURLs(Long chatId, String providedURL) {
        checkURL(providedURL);
        checkChatInSystem(chatId);
        if (!linkRepository.existsByUrl(providedURL)) {
            linkRepository.saveByUrl(providedURL);
        }

        Long linkId = linkRepository.getId(providedURL);
        ChatEntity chat = chatRepository.findById(chatId).get();
        LinkEntity link = linkRepository.findById(linkId).get();
        if (chat.getSubscribes().contains(link)) {
            throw new LinkReAddingException(
                "У чата со следующим id: " + chatId + " уже есть подпись на сайт: " + providedURL);
        }
        chat.addSubscribe(link);
        return linkId;
    }

    //А это ок делать проверки в транзакции?
    @Override
    @Transactional
    public Long removeTrackedURLs(Long chatId, String providedURL) {
        checkURL(providedURL);
        checkChatInSystem(chatId);
        if (!linkRepository.existsByUrl(providedURL)) {
            linkRepository.saveByUrl(providedURL);
        }
        Long linkId = linkRepository.getId(providedURL);
        ChatEntity chat = chatRepository.findById(chatId).get();
        LinkEntity link = linkRepository.findById(linkId).get();
        if (!chat.getSubscribes().contains(link)) {
            throw new LinkNotFoundException("У чата с id: " + chatId + " нет подписи на сайт: " + providedURL);
        }
        chat.removeSubscribe(link);
        return linkId;
    }

    private void checkChatInSystem(Long chatId) {
        if (!chatRepository.existsById(chatId)) {
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
