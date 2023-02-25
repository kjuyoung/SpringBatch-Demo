package com.ecsp.demo.repository.validation;

import com.ecsp.demo.models.entity.validation.TestValidEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TestValidRepository extends JpaRepository<TestValidEntity, Long> {
}
