package ru.rakalus.test.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.rakalus.test.model.Comfort;
import ru.rakalus.test.model.Room;
import ru.rakalus.test.model.Sex;
import ru.rakalus.test.service.RoomService;

import java.sql.Date;
import java.util.Calendar;
import java.util.List;

@RestController
public class RoomController {

    private final RoomService roomService;

    @Autowired
    public RoomController(RoomService roomService) {
        this.roomService = roomService;
    }

    @PostMapping(value = "/rooms")
    public ResponseEntity<?> create(@RequestBody Room room) {
        try {
            room.setCreated(new Date(Calendar.getInstance().getTime().getTime()));
            room.setEdited(new Date(Calendar.getInstance().getTime().getTime()));

            return new ResponseEntity<>(roomService.create(room), HttpStatus.CREATED);
        }
        catch (Exception e){
            return new ResponseEntity<>("Room is invalid",HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping(value = "/rooms")
    public ResponseEntity<?> read(@RequestParam(value = "empty", defaultValue = "false")String empty,@RequestParam(value = "type", required = false)String type,@RequestParam(value = "comfort",required = false)String comfort) {
        Room room = new Room();
        if (type!=null)
        {
            type = type.trim().toLowerCase();
            switch (type) {
                case "male":
                    room.setType(Sex.male);
                    break;
                case "female":
                    room.setType(Sex.female);
                    break;
                default:
                    return new ResponseEntity<>("Type must be \"male\" or \"female\"", HttpStatus.BAD_REQUEST);
            }
        }
        if (comfort!=null)
        {
            comfort = comfort.trim().toLowerCase();
            switch (comfort){
                case "standard":
                    room.setComfort(Comfort.standard);
                    break;
                case "high_comfort":
                    room.setComfort(Comfort.high_comfort);
                    break;
                case "luxury":
                    room.setComfort(Comfort.luxury);
                    break;
                default:
                    return new ResponseEntity<>("Comfort must be \"standard\", \"high_comfort\" or \"luxury\"", HttpStatus.BAD_REQUEST);
            }
        }
        boolean fl = Boolean.parseBoolean(empty);
        final List<Room> rooms = roomService.readAll(room,fl);

        return rooms != null && !rooms.isEmpty()
                ? new ResponseEntity<>(rooms, HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping(value = "/rooms/{id}")
    public ResponseEntity<Room> read(@PathVariable(name = "id") int id) {

        try {
            final Room room = roomService.read(id);
            return new ResponseEntity<>(room, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

    }

    @PutMapping(value = "/rooms/{id}")
    public ResponseEntity<?> update(@PathVariable(name = "id") int id, @RequestBody Room room) {
        try {
            if(!roomService.read(id).getGuests().isEmpty())
                return new ResponseEntity<>("You can't modify room when there are guests",HttpStatus.BAD_REQUEST);
            return new ResponseEntity<>(roomService.update(room, id),HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

    }

    @DeleteMapping(value = "/rooms/{id}")
    public ResponseEntity<?> delete(@PathVariable(name = "id") int id) {

        try {
            if (!roomService.read(id).getGuests().isEmpty())
                return new ResponseEntity<>("You can't delete room when there are guests", HttpStatus.BAD_REQUEST);
            roomService.delete(id);
            return new ResponseEntity<>(HttpStatus.OK);
        }
        catch (Exception e){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
