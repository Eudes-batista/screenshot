package br.com.screenshot;

import java.awt.Image;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

public class TransferableImage implements Transferable {

    private Image image;

    public TransferableImage(Image image) {
        this.image = image;
    }

    @Override
    public Object getTransferData(DataFlavor flavor)
            throws UnsupportedFlavorException, IOException {
        if (flavor.equals(DataFlavor.imageFlavor) && image != null) {
            return image;
        } else {
            throw new UnsupportedFlavorException(flavor);
        }
    }

    @Override
    public DataFlavor[] getTransferDataFlavors() {
        final DataFlavor[] flavors = new DataFlavor[1];
        flavors[0] = DataFlavor.imageFlavor;
        return flavors;
    }

    @Override
    public boolean isDataFlavorSupported(DataFlavor flavor) {
        final DataFlavor[] flavors = getTransferDataFlavors();
        for (DataFlavor flavor1 : flavors) {
            if (flavor.equals(flavor1)) {
                return true;
            }
        }
        return false;
    }

}
