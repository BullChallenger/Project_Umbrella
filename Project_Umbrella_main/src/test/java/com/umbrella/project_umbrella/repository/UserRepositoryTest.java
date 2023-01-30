package com.umbrella.project_umbrella.repository;

import org.junit.jupiter.api.AfterEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

@SpringBootTest
@Transactional
public class UserRepositoryTest {

    @Autowired
    UserRepository userRepository;
    @Autowired
    EntityManager em;

    @AfterEach
    private void after() {
        em.clear();
    }
}
