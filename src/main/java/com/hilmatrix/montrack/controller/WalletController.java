package com.hilmatrix.montrack.controller;

import com.hilmatrix.montrack.model.Wallet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/")
public class WalletController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @GetMapping("/wallets")
    public List<Wallet> getAllWallets() {
        String sql = "SELECT id, name, amount, is_active FROM wallets";
        return jdbcTemplate.query(sql, (rs, rowNum) ->
                new Wallet(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getInt("amount"),
                        rs.getBoolean("is_active")
                )
        );
    }

    @GetMapping("/wallet/{id}")
    public ResponseEntity<Wallet> getWalletById(@PathVariable int id) {
        String sql = "SELECT id, name, amount, is_active FROM wallets WHERE id = ?";
        try {
            Wallet wallet = jdbcTemplate.queryForObject(sql, new Object[]{id}, (rs, rowNum) ->
                    new Wallet(
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getInt("amount"),
                            rs.getBoolean("is_active")
                    )
            );
            return ResponseEntity.ok(wallet);
        } catch (EmptyResultDataAccessException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
