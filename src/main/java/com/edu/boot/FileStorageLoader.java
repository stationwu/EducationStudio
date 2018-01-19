package com.edu.boot;

import com.edu.storage.FileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
@Order(1)
public class FileStorageLoader implements CommandLineRunner {
    @Autowired
    private Environment environment;

    @Autowired
    FileStorageService service;

    @Override
    public void run(String... args) throws Exception {
        String ddlAuto = environment.getProperty("spring.jpa.hibernate.ddl-auto");
        if (ddlAuto.equals("create-drop") || ddlAuto.equals("create")) {
            service.deleteAll();
        }
        service.init();
    }
}
