package com.bramix.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.Objects;

@Getter
@Setter
@MappedSuperclass
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Integer id;
    private boolean account_enabled = false;
    private String firstName;
    private String lastName;
    private String birthday;
    private String contactPhone1;
    private String email;
    @Type(type = "text")
    private String photo;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Type(type = "text")
    private String photo_mini;

    @JsonIgnore
    private String type;
    @JsonIgnore
    private String tokenId;


    public Account() {
    }

    public Account(String firstName, String lastName, String phone, String email, String type, String photo, String tokenId ) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.contactPhone1 = phone;
        this.email = email;
        this.photo = photo;
        this.type = type;
        this.tokenId = tokenId;
    }

    public Account(String firstName) {
        this.firstName = firstName;
    }

    public Account(String firstName, String lastName, String email) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Account account = (Account) o;
        return account_enabled == account.account_enabled &&
                Objects.equals(firstName, account.firstName) &&
                Objects.equals(lastName, account.lastName) &&
                Objects.equals(birthday, account.birthday) &&
                Objects.equals(contactPhone1, account.contactPhone1) &&
                Objects.equals(email, account.email) &&
                Objects.equals(photo, account.photo) &&
                Objects.equals(photo_mini, account.photo_mini) &&
                Objects.equals(type, account.type) &&
                Objects.equals(tokenId, account.tokenId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, account_enabled, firstName, lastName, birthday, contactPhone1, email, photo, photo_mini, type, tokenId);
    }
}
