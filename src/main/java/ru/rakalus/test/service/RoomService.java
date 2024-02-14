package ru.rakalus.test.service;

import ru.rakalus.test.model.Room;

import java.util.List;

public interface RoomService {
    Room create(Room room);

    List<Room> readAll(Room room, String empty);

    Room read(int id);

    Room update(Room room, int id);

    boolean delete(int id);
}
