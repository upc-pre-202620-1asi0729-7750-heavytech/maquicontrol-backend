package com.andesheavytech.maquicontrol.platform.profiles.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
public interface OrganizationJpaRepository extends JpaRepository<OrganizationPersistenceEntity, Long> { boolean existsByTaxId(String taxId); }
