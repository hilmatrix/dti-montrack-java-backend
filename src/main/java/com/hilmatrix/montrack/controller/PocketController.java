package com.hilmatrix.montrack.controller;

import com.hilmatrix.montrack.exception.ResourceNotFoundException;
import com.hilmatrix.montrack.model.Pocket;
import com.hilmatrix.montrack.repository.PocketRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1")
public class PocketController {

    @Autowired
    private PocketRepository pocketRepository;
    private final JwtDecoder jwtDecoder;
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public PocketController(PocketRepository pocketRepository, JwtDecoder jwtDecoder, JdbcTemplate jdbcTemplate) {
        this.pocketRepository = pocketRepository;
        this.jwtDecoder = jwtDecoder;
        this.jdbcTemplate = jdbcTemplate;
    }

    public String getEmailFromJwt(String authorizationHeader) {
        String token = authorizationHeader.replace("Bearer ", "");
        Jwt jwt = jwtDecoder.decode(token);
        return jwt.getSubject();
    }

    public Long getUserIdFromJwt(String authorizationHeader) {
        String email = getEmailFromJwt(authorizationHeader);
        String sql = "SELECT id FROM users WHERE email = ?";
        return jdbcTemplate.queryForObject(sql, new Object[]{email}, Long.class);
    }

    public Long getWalletIdFromJwt(String authorizationHeader) {
        String email = getEmailFromJwt(authorizationHeader);

        // First, find the user ID based on the email
        String userSql = "SELECT id FROM users WHERE email = ?";
        Long userId = jdbcTemplate.queryForObject(userSql, new Object[]{email}, Long.class);

        // Then, find the active wallet ID for that user
        String walletSql = "SELECT id FROM wallets WHERE user_id = ? AND is_active = true";
        return jdbcTemplate.queryForObject(walletSql, new Object[]{userId}, Long.class);
    }

    @GetMapping("/pockets")
    public ResponseEntity<List<Pocket>> getAllPockets(@RequestHeader("Authorization") String authorizationHeader) {
        Long userId = getUserIdFromJwt(authorizationHeader);
        List<Pocket> pockets = pocketRepository.findByWalletId(getWalletIdFromJwt(authorizationHeader));
        return new ResponseEntity<>(pockets, HttpStatus.OK);
    }

    @GetMapping("/pocket/{id}")
    public ResponseEntity<Pocket> getPocketById(@RequestHeader("Authorization") String authorizationHeader, @PathVariable Long id) {
        Long userId = getUserIdFromJwt(authorizationHeader);

        Optional<Pocket> pocket = pocketRepository.findByIdAndWalletId(id, getWalletIdFromJwt(authorizationHeader));
        return pocket.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/pockets")
    public ResponseEntity<Pocket> createPocket(@RequestHeader("Authorization") String authorizationHeader, @RequestBody Pocket pocket) {
        String email = getEmailFromJwt(authorizationHeader);
        Long walletId = getWalletIdFromJwt(email);

        pocket.setWalletId(walletId); // Ensure the new pocket is linked to the authenticated user
        Pocket createdPocket = pocketRepository.save(pocket);
        return new ResponseEntity<>(createdPocket, HttpStatus.CREATED);
    }

    @PutMapping("/pocket/{id}")
    public ResponseEntity<Pocket> updatePocket(
            @RequestHeader("Authorization") String authorizationHeader,
            @PathVariable Long id, @RequestBody Pocket pocketDetails) {
        Long userId = getUserIdFromJwt(authorizationHeader);

        Pocket pocket = pocketRepository.findByIdAndWalletId(id, getWalletIdFromJwt(authorizationHeader))
                .orElseThrow(() -> new ResourceNotFoundException("Pocket not found for this id :: " + id));

        pocket.setWalletId(pocketDetails.getWalletId());
        pocket.setName(pocketDetails.getName());
        pocket.setDescription(pocketDetails.getDescription());
        pocket.setEmoji(pocketDetails.getEmoji());
        pocket.setAmountLimit(pocketDetails.getAmountLimit());
        pocket.setUpdatedAt(pocketDetails.getUpdatedAt());

        Pocket updatedPocket = pocketRepository.save(pocket);
        return ResponseEntity.ok(updatedPocket);
    }

    @DeleteMapping("/pocket/{id}")
    public ResponseEntity<Void> deletePocket(@RequestHeader("Authorization") String authorizationHeader, @PathVariable Long id) {
        Long userId = getUserIdFromJwt(authorizationHeader);

        Pocket pocket = pocketRepository.findByIdAndWalletId(id, getWalletIdFromJwt(authorizationHeader))
                .orElseThrow(() -> new ResourceNotFoundException("Pocket not found for this id :: " + id));

        pocketRepository.delete(pocket);
        return ResponseEntity.noContent().build();
    }
}
