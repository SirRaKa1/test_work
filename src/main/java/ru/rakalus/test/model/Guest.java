package ru.rakalus.test.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;

import java.sql.Date;
import java.util.Objects;

@Entity
@Table(name = "guests")
public class Guest {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "guests_seq")
    @SequenceGenerator(name = "guests_seq",sequenceName = "guests_seq", allocationSize = 1)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "room")
    private Room room;

    @Column(name = "surname")
    private String surname;

    @Column(name = "name")
    private String name;

    @Column(name = "patronymic")
    private String patronymic;

    @Column(name = "sex")
    @Enumerated(EnumType.ORDINAL)
    private Sex sex;

    @Column(name = "created")
    private Date created;

    @Column(name = "edited")
    private Date edited;

    public Guest() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Guest guest = (Guest) o;
        return Objects.equals(id, guest.id) && Objects.equals(room.getId(), guest.room.getId()) && Objects.equals(surname, guest.surname) && Objects.equals(name, guest.name) && Objects.equals(patronymic, guest.patronymic) && sex == guest.sex && Objects.equals(created, guest.created) && Objects.equals(edited, guest.edited);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, room, surname, name, patronymic, sex, created, edited);
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPatronymic() {
        return patronymic;
    }

    public void setPatronymic(String patronymic) {
        this.patronymic = patronymic;
    }

    public Sex getSex() {
        return sex;
    }

    public void setSex(Sex sex) {
        this.sex = sex;
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
}
