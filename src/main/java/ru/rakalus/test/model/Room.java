package ru.rakalus.test.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.sql.Date;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "rooms")
public class Room {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "rooms_seq")
    @SequenceGenerator(name = "rooms_seq",sequenceName = "rooms_seq", allocationSize = 1)
    private Integer id;

    @Column(name = "floor")
    private Integer floor;

    @Column(name = "number")
    private Integer number;

    @Column(name = "type")
    @Enumerated(EnumType.ORDINAL)
    private Sex type;

    @Column(name = "comfort")
    @Enumerated(EnumType.ORDINAL)
    private Comfort comfort;

    @Column(name = "beds")
    private Integer beds;

    @Column(name = "created")
    private Date created;

    @Column(name = "edited")
    private Date edited;

    @OneToMany(mappedBy = "room")
    @JsonIgnore
    private Set<Guest> guests;

    public Room() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Room room = (Room) o;
        return Objects.equals(id, room.id) && Objects.equals(floor, room.floor) && Objects.equals(number, room.number) && type == room.type && comfort == room.comfort && Objects.equals(beds, room.beds) && Objects.equals(created, room.created) && Objects.equals(edited, room.edited);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, floor, number, type, comfort, beds, created, edited);
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getFloor() {
        return floor;
    }

    public void setFloor(Integer floor) {
        this.floor = floor;
    }

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }

    public Sex getType() {
        return type;
    }

    public void setType(Sex type) {
        this.type = type;
    }

    public Comfort getComfort() {
        return comfort;
    }

    public void setComfort(Comfort comfort) {

        this.comfort = comfort;
    }

    public Integer getBeds() {
        return beds;
    }

    public void setBeds(Integer beds) {
        this.beds = beds;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public Date getEdited() {
        return edited;
    }

    public void setEdited(Date edited) {
        this.edited = edited;
    }

    public Set<Guest> getGuests() {
        return guests;
    }

    public void setGuests(Set<Guest> guests) {
        this.guests = guests;
    }
}
