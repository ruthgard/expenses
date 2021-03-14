package org.ruthgard.expenses.repo;

import org.ruthgard.expenses.model.Settings;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface SettingsRepository extends CrudRepository<Settings, Long> {
}
