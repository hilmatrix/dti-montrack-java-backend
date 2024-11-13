package com.hilmatrix.montrack.repository;

import com.hilmatrix.montrack.model.Pocket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PocketRepository extends JpaRepository<Pocket, Long> {
    List<Pocket> findByWalletId(Long userId);
    Optional<Pocket> findByIdAndWalletId(Long id, Long walletId);
}