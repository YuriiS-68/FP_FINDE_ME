package com.findme.models;

import java.util.Objects;

public class Form {
    private String email;
    private String password;

    public Form(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Form form = (Form) o;
        return email.equals(form.email) &&
                password.equals(form.password);
    }

    @Override
    public int hashCode() {
        return Objects.hash(email, password);
    }
}
