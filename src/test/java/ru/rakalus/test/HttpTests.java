package ru.rakalus.test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.client.RestClient;
import ru.rakalus.test.model.Comfort;
import ru.rakalus.test.model.Guest;
import ru.rakalus.test.model.Room;
import ru.rakalus.test.model.Sex;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.springframework.http.MediaType.APPLICATION_JSON;


class RoomControllerTest {

    @Test
    public void allHttpRoom() throws JSONException, IOException {
        RestClient client = RestClient.create();

        JSONObject putRoom = new JSONObject();
        putRoom.put("number", 1);
        putRoom.put("floor", 1);
        putRoom.put("type", Sex.male);
        putRoom.put("comfort", Comfort.standard);
        putRoom.put("beds", 10);

        Room room = client.post()
                .uri("http://localhost:8080/rooms")
                .contentType(APPLICATION_JSON)
                .body(putRoom.toString())
                .retrieve()
                .onStatus(HttpStatusCode::isError, ((request, response) -> {
                    throw new RuntimeException();
                }))
                .body(Room.class);
        assert room != null;

        var roomListRaw = client.get()
                .uri("http://localhost:8080/rooms")
                .retrieve()
                .onStatus(HttpStatusCode::isError, ((request, response) -> {
                    throw new RuntimeException();
                }))
                .body(List.class);

        assert roomListRaw != null;

        assert !roomListRaw.isEmpty();

        List<Room> roomList = deserializer(roomListRaw);

        assert roomList.contains(room);


        assert room.equals(client.get()
                .uri("http://localhost:8080/rooms/" + room.getId())
                .retrieve()
                .onStatus(HttpStatusCode::isError, ((request, response) -> {
                    throw new RuntimeException();
                }))
                .body(Room.class));

        JSONObject changes = new JSONObject();
        changes.put("type", "female");
        Room put = client.put()
                .uri("http://localhost:8080/rooms/" + room.getId())
                .contentType(APPLICATION_JSON)
                .body(changes.toString())
                .retrieve()
                .onStatus(HttpStatusCode::isError, ((request, response) -> {
                    throw new RuntimeException();
                }))
                .body(Room.class);

        assert put != null;
        assert (Objects.equals(put.getId(), room.getId())) && (put.getType() != room.getType());


        client.delete()
                .uri("http://localhost:8080/rooms/" + room.getId())
                .retrieve()
                .onStatus(HttpStatusCode::isError, ((request, response) -> {
                    throw new RuntimeException();
                }));

    }

