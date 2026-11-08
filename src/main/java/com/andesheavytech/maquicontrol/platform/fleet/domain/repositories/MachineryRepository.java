package com.andesheavytech.maquicontrol.platform.fleet.domain.repositories;

import com.andesheavytech.maquicontrol.platform.fleet.domain.model.aggregates.Machinery;

import java.util.List;
import java.util.Optional;

public interface MachineryRepository {

    Machinery save(Machinery machinery);

    Optional<Machinery> findById(Long id);

    List<Machinery> findAll();

    List<Machinery> findByCategoryId(Long categoryId);

    List<Machinery> findByOwnerProfileId(Long ownerProfileId);

    boolean existsByCategoryId(Long categoryId);

    void deleteById(Long id);
}
