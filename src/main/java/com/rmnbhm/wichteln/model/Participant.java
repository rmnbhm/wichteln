package com.rmnbhm.wichteln.model;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Participant {

    private String firstName;
    private String lastName;
    private String email;
}
