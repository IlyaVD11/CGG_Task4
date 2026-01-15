package com.cg.ui;

import com.cg.model.Model;
import com.cg.objreader.ObjReader;
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

public class GuiController {

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

    private float cameraRadius = 100.0F;
    private float cameraAzimuth = 0.0F;
    private float cameraZenith = 0.0F;
    private double mouseX, mouseY;
    private boolean isDragging = false;

    @FXML
    private void initialize() {
        anchorPane.prefWidthProperty().addListener((ov, oldValue, newValue) -> canvas.setWidth(newValue.doubleValue()));
        anchorPane.prefHeightProperty().addListener((ov, oldValue, newValue) -> canvas.setHeight(newValue.doubleValue()));

        Camera cam1 = new Camera(new Vector3f(0, 0, 100), new Vector3f(0, 0, 0), 1.0F, 1, 0.01F, 100);
        cameras.add(cam1);
        cameraNames.add("Main Camera");
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
            activeCamera.setAspectRatio((float) (width / height));

            for (RenderObject obj : sceneObjects) {
                Matrix4x4 modelMatrix = GraphicConveyor.rotateScaleTranslate(
                        obj.getScale().x, obj.getScale().y, obj.getScale().z,
                        obj.getRotation().x, obj.getRotation().y, obj.getRotation().z,
                        obj.getPosition().x, obj.getPosition().y, obj.getPosition().z
                );
                RenderEngine.render(canvas.getGraphicsContext2D(), activeCamera, obj.getMesh(), (int) width, (int) height, modelMatrix,
                        cbWireframe.isSelected(), cbTexture.isSelected(), cbLighting.isSelected(), obj.getTexture());
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
            if (!txtCamPosX.isFocused()) txtCamPosX.setText(String.format("%.2f", pos.x));
            if (!txtCamPosY.isFocused()) txtCamPosY.setText(String.format("%.2f", pos.y));
            if (!txtCamPosZ.isFocused()) txtCamPosZ.setText(String.format("%.2f", pos.z));
            if (!txtCamTargetX.isFocused()) txtCamTargetX.setText(String.format("%.2f", target.x));
            if (!txtCamTargetY.isFocused()) txtCamTargetY.setText(String.format("%.2f", target.y));
            if (!txtCamTargetZ.isFocused()) txtCamTargetZ.setText(String.format("%.2f", target.z));
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
            if (activeCamera == null || index == camerasList.getSelectionModel().getSelectedIndex()) {
                activeCamera = cameras.get(0);
                camerasList.getSelectionModel().select(0);
            }
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

    private void scaleXChange() { if (selectedObject != null) try { selectedObject.getScale().x = Float.parseFloat(txtScaleX.getText()); } catch (Exception e) {} }
    private void scaleYChange() { if (selectedObject != null) try { selectedObject.getScale().y = Float.parseFloat(txtScaleY.getText()); } catch (Exception e) {} }
    private void scaleZChange() { if (selectedObject != null) try { selectedObject.getScale().z = Float.parseFloat(txtScaleZ.getText()); } catch (Exception e) {} }
    private void thetaChange() { if (selectedObject != null) try { selectedObject.getRotation().x = Float.parseFloat(txtTheta.getText()); } catch (Exception e) {} }
    private void psiChange() { if (selectedObject != null) try { selectedObject.getRotation().y = Float.parseFloat(txtPsi.getText()); } catch (Exception e) {} }
    private void phiChange() { if (selectedObject != null) try { selectedObject.getRotation().z = Float.parseFloat(txtPhi.getText()); } catch (Exception e) {} }
    private void translateXChange() { if (selectedObject != null) try { selectedObject.getPosition().x = Float.parseFloat(txtTranslateX.getText()); } catch (Exception e) {} }
    private void translateYChange() { if (selectedObject != null) try { selectedObject.getPosition().y = Float.parseFloat(txtTranslateY.getText()); } catch (Exception e) {} }
    private void translateZChange() { if (selectedObject != null) try { selectedObject.getPosition().z = Float.parseFloat(txtTranslateZ.getText()); } catch (Exception e) {} }

    private void camPosChange() {
        if (activeCamera != null) {
            try {
                float x = Float.parseFloat(txtCamPosX.getText());
                float y = Float.parseFloat(txtCamPosY.getText());
                float z = Float.parseFloat(txtCamPosZ.getText());
                activeCamera.setPosition(new Vector3f(x, y, z));
            } catch (Exception e) {}
        }
    }

    private void camTargetChange() {
        if (activeCamera != null) {
            try {
                float x = Float.parseFloat(txtCamTargetX.getText());
                float y = Float.parseFloat(txtCamTargetY.getText());
                float z = Float.parseFloat(txtCamTargetZ.getText());
                activeCamera.setTarget(new Vector3f(x, y, z));
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

    private void handleMousePress(MouseEvent mouseEvent) {
        if (mouseEvent.getButton() == MouseButton.PRIMARY) {
            mouseX = mouseEvent.getX(); mouseY = mouseEvent.getY();
            isDragging = true;
        }
    }

    private void handleMouseDragged(MouseEvent mouseEvent) {
        if (isDragging) {
            float dx = (float) (mouseEvent.getX() - mouseX);
            float dy = (float) (mouseEvent.getY() - mouseY);
            cameraAzimuth -= dx * 0.005F;
            cameraZenith -= dy * 0.005F;
            updateCameraPosition();
            mouseX = mouseEvent.getX(); mouseY = mouseEvent.getY();
        }
    }

    private void handleMouseRelease(MouseEvent mouseEvent) { isDragging = false; }

    private void handleOnScroll(ScrollEvent mouseEvent) {
        if (mouseEvent.getDeltaY() > 0) cameraRadius = Math.max(1.0f, cameraRadius - 2.5f);
        else cameraRadius += 2.5f;
        updateCameraPosition();
    }

    private void updateCameraPosition() {
        cameraZenith = Math.max(-1.55f, Math.min(1.55f, cameraZenith));
        float x = (float) (Math.sin(cameraAzimuth) * Math.cos(cameraZenith) * cameraRadius);
        float y = (float) (Math.sin(cameraZenith) * cameraRadius);
        float z = (float) (Math.cos(cameraAzimuth) * Math.cos(cameraZenith) * cameraRadius);
        if (activeCamera != null) {
            activeCamera.setPosition(new Vector3f(x, y, z));
            activeCamera.setTarget(new Vector3f(0, 0, 0));
        }
    }

    @FXML
    private void onOpenModelMenuItemClick() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Model (*.obj)", "*.obj"));
        File file = fileChooser.showOpenDialog((Stage) canvas.getScene().getWindow());
        if (file == null) return;
        try {
            Model mesh = ObjReader.read(Files.readString(file.toPath()));
            mesh.triangulate();
            mesh.recalculateNormals();
            sceneObjects.add(new RenderObject(file.getName(), mesh));
        } catch (IOException e) {}
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

    @FXML private void handleDeleteModel() { if (selectedObject != null) { sceneObjects.remove(selectedObject); selectedObject = null; } }
    @FXML private void handleDeleteTexture() { if (selectedObject != null) selectedObject.setTexture(null); }
}