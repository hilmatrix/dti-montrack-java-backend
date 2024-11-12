package com.hilmatrix.montrack.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.Data;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "pockets")
@Data
public class Pocket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "wallet_id")
    private Integer walletId;

    private String name;
    private String description;
    private String emoji;

    @Column(name = "amount_limit")
    private Integer amountLimit;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;
}
