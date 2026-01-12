package com.cg.ui;

import com.cg.model.Model;
import com.cg.objreader.ObjReader;
import com.cg.render_engine.Camera;
import com.cg.render_engine.GraphicConveyor;
import com.cg.render_engine.RenderEngine;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.value.ChangeListener;
import javafx.scene.control.TextField;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
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

    @FXML
    private TextField txtScaleX;

    @FXML
    private TextField txtScaleY;

    @FXML
    private TextField txtScaleZ;

    @FXML
    private TextField txtTheta;

    @FXML
    private TextField txtPsi;

    @FXML
    private TextField txtPhi;

    @FXML
    private TextField txtTranslateX;

    @FXML
    private TextField txtTranslateY;

    @FXML
    private TextField txtTranslateZ;

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

    private float translateX = 0.0F;
    private float translateY = 0.0F;
    private float translateZ = 0.0F;

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
                        translateX, translateY, translateZ
                );
                RenderEngine.render(canvas.getGraphicsContext2D(), camera, mesh, (int) width, (int) height, modelMatrix);
            }
        });

        timeline.getKeyFrames().add(frame);
        timeline.play();

        canvas.setOnMousePressed(this::handleMousePress);
        canvas.setOnMouseDragged(this::handleMouseDragged);
        canvas.setOnMouseReleased(this::handleMouseRelease);
        canvas.setOnScroll(this::handleOnScroll);
        updateUI();
    }

    private void loadFromTextField() {
        try {
            scaleX = Float.parseFloat(txtScaleX.getText());
            scaleY = Float.parseFloat(txtScaleY.getText());
            scaleZ = Float.parseFloat(txtScaleZ.getText());

            theta = Float.parseFloat(txtTheta.getText());
            psi = Float.parseFloat(txtPsi.getText());
            phi = Float.parseFloat(txtPhi.getText());

            translateX = Float.parseFloat(txtTranslateX.getText());
            translateY = Float.parseFloat(txtTranslateY.getText());
            translateZ = Float.parseFloat(txtTranslateZ.getText());
        } catch (Exception ignored) {

        }
    }

    private void updateUI() {
        TextField[] fields = {
                txtScaleX, txtScaleY, txtScaleZ,
                txtTheta, txtPsi, txtPhi,
                txtTranslateX, txtTranslateY, txtTranslateZ
        };
        ChangeListener<String> listener = (observable, oldValue, newValue) -> loadFromTextField();
        for (TextField field : fields) {
            field.textProperty().addListener(listener);
        }
    }

    private void handleMousePress(MouseEvent mouseEvent) {
        if (mouseEvent.getButton() == MouseButton.PRIMARY) {
            mouseX = mouseEvent.getX();
            mouseY = mouseEvent.getY();
            isDragging = true;
        }
    }

    private void handleMouseDragged(MouseEvent mouseEvent) {
        if (isDragging) {
            float dx = (float) (mouseEvent.getX() - mouseX);
            float dy = (float) (mouseEvent.getY() - mouseY);
            mouseX = mouseEvent.getX();
            mouseY = mouseEvent.getY();

            camera.movePosition(new Vector3f(dx * 0.01F, dy * 0.01F, 0.0F));
        }
    }

    private void handleMouseRelease(MouseEvent mouseEvent) {
        isDragging = false;
    }

    private void handleOnScroll(ScrollEvent mouseEvent) {
        float zoomStep = (float) (mouseEvent.getDeltaY() / 100.0F);
        camera.movePosition(new Vector3f(0.0F, 0.0F, zoomStep));
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

    // todo: сделать сохранение модели до и после преобразований
    // todo: также нужно добавить текстовые поля для масштабирования, поворота и перемещения
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
}