package com.pao.laboratory05.playlist;

public record Song(String title, String artist, int durationInSeconds) implements Comparable<Song>{
    @Override
    public int compareTo(Song o) {
        return title.compareTo(o.title);
    }
}