package org.ruthgard.expenses.repo;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.ruthgard.expenses.model.User;
import org.ruthgard.expenses.model.Wallet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class WalletRepositoryTest {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private WalletRepository walletRepository;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();

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

        walletRepository.deleteAll();

        Wallet ruthgard = new Wallet();
        ruthgard.setName("Ruthgårds");

        ArrayList ruthgardList = new ArrayList<User>();
        ruthgardList.add(gustav);
        ruthgardList.add(maria);
        ruthgard.setUsers(ruthgardList);
        walletRepository.save(ruthgard);


        Wallet padron = new Wallet();
        padron.setName("Padrone");

        ArrayList padronList = new ArrayList<User>();
        padronList.add(thomas);
        padron.setUsers(padronList);
        walletRepository.save(padron);
    }

    @Test
    void findByName() {
        List<Wallet> ruthgard = walletRepository.findByName("Ruthgårds");
        assertTrue(ruthgard.size() > 0, "Fler än 0 Ruthgårds");

        List<Wallet> padron = walletRepository.findByName("Padrone");
        assertTrue(padron.size() > 0, "Fler än 0 Padrones");

        Wallet r = ruthgard.get(0);
        Wallet p = padron.get(0);

        assertTrue(r.getUsers().size() == 2, "Två användare i Ruthgårds");
        assertTrue(p.getUsers().size() == 1, "En användare i Padrone");

    }
}