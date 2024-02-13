package ru.rakalus.test.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.rakalus.test.model.Guest;

public interface GuestRepository extends JpaRepository<Guest, Integer> {
}
