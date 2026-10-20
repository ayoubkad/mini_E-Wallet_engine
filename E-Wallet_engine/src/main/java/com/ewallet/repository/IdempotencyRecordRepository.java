package com.ewallet.repository;

import com.ewallet.entity.IdempotencyRecord;
import com.ewallet.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface IdempotencyRecordRepository extends JpaRepository<IdempotencyRecord, String> {

    Optional<IdempotencyRecord> findByKey(String key);

    boolean existsByKey(String key);


}
