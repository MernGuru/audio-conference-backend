package com.brh.entities.cores;

import com.brh.entities.bases.Person;
import com.brh.entities.cores.types.Gender;
import com.brh.entities.cores.types.GenderAdapter;
import jakarta.json.bind.annotation.JsonbTypeAdapter;
import jakarta.persistence.*;
import jakarta.security.enterprise.authentication.mechanism.http.openid.ClaimsDefinition;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Objects;

@MappedSuperclass
@SuppressWarnings("unused")
public abstract class PersonWithDetails<ID extends java.io.Serializable> extends Person<ID> {
    @Basic
    private java.lang.String email;

    @Basic
    private Integer referr;

//    @Column(columnDefinition = "enum('M','F','N')")
//    @XmlJavaTypeAdapter(GenderAdapter.class)
//    @JsonbTypeAdapter(GenderAdapter.class)
//    private Gender gender;
//
//    @ManyToOne(fetch = FetchType.EAGER,cascade=CascadeType.ALL)
//    @JoinColumn(name = "address_id", referencedColumnName = "address_id")
//    private Address address;
    private LocalDate join_date;

    public java.lang.String getEmail() {
        return email;
    }

    public void setEmail(java.lang.String email) {
        this.email = email;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        PersonWithDetails<?> that = (PersonWithDetails<?>) o;
        return Objects.equals(email, that.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), email);
    }

    @Override
    public java.lang.String toString() {
        return "{\"super\":" + super.toString() +
                ",\"email\":\"" + email + "\"";
    }
}
