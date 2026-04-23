//Author : Rafia Malik 
//Student ID # w2069502
/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.smartcampusapi.resources;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import com.mycompany.smartcampusapi.model.Sensor;
import com.mycompany.smartcampusapi.repository.DataStore;
import com.mycompany.smartcampusapi.exceptions.LinkedResourceNotFoundException;

@Path("/sensors")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SensorResource {

    @GET
    public List<Sensor> getSensors(@QueryParam("type") String type) {
        List<Sensor> sensorList = new ArrayList<>(DataStore.sensors.values());
        if (type != null && !type.isEmpty()) {
            return sensorList.stream()
                    .filter(s -> s.getType().equalsIgnoreCase(type))
                    .collect(Collectors.toList());
        }
        return sensorList;
    }

    @POST
    public Response addSensor(Sensor sensor) {
        if (!DataStore.rooms.containsKey(sensor.getRoomId())) {
            // Using your custom exception instead of hardcoded 422
            throw new LinkedResourceNotFoundException("Error: Room ID " + sensor.getRoomId() + " does not exist.");
        }
        DataStore.sensors.put(sensor.getId(), sensor);
        return Response.status(Response.Status.CREATED).entity(sensor).build();
    }

    @DELETE
    @Path("/{id}")
    public Response deleteSensor(@PathParam("id") String id) {
        if (DataStore.sensors.containsKey(id)) {
            DataStore.sensors.remove(id);
            return Response.noContent().build();
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }

    @Path("/{sensorId}/readings")
    public SensorReadingResource getReadingResource(@PathParam("sensorId") String sensorId) {
        return new SensorReadingResource(sensorId);
    }
}
