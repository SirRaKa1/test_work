package ru.rakalus.test.service;

import ru.rakalus.test.model.Guest;

import java.util.List;

public interface GuestService {

    Guest create(Guest guest);

    List<Guest> readAll(Guest guest);

    Guest read(int id);

    Guest update(Guest guest, int id);

    boolean delete(int id);
}
