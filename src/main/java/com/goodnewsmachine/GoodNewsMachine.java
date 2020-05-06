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

        BackgroundImage background = new BackgroundImage(new Image("images/Background1.jpg"),
            BackgroundRepeat.NO_REPEAT, BackgroundRepeat.REPEAT, BackgroundPosition.CENTER, BackgroundSize.DEFAULT);
            stage.setBackground(new Background(background));
        
        VBox title = new VBox();
        Image logo = new Image("images/Logo.jpg");
        ImageView logoNode = new ImageView(logo);

        HBox search = new HBox();
        TextField t = new TextField();
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
        title.getChildren().addAll(logoNode, search, go);
        title.setSpacing(10);

        StackPane stack = new StackPane(title);
        StackPane.setAlignment(go, Pos.BASELINE_CENTER);

        //Main layout of the stage will follow a grid. Means you can move stuff around relatively easily
        /*GridPane gridp = new GridPane();
        //REPLACE WITH LOGO

        //the setConstraints() method is effectively a coordinate system for moving controls around on the page
        gridp.setConstraints(welcome, 1, 0);

        //Used for user input for search functionality
        TextField t = new TextField();
        gridp.setConstraints(t, 1, 2);

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
        //Intellij told me to make this final but I don't know why
        //Hasn't broken anything so far
        final ComboBox<String> comboBox = new ComboBox<>(options);
        comboBox.setValue("BBC");
        stack.getChildren().addAll(go);
        stack.setStyle("-fx-background-color: #87CEFA");
        gridp.setConstraints(comboBox, 0, 2);

        //Button go = new Button("GO");
        gridp.setConstraints(go, 0, 3);*/

        //Need to actually bind these controls to the grid pane otherwise they wont show up
        //gridp.getChildren().addAll(welcome, t, comboBox, go);

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
                        ArrayList<String[]> links = switch (site) {
                            //May as well do it by string - increases visibility
                            case "BBC" -> s.getBBCLinks();
                            case "The Guardian" -> s.getGuardianLinks();
                            case "The Telegraph" -> s.getTelegraphLinks();
                            case "The Independent" -> s.getIndependentLinks();
                            case "Reuters - Oddly Enough" -> s.getReutersLinks();
                            //This will never be used but needs to be there for the compiler
                            default -> new ArrayList<>();
                        };

                        //Get url and text for each headline
                        for(String[] pair: links) {
                            String text = pair[0];
                            String url = pair[1];
                            //Hyperlinks don't actually link to the url without adding a handler
                            Hyperlink h = new Hyperlink(text);
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
                        }
                        //Add the VBox to the scrollbar
                        scroll.setContent(v);
                        //Give it a nice title
                        dialog.setTitle("Good News!");
                        //Add the scrollbar to the dialog box
                        dialog.setScene(new Scene(scroll, 600,600));
                        dialog.getIcons().add(new Image("images/icon.jpg"));



                        //Show it to the user
                        dialog.show();


                    }
                });

        //Add the GridBox to the Scene
        Scene scene = new Scene(stack, 640, 480);
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
