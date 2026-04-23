//Author : Rafia Malik 
//Student ID # w2069502
package com.mycompany.smartcampusapi.repository;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import com.mycompany.smartcampusapi.model.Room;
import com.mycompany.smartcampusapi.model.Sensor;
import java.util.List;
import java.util.ArrayList;
import com.mycompany.smartcampusapi.model.SensorReading;

public class DataStore {

    public static Map<String, Room> rooms = new ConcurrentHashMap<>();
    public static Map<String, Sensor> sensors = new ConcurrentHashMap<>();
    public static Map<String, List<SensorReading>> readings = new ConcurrentHashMap<>();

    static {
        rooms.put("R1", new Room("R1", "Main Lab", 30));

        sensors.put("S1", new Sensor("S1", "Temperature", "R1"));
    }
}
