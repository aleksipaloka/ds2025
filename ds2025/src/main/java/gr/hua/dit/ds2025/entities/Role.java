package gr.hua.dit.ds2025.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "roles")
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column
    private long id;

    @Column
    private String roleName;

    public Role() {

    }

    public long getId() {
        return id;
    }

    public void setroleName(String roleName) {
        roleName = roleName;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getroleName() {
        return roleName;


    }

    public Role(long id, String roleName) {
        this.id = id;
        roleName = roleName;
    }

    @Override
    public String toString() {
        return "role{" +
                "id=" + id +
                ", name='" + roleName + '\'' +
                '}';
    }
}
