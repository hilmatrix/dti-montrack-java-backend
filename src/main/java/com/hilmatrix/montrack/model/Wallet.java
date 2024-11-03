package com.hilmatrix.montrack.model;

public class Wallet {
    private Integer id;
    private String name;
    private Integer amount;
    private Boolean isActive;

    // Constructors
    public Wallet(Integer id, String name, Integer amount, Boolean isActive) {
        this.id = id;
        this.name = name;
        this.amount = amount;
        this.isActive = isActive;
    }

    // Getters
    public Integer getId() { return id; }
    public String getName() { return name; }
    public Integer getAmount() { return amount; }
    public Boolean getIsActive() { return isActive; }
}
