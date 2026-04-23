//Author : Rafia Malik 
//Student ID # w2069502
/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.smartcampusapi.resources;

import java.util.ArrayList;
import java.util.List;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import com.mycompany.smartcampusapi.model.Room;
import com.mycompany.smartcampusapi.repository.DataStore;
import com.mycompany.smartcampusapi.exceptions.RoomNotEmptyException; // ADDED THIS

@Path("/rooms")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class RoomResource {

    @GET
    public List<Room> getAllRooms() {
        return new ArrayList<>(DataStore.rooms.values());
    }

    @GET
    @Path("/{id}")
    public Response getRoomById(@PathParam("id") String id) {
        Room room = DataStore.rooms.get(id);
        if (room == null) {
            return Response.status(Response.Status.NOT_FOUND).entity("Room not found").build();
        }
        return Response.ok(room).build();
    }

    @POST
    public Response addRoom(Room room) {
        DataStore.rooms.put(room.getId(), room);
        return Response.status(Response.Status.CREATED).entity(room).build();
    }

    @DELETE
    @Path("/{roomId}")
    public Response deleteRoom(@PathParam("roomId") String roomId) {
        if (!DataStore.rooms.containsKey(roomId)) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        boolean isOccupied = DataStore.sensors.values().stream()
                .anyMatch(s -> s.getRoomId().equals(roomId));

        if (isOccupied) {
            throw new RoomNotEmptyException("Cannot delete room: " + roomId + " has active sensors.");
        }

        DataStore.rooms.remove(roomId);
        return Response.noContent().build();
    }
}
