package com.realsight.brain.timeseries.test;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class Main extends Application {

    //private SFFT sfft=new SFFT();
    private spectrumAnl spec=new spectrumAnl(1900,1800);

    @Override
    public void start(Stage primaryStage) throws Exception{
        AnchorPane anc=new AnchorPane();
        anc.getChildren().add(spec.getImage());
        //AnchorPane.setTopAnchor(spec.getImage(), 0.0);
        Scene scene=new Scene(anc);

        primaryStage.setTitle("Hello World");
        primaryStage.setScene(scene);
        primaryStage.show();

        spec.start();
    }


    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void stop()
    {
        spec.Stop();
    }
}