    @Test
    public void filtration() throws JSONException, IOException {
        RestClient client = RestClient.create();

        JSONObject putRoom1 = new JSONObject();
        putRoom1.put("number", 1);
        putRoom1.put("floor", 1);
        putRoom1.put("type", Sex.male);
        putRoom1.put("comfort", Comfort.standard);
        putRoom1.put("beds", 1);


        Room room1 = client.post()
                .uri("http://localhost:8080/rooms")
                .contentType(APPLICATION_JSON)
                .body(putRoom1.toString())
                .retrieve()
                .onStatus(HttpStatusCode::isError, ((request, response) -> {
                    throw new RuntimeException();
                }))
                .body(Room.class);
        assert room1 != null;

        JSONObject putRoom2 = new JSONObject();
        putRoom2.put("number", 2);
        putRoom2.put("floor", 1);
        putRoom2.put("type", Sex.female);
        putRoom2.put("comfort", Comfort.high_comfort);
        putRoom2.put("beds", 10);

        Room room2 = client.post()
                .uri("http://localhost:8080/rooms")
                .contentType(APPLICATION_JSON)
                .body(putRoom2.toString())
                .retrieve()
                .onStatus(HttpStatusCode::isError, ((request, response) -> {
                    throw new RuntimeException();
                }))
                .body(Room.class);
        assert room2 != null;


        JSONObject putRoom3 = new JSONObject();
        putRoom3.put("number", 3);
        putRoom3.put("floor", 1);
        putRoom3.put("type", Sex.female);
        putRoom3.put("comfort", Comfort.luxury);
        putRoom3.put("beds", 10);

        Room room3 = client.post()
                .uri("http://localhost:8080/rooms")
                .contentType(APPLICATION_JSON)
                .body(putRoom3.toString())
                .retrieve()
                .onStatus(HttpStatusCode::isError, ((request, response) -> {
                    throw new RuntimeException();
                }))
                .body(Room.class);
        assert room3 != null;

        Guest guest = new Guest();
        guest.setRoom(room1);
        guest.setName("Richard");
        guest.setSurname("Dickson");
        guest.setSex(Sex.male);

        guest = client.post()
                .uri("http://localhost:8080/guests")
                .contentType(APPLICATION_JSON)
                .body(guest)
                .retrieve()
                .onStatus(HttpStatusCode::isError, ((request, response) -> {
                    throw new RuntimeException();
                }))
                .body(Guest.class);
        assert guest != null;

        List<Room> roomList;

        //проверка на пустые комнаты

        roomList = deserializer(Objects.requireNonNull(client.get()
                .uri("http://localhost:8080/rooms?empty=true")
                .retrieve()
                .onStatus(HttpStatusCode::isError, ((request, response) -> {
                    throw new RuntimeException();
                }))
                .body(List.class)));

        assert !(roomList.contains(room1))&&(roomList.contains(room2))&&(roomList.contains(room3));

        roomList = deserializer(Objects.requireNonNull(client.get()
                .uri("http://localhost:8080/rooms?empty=AAAAAAAAAAAAAAAAAAAAAAAA")
                .retrieve()
                .onStatus(HttpStatusCode::isError, ((request, response) -> {
                    throw new RuntimeException();
                }))
                .body(List.class)));

        assert (roomList.contains(room1))&&(roomList.contains(room2))&&(roomList.contains(room3));

        //проверка на тип комнаты

        roomList = deserializer(Objects.requireNonNull(client.get()
                .uri("http://localhost:8080/rooms?type=male")
                .retrieve()
                .onStatus(HttpStatusCode::isError, ((request, response) -> {
                    throw new RuntimeException();
                }))
                .body(List.class)));

        assert (roomList.contains(room1))&&!(roomList.contains(room2))&&!(roomList.contains(room3));

        roomList = deserializer(Objects.requireNonNull(client.get()
                .uri("http://localhost:8080/rooms?type=female")
                .retrieve()
                .onStatus(HttpStatusCode::isError, ((request, response) -> {
                    throw new RuntimeException();
                }))
                .body(List.class)));

        assert !(roomList.contains(room1))&&(roomList.contains(room2))&&(roomList.contains(room3));

        client.get()
                .uri("http://localhost:8080/rooms?type=AAAAAAAAAAAAAAAAAAAAAAAA")
                .retrieve()
                .onStatus(HttpStatusCode::is2xxSuccessful, ((request, response) -> {
                    throw new RuntimeException();
                }));


        //проверка на уровень комфорта

        roomList = deserializer(Objects.requireNonNull(client.get()
                .uri("http://localhost:8080/rooms?comfort=standard")
                .retrieve()
                .onStatus(HttpStatusCode::isError, ((request, response) -> {
                    throw new RuntimeException();
                }))
                .body(List.class)));

        assert (roomList.contains(room1))&&!(roomList.contains(room2))&&!(roomList.contains(room3));

        roomList = deserializer(Objects.requireNonNull(client.get()
                .uri("http://localhost:8080/rooms?comfort=high_comfort")
                .retrieve()
                .onStatus(HttpStatusCode::isError, ((request, response) -> {
                    throw new RuntimeException();
                }))
                .body(List.class)));

        assert !(roomList.contains(room1))&&(roomList.contains(room2))&&!(roomList.contains(room3));

        roomList = deserializer(Objects.requireNonNull(client.get()
                .uri("http://localhost:8080/rooms?comfort=luxury")
                .retrieve()
                .onStatus(HttpStatusCode::isError, ((request, response) -> {
                    throw new RuntimeException();
                }))
                .body(List.class)));

        assert !(roomList.contains(room1))&&!(roomList.contains(room2))&&(roomList.contains(room3));


        client.get()
                .uri("http://localhost:8080/rooms?comfort=AAAAAAAAAAAAAAAAAAAAAAAA")
                .retrieve()
                .onStatus(HttpStatusCode::is2xxSuccessful, ((request, response) -> {
                    throw new RuntimeException();
                }));


        client.delete()
                .uri("http://localhost:8080/guests/" + guest.getId())
                .retrieve()
                .onStatus(HttpStatusCode::isError, ((request, response) -> {
                    throw new RuntimeException();
                }));

        client.delete()
                .uri("http://localhost:8080/rooms/" + room1.getId())
                .retrieve()
                .onStatus(HttpStatusCode::isError, ((request, response) -> {
                    throw new RuntimeException();
                }));

        client.delete()
                .uri("http://localhost:8080/rooms/" + room2.getId())
                .retrieve()
                .onStatus(HttpStatusCode::isError, ((request, response) -> {
                    throw new RuntimeException();
                }));

        client.delete()
                .uri("http://localhost:8080/rooms/" + room3.getId())
                .retrieve()
                .onStatus(HttpStatusCode::isError, ((request, response) -> {
                    throw new RuntimeException();
                }));


    }

