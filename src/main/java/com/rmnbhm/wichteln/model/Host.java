package com.rmnbhm.wichteln.model;

import com.rmnbhm.wichteln.validation.NoHtml;
import com.rmnbhm.wichteln.validation.NoJavaControlCharacters;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public class Host {

    @NotBlank
    @Size(max = 100)
    @NoJavaControlCharacters
    @NoHtml
    private String name;

    @NotBlank
    @Email
    @NoJavaControlCharacters
    @NoHtml
    private String email;

    public Host() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String toString() {
        return String.format("Event.Host(name=%s, email=%s)", this.getName(), this.getEmail());
    }
}
