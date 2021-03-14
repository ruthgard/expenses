package org.ruthgard.expenses.repo;

import org.ruthgard.expenses.model.Expense;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;


public interface ExpenseRepository extends JpaRepository<Expense, Long> {
    @Query("SELECT e FROM Expense e WHERE date >= :mydate ORDER BY date DESC, id DESC")
    Page<Expense> findBeforeDate(@Param("mydate") Date date, Pageable pageable);
    @Query("SELECT e FROM Expense e WHERE date >= :mydate")
    List<Expense> findBeforeDate(@Param("mydate") Date date);
    @Query("SELECT COUNT(e) FROM Expense e WHERE date >= :mydate")
    long count(@Param("mydate") Date date);


}
