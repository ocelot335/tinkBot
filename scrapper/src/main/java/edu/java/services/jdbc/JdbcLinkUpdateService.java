package edu.java.services.jdbc;

import edu.java.clients.apiclients.GitHubClient;
import edu.java.clients.apiclients.IAPIClient;
import edu.java.clients.dto.LinkUpdate;
import edu.java.domain.dto.LinkDTO;
import edu.java.domain.dto.SubscribeDTO;
import edu.java.domain.jdbc.JdbcLinksDAO;
import edu.java.domain.jdbc.JdbcSubscribesDAO;
import edu.java.services.IMessageTransporter;
import edu.java.services.interfaces.ILinkUpdateService;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Objects;
import org.springframework.web.reactive.function.client.WebClientResponseException;

public class JdbcLinkUpdateService implements ILinkUpdateService {
    private JdbcLinksDAO linkRepository;
    private JdbcSubscribesDAO subscribesRepository;
    private IMessageTransporter messageTransporter;
    private IAPIClient[] apiClients;

    public JdbcLinkUpdateService(
        JdbcLinksDAO linkRepository,
        IMessageTransporter messageTransporter,
        IAPIClient[] apiClients,
        JdbcSubscribesDAO subscribesRepository
    ) {
        this.linkRepository = linkRepository;
        this.messageTransporter = messageTransporter;
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

    private boolean check(LinkDTO link) {
        for (IAPIClient client : apiClients) {
            if (client.isCorrectURL(link.getUrl())) {
                OffsetDateTime lastUpdatedAt;
                try {
                    lastUpdatedAt = client.getResponse(link).getLastUpdatedAt();
                } catch (WebClientResponseException | NullPointerException e) {
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
        messageTransporter.send(new LinkUpdate(link.getId(), link.getUrl(), description, usersToNotify));
    }
}
