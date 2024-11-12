package com.hilmatrix.montrack.repository;

import com.hilmatrix.montrack.model.Pocket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PocketRepository extends JpaRepository<Pocket, Integer> {

}
