package gr.hua.dit.ds2025.core.model;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column
    private Long id;

    @Column
    private String name;

    @Column
    private String lastName;

    @Column
    private String username;

    @Column
    private String email;

    @Column
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(name = "roles", nullable = false, length = 20)
    private Role role;

    @Column(nullable = false)
    private boolean enabled = true;

    @OneToMany(mappedBy = "driver", cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST})
    private List<Trip> tripsAsDriver;

    @ManyToMany(mappedBy = "passengers")
    private List<Trip> tripsAsPassenger;

    @OneToMany(mappedBy = "reviewer", cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST})
    private List<Review> reviewsWritten;

    @OneToMany(mappedBy = "reviewee", cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST})
    private List<Review> reviewsConcerning;

    public User() {}

    public User(
            Long id,
            String name,
            String lastName,
            String username,
            String email,
            String password,
            Role role,
            boolean enabled,
            List<Trip> tripsAsDriver,
            List<Trip> tripsAsPassenger,
            List<Review> reviewsWritten,
            List<Review> reviewsConcerning
    ) {
        this.id = id;
        this.name = name;
        this.lastName = lastName;
        this.username = username;
        this.email = email;
        this.password = password;
        this.role = role;
        this.enabled = enabled;
        this.tripsAsDriver = tripsAsDriver;
        this.tripsAsPassenger = tripsAsPassenger;
        this.reviewsWritten = reviewsWritten;
        this.reviewsConcerning = reviewsConcerning;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public List<Trip> getTripsAsDriver() {
        return tripsAsDriver;
    }

    public void setTripsAsDriver(List<Trip> tripsAsDriver) {
        this.tripsAsDriver = tripsAsDriver;
    }

    public List<Trip> getTripsAsPassenger() {
        return tripsAsPassenger;
    }

    public void setTripsAsPassenger(List<Trip> tripsAsPassenger) {
        this.tripsAsPassenger = tripsAsPassenger;
    }

    public List<Review> getReviewsWritten() {
        return reviewsWritten;
    }

    public void setReviewsWritten(List<Review> reviewsWritten) {
        this.reviewsWritten = reviewsWritten;
    }

    public List<Review> getReviewsConcerning() {
        return reviewsConcerning;
    }

    public void setReviewsConcerning(List<Review> reviewsConcerning) {
        this.reviewsConcerning = reviewsConcerning;
    }
}
