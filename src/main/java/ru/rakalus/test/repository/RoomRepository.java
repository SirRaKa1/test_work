package ru.rakalus.test.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.rakalus.test.model.Room;

public interface RoomRepository extends JpaRepository<Room, Integer> {
}
