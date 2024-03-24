package edu.java.services.jdbc;

import edu.java.clients.BotClient;
import edu.java.clients.apiclients.IAPIClient;
import edu.java.domain.jdbc.JdbcLinksDAO;
import edu.java.domain.jdbc.dto.LinkDTO;
import edu.java.services.interfaces.ILinkUpdateService;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import java.net.URI;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.List;

@Service
public class JdbcLinkUpdateService implements ILinkUpdateService {
    private JdbcLinksDAO linkRepository;
    private BotClient botClient;
    private IAPIClient[] apiClients;

    public JdbcLinkUpdateService(JdbcLinksDAO linkRepository, BotClient botClient, IAPIClient[] apiClients) {
        this.linkRepository = linkRepository;
        this.botClient = botClient;
        this.apiClients = apiClients;
    }

    @Override
    public void update(Duration forceCheckDelay) {
        List<LinkDTO> links = linkRepository.findAll();
        OffsetDateTime now = OffsetDateTime.now();
        List<LinkDTO> linksToCheck = links.stream().filter(link -> Duration.between(link.getCheckedAt(), now)
            .compareTo(forceCheckDelay) >= 0).toList();

        for (LinkDTO link : linksToCheck) {
            if (check(link)) {
                notifyUsers(link);
            }
        }
    }

    @SneakyThrows
    private boolean check(LinkDTO link) {
        URI url = new URI(link.getUrl());
        for (IAPIClient client : apiClients) {
            if (client.getClientName().equals(url.getHost())) {
                if (link.getLastUpdatedAt().isBefore(client.getResponse(link).getLastUpdatedAt())) {
                    linkRepository.updateLastUpdate(link, client.getResponse(link).getLastUpdatedAt());
                    return true;
                }
                break;
            }
        }
        return false;
    }

    private void notifyUsers(LinkDTO link) {

    }
}
