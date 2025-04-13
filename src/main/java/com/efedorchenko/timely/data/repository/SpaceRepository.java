package com.efedorchenko.timely.data.repository;

import com.efedorchenko.timely.data.entity.Space;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SpaceRepository extends JpaRepository<Space, Long> {

    Optional<Space> findByWorkerKey(String workerKey);

    Optional<Space> findByBossKey(String bossKey);

}
