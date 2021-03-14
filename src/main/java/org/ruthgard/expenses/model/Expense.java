package org.ruthgard.expenses.model;

import org.hibernate.annotations.IndexColumn;

import javax.persistence.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Entity
public class Expense {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false)
    private Date date;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Double amount;

    @Lob
    @Column(nullable = true)
    private String description;

    @ManyToMany( fetch = FetchType.LAZY)
    @JoinTable(name = "expense_payers",
            joinColumns = {
            @JoinColumn(name = "expense_id", referencedColumnName = "id",
                    nullable = false, updatable = true)},
            inverseJoinColumns = {
                    @JoinColumn(name = "wallet_id", referencedColumnName = "id",
                            nullable = false, updatable = true)}
    )
    private List<Wallet> payers = new ArrayList<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "expense_buyers",
    joinColumns = {
        @JoinColumn(name = "expense_id", referencedColumnName = "id",
                nullable = false, updatable = true)},
    inverseJoinColumns = {
        @JoinColumn(name = "wallet_id", referencedColumnName = "id",
                nullable = false, updatable = true)}
    )
    private List<Wallet> byers = new ArrayList<>();


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getStringDate() {
        if(date == null) return null;
        String pattern = "yyyy-MM-dd";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        return simpleDateFormat.format(date);
    }

    public void setStringDate(String date) {
        String pattern = "yyyy-MM-dd";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        Date myDate = null;
        try {
            this.date = simpleDateFormat.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public void setDate(Date date) { this.date = date; }

    public Date getDate() {return this.date; }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public List<Wallet> getPayers() {
        return payers;
    }

    public void setPayers(List<Wallet> payers) {
        this.payers = payers;
    }

    public void addPayer(Wallet payer) {
        if ( this.payers == null )
            this.payers = new ArrayList<Wallet>();
        this.payers.add(payer);
    }

    public List<Wallet> getByers() {
        return byers;
    }

    public void setByers(List<Wallet> byers) {
        this.byers = byers;
    }

    public void addBuyer(Wallet buyer) {
        if ( this.byers == null )
            this.byers = new ArrayList<Wallet>();
        this.byers.add(buyer);
    }
}
