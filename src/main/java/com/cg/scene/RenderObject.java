package com.cg.scene;

import com.cg.math.Vector3f;
import com.cg.model.Model;
import javafx.scene.image.Image;

public class RenderObject {

    private String name;
    private Model mesh;
    private Image texture;

    private Vector3f position;
    private Vector3f rotation;
    private Vector3f scale;

    public RenderObject(String name, Model mesh) {
        this.name = name;
        this.mesh = mesh;
        this.texture = null;

        this.position = new Vector3f(0, 0, 0);
        this.rotation = new Vector3f(0, 0, 0);
        this.scale = new Vector3f(1, 1, 1);
    }

    public Model getMesh() {
        return mesh;
    }

    public Image getTexture() {
        return texture;
    }

    public void setTexture(Image texture) {
        this.texture = texture;
    }

    public Vector3f getPosition() {
        return position;
    }

    public Vector3f getRotation() {
        return rotation;
    }

    public Vector3f getScale() {
        return scale;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }
}