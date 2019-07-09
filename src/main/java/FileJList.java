import javax.swing.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * A JList that has files as elements, and can be reordered via drag and drop.
 *
 * created on 2019-07-05
 */
class FileJList extends JList<String> {

    private DefaultListModel<String> view;
    private List<File> model;

    FileJList() {
        super();

        view = new DefaultListModel<>();
        model = new ArrayList<>();

        setModel(view);
        setDragEnabled(true);
        setDropMode(DropMode.INSERT);
        setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        setTransferHandler(new DragAndDrop());
    }

    List<File> getElements() {
        return new ArrayList<>(model);
    }

    boolean isEmpty() {
        return model.isEmpty() && view.isEmpty();
    }

    void addElement(int i, File file) {
        model.add(i, file);
        view.addElement(file.getName());
    }

    void addElement(File file) {
        model.add(file);
        view.addElement(file.getName());
    }

    File getElement(int i) {
        return model.get(i);
    }

    File removeElement(int i) {
        view.remove(i);
        return model.remove(i);
    }

    void clear() {
        view.clear();
        model.clear();
    }

    private class DragAndDrop extends TransferHandler {
        private int index;

        @Override
        public int getSourceActions(JComponent comp) {
            return MOVE;
        }

        @Override
        public Transferable createTransferable(JComponent comp) {
            index = getSelectedIndex();
            return new StringSelection(getSelectedValue());
        }

        @Override
        public void exportDone(JComponent comp, Transferable trans, int action) {
            removeElement(action == MOVE ? index + 1 : index);
        }

        @Override
        public boolean canImport(TransferHandler.TransferSupport support) {
            return support.isDataFlavorSupported(DataFlavor.stringFlavor);
        }

        @Override
        public boolean importData(TransferHandler.TransferSupport support) {
            JList.DropLocation dl = (JList.DropLocation) support.getDropLocation();
            addElement(dl.getIndex(), model.get(index));
            return true;
        }
    }
}
