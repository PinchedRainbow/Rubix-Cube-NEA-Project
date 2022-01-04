package com.jawaadianinc.rubixcubesolver;

import org.jetbrains.annotations.NotNull;

public class TimeModel {

    private String timeSolved;
    private String dateTime;
    private String Shuffle;
    private String TypeOfCube;


    public TimeModel(String timeSolved, String dateTime, String Shuffle, String TypeOfCube) {
        this.timeSolved = timeSolved;
        this.dateTime = dateTime;
        this.Shuffle = Shuffle;
        this.TypeOfCube = TypeOfCube;
    }

    @NotNull
    @Override
    public String toString() {
        return "Time: " + timeSolved;
    }

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


}
