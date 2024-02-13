package ru.rakalus.test.service;

import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;
import ru.rakalus.test.model.Guest;
import ru.rakalus.test.model.Room;
import ru.rakalus.test.repository.GuestRepository;

import java.sql.Date;
import java.util.Calendar;
import java.util.List;

@Service
public class GuestServiceImpl implements GuestService {

    private final GuestRepository repository;

    public GuestServiceImpl(GuestRepository repository) {
        this.repository = repository;
    }


    @Override
    public void create(Guest guest) {
        repository.save(guest);
    }

    @Override
    public List<Guest> readAll(Guest guest) {
        if (guest != null) return repository.findAll(Example.of(guest));
        else return repository.findAll();
    }

    @Override
    public Guest read(int id) {
        return repository.findById(id).orElseThrow(RuntimeException::new);
    }

    @Override
    public void update(Guest guest, int id) {
        Guest temp = repository.findById(id).orElseThrow(RuntimeException::new);
        if (guest.getRoom() != null) {
            temp.setRoom(guest.getRoom());
        }
        if (guest.getSurname() != null) {
            temp.setSurname(guest.getSurname());
        }
        if (guest.getName() != null) {
            temp.setName(guest.getName());
        }
        if (guest.getPatronymic() != null) {
            temp.setPatronymic(guest.getPatronymic());
        }
        if (guest.getSex() != null) {
            temp.setSex(guest.getSex());
        }
        temp.setEdited(new Date(Calendar.getInstance().getTime().getTime()));
        repository.save(temp);
    }

    @Override
    public boolean delete(int id) {
        if (repository.existsById(id)) {
            repository.deleteById(id);
            return true;
        }
        return false;
    }
}
