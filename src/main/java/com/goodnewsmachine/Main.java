package com.goodnewsmachine;

public class Main {

    //Worlds most basic class
    //It's actually a wraparound to fix an issue that I encountered with JavaFX
    //Because javaFX was removed from Java in an earlier edition, having your main class
    //in a jar extend the javaFX Application class throws an error
    //This class is technically now the main class to work around that but all it does is point to GoodNewsMachine
    public static void main(String[] args)
    {
        GoodNewsMachine.main(args);
    }
}
