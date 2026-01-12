package gr.hua.dit.ds2025.core.service;

import jakarta.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicBoolean;


@Service
public class InitializationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(InitializationService.class);

    private final UserBusinessLogicService userBusinessLogicService;
    private final AtomicBoolean initialized;

    public InitializationService(final UserBusinessLogicService personBusinessLogicService) {
        if (personBusinessLogicService == null) throw new NullPointerException();
        this.userBusinessLogicService = personBusinessLogicService;
        this.initialized = new AtomicBoolean(false);
    }

    @PostConstruct
    public void populateDatabaseWithInitialData() {
        final boolean alreadyInitialized = this.initialized.getAndSet(true);
        if (alreadyInitialized) {
            LOGGER.warn("Database initialization skipped: initial data has already been populated.");
            return;
        }
        LOGGER.info("Starting database initialization with initial data...");

        userBusinessLogicService.createAdmin(
                "Admin",
                "Admin",
                "admin",
                "admin@example.com",
                "admin123"
        );

        LOGGER.info("Database initialization completed successfully.");
    }


}
