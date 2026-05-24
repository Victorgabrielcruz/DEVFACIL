package com.devfacil.api.repository;

import com.devfacil.api.model.EventOutbox;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventOutboxRepository extends JpaRepository<EventOutbox, Long> {
}
