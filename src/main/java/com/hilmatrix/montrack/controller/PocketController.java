package com.hilmatrix.montrack.controller;

import com.hilmatrix.montrack.model.Pocket;
import com.hilmatrix.montrack.repository.PocketRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1")
public class PocketController {

    @Autowired
    private PocketRepository pocketRepository;

    @GetMapping("/pockets")
    public List<Pocket> getAllPockets() {
        return pocketRepository.findAll();
    }

    @GetMapping("/pocket/{id}")
    public ResponseEntity<Pocket> getPocketById(@PathVariable Integer id) {
        Optional<Pocket> pocket = pocketRepository.findById(id);
        return pocket.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public Pocket createPocket(@RequestBody Pocket pocket) {
        return pocketRepository.save(pocket);
    }

    @PutMapping("/pocket/{id}")
    public ResponseEntity<Pocket> updatePocket(@PathVariable Integer id, @RequestBody Pocket pocketDetails) {
        Optional<Pocket> optionalPocket = pocketRepository.findById(id);

        if (optionalPocket.isPresent()) {
            Pocket pocket = optionalPocket.get();
            pocket.setWalletId(pocketDetails.getWalletId());
            pocket.setName(pocketDetails.getName());
            pocket.setDescription(pocketDetails.getDescription());
            pocket.setEmoji(pocketDetails.getEmoji());
            pocket.setAmountLimit(pocketDetails.getAmountLimit());
            pocket.setUpdatedAt(pocketDetails.getUpdatedAt());

            Pocket updatedPocket = pocketRepository.save(pocket);
            return ResponseEntity.ok(updatedPocket);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/pocket/{id}")
    public ResponseEntity<Void> deletePocket(@PathVariable Integer id) {
        if (pocketRepository.existsById(id)) {
            pocketRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
