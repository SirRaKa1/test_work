package ru.rakalus.test.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.rakalus.test.model.Room;
import ru.rakalus.test.service.RoomService;

import java.sql.Date;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

@RestController
public class RoomController {

    private final RoomService roomService;

    @Autowired
    public RoomController(RoomService roomService){
        this.roomService = roomService;
    }

    @PostMapping(value = "/rooms")
    public ResponseEntity<?> create(@RequestBody Room room){
        room.setCreated(new Date(Calendar.getInstance().getTime().getTime()));
        room.setEdited(new Date(Calendar.getInstance().getTime().getTime()));
        roomService.create(room);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @GetMapping(value = "/rooms")
    public ResponseEntity<List<Room>> read(@RequestBody(required = false) Room room){
        final List<Room> rooms = roomService.readAll(room);

        return rooms!=null && !rooms.isEmpty()
                ? new ResponseEntity<>(rooms,HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping(value = "/rooms/{id}")
    public ResponseEntity<Room> read(@PathVariable(name = "id") int id) {

        try{
            final Room room = roomService.read(id);
            return new ResponseEntity<>(room, HttpStatus.OK);
        }
        catch (Exception e){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

    }

    @PutMapping(value = "/rooms/{id}")
    public ResponseEntity<?> update(@PathVariable(name = "id") int id, @RequestBody Room room) {
        try {
            roomService.update(room, id);
            return new ResponseEntity<>(HttpStatus.OK);
        }
        catch (Exception e){
            return new ResponseEntity<>(HttpStatus.NOT_MODIFIED);
        }

    }

    @DeleteMapping(value = "/rooms/{id}")
    public ResponseEntity<?> delete(@PathVariable(name = "id") int id) {
        final boolean deleted = roomService.delete(id);

        return deleted
                ? new ResponseEntity<>(HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.NOT_MODIFIED);
    }
}