package org.ruthgard.expenses.repo;

import org.ruthgard.expenses.model.User;
import org.ruthgard.expenses.model.Wallet;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface WalletRepository extends CrudRepository<Wallet, Long> {
    List<Wallet> findByName(String name);
}
