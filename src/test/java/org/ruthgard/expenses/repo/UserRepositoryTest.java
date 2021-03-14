package org.ruthgard.expenses.repo;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.ruthgard.expenses.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class UserRepositoryTest  {


    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {


        User gustav = new User();
        gustav.setName("Gustav Ruthgård");
        gustav.setEmail("gustav@ruthgard.org");
        gustav.setPassword("asasd");
        gustav.setAdmin(false);

        userRepository.save(gustav);

        User maria = new User();
        maria.setName("Maria Ruthgård");
        maria.setEmail("maria@ruthgard.org");
        maria.setPassword("ölkölk");
        maria.setAdmin(false);

        userRepository.save(maria);

        User thomas = new User();
        thomas.setName("Thomas Padron McCarthy");
        thomas.setEmail("thomas.padron.mccarthy@oru.se");
        thomas.setPassword("zxczxc");
        thomas.setAdmin(false);

        userRepository.save(thomas);

    }
    @AfterEach
    void tearDown() {
    }

    @Test
    void findByName() {
       List<User> gustav = userRepository.findByName("Gustav Ruthgård");
       assertTrue(gustav.size() > 0, "Fler än 0 träffar");

        List<User> maria = userRepository.findByName("Maria Ruthgård");
        assertTrue(maria.size() > 0, "Fler än 0 träffar");

        List<User> thomas = userRepository.findByName("Thomas Padron McCarthy");
        assertTrue(thomas.size() > 0, "Fler än 0 träffar");
    }

    @Test
    void findAll() {
        Iterable<User> all = userRepository.findAll();
        int count = 0;
        for (User user : all) {
            count++;
            assertTrue(user.getName().length() > 0 , "Vi har ett namn!");
        }
        assertTrue(count > 2, "Tre users!");

    }
}