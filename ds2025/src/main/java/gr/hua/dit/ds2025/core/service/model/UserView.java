package gr.hua.dit.ds2025.core.service.model;

public record UserView(

        long id,
        String name,
        String lastName,
        String username,
        String email

) {

    public String fullName() {
        return this.name + " " + this.lastName;
    }

}
