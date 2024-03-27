package edu.java.services.jdbc;

import edu.java.clients.BotClient;
import edu.java.clients.apiclients.IAPIClient;
import edu.java.domain.jdbc.JdbcLinksDAO;
import edu.java.domain.jdbc.JdbcSubscribesDAO;
import edu.java.domain.jdbc.dto.LinkDTO;
import edu.java.domain.jdbc.dto.SubscribeDTO;
import edu.java.services.interfaces.ILinkUpdateService;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.List;

@Service
public class JdbcLinkUpdateService implements ILinkUpdateService {
    private JdbcLinksDAO linkRepository;
    private JdbcSubscribesDAO subscribesRepository;
    private BotClient botClient;
    private IAPIClient[] apiClients;

    public JdbcLinkUpdateService(
        JdbcLinksDAO linkRepository,
        BotClient botClient,
        IAPIClient[] apiClients,
        JdbcSubscribesDAO subscribesRepository
    ) {
        this.linkRepository = linkRepository;
        this.botClient = botClient;
        this.apiClients = apiClients;
        this.subscribesRepository = subscribesRepository;
    }

    @Override
    public void update(Duration forceCheckDelay) {
        List<LinkDTO> links = linkRepository.findAll();
        OffsetDateTime now = OffsetDateTime.now();
        List<LinkDTO> linksToCheck = links.stream().filter(link -> Duration.between(link.getCheckedAt(), now)
            .compareTo(forceCheckDelay) >= 0).toList();
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
                if (link.getLastUpdatedAt().isBefore(client.getResponse(link).getLastUpdatedAt())) {
                    linkRepository.updateLastUpdate(link, client.getResponse(link).getLastUpdatedAt());
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
