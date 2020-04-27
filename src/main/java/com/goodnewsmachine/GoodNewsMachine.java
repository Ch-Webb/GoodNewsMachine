package com.goodnewsmachine;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class GoodNewsMachine extends Application {

    @Override
    public void start(Stage stage) {
        StackPane stack = new StackPane();

        GridPane gridp = new GridPane();
        Label welcome = new Label("Welcome to the Good News Machine!");
        gridp.setConstraints(welcome, 1, 0);

        TextField t = new TextField();
        gridp.setConstraints(t, 0, 2);
        /* Need to work margins out i think */
        t.getStyleClass().add(".text-field");

        Button go = new Button("GO");
        gridp.setConstraints(go, 0, 3);

        gridp.getChildren().addAll(welcome, t, go);

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

                        for(String text: s.getAllLinks(t.getText())) {
                            v.getChildren().add(new Label(text));
                        }
                        scroll.setContent(v);
                        dialog.setTitle("Testing!");
                        dialog.setScene(new Scene(scroll, 600,600));




                        dialog.show();
                    }
                });


        Scene scene = new Scene(gridp, 640, 480);
        scene.getStylesheets().add("resources/testcss.css");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }

}