    @Test
    public void limitations() throws JSONException {
        RestClient client = RestClient.create();

        JSONObject putRoom = new JSONObject();
        putRoom.put("number", 1);
        putRoom.put("floor", 1);
        putRoom.put("type", Sex.male);
        putRoom.put("comfort", Comfort.standard);
        putRoom.put("beds", 1);

        Room room = client.post()
                .uri("http://localhost:8080/rooms")
                .contentType(APPLICATION_JSON)
                .body(putRoom.toString())
                .retrieve()
                .onStatus(HttpStatusCode::isError, ((request, response) -> {
                    throw new RuntimeException();
                }))
                .body(Room.class);
        assert room != null;



        Guest guest = new Guest();
        Room post = new Room();

        post.setId(room.getId());

        guest.setRoom(post);
        guest.setName("Richard");
        guest.setSurname("Dickson");
        guest.setSex(Sex.male);


        guest = client.post()
                .uri("http://localhost:8080/guests")
                .contentType(APPLICATION_JSON)
                .body(guest)
                .retrieve()
                .onStatus(HttpStatusCode::isError, ((request, response) -> {
                    throw new RuntimeException();
                }))
                .body(Guest.class);
        assert guest != null;

        JSONObject changes = new JSONObject();
        changes.put("type", "female");
        client.put()
                .uri("http://localhost:8080/rooms/" + room.getId())
                .contentType(APPLICATION_JSON)
                .body(changes.toString())
                .retrieve()
                .onStatus(HttpStatusCode::is2xxSuccessful, ((request, response) -> {
                    throw new RuntimeException();
                }));



        client.delete()
                .uri("http://localhost:8080/rooms/" + room.getId())
                .retrieve()
                .onStatus(HttpStatusCode::is2xxSuccessful, ((request, response) -> {
                    throw new RuntimeException();
                }));


        client.delete()
                .uri("http://localhost:8080/guests/" + guest.getId())
                .retrieve()
                .onStatus(HttpStatusCode::isError, ((request, response) -> {
                    throw new RuntimeException();
                }));

        client.delete()
                .uri("http://localhost:8080/rooms/" + room.getId())
                .retrieve()
                .onStatus(HttpStatusCode::isError, ((request, response) -> {
                    throw new RuntimeException();
                }));


    }

    private List<Room> deserializer(List raw) throws JSONException, JsonProcessingException {
        List<Room> roomList = new ArrayList<>();

        ObjectMapper objectMapper = new ObjectMapper();

        JSONObject convert;

        for (var temp : raw) {
            convert = new JSONObject(String.valueOf(temp));
            roomList.add(objectMapper.readValue(convert.toString(), Room.class));
        }
        return roomList;
    }



}





class GuestControllerTest {

