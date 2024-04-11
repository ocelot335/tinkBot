package edu.java.services.jpa;

import edu.java.clients.BotApiException;
import edu.java.clients.BotClient;
import edu.java.clients.apiclients.GitHubClient;
import edu.java.clients.apiclients.IAPIClient;
import edu.java.domain.dto.LinkDTO;
import edu.java.domain.jpa.JpaLinksDAO;
import edu.java.domain.jpa.entities.LinkEntity;
import edu.java.services.interfaces.ILinkUpdateService;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@Service
@Slf4j
public class JpaLinkUpdateService implements ILinkUpdateService {
    private JpaLinksDAO linkRepository;
    private BotClient botClient;
    private IAPIClient[] apiClients;

    public JpaLinkUpdateService(
        JpaLinksDAO linkRepository,
        BotClient botClient,
        IAPIClient[] apiClients
    ) {
        this.linkRepository = linkRepository;
        this.botClient = botClient;
        this.apiClients = apiClients;
    }

    @Override
    public void update(Duration forceCheckDelay) {
        OffsetDateTime now = OffsetDateTime.now();
        /*
        List<LinkEntity> links = linkRepository.findAll();
        List<LinkEntity> linksToCheck = links.stream().filter(link -> Duration.between(link.getCheckedAt(), now)
            .compareTo(forceCheckDelay) >= 0).toList();
        */
        List<LinkEntity> linksToCheck =
            linkRepository.findAllFilteredToCheck(OffsetDateTime.now().minus(forceCheckDelay));
        for (LinkEntity link : linksToCheck) {
            OffsetDateTime lastUpdatedTimeBeforeCheck = link.getLastUpdatedAt();
            if (check(link)) {
                notifyUsers(link, getUsersToNotify(link), getDescription(link, lastUpdatedTimeBeforeCheck));
            }
            link.setCheckedAt(now);
        }
        linkRepository.saveAll(linksToCheck);
    }

    @Transactional
    public List<Long> getUsersToNotify(LinkEntity link) {
        return linkRepository.getIdsOfUsersToNotifyByLinkId(link.getId());
    }

    private boolean check(LinkEntity link) {
        for (IAPIClient client : apiClients) {
            if (client.isCorrectURL(link.getUrl())) {
                OffsetDateTime lastUpdatedAt;
                try {
                    lastUpdatedAt = client.getResponse(new LinkDTO(link.getUrl())).getLastUpdatedAt();
                } catch (WebClientResponseException | NullPointerException e) {
                    continue;
                    //TODO::handle not existing urls ?? Maybe delete from DataBase?
                }
                if (link.getLastUpdatedAt().isBefore(lastUpdatedAt)) {
                    link.setLastUpdatedAt(lastUpdatedAt);
                    return true;
                }
                break;
            }
        }
        return false;
    }

    private String getDescription(LinkEntity link, OffsetDateTime lastUpdatedTimeBeforeLastUpdate) {
        for (IAPIClient client : apiClients) {
            if (client.isCorrectURL(link.getUrl())) {
                if (client instanceof GitHubClient) {
                    return ((GitHubClient) client).getDescription(
                        new LinkDTO(link.getUrl()),
                        lastUpdatedTimeBeforeLastUpdate
                    );
                } else {
                    break;
                }
            }
        }

        //Default description
        return "Обновление произошло в " + link.getLastUpdatedAt().toString();
    }

    private void notifyUsers(LinkEntity link, List<Long> userIdsToNotify, String description) {
        try {
            botClient.postUpdates(
                link.getId(),
                link.getUrl(),
                description,
                userIdsToNotify
            );
        } catch (BotApiException e) {
            log.error(e.getMessage());
        }
    }
}
