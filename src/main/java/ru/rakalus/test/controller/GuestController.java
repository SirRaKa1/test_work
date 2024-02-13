package ru.rakalus.test.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.rakalus.test.model.Guest;
import ru.rakalus.test.service.GuestService;

import java.sql.Date;
import java.util.Calendar;
import java.util.List;

@RestController
public class GuestController {

    private final GuestService guestService;

    @Autowired
    public GuestController(GuestService guestService) {
        this.guestService = guestService;
    }

    @PostMapping(value = "/guests")
    public ResponseEntity<?> create(@RequestBody Guest guest) {
        guest.setCreated(new Date(Calendar.getInstance().getTime().getTime()));
        guest.setEdited(new Date(Calendar.getInstance().getTime().getTime()));
        guestService.create(guest);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @GetMapping(value = "/guests")
    public ResponseEntity<List<Guest>> read(@RequestBody(required = false) Guest guest) {
        final List<Guest> rooms = guestService.readAll(guest);

        return rooms != null && !rooms.isEmpty()
                ? new ResponseEntity<>(rooms, HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.NOT_FOUND);
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
            guestService.update(guest, id);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_MODIFIED);
        }

    }

    @DeleteMapping(value = "/guests/{id}")
    public ResponseEntity<?> delete(@PathVariable(name = "id") int id) {
        final boolean deleted = guestService.delete(id);

        return deleted
                ? new ResponseEntity<>(HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.NOT_MODIFIED);
    }

}
