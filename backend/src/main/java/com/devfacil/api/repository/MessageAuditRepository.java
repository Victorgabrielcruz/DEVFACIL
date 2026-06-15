package com.devfacil.api.repository;

import com.devfacil.api.model.MessageAudit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MessageAuditRepository extends JpaRepository<MessageAudit, Long> {

    List<MessageAudit> findTop100ByOrderByCreatedAtDesc();
}
