package ru.rakalus.test.service;

import ru.rakalus.test.model.Guest;

import java.util.List;

public interface GuestService {

    void create(Guest guest);

    List<Guest> readAll(Guest guest);

    Guest read(int id);

    void update(Guest guest, int id);

    boolean delete(int id);
}
