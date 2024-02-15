package ru.rakalus.test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.client.RestClient;
import ru.rakalus.test.model.Comfort;
import ru.rakalus.test.model.Guest;
import ru.rakalus.test.model.Room;
import ru.rakalus.test.model.Sex;

import java.io.IOException;
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

        JSONObject putGuest = new JSONObject();

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
                .uri("http://localhost:8080/guests/"+guest.getId())
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


