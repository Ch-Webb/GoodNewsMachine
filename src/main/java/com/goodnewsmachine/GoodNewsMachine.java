package com.goodnewsmachine;

import com.jaunt.UserAgent;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.ArrayList;

public class GoodNewsMachine extends Application {

    @Override
    public void start(Stage stage) {
        StackPane stack = new StackPane();
        System.out.println(UserAgent.getVersionInfo());

        GridPane gridp = new GridPane();
        Label welcome = new Label("Welcome to the Good News Machine!");
        gridp.setConstraints(welcome, 1, 0);

        TextField t = new TextField();
        gridp.setConstraints(t, 1, 2);
        /* Need to work margins out i think */
        t.getStyleClass().add(".text-field");

        ObservableList<String> options =
                FXCollections.observableArrayList(
                        "BBC",
                        "The Guardian",
                        "The Telegraph",
                        "The Independent",
                        "Reuters - Oddly Enough",
                        "Option 6"
                );
        final ComboBox comboBox = new ComboBox(options);
        comboBox.setValue("BBC");
        gridp.setConstraints(comboBox, 0, 2);

        Button go = new Button("GO");
        gridp.setConstraints(go, 0, 3);

        gridp.getChildren().addAll(welcome, t, comboBox, go);

        //Working out event handlers and popup boxes
        go.setOnAction(
                new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        final Stage dialog = new Stage();

                        dialog.initModality(Modality.APPLICATION_MODAL);
                        Scraper s = new Scraper();
                        dialog.initOwner(stage);
                        ScrollPane scroll = new ScrollPane();
                        VBox v = new VBox();
                        String site = comboBox.getValue().toString();
                        ArrayList<String[]> links = switch (site) {
                            case "BBC" -> s.getBBCLinks();
                            case "The Guardian" -> s.getGuardianLinks();
                            case "The Telegraph" -> s.getTelegraphLinks();
                            case "The Independent" -> s.getIndependentLinks();
                            case "Reuters - Oddly Enough" -> s.getReutersLinks();
                            default -> new ArrayList<>();
                        };

                        for(String[] pair: links) {
                            String text = pair[0];
                            String url = pair[1];
                            Hyperlink h = new Hyperlink(text);
                            h.setOnAction(new EventHandler<ActionEvent>() {
                                @Override
                                public void handle(ActionEvent e) {
                                    getHostServices().showDocument(url);
                                }

                            });

                            v.getChildren().add(h);
                        }
                        scroll.setContent(v);
                        dialog.setTitle("Good News!");
                        dialog.setScene(new Scene(scroll, 600,600));




                        dialog.show();


                    }
                });


        Scene scene = new Scene(gridp, 640, 480);
        scene.getStylesheets().add("testcss.css");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }

}
