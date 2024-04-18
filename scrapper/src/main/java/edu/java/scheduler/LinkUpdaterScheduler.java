package edu.java.scheduler;

import edu.java.configuration.ApplicationConfig;
import edu.java.services.interfaces.ILinkUpdateService;
import java.util.logging.Logger;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class LinkUpdaterScheduler {
    private static final Logger LOGGER = Logger.getLogger(LinkUpdaterScheduler.class.getName());

    private ApplicationConfig.Scheduler scheduler;
    private ILinkUpdateService linkUpdateService;

    public LinkUpdaterScheduler(ApplicationConfig.Scheduler scheduler, ILinkUpdateService linkUpdateService) {
        this.scheduler = scheduler;
        this.linkUpdateService = linkUpdateService;
    }

    @Scheduled(fixedDelayString = "#{@scheduler.interval}")
    public void update() {
        //Если я вообще всё правильно понял, то шедулер каждые inteval будет проверять ссылки и
        // те которые старше forceCheckDelay он проверяет через клиент. Надеюсь правильно понял задание
        linkUpdateService.update(scheduler.forceCheckDelay());
    }
}
