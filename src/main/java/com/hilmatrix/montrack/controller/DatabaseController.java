package com.hilmatrix.montrack.controller;

import com.hilmatrix.montrack.model.Wallet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/wallets")
public class DatabaseController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @GetMapping
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
}
