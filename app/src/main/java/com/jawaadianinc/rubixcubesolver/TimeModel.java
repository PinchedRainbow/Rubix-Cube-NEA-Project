package com.jawaadianinc.rubixcubesolver;

import org.jetbrains.annotations.NotNull;

public class TimeModel {

    //This is a class of time model

    private String timeSolved;
    private String dateTime;
    private String Shuffle;
    private String TypeOfCube;
    private String nameofUser;

    //Constructor
    public TimeModel(String timeSolved, String dateTime, String Shuffle, String TypeOfCube, String nameofUser) {
        this.timeSolved = timeSolved;
        this.dateTime = dateTime;
        this.Shuffle = Shuffle;
        this.TypeOfCube = TypeOfCube;
        this.nameofUser = nameofUser;
    }

    @NotNull
    @Override //inheritance of toString method
    public String toString() {
        return "Time: " + timeSolved;
    }

    //Getters and setters
    public String getTypeOfCube() {
        return TypeOfCube;
    }

    public void setTypeOfCube(String typeOfCube) {
        TypeOfCube = typeOfCube;
    }

    public String getTimeSolved() {
        return timeSolved;
    }

    public void setTimeSolved(String timeSolved) {
        this.timeSolved = timeSolved;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public String getShuffle() {
        return Shuffle;
    }

    public void setShuffle(String shuffle) {
        Shuffle = shuffle;
    }

    public String getNameofUser() {
        return nameofUser;
    }

    public void setNameofUser(String nameofUser) {
        this.nameofUser = nameofUser;
    }
}
