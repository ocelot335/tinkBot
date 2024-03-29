package edu.java.services.jooq;

import edu.java.clients.BotClient;
import edu.java.clients.apiclients.IAPIClient;
import edu.java.domain.jdbc.JdbcLinksDAO;
import edu.java.domain.jdbc.JdbcSubscribesDAO;
import edu.java.domain.jdbc.dto.LinkDTO;
import edu.java.domain.jdbc.dto.SubscribeDTO;
import edu.java.domain.jooq.JooqLinksDAO;
import edu.java.domain.jooq.JooqSubscribesDAO;
import edu.java.services.interfaces.ILinkUpdateService;
import lombok.SneakyThrows;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.List;

public class JooqLinkUpdateService implements ILinkUpdateService {
    private JooqLinksDAO linkRepository;
    private JooqSubscribesDAO subscribesRepository;
    private BotClient botClient;
    private IAPIClient[] apiClients;

    public JooqLinkUpdateService(
        JooqLinksDAO linkRepository,
        BotClient botClient,
        IAPIClient[] apiClients,
        JooqSubscribesDAO subscribesRepository
    ) {
        this.linkRepository = linkRepository;
        this.botClient = botClient;
        this.apiClients = apiClients;
        this.subscribesRepository = subscribesRepository;
    }

    @Override
    public void update(Duration forceCheckDelay) {
        OffsetDateTime now = OffsetDateTime.now();
        /*
        List<LinkDTO> links = linkRepository.findAll();
        List<LinkDTO> linksToCheck = links.stream().filter(link -> Duration.between(link.getCheckedAt(), now)
            .compareTo(forceCheckDelay) >= 0).toList();
        */
        List<LinkDTO> linksToCheck = linkRepository.findAllFilteredToCheck(forceCheckDelay);
        List<SubscribeDTO> subscribes = subscribesRepository.findAllSubscribes();
        for (LinkDTO link : linksToCheck) {
            linkRepository.updateCheckedAt(link, now);
            if (check(link)) {
                notifyUsers(link, subscribes);
            }
        }
    }

    @SneakyThrows
    private boolean check(LinkDTO link) {
        for (IAPIClient client : apiClients) {
            if (client.isCorrectURL(link.getUrl())) {
                OffsetDateTime lastUpdatedAt;
                try {
                    lastUpdatedAt = client.getResponse(link).getLastUpdatedAt();
                } catch (WebClientResponseException e) {
                    continue;
                    //TODO::handle not existing urls ?? Maybe delete from DataBase?
                }
                if (link.getLastUpdatedAt().isBefore(lastUpdatedAt)) {
                    linkRepository.updateLastUpdate(link, lastUpdatedAt);
                    return true;
                }
                break;
            }
        }
        return false;
    }

    private void notifyUsers(LinkDTO link, List<SubscribeDTO> subscribes) {
        List<Long> usersToNotify =
            subscribes.stream().filter(subscribeDTO -> subscribeDTO.getLinkId() == link.getId())
                .map(SubscribeDTO::getChatId).toList();
        botClient.postUpdates(link.getId(), link.getUrl(), "TODO", usersToNotify);
    }
}