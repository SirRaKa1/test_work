package ru.rakalus.test.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.rakalus.test.model.Comfort;
import ru.rakalus.test.model.Guest;
import ru.rakalus.test.model.Room;
import ru.rakalus.test.model.Sex;
import ru.rakalus.test.service.GuestService;
import ru.rakalus.test.service.RoomService;

import java.sql.Date;
import java.util.Calendar;
import java.util.List;

@RestController
public class GuestController {

    private final GuestService guestService;
    private final RoomService roomService;

    @Autowired
    public GuestController(GuestService guestService, RoomService roomService) {
        this.guestService = guestService;
        this.roomService = roomService;
    }

    @PostMapping(value = "/guests")
    public ResponseEntity<?> create(@RequestBody Guest guest) {
        try {
            if (guest.getRoom().getId() != null) {
                Room room = roomService.read(guest.getRoom().getId());
                if ((room.getBeds() > room.getGuests().size()) && (room.getType() == guest.getSex())) {
                    guest.setCreated(new Date(Calendar.getInstance().getTime().getTime()));
                    guest.setEdited(new Date(Calendar.getInstance().getTime().getTime()));
                    return new ResponseEntity<>(guestService.create(guest),HttpStatus.CREATED);
                } else {
                    String s;
                    if (room.getBeds() <= room.getGuests().size()) s = "The room " + room.getNumber() + " is full";
                    else s = "The room " + room.getNumber() + " has incompatible type with new guest's sex";
                    return new ResponseEntity<>(s, HttpStatus.BAD_REQUEST);
                }
            } else
                return new ResponseEntity<>("Guest must have room id", HttpStatus.BAD_REQUEST);
        }
        catch (RuntimeException e){
            if (e instanceof DataIntegrityViolationException){
                return new ResponseEntity<>("Guest is invalid", HttpStatus.BAD_REQUEST);
            }
            return new ResponseEntity<>("Guest must have valid room id", HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping(value = "/guests")
    public ResponseEntity<?> read(@RequestParam(name = "sex",required = false)String sex,@RequestParam(name = "room",required = false)String room_id,@RequestParam(name = "comfort",required = false)String comfort) {
        try {
            Guest guest = new Guest();
            Room room = new Room();
            if (sex != null) {
                sex = sex.trim().toLowerCase();
                switch (sex) {
                    case "male":
                        guest.setSex(Sex.male);
                        break;
                    case "female":
                        guest.setSex(Sex.female);
                        break;
                    case "yes":
                        return new ResponseEntity<>("Ok... G E N D E R", HttpStatus.BAD_REQUEST);
                    default:
                        return new ResponseEntity<>("Sex must be \"male\" or \"female\"", HttpStatus.BAD_REQUEST);
                }
            }
            if (comfort != null) {
                comfort = comfort.trim().toLowerCase();
                switch (comfort) {
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

            if (room_id != null)
                room.setId(Integer.valueOf(room_id));

            guest.setRoom(room);

            final List<Guest> rooms = guestService.readAll(guest);

            return rooms != null && !rooms.isEmpty()
                    ? new ResponseEntity<>(rooms, HttpStatus.OK)
                    : new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        catch (Exception e){
            return new ResponseEntity<>("Room id must be an integer type", HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping(value = "/guests/{id}")
    public ResponseEntity<Guest> read(@PathVariable(name = "id") int id) {

        try {
            final Guest guest = guestService.read(id);
            return new ResponseEntity<>(guest, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

    }

    @PutMapping(value = "/guests/{id}")
    public ResponseEntity<?> update(@PathVariable(name = "id") int id, @RequestBody Guest guest) {
        try {
            if ((guest.getRoom() != null) && (guest.getRoom().getId() != null)) {
                Room room = roomService.read(guest.getRoom().getId());
                if (room.getBeds() <= room.getGuests().size())
                    return new ResponseEntity<>("The room " + room.getNumber() + " is full", HttpStatus.BAD_REQUEST);
                if ((guest.getSex() != null) && (room.getType() != guest.getSex()))
                    return new ResponseEntity<>("The room " + room.getNumber() + " has incompatible type with the guest's new sex", HttpStatus.BAD_REQUEST);
                if ((guest.getSex() == null) && (guestService.read(id).getSex()!=room.getType())){
                    return new ResponseEntity<>("The room " + room.getNumber() + " has incompatible type with the guest's sex", HttpStatus.BAD_REQUEST);
                }
            } else guest.setRoom(null);


            guest = guestService.update(guest, id);
            return new ResponseEntity<>(guest, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping(value = "/guests/{id}")
    public ResponseEntity<?> delete(@PathVariable(name = "id") int id) {
        final boolean deleted = guestService.delete(id);

        return deleted
                ? new ResponseEntity<>(HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

}
