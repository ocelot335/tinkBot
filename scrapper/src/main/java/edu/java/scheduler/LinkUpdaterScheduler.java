package edu.java.scheduler;

import edu.java.configuration.ApplicationConfig;
import java.util.List;
import java.util.logging.Logger;
import edu.java.domain.jdbc.JdbcLinksDAO;
import edu.java.domain.jdbc.dto.LinkDTO;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class LinkUpdaterScheduler {
    private static final Logger LOGGER = Logger.getLogger(LinkUpdaterScheduler.class.getName());

    private ApplicationConfig.Scheduler scheduler;
    private JdbcLinksDAO k;

    public LinkUpdaterScheduler(ApplicationConfig.Scheduler scheduler) {
        this.scheduler = scheduler;
    }

    @Scheduled(fixedDelayString = "#{@scheduler.interval}")
    public void update() {
        List<LinkDTO> links = null;
    }
}
