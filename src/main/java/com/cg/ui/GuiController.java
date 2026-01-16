package com.cg.ui;

import com.cg.model.Model;
import com.cg.objreader.ObjReader;
import com.cg.objreader.ObjReaderException;
import com.cg.objwriter.ObjWriter;
import com.cg.render_engine.Camera;
import com.cg.render_engine.GraphicConveyor;
import com.cg.render_engine.RenderEngine;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.control.*;
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
import com.cg.scene.RenderObject;
import javafx.scene.image.Image;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import javafx.scene.control.Alert;

public class GuiController {

    final private float TRANSLATION = 1.0F;

    @FXML AnchorPane anchorPane;
    @FXML private Canvas canvas;

    @FXML private ListView<RenderObject> objectsList;
    @FXML private TextField txtScaleX, txtScaleY, txtScaleZ;
    @FXML private TextField txtTheta, txtPsi, txtPhi;
    @FXML private TextField txtTranslateX, txtTranslateY, txtTranslateZ;

    @FXML private ListView<String> camerasList;
    @FXML private TextField txtCamPosX, txtCamPosY, txtCamPosZ;
    @FXML private TextField txtCamTargetX, txtCamTargetY, txtCamTargetZ;

    @FXML private CheckBox cbWireframe, cbTexture, cbLighting;

    private ObservableList<RenderObject> sceneObjects = FXCollections.observableArrayList();
    private RenderObject selectedObject = null;

    private ArrayList<Camera> cameras = new ArrayList<>();
    private ObservableList<String> cameraNames = FXCollections.observableArrayList();
    private Camera activeCamera;

    private Timeline timeline;

    private double mouseX;
    private double mouseY;
    private boolean isDragging = false;

