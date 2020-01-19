package com.bramix.entities;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@NoArgsConstructor
public class RegistrationEntity {
    public Address address;
    public Client client;
    public Worker worker;

    public RegistrationEntity(Address address, Client client, Worker worker) {
        this.address = address;
        this.client = client;
        this.worker = worker;
    }
}
