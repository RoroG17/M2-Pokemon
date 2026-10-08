package com.example.m2pokemon.exception;

public class DresseurNotFoundException extends RuntimeException {

    public DresseurNotFoundException(Long id) {
        super("Dresseur " + id + " n'existe pas");
    }
}
