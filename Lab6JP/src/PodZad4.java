// Dodaj funkcję anulowania operacji przetwarzania obrazu w trakcie jej trwania. Spróbuj anulować operację w momencie,
// gdy jeden z SwingWorker jest w trakcie aktualizowania interfejsu graficznego, a drugi pracuje nad efektem.

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

public class PodZad4 extends JFrame {
    private BufferedImage image;
    private JLabel imageLabel;
    private JPanel controlPanel;
    private JButton loadButton;
    private JButton grayscaleButton;
    private JButton invertButton;
    private JButton sepiaButton;
    private JButton cancelButton;

    private SwingWorker<BufferedImage, Void> currentWorker;

    public PodZad4() {
        setTitle("Przetwarzanie obrazu - SwingWorker");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        imageLabel = new JLabel();
        add(imageLabel, BorderLayout.CENTER);

        controlPanel = new JPanel();
        controlPanel.setLayout(new FlowLayout());
        controlPanel.setBackground(Color.decode("#DC667C"));
        add(controlPanel, BorderLayout.SOUTH);

        loadButton = new JButton("Załaduj obraz");
        loadButton.addActionListener(e -> loadImage());
        controlPanel.add(loadButton);

        grayscaleButton = new JButton("Efekt: Czarno-białe");
        grayscaleButton.addActionListener(e -> applyEffectInThread("grayscale"));
        controlPanel.add(grayscaleButton);

        invertButton = new JButton("Efekt: Inwersja");
        invertButton.addActionListener(e -> applyEffectInThread("invert"));
        controlPanel.add(invertButton);

        sepiaButton = new JButton("Efekt: Sepia");
        sepiaButton.addActionListener(e -> applyEffectInThread("sepia"));
        controlPanel.add(sepiaButton);

        cancelButton = new JButton("Anuluj");
        cancelButton.addActionListener(e -> cancelProcessing());
        cancelButton.setEnabled(false);
        controlPanel.add(cancelButton);
    }

    private void loadImage() {
        JFileChooser fileChooser = new JFileChooser();
        int returnValue = fileChooser.showOpenDialog(this);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try {
                image = ImageIO.read(file);
                imageLabel.setIcon(new ImageIcon(image));
                repaint();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Błąd ładowania obrazu", "Błąd", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void applyEffectInThread(String effect) {
        if (image == null) {
            JOptionPane.showMessageDialog(this, "Najpierw załaduj obraz", "Brak obrazu", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (currentWorker != null && !currentWorker.isDone()) {
            JOptionPane.showMessageDialog(this, "Inny proces jest w toku", "Błąd", JOptionPane.WARNING_MESSAGE);
            return;
        }

        cancelButton.setEnabled(true);

        currentWorker = new SwingWorker<>() {
            @Override
            protected BufferedImage doInBackground() throws Exception {
                BufferedImage processedImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);

                for (int y = 0; y < image.getHeight(); y++) {
                    if (isCancelled()) {
                        break;
                    }
                    for (int x = 0; x < image.getWidth(); x++) {
                        if (isCancelled()) {
                            break;
                        }
                        int pixel = image.getRGB(x, y);
                        processedImage.setRGB(x, y, processPixel(pixel, effect));
                    }
                }

                return isCancelled() ? null : processedImage;
            }

            @Override
            protected void done() {
                try {
                    cancelButton.setEnabled(false);
                    if (!isCancelled()) {
                        image = get(); // Zastępujemy oryginalny obraz przetworzonym
                        imageLabel.setIcon(new ImageIcon(image));
                        repaint();
                    } else {
                        JOptionPane.showMessageDialog(null, "Operacja anulowana", "Anulowano", JOptionPane.INFORMATION_MESSAGE);
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(null, "Błąd przetwarzania obrazu", "Błąd", JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        currentWorker.execute();
    }

    private void cancelProcessing() {
        if (currentWorker != null && !currentWorker.isDone()) {
            currentWorker.cancel(true);
        }
    }

    private int processPixel(int pixel, String effect) {
        int r = (pixel >> 16) & 0xff;
        int g = (pixel >> 8) & 0xff;
        int b = pixel & 0xff;

        switch (effect) {
            case "grayscale":
                int gray = (r + g + b) / 3;
                return (0xff << 24) | (gray << 16) | (gray << 8) | gray;

            case "invert":
                return (0xff << 24) | ((255 - r) << 16) | ((255 - g) << 8) | (255 - b);

            case "sepia":
                int tr = (int)(0.393 * r + 0.769 * g + 0.189 * b);
                int tg = (int)(0.349 * r + 0.686 * g + 0.168 * b);
                int tb = (int)(0.272 * r + 0.534 * g + 0.131 * b);
                tr = Math.min(255, tr);
                tg = Math.min(255, tg);
                tb = Math.min(255, tb);
                return (0xff << 24) | (tr << 16) | (tg << 8) | tb;

            default:
                return pixel; // Brak przetwarzania
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            PodZad4 app = new PodZad4();
            app.setVisible(true);
        });
    }
}