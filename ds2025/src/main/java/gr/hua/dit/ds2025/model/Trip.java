package gr.hua.dit.ds2025.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
public class Trip {

    //columns
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column
    private long id;

    @Column
    private int availableSeats;

    @Column
    private String startingPoint;

    @Column
    private String destination;

    @Column
    private LocalDateTime departureTime;

    @ManyToMany
    @JoinTable(
            name = "trip_passengers",
            joinColumns = @JoinColumn(name = "trip_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private List<User> passengers;

    @ManyToOne
    @JoinColumn(name = "driver_id")
    private User driver;

    //constructors
    public Trip() {

    }

    public Trip(long id, int availableSeats, String startingPoint, String destination, LocalDateTime departureTime, List<User> passengers, User driver) {
        this.id = id;
        this.availableSeats = availableSeats;
        this.startingPoint = startingPoint;
        this.destination = destination;
        this.departureTime = departureTime;
        this.passengers = passengers;
        this.driver = driver;
    }

    //setters & getters
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getAvailableSeats() {
        return availableSeats;
    }

    public void setAvailableSeats(int availableSeats) {
        this.availableSeats = availableSeats;
    }

    public String getStartingPoint() {
        return startingPoint;
    }

    public void setStartingPoint(String startingPoint) {
        this.startingPoint = startingPoint;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public LocalDateTime getDepartureTime() {
        return departureTime;
    }

    public void setDepartureTime(LocalDateTime departureTime) {
        this.departureTime = departureTime;
    }

    public List<User> getPassengers() {
        return passengers;
    }

    public void setPassengers(List<User> passengers) {
        this.passengers = passengers;
    }

    public User getDriver() {
        return driver;
    }

    public void setDriver(User driver) {
        this.driver = driver;
    }
}
