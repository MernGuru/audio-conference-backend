package com.brh.entities.bases;

import jakarta.persistence.Basic;
import jakarta.persistence.MappedSuperclass;
import java.util.Objects;

@MappedSuperclass
@SuppressWarnings("unused")
public class Person<ID extends java.io.Serializable> extends SimplePKEntity<ID> {
    @Basic
    private String forename;

    @Basic
    private String surname;

    public String getForename() {
        return forename;
    }

    public void setForename(String firstName) {
        this.forename = firstName;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String lastName) {
        this.surname = lastName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Person<?> person = (Person<?>) o;
        return Objects.equals(forename, person.forename) && Objects.equals(surname, person.surname);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), forename, surname);
    }

    @Override
    public String toString() {
        return "{\"super\":" + super.toString() +
                ",\"forename\":\"" + forename + "\"" +
                ",\"surname\":\"" + surname + "\"}";
    }
}