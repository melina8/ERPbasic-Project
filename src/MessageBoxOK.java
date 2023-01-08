import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.*;
import javafx.scene.*;
import javafx.scene.layout.*;
import javafx.scene.control.*;
import javafx.geometry.*;

public class MessageBoxOK
{
    Stage stage;
    Label lbl;
    Button btnOK;
    String message;
    String title;

    public MessageBoxOK(String message, String title) {
        this.message = message;
        this.title = title;
        show();
    }

    public void show()
    {
        lbl = new Label();
        lbl.setText(message);
        lbl.setPrefHeight(50);

        btnOK = new Button();
        Image img = new Image("ok.png");
        ImageView view = new ImageView(img);
        btnOK.setGraphic(view);
        btnOK.setDefaultButton(true);
        btnOK.setPrefSize(60, 25);
        btnOK.setPrefHeight(20);
        btnOK.setOnAction(e -> ok());


        VBox pane = new VBox();


        pane.getChildren().addAll(lbl, btnOK);
        pane.setAlignment(Pos.CENTER);


        Scene scene = new Scene(pane);

        stage = new Stage();
        stage.setTitle(title);
        stage.setX((Screen.getPrimary().getVisualBounds().getWidth() - 300) / 2);
        stage.setY((Screen.getPrimary().getVisualBounds().getHeight() - 150) / 2);
        stage.setWidth(300);
        stage.setHeight(150);
        stage.setScene(scene);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.initStyle(StageStyle.UTILITY);
        stage.showAndWait();
    }

    public void ok() {
        stage.close();
    }
}