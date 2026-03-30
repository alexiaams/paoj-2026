package com.pao.laboratory05.playlist;

public class Playlist{
    private String name;
    private Song[] songs = new Song[0];

    public Playlist(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void addSong(Song song) {
        Song[] newSongs = new Song[songs.length + 1];
        System.arraycopy(songs, 0, newSongs, 0, songs.length);
        newSongs[songs.length] = song;
        songs = newSongs;
    }

    public void printSortedByTitle() {
        Song[] sortedSongs = songs.clone();
        java.util.Arrays.sort(sortedSongs);
        System.out.println("Playlist: " + name + " (sorted by title)");
        for (Song song : sortedSongs) {
            System.out.println(song);
        }
    }

    public void printSortedByDuration() {
        Song[] sortedSongs = songs.clone();
        java.util.Arrays.sort(sortedSongs, new SongDurationComparator());
        System.out.println("Playlist: " + name + " (sorted by duration)");
        for (Song song : sortedSongs) {
            System.out.println(song);
        }
    }

    public int getTotalDuration() {
        int totalDuration = 0;
        for (Song song : songs) {
            totalDuration += song.durationInSeconds();
        }
        return totalDuration;
    }



}