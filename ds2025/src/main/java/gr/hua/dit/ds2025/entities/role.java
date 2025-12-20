package gr.hu.dit.ds.ds2026.entities;

import jakarta.persistence.*;

@Entity
public class Role {

    @Id
    @GeneratedValue(Strategy = GenerationType.AUTO)
    @Column
    private long id;

    @Column
    private String RoleName;

    public int getId() {
        return id;
    }

    public void setRoleName(String roleName) {
        RoleName = roleName;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getRoleName() {
        return RoleName;


    }

    public Role(long id, String roleName) {
        this.id = id;
        RoleName = roleName;
    }

    @Override
    public String toString() {
        return "Role{" +
                "id=" + id +
                ", RoleName='" + RoleName + ''' +
        '}';
    }
}