    @Test
    public void allHttpGuest() throws JSONException, JsonProcessingException {
        RestClient client = RestClient.create();

        JSONObject putRoom = new JSONObject();
        putRoom.put("number", 1);
        putRoom.put("floor", 1);
        putRoom.put("type", Sex.male);
        putRoom.put("comfort", Comfort.standard);
        putRoom.put("beds", 10);

        Room room = client.post()
                .uri("http://localhost:8080/rooms")
                .contentType(APPLICATION_JSON)
                .body(putRoom.toString())
                .retrieve()
                .onStatus(HttpStatusCode::isError, ((request, response) -> {
                    throw new RuntimeException();
                }))
                .body(Room.class);
        assert room != null;

        Guest guest = new Guest();
        Room post = new Room();

        post.setId(room.getId());

        guest.setRoom(post);
        guest.setName("Richard");
        guest.setSurname("Dickson");
        guest.setSex(Sex.male);


        guest = client.post()
                .uri("http://localhost:8080/guests")
                .contentType(APPLICATION_JSON)
                .body(guest)
                .retrieve()
                .onStatus(HttpStatusCode::isError, ((request, response) -> {
                    throw new RuntimeException();
                }))
                .body(Guest.class);
        assert guest != null;


        var guestListRaw = client.get()
                .uri("http://localhost:8080/guests")
                .retrieve()
                .onStatus(HttpStatusCode::isError, ((request, response) -> {
                    throw new RuntimeException();
                }))
                .body(List.class);

        assert guestListRaw != null;

        assert !guestListRaw.isEmpty();

        List<Guest> guestList = deserializer(guestListRaw);

        assert guestList.contains(guest);


        assert guest.equals(client.get()
                .uri("http://localhost:8080/guests/" + guest.getId())
                .retrieve()
                .onStatus(HttpStatusCode::isError, ((request, response) -> {
                    throw new RuntimeException();
                }))
                .body(Guest.class));


        JSONObject changes = new JSONObject();
        changes.put("name", "John");
        Guest put = client.put()
                .uri("http://localhost:8080/guests/" + guest.getId())
                .contentType(APPLICATION_JSON)
                .body(changes.toString())
                .retrieve()
                .onStatus(HttpStatusCode::isError, ((request, response) -> {
                    throw new RuntimeException();
                }))
                .body(Guest.class);

        assert put != null;
        assert (Objects.equals(put.getId(), guest.getId())) && (!Objects.equals(put.getName(), guest.getName()));


        client.delete()
                .uri("http://localhost:8080/guests/" + guest.getId())
                .retrieve()
                .onStatus(HttpStatusCode::isError, ((request, response) -> {
                    throw new RuntimeException();
                }));

        client.delete()
                .uri("http://localhost:8080/rooms/" + room.getId())
                .retrieve()
                .onStatus(HttpStatusCode::isError, ((request, response) -> {
                    throw new RuntimeException();
                }));


    }

