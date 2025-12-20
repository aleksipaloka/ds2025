package gr.hua.dit.ds2025.core.model;

import jakarta.persistence.*;

@Entity
public class Review {

    //columns
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column
    private long id;

    @ManyToOne(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinColumn(name = "reviewer")
    private User reviewer;

    @ManyToOne(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinColumn(name = "reviewee")
    private User reviewee;

    @Column
    private int rating;

    @Column
    private String comments;

    //constructors
    public Review(){}

    public Review(long id, User reviewer, User reviewee, int rating, String comments) {
        this.id = id;
        this.reviewer = reviewer;
        this.reviewee = reviewee;
        this.rating = rating;
        this.comments = comments;
    }

    //setters & getters

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public User getReviewer() {
        return reviewer;
    }

    public void setReviewer(User reviewer) {
        this.reviewer = reviewer;
    }

    public User getReviewee() {
        return reviewee;
    }

    public void setReviewee(User reviewee) {
        this.reviewee = reviewee;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }
}
