package br.com.screenshot;

import java.awt.AWTException;
import java.awt.HeadlessException;
import java.awt.MouseInfo;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.Transferable;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;

public class ScreenshotForm {

    private final Stage stage;
    private double xOffset = 0, yOffset = 0;

    public ScreenshotForm(Stage stage) {
        this.stage = stage;
    }

    public void createScene() {
        final AnchorPane root = new AnchorPane();
        final HBox hBox = new HBox();
        AnchorPane.setBottomAnchor(hBox, 10.0);
        AnchorPane.setRightAnchor(hBox, 10.0);
        AnchorPane.setLeftAnchor(hBox, 0.0);
        hBox.setAlignment(Pos.BOTTOM_RIGHT);
        hBox.setSpacing(20);
        hBox.getChildren().add(this.createButtonCamaera());
        hBox.getChildren().add(this.createButtonExtractText());
        root.getChildren().add(hBox);
        root.setStyle("-fx-border-width:3px;-fx-border-color:gray;-fx-background-color:rgba(255,255,255,.1);-fx-border-style: dotted  dashed  dashed  dashed;");
        stage.initStyle(StageStyle.TRANSPARENT);
        stage.setScene(new Scene(root, 500, 500, Color.TRANSPARENT));
        stage.getScene().addEventFilter(MouseEvent.ANY, new ResizeHandler(stage, root));
        xOffset = 0;
        yOffset = 0;
        stage.addEventHandler(MouseEvent.MOUSE_DRAGGED, (MouseEvent event) -> {
            stage.setX(event.getScreenX() - xOffset);
            stage.setY(event.getScreenY() - yOffset);
        });
        stage.addEventHandler(MouseEvent.MOUSE_PRESSED, (MouseEvent event) -> {
            xOffset = event.getSceneX();
            yOffset = event.getSceneY();
        });
        final int pointerMouseX = (int) MouseInfo.getPointerInfo().getLocation().getX();
        final int pointerMouseY = (int) MouseInfo.getPointerInfo().getLocation().getY();
        stage.setX(pointerMouseX);
        stage.setY(pointerMouseY);
        stage.show();
    }

    private Button createButtonCamaera() {
        final InputStream imageCamera = ScreenShot.class.getResourceAsStream("/image/camera.png");
        final Button buttonCamera = new Button();
        final ImageView imageViewCamera = new ImageView(new javafx.scene.image.Image(imageCamera));
        imageViewCamera.setFitWidth(20);
        imageViewCamera.setFitHeight(20);
        buttonCamera.setGraphic(imageViewCamera);
        buttonCamera.setStyle("-fx-background-color:#f9f9f9;-fx-background-radius:50px");
        buttonCamera.setOnAction(e -> this.screenshot(stage, false));
        return buttonCamera;
    }

    private Button createButtonExtractText() {
        final InputStream extracttext = ScreenShot.class.getResourceAsStream("/image/extracttext.png");
        final Button buttonExtractText = new Button();
        final ImageView imageViewExtractText = new ImageView(new javafx.scene.image.Image(extracttext));
        imageViewExtractText.setFitWidth(20);
        imageViewExtractText.setFitHeight(20);
        buttonExtractText.setGraphic(imageViewExtractText);
        buttonExtractText.setStyle("-fx-background-color:#f9f9f9;-fx-background-radius:50px");
        buttonExtractText.setOnAction(e -> this.screenshot(stage, true));
        return buttonExtractText;
    }

    private void screenshot(Stage window, boolean extractText) {
        try {
            window.setIconified(true);
            final Robot robot = new Robot();
            final int positionX = (int) window.getX();
            final int positionY = (int) window.getY();
            final int widthRectangle = (int) window.getWidth();
            final int heightRectangle = (int) window.getHeight();
            final Rectangle captureSize = new Rectangle(positionX, positionY, widthRectangle, heightRectangle);
            final BufferedImage capturedImage = robot.createScreenCapture(captureSize);
            if (!extractText) {
                this.copyImage(capturedImage);
                return;
            }
            final String extrairTextoImagem = this.extrairTextoImagem(capturedImage);
            this.createWindowThatShowsTextCapture(extrairTextoImagem, window);
        } catch (AWTException e) {
            e.printStackTrace();
        }
    }

    private void copyImage(final BufferedImage capturedImage) throws HeadlessException {
        final TransferableImage transferableImage = new TransferableImage(capturedImage);
        final Clipboard systemClipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        systemClipboard.setContents(transferableImage, (Clipboard clipboard, Transferable contents) -> {
            System.out.println(clipboard.getName());
        });
        System.exit(0);
    }

    private void createWindowThatShowsTextCapture(final String extrairTextoImagem, Stage window) {
        final TextArea textArea = new TextArea(extrairTextoImagem);
        textArea.setStyle("-fx-font-weight:bold;-fx-font-size:20pt");
        AnchorPane.setBottomAnchor(textArea, 0.0);
        AnchorPane.setLeftAnchor(textArea, 0.0);
        AnchorPane.setRightAnchor(textArea, 0.0);
        AnchorPane.setTopAnchor(textArea, 0.0);
        final AnchorPane anchorPane = new AnchorPane();
        anchorPane.getChildren().add(textArea);
        final Stage stageExtractText = new Stage();
        final Scene scene = new Scene(anchorPane, 600, 400);
        stageExtractText.setScene(scene);
        stageExtractText.show();
        window.close();
    }

    private String extrairTextoImagem(BufferedImage capturedImage) {
        final ITesseract instance = new Tesseract();
        instance.setDatapath("tessdata");
        instance.setLanguage("por");
        try {
            final String result = instance.doOCR(capturedImage);
            return result;
        } catch (TesseractException e) {
            return "";
        }
    }

}
