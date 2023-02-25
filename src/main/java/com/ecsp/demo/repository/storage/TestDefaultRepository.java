package com.ecsp.demo.repository.storage;

import com.ecsp.demo.models.entity.storage.TestDefaultEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TestDefaultRepository extends JpaRepository<TestDefaultEntity, Long> {
}
