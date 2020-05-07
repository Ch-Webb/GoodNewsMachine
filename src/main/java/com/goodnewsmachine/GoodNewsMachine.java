package com.goodnewsmachine;

//Lots of imports - javafx doesn't allow import * so got to import all separately
//Probably better for memory usage though
import com.jaunt.UserAgent;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import static com.sun.javafx.scene.control.skin.Utils.getResource;

//Every JavaFX application has to extend the master Application class
public class GoodNewsMachine extends Application {

    //Override Application-provided start() method
    @Override
    public void start(Stage stage) {
        //DEBUG -- REMOVE
        System.out.println(UserAgent.getVersionInfo());

        VBox title = new VBox();
        Image logo = new Image("images/Logo.png");
        ImageView logoNode = new ImageView(logo);

        HBox search = new HBox();
        TextField t = new TextField();
        t.setPromptText("Leave blank to see all!");
        //Provide options for the drop down list. The dropdown requires an ObservableList class
        ObservableList<String> options =
                FXCollections.observableArrayList(
                        "BBC",
                        "The Guardian",
                        "The Telegraph",
                        "The Independent",
                        "Reuters - Oddly Enough",
                        "Option 6"
                );
        final ComboBox<String> comboBox = new ComboBox<>(options);
        comboBox.setValue("BBC");
        search.setSpacing(10);
        search.setPadding(new Insets(10, 10, 10, 10));
        search.getChildren().addAll(comboBox, t);


        Button go = new Button("GO");
        go.setAlignment(Pos.CENTER);
        go.setPrefSize(80, 40);
        go.setStyle("-fx-font-size:20");
        title.getChildren().addAll(logoNode, search, go);
        title.setSpacing(10);

        //Generates random background
        ArrayList<String> images = new ArrayList<String>();
        /*File directory = new File("images/background");

        File[] files = directory.listFiles();
        for (File f : files) {
            images.add(f.getName());
        }
        int countImages = images.size();
        int imageNumber = (int)(Math.random()*countImages);
        String image = images.get(imageNumber);

        //Sets background*/
        int image = 2 + (int)(Math.random() * ((8 - 2) + 1));
        if(!(image < 8 && image > 2)) {
            image = 6;
        }
        Image backI = new Image("images/background/Background" + image + ".jpg");
        BackgroundImage background = new BackgroundImage(backI, BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER,
                new BackgroundSize(600, 480, false, false, true, true));


        StackPane stack = new StackPane();
        stack.getChildren().addAll(title);
        stack.setBackground(new Background(background));
        stack.setAlignment(go, Pos.BASELINE_CENTER);


        //This looks confusing but is mainly simple
        //calling the setOnAction method on go adds a handler to that button
        go.setOnAction(
                //setOnAction requires an eventHandler class
                new EventHandler<ActionEvent>() {
                    //But an eventHandler comes empty because you need to define your own action
                    //To happen when the event happens.
                    //So override the handle method (which is empty in the parent) and make it do whatever
                    @Override
                    public void handle(ActionEvent event) {
                        Image backI = new Image("images/background/Background2.jpg");
                        BackgroundImage background = new BackgroundImage(backI, BackgroundRepeat.NO_REPEAT,
                                BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER,
                                new BackgroundSize(600, 480, false, false, true, true));

                        //We want a new stage to show up with the results (a new window)
                        final Stage dialog = new Stage();

                        //The modality of a window is a property that defines how the window interacts
                        //with other windows from the program
                        //This value says that while this window is open, you can't click on any of its
                        //parent windows
                        dialog.initModality(Modality.APPLICATION_MODAL);
                        //Instantiate scraper class - see Scraper.java
                        Scraper s = new Scraper();
                        //Binds this new window as a child of the original application
                        dialog.initOwner(stage);
                        //Handy scrollpane to allow us to scroll through the results
                        ScrollPane scroll = new ScrollPane();
                        //The VBox works by adding each new child directly below the last child
                        //Vertical listings.
                        VBox v = new VBox();
                        //Check user preference for which site to visit
                        String site = comboBox.getValue().toString();
                        String search = t.getText();
                        //Run a switch-case on the selection of the combo box
                        ArrayList<String[]> links = switch (site) {
                            //May as well do it by string - increases visibility
                            case "BBC" -> s.getBBCLinks(search);
                            case "The Guardian" -> s.getGuardianLinks(search);
                            case "The Telegraph" -> s.getTelegraphLinks(search);
                            case "Reuters - Oddly Enough" -> s.getReutersLinks(search);
                            //This will never be used but needs to be there for the compiler
                            default -> new ArrayList<>();
                        };

                        Image logos = new Image("images/Logo.png");
                        ImageView logoNodes = new ImageView(logos);
                        Text t = new Text("Click on the links to read the full story...");
                        t.setFont(new Font("Arial", 12));

                        v.getChildren().addAll(logoNodes, t);

                        //Get url and text for each headline
                        for(String[] pair: links) {
                            String text = pair[0];
                            String url = pair[1];
                            //Hyperlinks don't actually link to the url without adding a handler
                            Hyperlink h = new Hyperlink(text);
                            h.setTextFill(Color.BLACK);
                            h.setFont(new Font("Arial", 20));
                            h.setPadding(new Insets(20, 10, 0, 10));
                            h.setStyle("-fx-background-color: orange;");
                            //So add another handler! handler inside a handler inside an application
                            h.setOnAction(new EventHandler<ActionEvent>() {
                                @Override
                                public void handle(ActionEvent e) {
                                    //opens the url in the users default browser
                                    getHostServices().showDocument(url);
                                }

                            });
                            //Add the hyperlink to the VBox

                            v.getChildren().add(h);
                            v.setAlignment(Pos.CENTER);

                        }

                        //Add the VBox to the scrollbar
                        scroll.setContent(v);
                        //Give it a nice title
                        dialog.setTitle("Good News!");
                        //Add the scrollbar to the dialog box
                        dialog.setScene(new Scene(scroll, backI.getWidth(), backI.getHeight()));
                        dialog.getIcons().add(new Image("images/icon.png"));

                        //Show it to the user
                        dialog.show();


                    }
                });

        //Add the GridBox to the Scene
        System.out.println("Height: " + backI.getHeight() + " Width: " + backI.getWidth());
        int width = (int) Math.floor(backI.getWidth());
        int height = (int) Math.floor(backI.getHeight());


        Scene scene = new Scene(stack, 600, 480);

        //Bind the CSS sheet to the scene for a nice looking application
        scene.getStylesheets().add("testcss.css");
        //Give it a nice title
        stage.setTitle("The Good News Machine");
        stage.getIcons().add(new Image("images/Icon.png"));
        //Set the scene
        stage.setScene(scene);
        //Show it to the user
        stage.show();
    }

    public static void main(String[] args) {
        //Launch the application
        //Does some sort of trickery but that's all JavaFX side so I can't explain it
        launch();
    }

}
