import javafx.stage.*;
import javafx.scene.*;
import javafx.scene.layout.*;
import javafx.scene.control.*;
import javafx.geometry.*;

public class MessageBoxCancelYes
{
    Stage stage;
    Label lbl;
    Button btnYes, btnCancel;
    boolean returnValue;
    String message;
    String title;

    public MessageBoxCancelYes(String message, String title) {
        this.message = message;
        this.title = title;
        show();
    }

    public void show()
    {
        lbl = new Label();
        lbl.setText(message);

        btnCancel = new Button();
        btnCancel.setText("Cancel");
        btnCancel.setDefaultButton(true);
        btnCancel.setOnAction(e -> cancel());

        btnYes = new Button();
        btnYes.setText("Yes");
        btnYes.setDefaultButton(true);
        btnYes.setOnAction(e -> yes());


        VBox pane = new VBox();
        HBox pane2 = new HBox();

        pane.getChildren().addAll(lbl, pane2);
        pane.setAlignment(Pos.CENTER);
        pane.setSpacing(25);


        pane2.getChildren().addAll(btnCancel, btnYes);
        pane2.setSpacing(20);
        pane2.setAlignment(Pos.CENTER);


        Scene scene = new Scene(pane);

        stage = new Stage();
        stage.setScene(scene);
        stage.setTitle(title);
        stage.setX((Screen.getPrimary().getVisualBounds().getWidth() - 300) / 2);
        stage.setY((Screen.getPrimary().getVisualBounds().getHeight() - 150) / 2);
        stage.setWidth(300);
        stage.setHeight(150);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.initStyle(StageStyle.UTILITY);
        stage.showAndWait();
    }

    public void yes() {
        returnValue = true;
        stage.close();
    }

    public void cancel() {
        returnValue = false;
        stage.close();
    }

    public boolean getResponse() {
        return returnValue;
    }
}