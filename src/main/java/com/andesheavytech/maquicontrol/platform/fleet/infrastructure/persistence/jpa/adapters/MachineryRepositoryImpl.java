package com.andesheavytech.maquicontrol.platform.fleet.infrastructure.persistence.jpa.adapters;

import com.andesheavytech.maquicontrol.platform.fleet.domain.model.aggregates.Machinery;
import com.andesheavytech.maquicontrol.platform.fleet.domain.repositories.MachineryRepository;
import com.andesheavytech.maquicontrol.platform.fleet.infrastructure.persistence.jpa.assemblers.MachineryPersistenceAssembler;
import com.andesheavytech.maquicontrol.platform.fleet.infrastructure.persistence.jpa.entities.MachineryPersistenceEntity;
import com.andesheavytech.maquicontrol.platform.fleet.infrastructure.persistence.jpa.repositories.MachineryPersistenceRepository;
import com.andesheavytech.maquicontrol.platform.shared.infrastructure.events.DomainEventPublisher;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class MachineryRepositoryImpl implements MachineryRepository {

    private final MachineryPersistenceRepository machineryPersistenceRepository;
    private final DomainEventPublisher domainEventPublisher;

    public MachineryRepositoryImpl(MachineryPersistenceRepository machineryPersistenceRepository,
                                   DomainEventPublisher domainEventPublisher) {
        this.machineryPersistenceRepository = machineryPersistenceRepository;
        this.domainEventPublisher = domainEventPublisher;
    }

    @Override
    public Machinery save(Machinery machinery) {
        var saved = machineryPersistenceRepository.save(MachineryPersistenceAssembler.toPersistenceFromDomain(machinery));
        domainEventPublisher.publishAndClearEvents(machinery);
        return MachineryPersistenceAssembler.toDomainFromPersistence(saved);
    }

    @Override
    public Optional<Machinery> findById(Long id) {
        return machineryPersistenceRepository.findById(id).map(MachineryPersistenceAssembler::toDomainFromPersistence);
    }

    @Override
    public List<Machinery> findAll() {
        return toDomain(machineryPersistenceRepository.findAll());
    }

    @Override
    public List<Machinery> findByCategoryId(Long categoryId) {
        return toDomain(machineryPersistenceRepository.findByCategoryId(categoryId));
    }

    @Override
    public List<Machinery> findByOwnerProfileId(Long ownerProfileId) {
        return toDomain(machineryPersistenceRepository.findByOwnerProfileId(ownerProfileId));
    }

    @Override
    public boolean existsByCategoryId(Long categoryId) {
        return machineryPersistenceRepository.existsByCategoryId(categoryId);
    }

    @Override
    public void deleteById(Long id) {
        machineryPersistenceRepository.deleteById(id);
    }

    private static List<Machinery> toDomain(List<MachineryPersistenceEntity> entities) {
        return entities.stream().map(MachineryPersistenceAssembler::toDomainFromPersistence).toList();
    }
}
