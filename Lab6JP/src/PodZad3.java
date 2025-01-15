//  Wprowadź kilka różnych efektów przetwarzania obrazu, ale zrób to tak, aby każdy efekt działał w osobnym wątku
//  za pomocą SwingWorker. Nie synchronizuj dostępu do obiektu reprezentującego obraz.

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

public class PodZad3 extends JFrame {
    private BufferedImage image;
    private JLabel imageLabel;
    private JPanel controlPanel;
    private JButton loadButton;
    private JButton grayscaleButton;
    private JButton invertButton;
    private JButton sepiaButton;

    public PodZad3() {
        setTitle("Przetwarzanie obrazu - SwingWorker");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        imageLabel = new JLabel();
        add(imageLabel, BorderLayout.CENTER);

        controlPanel = new JPanel();
        controlPanel.setLayout(new FlowLayout());
        add(controlPanel, BorderLayout.SOUTH);

        loadButton = new JButton("Załaduj obraz");
        loadButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadImage();
            }
        });
        controlPanel.setBackground(Color.decode("#DC667C"));
        controlPanel.add(loadButton);

        grayscaleButton = new JButton("Efekt: Czarno-białe");
        grayscaleButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                applyEffectInThread("grayscale");
            }
        });
        controlPanel.add(grayscaleButton);

        invertButton = new JButton("Efekt: Inwersja");
        invertButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                applyEffectInThread("invert");
            }
        });
        controlPanel.add(invertButton);

        sepiaButton = new JButton("Efekt: Sepia");
        sepiaButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                applyEffectInThread("sepia");
            }
        });
        controlPanel.add(sepiaButton);
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

        // SwingWorker to proces wykonujący przetwarzanie w tle
        SwingWorker<BufferedImage, Void> worker = new SwingWorker<>() {
            @Override
            protected BufferedImage doInBackground() throws Exception {
                BufferedImage processedImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);

                for (int y = 0; y < image.getHeight(); y++) {
                    for (int x = 0; x < image.getWidth(); x++) {
                        int pixel = image.getRGB(x, y);
                        processedImage.setRGB(x, y, processPixel(pixel, effect));
                    }
                }

                return processedImage;
            }

            @Override
            protected void done() {
                try {
                    image = get(); // Zastępujemy oryginalny obraz przetworzonym
                    imageLabel.setIcon(new ImageIcon(image));
                    repaint();
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(null, "Błąd przetwarzania obrazu", "Błąd", JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
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
            PodZad3 app = new PodZad3();
            app.setVisible(true);
        });
    }
}
