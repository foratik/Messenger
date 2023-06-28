package org.example.model;

public enum React {
    LAUGH("laugh",""),
    LIKE("like",""),
    DISLIKE("dislike",""),
    ;
    public String reaction;
    public String pictureAddress;

    React(String pictureAddress, String reaction) {
        this.reaction = reaction;
        this.pictureAddress = pictureAddress;
    }
}
