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

import java.util.ArrayList;

//Every JavaFX application has to extend the master Application class
public class GoodNewsMachine extends Application {

    //Override Application-provided start() method
    @Override
    public void start(Stage stage) {
        //DEBUG -- REMOVE
        System.out.println(UserAgent.getVersionInfo());

        VBox title = new VBox();
        //Inserts the logo
        Image logo = new Image("images/Logo.png");
        ImageView logoNode = new ImageView(logo);

        //Creates a (HBox) horizontal box
        HBox search = new HBox();
        //Allows the user to refine their search and prompts them with instructions in the text field
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
        search.setAlignment(Pos.CENTER);


        Button go = new Button("GO");
        go.setAlignment(Pos.CENTER);
        //Resizes the go button
        go.setPrefSize(80, 40);
        //Sets the font size in the go button
        go.setStyle("-fx-font-size:20");
        //Adds the logo, HBox and go button in a (VBox)vertical box
        title.getChildren().addAll(logoNode, search, go);
        title.setSpacing(10);
        title.setAlignment(Pos.CENTER);

        //Generates random background
        int image = generateRandom(2, 8);
        Image backI = new Image("images/background/Background" + image + ".jpg");
        BackgroundImage background = new BackgroundImage(backI, BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER,
                new BackgroundSize(600, 480, false, false, true, true));

        //Stack pane is a layout tool
        StackPane stack = new StackPane();
        //Adds the VBox to the stack pane
        stack.getChildren().addAll(title);
        stack.setBackground(new Background(background));
        StackPane.setAlignment(title, Pos.CENTER);
        StackPane.setAlignment(go, Pos.BASELINE_CENTER);



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
                        //We want a new stage to show up with the results (a new window)
                        final Stage dialog = new Stage();
                        StackPane root = new StackPane();

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
                        String site = comboBox.getValue();
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
                            h.setFont(new Font("Arial", 18));
                            h.setPadding(new Insets(20, 10, 0, 10));
                            h.setStyle("-fx-background-color: white;");
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

                            v.setPrefSize(600, 480);
                            //Generates random background
                            int image = generateRandom(2, 8);
                            Image backI = new Image("images/background/Background" + image + ".jpg");
                            BackgroundImage background = new BackgroundImage(backI, BackgroundRepeat.NO_REPEAT,
                                    BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER,
                                    new BackgroundSize(600, 480, false, false, true, true));
                            v.setBackground(new Background(background));
                        }

                        root.getChildren().add(v);
                        StackPane.setAlignment(v, Pos.BASELINE_CENTER);
                        //Add the VBox to the scrollbar
                        scroll.setContent(v);

                        //scroll.setStyle("-fx-background-color: transparent;");
                        StackPane tempStack = new StackPane();
                        /*int rand = generateRandom(2, 8);
                        Image back = new Image("images/background/Background2.jpg");
                        BackgroundImage backg = new BackgroundImage(back, BackgroundRepeat.NO_REPEAT,
                                BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER,
                                new BackgroundSize(600, 600, false, false, true, true));
                        //v.setBackground(new Background(backg));


                        tempStack.getChildren().add(scroll);
                        tempStack.setBackground(new Background(backg));
                         */
                        //Give it a nice title
                        dialog.setTitle("Good News!");
                        //Add the scrollbar to the dialog box
                        dialog.setScene(new Scene(scroll, 600, 600));
                        dialog.getIcons().add(new Image("images/Icon.png"));

                        //Show it to the user
                        dialog.show();
                    }
                });

        //Add the GridBox to the Scene
        System.out.println("Height: " + backI.getHeight() + " Width: " + backI.getWidth());

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

    public int generateRandom(int min, int max) {
        int num = min + (int)(Math.random() * ((max - min) + 1));
        if(!(num < max && num > min)) {
            num = (int) Math.ceil((max + min)/2);
        }
        return num;
    }

}
