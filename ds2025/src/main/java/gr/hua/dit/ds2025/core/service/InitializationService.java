package gr.hua.dit.ds2025.core.service;

import gr.hua.dit.ds2025.core.model.Client;
import gr.hua.dit.ds2025.core.model.Role;
import gr.hua.dit.ds2025.core.repositories.ClientRepository;
import gr.hua.dit.ds2025.core.service.model.CreateUserRequest;
import jakarta.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;


@Service
public class InitializationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(InitializationService.class);

    private final ClientRepository clientRepository;
    private final UserBusinessLogicService userBusinessLogicService;
    private final AtomicBoolean initialized;

    public InitializationService(final ClientRepository clientRepository,
                                 final UserBusinessLogicService personBusinessLogicService) {
        if (clientRepository == null) throw new NullPointerException();
        if (personBusinessLogicService == null) throw new NullPointerException();
        this.clientRepository = clientRepository;
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
