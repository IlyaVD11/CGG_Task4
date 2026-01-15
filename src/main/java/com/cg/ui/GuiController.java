package com.cg.ui;

import com.cg.model.Model;
import com.cg.objreader.ObjReader;
import com.cg.render_engine.Camera;
import com.cg.render_engine.GraphicConveyor;
import com.cg.render_engine.RenderEngine;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.control.CheckBox;
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

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ListView;
import javafx.collections.ListChangeListener;
import com.cg.scene.RenderObject;

import javafx.scene.image.Image;
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
    private ListView<RenderObject> objectsList;

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

    private ObservableList<RenderObject> sceneObjects = FXCollections.observableArrayList();

    @FXML
    private CheckBox cbWireframe;
    @FXML
    private CheckBox cbTexture;
    @FXML
    private CheckBox cbLighting;

    private Image texture = null;
    private Camera camera = new Camera(
            new Vector3f(0, 0, 100),
            new Vector3f(0, 0, 0),
            1.0F, 1, 0.01F, 100);

    private Timeline timeline;

    private RenderObject selectedObject = null;

    private float cameraRadius = 100.0F;
    private float cameraAzimuth = 0.0F;
    private float cameraZenith = 0.0F;

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

            for (RenderObject obj : sceneObjects) {

                Matrix4x4 modelMatrix = GraphicConveyor.rotateScaleTranslate(
                        obj.getScale().x, obj.getScale().y, obj.getScale().z,
                        obj.getRotation().x, obj.getRotation().y, obj.getRotation().z,
                        obj.getPosition().x, obj.getPosition().y, obj.getPosition().z
                );

                RenderEngine.render(
                        canvas.getGraphicsContext2D(),
                        camera,
                        obj.getMesh(),
                        (int) width,
                        (int) height,
                        modelMatrix,
                        cbWireframe.isSelected(),
                        cbTexture.isSelected(),
                        cbLighting.isSelected(),
                        obj.getTexture()
                );
            }
        });

        timeline.getKeyFrames().add(frame);
        timeline.play();

        canvas.setFocusTraversable(true);
        canvas.setOnMouseClicked(event -> canvas.requestFocus());

        canvas.setOnMousePressed(this::handleMousePress);
        canvas.setOnMouseDragged(this::handleMouseDragged);
        canvas.setOnMouseReleased(this::handleMouseRelease);
        canvas.setOnScroll(this::handleOnScroll);

        updateUI();

        objectsList.setItems(sceneObjects);

        objectsList.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            selectedObject = newVal;
            if (newVal != null) {
                fillTextFields(newVal);
            }
        });

        objectsList.setFocusTraversable(false);
    }

    private void fillTextFields(RenderObject obj) {
        txtScaleX.setText(String.valueOf(obj.getScale().x));
        txtScaleY.setText(String.valueOf(obj.getScale().y));
        txtScaleZ.setText(String.valueOf(obj.getScale().z));

        txtTheta.setText(String.valueOf(obj.getRotation().x));
        txtPsi.setText(String.valueOf(obj.getRotation().y));
        txtPhi.setText(String.valueOf(obj.getRotation().z));

        txtTranslateX.setText(String.valueOf(obj.getPosition().x));
        txtTranslateY.setText(String.valueOf(obj.getPosition().y));
        txtTranslateZ.setText(String.valueOf(obj.getPosition().z));
    }

    private void scaleXChange() {
        if (selectedObject != null) {
            try {
                float value = Float.parseFloat(txtScaleX.getText());
                selectedObject.getScale().x = value;
            } catch (NumberFormatException e) {
            }
        }
    }

    private void scaleYChange() {
        if (selectedObject != null) {
            try {
                float value = Float.parseFloat(txtScaleY.getText());
                selectedObject.getScale().y = value;
            } catch (NumberFormatException e) {
            }
        }
    }

    private void scaleZChange() {
        if (selectedObject != null) {
            try {
                float value = Float.parseFloat(txtScaleZ.getText());
                selectedObject.getScale().z = value;
            } catch (NumberFormatException e) {
            }
        }
    }

    private void thetaChange() {
        if (selectedObject != null) {
            try {
                float value = Float.parseFloat(txtTheta.getText());
                selectedObject.getRotation().x = value;
            } catch (NumberFormatException e) {
            }
        }
    }

    private void psiChange() {
        if (selectedObject != null) {
            try {
                float value = Float.parseFloat(txtPsi.getText());
                selectedObject.getRotation().y = value;
            } catch (NumberFormatException e) {
            }
        }
    }

    private void phiChange() {
        if (selectedObject != null) {
            try {
                float value = Float.parseFloat(txtPhi.getText());
                selectedObject.getRotation().z = value;
            } catch (NumberFormatException e) {
            }
        }
    }

    private void translateXChange() {
        if (selectedObject != null) {
            try {
                float value = Float.parseFloat(txtTranslateX.getText());
                selectedObject.getPosition().x = value;
            } catch (NumberFormatException e) {
            }
        }
    }

    private void translateYChange() {
        if (selectedObject != null) {
            try {
                float value = Float.parseFloat(txtTranslateY.getText());
                selectedObject.getPosition().y = value;
            } catch (NumberFormatException e) {
            }
        }
    }

    private void translateZChange() {
        if (selectedObject != null) {
            try {
                float value = Float.parseFloat(txtTranslateZ.getText());
                selectedObject.getPosition().z = value;
            } catch (NumberFormatException e) {
            }
        }
    }

    private void updateUI() {
        txtScaleX.textProperty().addListener((observable, oldValue, newValue) -> scaleXChange());
        txtScaleY.textProperty().addListener((observable, oldValue, newValue) -> scaleYChange());
        txtScaleZ.textProperty().addListener((observable, oldValue, newValue) -> scaleZChange());

        txtTheta.textProperty().addListener((observable, oldValue, newValue) -> thetaChange());
        txtPsi.textProperty().addListener((observable, oldValue, newValue) -> psiChange());
        txtPhi.textProperty().addListener((observable, oldValue, newValue) -> phiChange());

        txtTranslateX.textProperty().addListener((observable, oldValue, newValue) -> translateXChange());
        txtTranslateY.textProperty().addListener((observable, oldValue, newValue) -> translateYChange());
        txtTranslateZ.textProperty().addListener((observable, oldValue, newValue) -> translateZChange());
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
            float sensitivity = 0.005F;

            cameraAzimuth -= dx * sensitivity;
            cameraZenith -= dy * sensitivity;

            updateCameraPosition();

            mouseX = mouseEvent.getX();
            mouseY = mouseEvent.getY();
        }
    }

    private void handleMouseRelease(MouseEvent mouseEvent) {
        isDragging = false;
    }

    private void handleOnScroll(ScrollEvent mouseEvent) {
        float zoomSpeed = 2.5F;
        if (mouseEvent.getDeltaY() > 0) {
            cameraRadius = Math.max(1.0f, cameraRadius - zoomSpeed);
        } else {
            cameraRadius += zoomSpeed;
        }

        updateCameraPosition();
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
            Model mesh = ObjReader.read(fileContent);

            mesh.triangulate();
            mesh.recalculateNormals();

            RenderObject newObject = new RenderObject(file.getName(), mesh);
            sceneObjects.add(newObject);

        } catch (IOException exception) {

        }
    }

    private void updateCameraPosition() {
        cameraZenith = Math.max(-1.55f, Math.min(1.55f, cameraZenith));

        float x = (float) (Math.sin(cameraAzimuth) * Math.cos(cameraZenith) * cameraRadius);
        float y = (float) (Math.sin(cameraZenith) * cameraRadius);
        float z = (float) (Math.cos(cameraAzimuth) * Math.cos(cameraZenith) * cameraRadius);

        camera.setPosition(new Vector3f(x, y, z));
        camera.setTarget(new Vector3f(0, 0, 0));
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
}