package ru.rakalus.test.service;

import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;
import ru.rakalus.test.model.Room;
import ru.rakalus.test.repository.RoomRepository;

import java.sql.Date;
import java.util.Calendar;
import java.util.List;

@Service
public class RoomServiceImpl implements RoomService{

    private final RoomRepository repository;

    public RoomServiceImpl(RoomRepository repository){
        this.repository = repository;
    }

    @Override
    public void create(Room room) {
        repository.save(room);
    }

    @Override
    public List<Room> readAll(Room room) {
        if (room!=null)
            return repository.findAll(Example.of(room));
        else
            return repository.findAll();
    }

    @Override
    public Room read(int id) {
        return repository.findById(id)
                .orElseThrow(RuntimeException::new);
    }

    @Override
    public void update(Room room, int id) {
        Room temp = repository.findById(id)
                .orElseThrow(RuntimeException::new);
        if(room.getBeds()!=null){
            temp.setBeds(room.getBeds());
        }
        if (room.getComfort()!=null){
            temp.setComfort(room.getComfort());
        }
        if (room.getNumber()!=null){
            temp.setNumber(room.getNumber());
        }
        if (room.getType()!=null){
            temp.setType(room.getType());
        }
        if (room.getFloor()!=null){
            temp.setFloor(room.getFloor());
        }
        temp.setEdited(new Date(Calendar.getInstance().getTime().getTime()));
        repository.save(temp);
    }

    @Override
    public boolean delete(int id) {
        if (repository.existsById(id)){
            repository.deleteById(id);
            return true;
        }
        return false;
    }
}
