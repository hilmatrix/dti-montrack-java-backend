package com.hilmatrix.montrack.controller;

import com.hilmatrix.montrack.model.Wallet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api/v1/")
public class WalletController {

    private final JwtDecoder jwtDecoder;
    private final JdbcTemplate jdbcTemplate;

    public WalletController(JwtDecoder jwtDecoder, JdbcTemplate jdbcTemplate) {
        this.jwtDecoder = jwtDecoder;
        this.jdbcTemplate = jdbcTemplate;
    }

    @GetMapping("/wallets")
    public List<Wallet> getUserWallets(@RequestHeader("Authorization") String authorizationHeader) {
        try {
            // Extract token from Authorization header
            String token = authorizationHeader.replace("Bearer ", "");

            // Decode the JWT to get email
            Jwt jwt = jwtDecoder.decode(token);
            String email = jwt.getSubject();

            // Query to get the user ID from the users table using email
            String getUserIdSql = "SELECT id FROM users WHERE email = ?";
            Integer userId = jdbcTemplate.queryForObject(getUserIdSql, new Object[]{email}, Integer.class);

            if (userId == null) {
                return Collections.emptyList(); // Return an empty list if user not found
            }

            // Query to get wallets where wallets.user_id = userId
            String getWalletsSql = "SELECT id, user_id, name, amount, is_active FROM wallets WHERE user_id = ?";
            return jdbcTemplate.query(getWalletsSql, new Object[]{userId}, (rs, rowNum) ->
                    new Wallet(
                            rs.getInt("id"),
                            rs.getInt("user_id"),
                            rs.getString("name"),
                            rs.getInt("amount"),
                            rs.getBoolean("is_active")
                    )
            );

        } catch (Exception e) {
            // Handle exceptions, e.g., JWT decoding or database errors
            return Collections.emptyList();
        }
    }

    @GetMapping("/wallet/{id}")
    public ResponseEntity<Wallet> getWalletById(
            @RequestHeader("Authorization") String authorizationHeader,
            @PathVariable int id) {
        try {
            // Extract token from Authorization header
            String token = authorizationHeader.replace("Bearer ", "");

            // Decode the JWT to get email
            Jwt jwt = jwtDecoder.decode(token);
            String email = jwt.getSubject();

            // Query to get the user ID from the users table using email
            String getUserIdSql = "SELECT id FROM users WHERE email = ?";
            Integer userId = jdbcTemplate.queryForObject(getUserIdSql, new Object[]{email}, Integer.class);

            if (userId == null) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build(); // Forbidden if user not found
            }

            // Query to get the wallet by ID only if it belongs to the authenticated user
            String getWalletSql = "SELECT id, user_id, name, amount, is_active FROM wallets WHERE id = ? AND user_id = ?";
            Wallet wallet = jdbcTemplate.queryForObject(getWalletSql, new Object[]{id, userId}, (rs, rowNum) ->
                    new Wallet(
                            rs.getInt("id"),
                            rs.getInt("user_id"),
                            rs.getString("name"),
                            rs.getInt("amount"),
                            rs.getBoolean("is_active")
                    )
            );

            return ResponseEntity.ok(wallet);

        } catch (EmptyResultDataAccessException e) {
            return ResponseEntity.notFound().build(); // Wallet not found or doesn't belong to the user
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build(); // Handle other errors
        }
    }
}
