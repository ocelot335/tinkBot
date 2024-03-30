package edu.java.services.jooq;

import edu.java.clients.BotClient;
import edu.java.clients.apiclients.GitHubClient;
import edu.java.clients.apiclients.IAPIClient;
import edu.java.domain.jdbc.dto.LinkDTO;
import edu.java.domain.jdbc.dto.SubscribeDTO;
import edu.java.domain.jooq.JooqLinksDAO;
import edu.java.domain.jooq.JooqSubscribesDAO;
import edu.java.services.interfaces.ILinkUpdateService;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Objects;
import lombok.SneakyThrows;
import org.springframework.web.reactive.function.client.WebClientResponseException;

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
            if (check(link)) {
                String description = getDescription(link);
                notifyUsers(link, subscribes, description);
            }
            linkRepository.updateCheckedAt(link, now);
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

    private String getDescription(LinkDTO link) {
        for (IAPIClient client : apiClients) {
            if (client.isCorrectURL(link.getUrl())) {
                if (client instanceof GitHubClient) {
                    return ((GitHubClient) client).getDescription(link, link.getLastUpdatedAt());
                } else {
                    break;
                }
            }
        }

        //Default description
        return "Обновление произошло в " + link.getLastUpdatedAt().toString();
    }

    private void notifyUsers(LinkDTO link, List<SubscribeDTO> subscribes, String description) {
        List<Long> usersToNotify =
            subscribes.stream().filter(subscribeDTO -> Objects.equals(subscribeDTO.getLinkId(), link.getId()))
                .map(SubscribeDTO::getChatId).toList();
        botClient.postUpdates(link.getId(), link.getUrl(), description, usersToNotify);
    }
}