    @Test
    public void filtration() throws JSONException, IOException {
        RestClient client = RestClient.create();

        JSONObject putRoom1 = new JSONObject();
        putRoom1.put("number", 1);
        putRoom1.put("floor", 1);
        putRoom1.put("type", Sex.male);
        putRoom1.put("comfort", Comfort.standard);
        putRoom1.put("beds", 1);


        Room room1 = client.post()
                .uri("http://localhost:8080/rooms")
                .contentType(APPLICATION_JSON)
                .body(putRoom1.toString())
                .retrieve()
                .onStatus(HttpStatusCode::isError, ((request, response) -> {
                    throw new RuntimeException();
                }))
                .body(Room.class);
        assert room1 != null;

        JSONObject putRoom2 = new JSONObject();
        putRoom2.put("number", 2);
        putRoom2.put("floor", 1);
        putRoom2.put("type", Sex.male);
        putRoom2.put("comfort", Comfort.high_comfort);
        putRoom2.put("beds", 1);

        Room room2 = client.post()
                .uri("http://localhost:8080/rooms")
                .contentType(APPLICATION_JSON)
                .body(putRoom2.toString())
                .retrieve()
                .onStatus(HttpStatusCode::isError, ((request, response) -> {
                    throw new RuntimeException();
                }))
                .body(Room.class);
        assert room2 != null;


        JSONObject putRoom3 = new JSONObject();
        putRoom3.put("number", 3);
        putRoom3.put("floor", 1);
        putRoom3.put("type", Sex.female);
        putRoom3.put("comfort", Comfort.luxury);
        putRoom3.put("beds", 1);

        Room room3 = client.post()
                .uri("http://localhost:8080/rooms")
                .contentType(APPLICATION_JSON)
                .body(putRoom3.toString())
                .retrieve()
                .onStatus(HttpStatusCode::isError, ((request, response) -> {
                    throw new RuntimeException();
                }))
                .body(Room.class);
        assert room3 != null;

        Guest guest1 = new Guest();
        guest1.setRoom(room1);
        guest1.setName("Richard");
        guest1.setSurname("Dickson");
        guest1.setSex(Sex.male);

        guest1 = client.post()
                .uri("http://localhost:8080/guests")
                .contentType(APPLICATION_JSON)
                .body(guest1)
                .retrieve()
                .onStatus(HttpStatusCode::isError, ((request, response) -> {
                    throw new RuntimeException();
                }))
                .body(Guest.class);
        assert guest1 != null;

        Guest guest2 = new Guest();
        guest2.setRoom(room2);
        guest2.setName("Richard");
        guest2.setSurname("Dickson");
        guest2.setSex(Sex.male);

        guest2 = client.post()
                .uri("http://localhost:8080/guests")
                .contentType(APPLICATION_JSON)
                .body(guest2)
                .retrieve()
                .onStatus(HttpStatusCode::isError, ((request, response) -> {
                    throw new RuntimeException();
                }))
                .body(Guest.class);
        assert guest2 != null;

        Guest guest3 = new Guest();
        guest3.setRoom(room3);
        guest3.setName("Richard");
        guest3.setSurname("Dickson");
        guest3.setSex(Sex.female);

        guest3 = client.post()
                .uri("http://localhost:8080/guests")
                .contentType(APPLICATION_JSON)
                .body(guest3)
                .retrieve()
                .onStatus(HttpStatusCode::isError, ((request, response) -> {
                    throw new RuntimeException();
                }))
                .body(Guest.class);
        assert guest3 != null;

        List<Guest> guestList;

        //проверка на пол

        guestList = deserializer(Objects.requireNonNull(client.get()
                .uri("http://localhost:8080/guests?sex=male")
                .retrieve()
                .onStatus(HttpStatusCode::isError, ((request, response) -> {
                    throw new RuntimeException();
                }))
                .body(List.class)));

        assert (guestList.contains(guest1))&&(guestList.contains(guest2))&&!(guestList.contains(guest3));

        guestList = deserializer(Objects.requireNonNull(client.get()
                .uri("http://localhost:8080/guests?sex=female")
                .retrieve()
                .onStatus(HttpStatusCode::isError, ((request, response) -> {
                    throw new RuntimeException();
                }))
                .body(List.class)));

        assert !(guestList.contains(guest1))&&!(guestList.contains(guest2))&&(guestList.contains(guest3));

        client.get()
                .uri("http://localhost:8080/guests?sex=AAAAAAAAAAAAAAAAAAAAAAAA")
                .retrieve()
                .onStatus(HttpStatusCode::is2xxSuccessful, ((request, response) -> {
                    throw new RuntimeException();
                }));

        //проверка поиска по комнате

        guestList = deserializer(Objects.requireNonNull(client.get()
                .uri("http://localhost:8080/guests?room="+room1.getId())
                .retrieve()
                .onStatus(HttpStatusCode::isError, ((request, response) -> {
                    throw new RuntimeException();
                }))
                .body(List.class)));

        assert (guestList.contains(guest1))&&!(guestList.contains(guest2))&&!(guestList.contains(guest3));

        client.get()
                .uri("http://localhost:8080/guests?room=AAAAAAAAAAAAAAAAAAAAAAAA")
                .retrieve()
                .onStatus(HttpStatusCode::is2xxSuccessful, ((request, response) -> {
                    throw new RuntimeException();
                }));

        client.get()
                .uri("http://localhost:8080/guests?room=-1")
                .retrieve()
                .onStatus(HttpStatusCode::is2xxSuccessful, ((request, response) -> {
                    throw new RuntimeException();
                }));

        //проверка на уровень комфорта

        guestList = deserializer(Objects.requireNonNull(client.get()
                .uri("http://localhost:8080/guests?comfort=standard")
                .retrieve()
                .onStatus(HttpStatusCode::isError, ((request, response) -> {
                    throw new RuntimeException();
                }))
                .body(List.class)));

        assert (guestList.contains(guest1))&&!(guestList.contains(guest2))&&!(guestList.contains(guest3));

        guestList = deserializer(Objects.requireNonNull(client.get()
                .uri("http://localhost:8080/guests?comfort=high_comfort")
                .retrieve()
                .onStatus(HttpStatusCode::isError, ((request, response) -> {
                    throw new RuntimeException();
                }))
                .body(List.class)));

        assert !(guestList.contains(guest1))&&(guestList.contains(guest2))&&!(guestList.contains(guest3));

        guestList = deserializer(Objects.requireNonNull(client.get()
                .uri("http://localhost:8080/guests?comfort=luxury")
                .retrieve()
                .onStatus(HttpStatusCode::isError, ((request, response) -> {
                    throw new RuntimeException();
                }))
                .body(List.class)));

        assert !(guestList.contains(guest1))&&!(guestList.contains(guest2))&&(guestList.contains(guest3));

        client.get()
                .uri("http://localhost:8080/guests?comfort=AAAAAAAAAAAAAAAAAAAAAAAA")
                .retrieve()
                .onStatus(HttpStatusCode::is2xxSuccessful, ((request, response) -> {
                    throw new RuntimeException();
                }));


        client.delete()
                .uri("http://localhost:8080/guests/" + guest1.getId())
                .retrieve()
                .onStatus(HttpStatusCode::isError, ((request, response) -> {
                    throw new RuntimeException();
                }));

        client.delete()
                .uri("http://localhost:8080/guests/" + guest2.getId())
                .retrieve()
                .onStatus(HttpStatusCode::isError, ((request, response) -> {
                    throw new RuntimeException();
                }));

        client.delete()
                .uri("http://localhost:8080/guests/" + guest3.getId())
                .retrieve()
                .onStatus(HttpStatusCode::isError, ((request, response) -> {
                    throw new RuntimeException();
                }));

        client.delete()
                .uri("http://localhost:8080/rooms/" + room1.getId())
                .retrieve()
                .onStatus(HttpStatusCode::isError, ((request, response) -> {
                    throw new RuntimeException();
                }));

        client.delete()
                .uri("http://localhost:8080/rooms/" + room2.getId())
                .retrieve()
                .onStatus(HttpStatusCode::isError, ((request, response) -> {
                    throw new RuntimeException();
                }));

        client.delete()
                .uri("http://localhost:8080/rooms/" + room3.getId())
                .retrieve()
                .onStatus(HttpStatusCode::isError, ((request, response) -> {
                    throw new RuntimeException();
                }));
    }



