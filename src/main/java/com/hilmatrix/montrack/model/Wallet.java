package com.hilmatrix.montrack.model;

public class Wallet {
    private Integer id;
    private Integer userId;
    private String name;
    private Integer amount;
    private Boolean isActive;

    // Constructors
    public Wallet(Integer id, Integer userId, String name, Integer amount, Boolean isActive) {
        this.id = id;
        this.name = name;
        this.amount = amount;
        this.isActive = isActive;
        this.userId = userId;
    }

    // Getters
    public Integer getId() { return id; }
    public Integer getUserId() { return userId; }
    public String getName() { return name; }
    public Integer getAmount() { return amount; }
    public Boolean getIsActive() { return isActive; }
}
