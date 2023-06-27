import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

public class NotesExtractor {
    private Path listOfNotesPath;
    private Path outputFolder;
    private Path vaultPath;
    private List<Path> paths;

    public NotesExtractor(Path vaultPath, Path listOfNotesPath, Path outputFolder) {
        this.listOfNotesPath = listOfNotesPath;
        this.outputFolder = outputFolder;
        this.vaultPath = vaultPath;
        paths = new ArrayList<>();
    }

    private void extractNotes() {
        if (Files.notExists(vaultPath)) {
            JOptionPane.showMessageDialog(null, "Хранилища не существует!", "Ошибка", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!Files.isRegularFile(listOfNotesPath)) {
            JOptionPane.showMessageDialog(null, "Переданная строка не является файлом!", "Ошибка", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!Files.exists(listOfNotesPath)) {
            JOptionPane.showMessageDialog(null, "Такого файла с путями заметок не существует!", "Ошибка", JOptionPane.ERROR_MESSAGE);
            return;
        }

        List<String> lines;
        try {
            lines = Files.readAllLines(listOfNotesPath, StandardCharsets.UTF_8);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Ошибка при чтении файла...", "Ошибка", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (lines.isEmpty()) {
            JOptionPane.showMessageDialog(null, "В файле нет путей заметок", "Ошибка", JOptionPane.ERROR_MESSAGE);
            return;
        }

        List<Path> paths = new ArrayList<>();
        for (String line : lines) {
            Path path = Paths.get(line);
            path = vaultPath.resolve(path);
            path = path.normalize();
            if (Files.exists(path)) {
                paths.add(path);
            }
        }
        this.paths = paths;
    }

    private void copyNotesInFolder() {
        if (Files.notExists(outputFolder)) {
            JOptionPane.showMessageDialog(null, "Переданной папки: " + outputFolder.getFileName() + " для копирования файлов не сущестувует!", "Ошибка", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!paths.isEmpty()) {
            try {
                for (Path path : paths) {
                    Files.copy(path, outputFolder.resolve(path.getFileName()), StandardCopyOption.REPLACE_EXISTING);
                }
                JOptionPane.showMessageDialog(null, "Копирование прошло успешно", "Успех", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException ignore) {

            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            NotesExtractorGUI gui = new NotesExtractorGUI();
            gui.setVisible(true);
        });
    }

    public void extract() {
        extractNotes();
        copyNotesInFolder();
    }

}

class NotesExtractorGUI extends JFrame implements ActionListener {
    private JTextField vaultPathTextField;
    private JTextField listOfNotesTextField;
    private JTextField outputFolderTextField;
    private JButton extractButton;

    public NotesExtractorGUI() {
        setTitle("Notes Extractor");
        setSize(400, 200);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new GridBagLayout());

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.anchor = GridBagConstraints.LINE_START;
        constraints.insets = new Insets(5, 5, 5, 5);

        Font font = new Font(Font.SANS_SERIF, Font.BOLD, 15);

        JLabel vaultPathLabel = new JLabel("Vault Path:");
        vaultPathTextField = new JTextField();
        vaultPathTextField.setPreferredSize(new Dimension(300, 25));
        vaultPathTextField.setFont(font);

        JLabel listOfNotesLabel = new JLabel("List of Notes Path:");
        listOfNotesTextField = new JTextField();
        listOfNotesTextField.setPreferredSize(new Dimension(300, 25));
        listOfNotesTextField.setFont(font);

        JLabel outputFolderLabel = new JLabel("Output Folder Path:");
        outputFolderTextField = new JTextField();
        outputFolderTextField.setPreferredSize(new Dimension(300, 25));
        outputFolderTextField.setFont(font);

        extractButton = new JButton("Extract");
        extractButton.setFont(font);
        extractButton.addActionListener(this);

        add(vaultPathLabel, constraints);
        constraints.gridy++;
        add(vaultPathTextField, constraints);
        constraints.gridy++;
        add(listOfNotesLabel, constraints);
        constraints.gridy++;
        add(listOfNotesTextField, constraints);
        constraints.gridy++;
        add(outputFolderLabel, constraints);
        constraints.gridy++;
        add(outputFolderTextField, constraints);
        constraints.gridy++;
        constraints.anchor = GridBagConstraints.CENTER;
        add(extractButton, constraints);

        pack();
        setLocationRelativeTo(null);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String vaultPath = vaultPathTextField.getText();
        String listOfNotesPath = listOfNotesTextField.getText();
        String outputFolderPath = outputFolderTextField.getText();

        Path vault = Paths.get(vaultPath);
        Path listOfNotes = Paths.get(listOfNotesPath);
        Path outputFolder = Paths.get(outputFolderPath);

        NotesExtractor notesExtractor = new NotesExtractor(vault, listOfNotes, outputFolder);
        notesExtractor.extract();
    }
}