    //TODO: закончить
    @Test
    public void limitations() throws JSONException {
        RestClient client = RestClient.create();

        JSONObject putRoom = new JSONObject();
        putRoom.put("number", 1);
        putRoom.put("floor", 1);
        putRoom.put("type", Sex.male);
        putRoom.put("comfort", Comfort.standard);
        putRoom.put("beds", 1);

        Room room1 = client.post()
                .uri("http://localhost:8080/rooms")
                .contentType(APPLICATION_JSON)
                .body(putRoom.toString())
                .retrieve()
                .onStatus(HttpStatusCode::isError, ((request, response) -> {
                    throw new RuntimeException();
                }))
                .body(Room.class);
        assert room1 != null;

        putRoom = new JSONObject();
        putRoom.put("number", 1);
        putRoom.put("floor", 1);
        putRoom.put("type", Sex.female);
        putRoom.put("comfort", Comfort.standard);
        putRoom.put("beds", 1);

        Room room2 = client.post()
                .uri("http://localhost:8080/rooms")
                .contentType(APPLICATION_JSON)
                .body(putRoom.toString())
                .retrieve()
                .onStatus(HttpStatusCode::isError, ((request, response) -> {
                    throw new RuntimeException();
                }))
                .body(Room.class);
        assert room2 != null;

        Guest guest = new Guest();

        guest.setRoom(room2);
        guest.setName("Richard");
        guest.setSurname("Dickson");
        guest.setSex(Sex.male);

        //Проверка, что нельзя добавить не в свой тип комнаты
        client.post()
                .uri("http://localhost:8080/guests")
                .contentType(APPLICATION_JSON)
                .body(guest)
                .retrieve()
                .onStatus(HttpStatusCode::is2xxSuccessful, ((request, response) -> {
                    throw new RuntimeException();
                }));

        guest.setRoom(room1);

        guest = client.post()
                .uri("http://localhost:8080/guests")
                .contentType(APPLICATION_JSON)
                .body(guest)
                .retrieve()
                .onStatus(HttpStatusCode::isError, ((request, response) -> {
                    throw new RuntimeException();
                })).body(Guest.class);
        assert guest!=null;

        //Проверка, что в заполненную комнату нельзя добавить гостя
        client.post()
                .uri("http://localhost:8080/guests")
                .contentType(APPLICATION_JSON)
                .body(guest)
                .retrieve()
                .onStatus(HttpStatusCode::is2xxSuccessful, ((request, response) -> {
                    throw new RuntimeException();
                }));

        guest.setRoom(room2);


        //Проверка, что нельзя добавить не в свой тип комнаты (через put)
        client.put()
                .uri("http://localhost:8080/guests/" + guest.getId())
                .contentType(APPLICATION_JSON)
                .body(guest)
                .retrieve()
                .onStatus(HttpStatusCode::is2xxSuccessful, ((request, response) -> {
                    throw new RuntimeException();
                }));

        guest.setRoom(room1);
        guest.setSex(Sex.female);

        //Проверка, что нельзя поменять пол* и остаться в той же комнате (через put) ((*ЛГБТ признана экстремистской организацией))
        client.put()
                .uri("http://localhost:8080/guests/" + guest.getId())
                .contentType(APPLICATION_JSON)
                .body(guest)
                .retrieve()
                .onStatus(HttpStatusCode::is2xxSuccessful, ((request, response) -> {
                    throw new RuntimeException();
                }));

        guest.setRoom(room2);

        Guest guest2 = client.post()
                .uri("http://localhost:8080/guests")
                .contentType(APPLICATION_JSON)
                .body(guest)
                .retrieve()
                .onStatus(HttpStatusCode::isError, ((request, response) -> {
                    throw new RuntimeException();
                })).body(Guest.class);

        assert guest2 != null;

        //Проверка, что в заполненную комнату нельзя добавить гостя (через put)
        client.put()
                .uri("http://localhost:8080/guests/" + guest.getId())
                .contentType(APPLICATION_JSON)
                .body(guest)
                .retrieve()
                .onStatus(HttpStatusCode::is2xxSuccessful, ((request, response) -> {
                    throw new RuntimeException();
                }));



        client.delete()
                .uri("http://localhost:8080/guests/" + guest.getId())
                .retrieve()
                .onStatus(HttpStatusCode::isError, ((request, response) -> {
                    throw new RuntimeException();
                }));

        client.delete()
                .uri("http://localhost:8080/guests/" + guest2.getId())
                .retrieve()
                .onStatus(HttpStatusCode::isError, ((request, response) -> {
                    throw new RuntimeException();
                }));

        client.delete()
                .uri("http://localhost:8080/rooms/" + room1.getId())
                .retrieve()
                .onStatus(HttpStatusCode::isError, ((request, response) -> {
                    throw new RuntimeException();
                }));

        client.delete()
                .uri("http://localhost:8080/rooms/" + room2.getId())
                .retrieve()
                .onStatus(HttpStatusCode::isError, ((request, response) -> {
                    throw new RuntimeException();
                }));

    }



    private List<Guest> deserializer(List raw) throws JSONException, JsonProcessingException {
        List<Guest> guestList = new ArrayList<>();

        ObjectMapper objectMapper = new ObjectMapper();

        JSONObject convert;

        for (var temp : raw) {
            convert = new JSONObject(String.valueOf(temp));
            guestList.add(objectMapper.readValue(convert.toString(), Guest.class));
        }
        return guestList;
    }

}


