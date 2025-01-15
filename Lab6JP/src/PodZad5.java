// Wprowadź mechanizm synchronizacji, aby kontrolować jednoczesny dostęp do obiektu reprezentującego obraz. Użyj bloku
// synchronized w odpowiednich miejscach, aby zapewnić bezpieczny dostęp do wspólnego obiektu przez różne wątki. Upewnij się, że
// operacje modyfikujące i odczytujące obiekt są otoczone blokiem synchronized, aby uniknąć współbieżnych modyfikacji.
//Pamiętaj, żeby ograniczyć zakres bloku synchronized do minimum, aby unikać nadmiernego blokowania, co może wpłynąć na
// wydajność. W przypadku tego podzadania, blok synchronized powinien obejmować tylko te części kodu, które faktycznie
// modyfikują lub odczytują wspólny obiekt reprezentujący obraz.

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

public class PodZad5 extends JFrame {
    private BufferedImage image;
    private JLabel imageLabel;
    private JPanel controlPanel;
    private JButton loadButton;
    private JButton grayscaleButton;
    private JButton invertButton;
    private JButton sepiaButton;

    private final Object imageLock = new Object();

    public PodZad5() {
        setTitle("Przetwarzanie obrazu - Asynchroniczne operacje");
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
        grayscaleButton.addActionListener(e -> applyEffectAsync("grayscale"));
        controlPanel.add(grayscaleButton);

        invertButton = new JButton("Efekt: Inwersja");
        invertButton.addActionListener(e -> applyEffectAsync("invert"));
        controlPanel.add(invertButton);

        sepiaButton = new JButton("Efekt: Sepia");
        sepiaButton.addActionListener(e -> applyEffectAsync("sepia"));
        controlPanel.add(sepiaButton);
    }

    private void loadImage() {
        JFileChooser fileChooser = new JFileChooser();
        int returnValue = fileChooser.showOpenDialog(this);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try {
                synchronized (imageLock) {
                    image = ImageIO.read(file);
                }
                imageLabel.setIcon(new ImageIcon(image));
                repaint();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Błąd ładowania obrazu", "Błąd", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void applyEffectAsync(String effect) {
        if (image == null) {
            JOptionPane.showMessageDialog(this, "Najpierw załaduj obraz", "Brak obrazu", JOptionPane.WARNING_MESSAGE);
            return;
        }

        new Thread(() -> {
            BufferedImage tempImage;
            synchronized (imageLock) {
                tempImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);

                for (int y = 0; y < image.getHeight(); y++) {
                    for (int x = 0; x < image.getWidth(); x++) {
                        int pixel = image.getRGB(x, y);
                        tempImage.setRGB(x, y, processPixel(pixel, effect));
                    }
                }
            }

            // Aktualizujemy obraz w głównym wątku
            SwingUtilities.invokeLater(() -> {
                synchronized (imageLock) {
                    image = tempImage;
                }
                imageLabel.setIcon(new ImageIcon(image));
                repaint();
            });
        }).start();
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
            PodZad5 app = new PodZad5();
            app.setVisible(true);
        });
    }
}
