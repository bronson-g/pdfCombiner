import org.apache.pdfbox.io.MemoryUsageSetting;
import org.apache.pdfbox.multipdf.PDFMergerUtility;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;

/**
 * Simple GUI to quickly merge multiple pdf files into a single pdf file.
 *
 * created on 2019-06-25
 */
public class PdfCombiner extends JFrame {
    private FileJList list;
    private JFileChooser chooser;

    public static void main(String... args) {
        EventQueue.invokeLater(PdfCombiner::new);
    }

    private PdfCombiner() {
        chooser = new JFileChooser();
        list = new FileJList();

        chooser.setFileFilter(new FileNameExtensionFilter("Portable Document Format", "pdf"));

        setTitle("PDF Combiner");
        setLocationRelativeTo(null);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        initialize();

        pack();

        setMinimumSize(getSize());
        setVisible(true);
    }

    private void initialize() {
        setLayout(new BorderLayout());

        JScrollPane scroller = new JScrollPane();
        scroller.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scroller.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        scroller.setViewportView(list);
        scroller.setMinimumSize(new Dimension(100, 100));

        add(scroller, BorderLayout.CENTER);

        JPanel bottom = new JPanel(new GridLayout());

        JButton addButton = new JButton("+");
        addButton.addActionListener(l -> add());
        bottom.add(addButton);

        JButton removeButton = new JButton("-");
        removeButton.addActionListener(l -> remove());
        bottom.add(removeButton);

        JButton clearButton = new JButton("Clear");
        clearButton.addActionListener(l -> list.clear());
        bottom.add(clearButton);

        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(l -> save());
        bottom.add(saveButton);

        add(bottom, BorderLayout.SOUTH);
    }

    private void remove() {
        int[] indices = list.getSelectedIndices();

        if(!list.isEmpty() && indices.length > 0) {
            Arrays.sort(indices);

            for(int i = indices.length - 1; i >= 0; i--) {
                list.removeElement(indices[i]);
            }
        }
    }

    private void save() {
        if(!list.isEmpty()) try {
            PDFMergerUtility merger = new PDFMergerUtility();
            for(File pdf : list.getElements()) {
                merger.addSource(pdf);
            }

            chooser.setMultiSelectionEnabled(false);
            chooser.showSaveDialog(this);

            String fileName = chooser.getSelectedFile().getAbsolutePath();

            if(!fileName.toLowerCase().endsWith(".pdf")) {
                fileName += ".pdf";
            }

            merger.setDestinationFileName(fileName);
            merger.mergeDocuments(MemoryUsageSetting.setupTempFileOnly());
        } catch(FileNotFoundException e) {
            warn("One or more of the selected files could not be found.");
        } catch(IOException e) {
            warn("An error occurred while saving.");
            e.printStackTrace();
        } else {
            warn("No PDFs were provided.");
        }
    }

    private void add() {
        chooser.setMultiSelectionEnabled(true);
        chooser.showOpenDialog(this);

        for(File pdf : chooser.getSelectedFiles()) {
            if(!list.getElements().contains(pdf)) {
                list.addElement(pdf);
            }
        }
    }

    private void warn(String message) {
        JOptionPane.showConfirmDialog(
                this,
                message,
                "Error",
                JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE,
                null
        );
    }
}
