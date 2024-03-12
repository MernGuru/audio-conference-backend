package com.brh.entities.cores.types;

public enum Gender {
    MALE('M'),FEMALE('F'), NOT_APPLICABLE('N');

    private final Character value;

    Gender(Character value){
        this.value = value;
    }

    public Character getValue(){
        return value;
    }
}
