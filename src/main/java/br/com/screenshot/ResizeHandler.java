/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.com.screenshot;

import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 *
 * @author eudes
 */
public class ResizeHandler implements EventHandler<MouseEvent> {

    /**
     * Space to consider around the stage border for resizing
     */
    private static final double BORDER = 6;

    /**
     * Space to consider the border width factor while resizing
     */
    private final Stage window;
    /**
     * Current cursor reference to the scene
     */
    private Cursor cursor = Cursor.DEFAULT;

    /**
     * X position of the drag start
     */
    private double startX = 0;

    /**
     * Y position of the drag start
     */
    private double startY = 0;
    private final double widthOrigin;

    public ResizeHandler(Stage window, AnchorPane root) {
        this.window = window;
        this.widthOrigin = root.getWidth();
    }

    @Override
    public void handle(final MouseEvent event) {
        final EventType<? extends MouseEvent> eventType = event.getEventType();
        final Scene scene = window.getScene();
        final double mouseEventX = event.getSceneX();
        final double mouseEventY = event.getSceneY();
        final double sceneWidth = scene.getWidth();
        final double sceneHeight = scene.getHeight();
        final boolean isResizable = true;
        if (!isResizable) {
            return;
        }
        if (MouseEvent.MOUSE_MOVED.equals(eventType)) {
            assignCursor(scene, event, mouseEventX, mouseEventY, sceneWidth, sceneHeight);
            return;
        }
        if (MouseEvent.MOUSE_PRESSED.equals(eventType)) {
            startX = window.getWidth() - mouseEventX;
            startY = window.getHeight() - mouseEventY;
            consumeEventIfNotDefaultCursor(event);
            return;
        }
        if (MouseEvent.MOUSE_DRAGGED.equals(eventType) && !Cursor.DEFAULT.equals(cursor)) {
            consumeEventIfNotDefaultCursor(event);
            if (!Cursor.W_RESIZE.equals(cursor) && !Cursor.E_RESIZE.equals(cursor)) {
                handleHeightResize(event);
            }

            if (!Cursor.N_RESIZE.equals(cursor) && !Cursor.S_RESIZE.equals(cursor)) {
                handleWidthResize(event);
            }
            return;
        }
    }

    private void assignCursor(final Scene scene, final MouseEvent event, final double mouseEventX,
            final double mouseEventY, final double sceneWidth, final double sceneHeight) {
        final Cursor cursor1;

        if (mouseEventX < BORDER && mouseEventY < BORDER) {
            cursor1 = Cursor.NW_RESIZE;
        } else if (mouseEventX < BORDER && mouseEventY > sceneHeight - BORDER) {
            cursor1 = Cursor.SW_RESIZE;
        } else if (mouseEventX > sceneWidth - BORDER
                && mouseEventY < BORDER) {
            cursor1 = Cursor.NE_RESIZE;
        } else if (mouseEventX > sceneWidth - BORDER && mouseEventY > sceneHeight - BORDER) {
            cursor1 = Cursor.SE_RESIZE;
        } else if (mouseEventX < BORDER) {
            cursor1 = Cursor.W_RESIZE;
        } else if (mouseEventX > sceneWidth - BORDER) {
            cursor1 = Cursor.E_RESIZE;
        } else if (mouseEventY < BORDER) {
            cursor1 = Cursor.N_RESIZE;
        } else if (mouseEventY > sceneHeight - BORDER) {
            cursor1 = Cursor.S_RESIZE;
        } else {
            cursor1 = Cursor.DEFAULT;
        }
        cursor = cursor1;
        scene.setCursor(cursor);
    }

    /**
     * Consumes the mouse event if the cursor is not the DEFAULT cursor.
     *
     * @param event MouseEvent instance
     */
    private void consumeEventIfNotDefaultCursor(final MouseEvent event) {
        if (!cursor.equals(Cursor.DEFAULT)) {
            event.consume();
        }
    }

    /**
     * Processes the vertical drag movement and resizes the window height.
     *
     * @param event MouseEvent instance
     */
    private void handleHeightResize(final MouseEvent event) {
        final double mouseEventY = event.getSceneY();
        final double minHeight = 30;
        if (Cursor.NW_RESIZE.equals(cursor)
                || Cursor.N_RESIZE.equals(cursor)
                || Cursor.NE_RESIZE.equals(cursor)) {
            if (window.getHeight() > minHeight || mouseEventY < 0) {
                final double newHeight = window.getY() - event.getScreenY() + window.getHeight();
                window.setHeight(max(newHeight, minHeight));
                window.setY(event.getScreenY());
            }
        } else if (window.getHeight() > minHeight || mouseEventY + startY - window.getHeight() > 0) {
            final double newHeight = mouseEventY + startY;
            window.setHeight(max(newHeight, minHeight));
        }
    }

    /**
     * Processes the horizontal drag movement and resizes the window width.
     *
     * @param event MouseEvent instance
     */
    private void handleWidthResize(final MouseEvent event) {
        final double mouseEventX = event.getSceneX();
        final double minWidth = 100;
        if (Cursor.NW_RESIZE.equals(cursor)
                || Cursor.W_RESIZE.equals(cursor)
                || Cursor.SW_RESIZE.equals(cursor)) {
            if (window.getWidth() > minWidth || mouseEventX < 0) {
                final double newWidth = window.getX() - event.getScreenX() + window.getWidth();
                window.setWidth(max(newWidth, minWidth));
                window.setX(event.getScreenX());
            }
        } else if (window.getWidth() > minWidth || mouseEventX + startX - window.getWidth() > 0) {
            final double newWidth = mouseEventX + startX;
            window.setWidth(max(newWidth, minWidth));
        }
    }

    /**
     * Determines the max value among the provided two values.
     *
     * @param value1 First value
     * @param value2 Second value
     * @return Maximum value of the given two values.
     */
    private double max(final double value1, final double value2) {
        return value1 > value2 ? value1 : value2;
    }
}
