package com.brh.entities.cores;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "services")
@SuppressWarnings("unused")
public class Service {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(unique = true,nullable = false, length = 32)
    private String title;

    @Column(nullable = false)
    private String description;
    @Column(nullable = false)
    private int price;
    @Column(nullable = false)
    private String image;

    public Service(){

    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    public int getPrice() {
        return price;
    }
    public void setPrice(int price) {
        this.price = price;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Service user = (Service) o;
        return title.equals(user.title) && description.equals(user.description) && image.equals(user.image);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), title, description, price);
    }

    @Override
    public String toString() {
        return "{\"super\":" + super.toString() +
                ",\"username\":\"" + title + "\"" +
                ",\"password\":\"" + description + "\"" +
                ",\"roles\":" + price +
                "}";
    }

}