    @FXML
    private void initialize() {
        anchorPane.prefWidthProperty().addListener((ov, oldValue, newValue) -> canvas.setWidth(newValue.doubleValue()));
        anchorPane.prefHeightProperty().addListener((ov, oldValue, newValue) -> canvas.setHeight(newValue.doubleValue()));

        Camera cam1 = new Camera(new Vector3f(0, 0, 100), new Vector3f(0, 0, 0), 1.0F, 1, 0.01F, 100);
        cameras.add(cam1);
        cameraNames.add("Camera 1");
        activeCamera = cam1;

        camerasList.setItems(cameraNames);
        camerasList.getSelectionModel().select(0);
        camerasList.getSelectionModel().selectedIndexProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && newVal.intValue() >= 0) {
                activeCamera = cameras.get(newVal.intValue());
                updateCameraTextFields();
            }
        });

        timeline = new Timeline();
        timeline.setCycleCount(Animation.INDEFINITE);
        KeyFrame frame = new KeyFrame(Duration.millis(15), event -> {
            double width = canvas.getWidth();
            double height = canvas.getHeight();
            canvas.getGraphicsContext2D().clearRect(0, 0, width, height);

            if (activeCamera != null) {
                activeCamera.setAspectRatio((float) (width / height));

                for (RenderObject obj : sceneObjects) {
                    Matrix4x4 modelMatrix = GraphicConveyor.rotateScaleTranslate(
                            obj.getScale().x, obj.getScale().y, obj.getScale().z,
                            obj.getRotation().x, obj.getRotation().y, obj.getRotation().z,
                            obj.getPosition().x, obj.getPosition().y, obj.getPosition().z
                    );
                    RenderEngine.render(
                            canvas.getGraphicsContext2D(),
                            activeCamera,
                            obj.getMesh(),
                            (int) width, (int) height,
                            modelMatrix,
                            cbWireframe.isSelected(),
                            cbTexture.isSelected(),
                            cbLighting.isSelected(),
                            obj.getTexture()
                    );
                }
            }
            updateCameraTextFields();
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
            if (newVal != null) fillTextFields(newVal);
        });
    }

    private void updateCameraTextFields() {
        if (activeCamera != null) {
            Vector3f pos = activeCamera.getPosition();
            Vector3f target = activeCamera.getTarget();
            if (!txtCamPosX.isFocused()) txtCamPosX.setText(String.format(java.util.Locale.ROOT, "%.2f", pos.x));
            if (!txtCamPosY.isFocused()) txtCamPosY.setText(String.format(java.util.Locale.ROOT, "%.2f", pos.y));
            if (!txtCamPosZ.isFocused()) txtCamPosZ.setText(String.format(java.util.Locale.ROOT, "%.2f", pos.z));
            if (!txtCamTargetX.isFocused()) txtCamTargetX.setText(String.format(java.util.Locale.ROOT, "%.2f", target.x));
            if (!txtCamTargetY.isFocused()) txtCamTargetY.setText(String.format(java.util.Locale.ROOT, "%.2f", target.y));
            if (!txtCamTargetZ.isFocused()) txtCamTargetZ.setText(String.format(java.util.Locale.ROOT, "%.2f", target.z));
        }
    }

    @FXML
    private void handleAddCamera() {
        Camera newCam = new Camera(new Vector3f(0, 0, 100), new Vector3f(0, 0, 0), 1.0F, 1, 0.01F, 100);
        cameras.add(newCam);
        cameraNames.add("Camera " + cameras.size());
    }

    @FXML
    private void handleRemoveCamera() {
        int index = camerasList.getSelectionModel().getSelectedIndex();
        if (index >= 0 && cameras.size() > 1) {
            cameras.remove(index);
            cameraNames.remove(index);
            activeCamera = cameras.get(0);
            camerasList.getSelectionModel().select(0);
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
            float sensitivity = 0.005F;

            if (selectedObject != null) {
                selectedObject.getRotation().y += dx * sensitivity;
                selectedObject.getRotation().x += dy * sensitivity;
                fillTextFields(selectedObject);
            }

            if (activeCamera != null) {
                activeCamera.movePosition(new Vector3f(dx * 0.01F, dy * 0.01F, 0.0F));
            }

            mouseX = mouseEvent.getX();
            mouseY = mouseEvent.getY();
        }
    }

    private void handleMouseRelease(MouseEvent mouseEvent) {
        isDragging = false;
    }

    private void handleOnScroll(ScrollEvent mouseEvent) {
        float zoomStep = (float) (mouseEvent.getDeltaY() / 100.0F);
        if (activeCamera != null) {
            activeCamera.movePosition(new Vector3f(0.0F, 0.0F, zoomStep));
        }
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

    private void scaleXChange() { if (selectedObject != null) try { selectedObject.getScale().x = Float.parseFloat(txtScaleX.getText().replace(',', '.')); } catch (Exception e) {} }
    private void scaleYChange() { if (selectedObject != null) try { selectedObject.getScale().y = Float.parseFloat(txtScaleY.getText().replace(',', '.')); } catch (Exception e) {} }
    private void scaleZChange() { if (selectedObject != null) try { selectedObject.getScale().z = Float.parseFloat(txtScaleZ.getText().replace(',', '.')); } catch (Exception e) {} }
    private void thetaChange() { if (selectedObject != null) try { selectedObject.getRotation().x = Float.parseFloat(txtTheta.getText().replace(',', '.')); } catch (Exception e) {} }
    private void psiChange() { if (selectedObject != null) try { selectedObject.getRotation().y = Float.parseFloat(txtPsi.getText().replace(',', '.')); } catch (Exception e) {} }
    private void phiChange() { if (selectedObject != null) try { selectedObject.getRotation().z = Float.parseFloat(txtPhi.getText().replace(',', '.')); } catch (Exception e) {} }
    private void translateXChange() { if (selectedObject != null) try { selectedObject.getPosition().x = Float.parseFloat(txtTranslateX.getText().replace(',', '.')); } catch (Exception e) {} }
    private void translateYChange() { if (selectedObject != null) try { selectedObject.getPosition().y = Float.parseFloat(txtTranslateY.getText().replace(',', '.')); } catch (Exception e) {} }
    private void translateZChange() { if (selectedObject != null) try { selectedObject.getPosition().z = Float.parseFloat(txtTranslateZ.getText().replace(',', '.')); } catch (Exception e) {} }

    private void camPosChange() {
        if (activeCamera != null) {
            try {
                activeCamera.setPosition(new Vector3f(
                        Float.parseFloat(txtCamPosX.getText().replace(',', '.')),
                        Float.parseFloat(txtCamPosY.getText().replace(',', '.')),
                        Float.parseFloat(txtCamPosZ.getText().replace(',', '.'))
                ));
            } catch (Exception e) {}
        }
    }

    private void camTargetChange() {
        if (activeCamera != null) {
            try {
                activeCamera.setTarget(new Vector3f(
                        Float.parseFloat(txtCamTargetX.getText().replace(',', '.')),
                        Float.parseFloat(txtCamTargetY.getText().replace(',', '.')),
                        Float.parseFloat(txtCamTargetZ.getText().replace(',', '.'))
                ));
            } catch (Exception e) {}
        }
    }

    private void updateUI() {
        txtScaleX.textProperty().addListener((o, oldV, newV) -> scaleXChange());
        txtScaleY.textProperty().addListener((o, oldV, newV) -> scaleYChange());
        txtScaleZ.textProperty().addListener((o, oldV, newV) -> scaleZChange());
        txtTheta.textProperty().addListener((o, oldV, newV) -> thetaChange());
        txtPsi.textProperty().addListener((o, oldV, newV) -> psiChange());
        txtPhi.textProperty().addListener((o, oldV, newV) -> phiChange());
        txtTranslateX.textProperty().addListener((o, oldV, newV) -> translateXChange());
        txtTranslateY.textProperty().addListener((o, oldV, newV) -> translateYChange());
        txtTranslateZ.textProperty().addListener((o, oldV, newV) -> translateZChange());

        txtCamPosX.textProperty().addListener((o, oldV, newV) -> camPosChange());
        txtCamPosY.textProperty().addListener((o, oldV, newV) -> camPosChange());
        txtCamPosZ.textProperty().addListener((o, oldV, newV) -> camPosChange());
        txtCamTargetX.textProperty().addListener((o, oldV, newV) -> camTargetChange());
        txtCamTargetY.textProperty().addListener((o, oldV, newV) -> camTargetChange());
        txtCamTargetZ.textProperty().addListener((o, oldV, newV) -> camTargetChange());
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void onOpenModelMenuItemClick() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Model (*.obj)", "*.obj"));
        File file = fileChooser.showOpenDialog((Stage) canvas.getScene().getWindow());
        if (file == null) return;

        try {
            String fileContent = Files.readString(file.toPath());
            Model mesh = ObjReader.read(fileContent);

            mesh.triangulate();
            mesh.recalculateNormals();

            RenderObject newObject = new RenderObject(file.getName(), mesh);
            sceneObjects.add(newObject);

        } catch (ObjReaderException e) {
            showError("Parsing Error", "Failed to parse OBJ file:\n" + e.getMessage());
        } catch (IOException e) {
            showError("IO Error", "Failed to read file:\n" + e.getMessage());
        } catch (Exception e) {
            showError("Unknown Error", "An unexpected error occurred:\n" + e.getMessage());
        }
    }

    @FXML
    private void onOpenTextureMenuItemClick() {
        if (selectedObject == null) return;
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.bmp"));
        File file = fileChooser.showOpenDialog((Stage) canvas.getScene().getWindow());
        if (file != null) {
            try { selectedObject.setTexture(new Image(file.toURI().toString())); } catch (Exception e) {}
        }
    }

    @FXML
    private void onSaveModelMenuItemClick() {
        if (selectedObject == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("No Selection");
            alert.setHeaderText(null);
            alert.setContentText("Please select a model from the list to save.");
            alert.showAndWait();
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Model");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Model (*.obj)", "*.obj"));

        fileChooser.setInitialFileName(selectedObject.getName().replace(".obj", "") + "_saved.obj");

        File file = fileChooser.showSaveDialog((Stage) canvas.getScene().getWindow());

        if (file != null) {
            try {
                ObjWriter.write(selectedObject.getMesh(), file.getAbsolutePath());

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Success");
                alert.setHeaderText(null);
                alert.setContentText("Model saved successfully to: " + file.getName());
                alert.showAndWait();

            } catch (IOException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Could not save model");
                alert.setContentText(e.getMessage());
                alert.showAndWait();
            }
        }
    }

    @FXML private void handleDeleteModel() { if (selectedObject != null) { sceneObjects.remove(selectedObject); selectedObject = null; } }
    @FXML private void handleDeleteTexture() { if (selectedObject != null) selectedObject.setTexture(null); }

    @FXML
    public void handleCameraForward(ActionEvent actionEvent) {
        if (activeCamera != null) activeCamera.movePosition(new Vector3f(0, 0, -TRANSLATION));
    }
    @FXML
    public void handleCameraBackward(ActionEvent actionEvent) {
        if (activeCamera != null) activeCamera.movePosition(new Vector3f(0, 0, TRANSLATION));
    }
    @FXML
    public void handleCameraLeft(ActionEvent actionEvent) {
        if (activeCamera != null) activeCamera.movePosition(new Vector3f(TRANSLATION, 0, 0));
    }
    @FXML
    public void handleCameraRight(ActionEvent actionEvent) {
        if (activeCamera != null) activeCamera.movePosition(new Vector3f(-TRANSLATION, 0, 0));
    }
    @FXML
    public void handleCameraUp(ActionEvent actionEvent) {
        if (activeCamera != null) activeCamera.movePosition(new Vector3f(0, TRANSLATION, 0));
    }
    @FXML
    public void handleCameraDown(ActionEvent actionEvent) {
        if (activeCamera != null) activeCamera.movePosition(new Vector3f(0, -TRANSLATION, 0));
    }
}