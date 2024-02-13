package ru.rakalus.test.model;

import io.hypersistence.utils.hibernate.type.basic.PostgreSQLCITextType;

import jakarta.persistence.*;
import org.hibernate.annotations.Type;
import org.hibernate.dialect.PostgreSQLEnumJdbcType;


import java.sql.Date;

@Entity
@Table(name = "rooms")
public class Room {
    @Id
    @Column(name = "id")
    @GeneratedValue
    private Integer id;

    @Column(name = "floor")
    private Integer floor;

    @Column(name = "number")
    private Integer number;

    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    private Sex type;

    @Column(name = "comfort")
    @Enumerated(EnumType.STRING)
    private Comfort comfort;

    @Column(name = "beds")
    private Integer beds;

    @Column(name = "created")
    private Date created;

    @Column(name = "edited")
    private Date edited;

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
}
