package com.cg.ui;

import com.cg.model.Model;
import com.cg.objreader.ObjReader;
import com.cg.render_engine.Camera;
import com.cg.render_engine.GraphicConveyor;
import com.cg.render_engine.RenderEngine;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;

import com.cg.math.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class GuiController {

    final private float TRANSLATION = 0.5F;

    @FXML
    AnchorPane anchorPane;

    @FXML
    private Canvas canvas;

    private Model mesh = null;

    private Camera camera = new Camera(
            new Vector3f(0, 0, 100),
            new Vector3f(0, 0, 0),
            1.0F, 1, 0.01F, 100);

    private Timeline timeline;

    private float scaleX = 1.0F;
    private float scaleY = 1.0F;
    private float scaleZ = 1.0F;

    private float theta = 0.0F;
    private float psi= 0.0F;
    private float phi = 0.0F;

    private Vector3f translationVector = new Vector3f(0.0F, 0.0F, 0.0F);

    private double mouseX;
    private double mouseY;
    private boolean isDragging = false;

    @FXML
    private void initialize() {
        anchorPane.prefWidthProperty().addListener((ov, oldValue, newValue) -> canvas.setWidth(newValue.doubleValue()));
        anchorPane.prefHeightProperty().addListener((ov, oldValue, newValue) -> canvas.setHeight(newValue.doubleValue()));

        timeline = new Timeline();
        timeline.setCycleCount(Animation.INDEFINITE);

        KeyFrame frame = new KeyFrame(Duration.millis(15), event -> {
            double width = canvas.getWidth();
            double height = canvas.getHeight();

            canvas.getGraphicsContext2D().clearRect(0, 0, width, height);
            camera.setAspectRatio((float) (width / height));

            if (mesh != null) {

                Matrix4x4 modelMatrix = GraphicConveyor.rotateScaleTranslate(
                        scaleX, scaleY, scaleZ,
                        theta, psi, phi,
                        translationVector
                );
                RenderEngine.render(canvas.getGraphicsContext2D(), camera, mesh, (int) width, (int) height, modelMatrix);
            }
        });

        timeline.getKeyFrames().add(frame);
        timeline.play();

        canvas.setOnMousePressed(mouseEvent -> {
            if (mouseEvent.getButton() == MouseButton.PRIMARY) {
                mouseX = mouseEvent.getX();
                mouseY = mouseEvent.getY();
                isDragging = true;
            }
        });

        canvas.setOnMouseDragged(mouseEvent -> {
            if (isDragging) {
                float dx = (float) (mouseEvent.getX() - mouseX);
                float dy = (float) (mouseEvent.getY() - mouseY);
                mouseX = mouseEvent.getX();
                mouseY = mouseEvent.getY();

                camera.movePosition(new Vector3f(dx * 0.01F, dy * 0.01F, 0.0F));
            }
        });

        canvas.setOnMouseReleased(mouseEvent -> {
            isDragging = false;
        });

        canvas.setOnScroll(mouseEvent -> {
            float zoomStep = (float) (mouseEvent.getDeltaY() / 100.0F);
            camera.movePosition(new Vector3f(0.0F, 0.0F, zoomStep));
        });
    }

    @FXML
    private void onOpenModelMenuItemClick() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Model (*.obj)", "*.obj"));
        fileChooser.setTitle("Load Model");

        File file = fileChooser.showOpenDialog((Stage) canvas.getScene().getWindow());
        if (file == null) {
            return;
        }

        Path fileName = Path.of(file.getAbsolutePath());

        try {
            String fileContent = Files.readString(fileName);
            mesh = ObjReader.read(fileContent);
            // todo: обработка ошибок
        } catch (IOException exception) {

        }
    }

    @FXML
    public void handleCameraForward(ActionEvent actionEvent) {
        camera.movePosition(new Vector3f(0, 0, -TRANSLATION));
    }

    @FXML
    public void handleCameraBackward(ActionEvent actionEvent) {
        camera.movePosition(new Vector3f(0, 0, TRANSLATION));
    }

    @FXML
    public void handleCameraLeft(ActionEvent actionEvent) {
        camera.movePosition(new Vector3f(TRANSLATION, 0, 0));
    }

    @FXML
    public void handleCameraRight(ActionEvent actionEvent) {
        camera.movePosition(new Vector3f(-TRANSLATION, 0, 0));
    }

    @FXML
    public void handleCameraUp(ActionEvent actionEvent) {
        camera.movePosition(new Vector3f(0, TRANSLATION, 0));
    }

    @FXML
    public void handleCameraDown(ActionEvent actionEvent) {
        camera.movePosition(new Vector3f(0, -TRANSLATION, 0));
    }

    @FXML
    public void increaseScaleX(ActionEvent actionEvent) {
        scaleX += 0.1F;
    }

    @FXML
    public void decreaseScaleX(ActionEvent actionEvent) {
        scaleX -= 0.1F;
    }

    @FXML
    public void increaseScaleY(ActionEvent actionEvent) {
        scaleY += 0.1F;
    }

    @FXML
    public void decreaseScaleY(ActionEvent actionEvent) {
        scaleY -= 0.1F;
    }

    @FXML
    public void increaseScaleZ(ActionEvent actionEvent) {
        scaleZ += 0.1F;
    }

    @FXML
    public void decreaseScaleZ(ActionEvent actionEvent) {
        scaleZ -= 0.1F;
    }

    @FXML
    public void increaseTheta(ActionEvent actionEvent) {
        theta += 0.1F;
    }

    @FXML
    public void decreaseTheta(ActionEvent actionEvent) {
        theta -= 0.1F;
    }

    @FXML
    public void increasePsi(ActionEvent actionEvent) {
        psi += 0.1F;
    }

    @FXML
    public void decreasePsi(ActionEvent actionEvent) {
        psi -= 0.1F;
    }

    @FXML
    public void increasePhi(ActionEvent actionEvent) {
        phi += 0.1F;
    }

    @FXML
    public void decreasePhi(ActionEvent actionEvent) {
        phi -= 0.1F;
    }

    @FXML
    public void increaseTranslateX(ActionEvent actionEvent) {
        translationVector = new Vector3f(translationVector.x + 0.1F, translationVector.y, translationVector.z);
    }

    @FXML
    public void decreaseTranslateX(ActionEvent actionEvent) {
        translationVector = new Vector3f(translationVector.x - 0.1F, translationVector.y, translationVector.z);
    }

    @FXML
    public void increaseTranslateY(ActionEvent actionEvent) {
        translationVector = new Vector3f(translationVector.x, translationVector.y + 0.1F, translationVector.z);
    }

    @FXML
    public void decreaseTranslateY(ActionEvent actionEvent) {
        translationVector = new Vector3f(translationVector.x, translationVector.y - 0.1F, translationVector.z);
    }

    @FXML
    public void increaseTranslateZ(ActionEvent actionEvent) {
        translationVector = new Vector3f(translationVector.x, translationVector.y, translationVector.z + 0.1F);
    }

    @FXML
    public void decreaseTranslateZ(ActionEvent actionEvent) {
        translationVector = new Vector3f(translationVector.x, translationVector.y, translationVector.z - 0.1F);
    }
}