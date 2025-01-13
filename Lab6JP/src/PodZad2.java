import javax.swing.*; // Importowanie komponentów Swing do tworzenia GUI
import java.awt.*; // Importowanie klas do tworzenia interfejsu graficznego (np. LayoutManager)
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage; // Importowanie klasy BufferedImage do przetwarzania obrazów
import java.io.File; // Importowanie klasy File do obsługi plików
import javax.imageio.ImageIO; // Importowanie klasy ImageIO do ładowania obrazów
import javax.swing.event.ChangeEvent;

public class PodZad2 extends JFrame { // Klasa główna aplikacji, dziedziczy po JFrame
    private BufferedImage image; // Obraz do przetwarzania
    private JLabel imageLabel; // Etykieta wyświetlająca obraz
    private JSlider saturationSlider; // Suwak do regulacji nasycenia

    public PodZad2() {
        setTitle("Przetwarzanie obrazu w czasie rzeczywistym");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Etykieta do wyświetlania obrazu
        imageLabel = new JLabel();
        add(imageLabel, BorderLayout.CENTER);

        // Panel z kontrolkami
        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new FlowLayout());
        controlPanel.setBackground(Color.decode("#DC667C"));

        // Przycisk ładowania obrazu
        JButton loadButton = new JButton("Załaduj obraz");
        loadButton.addActionListener(e -> loadImage());
        controlPanel.add(loadButton);

        // Przycisk odcieni szarości
        JButton grayscaleButton = new JButton("Czarno-białe");
        grayscaleButton.addActionListener(e -> applyGrayscaleInWorker());
        controlPanel.add(grayscaleButton);

        // Suwak nasycenia
        saturationSlider = new JSlider(0, 200, 100);
        saturationSlider.setMajorTickSpacing(50);
        saturationSlider.setMinorTickSpacing(10);
        saturationSlider.setPaintTicks(true);
        saturationSlider.setPaintLabels(true);
        saturationSlider.addChangeListener(e -> adjustSaturationInWorker());
        controlPanel.add(saturationSlider);

        // Przycisk filtra kolorystycznego
        JButton colorFilterButton = new JButton("Filtr kolorystyczny");
        colorFilterButton.addActionListener(e -> applyColorFilterInWorker());
        controlPanel.add(colorFilterButton);

        add(controlPanel, BorderLayout.SOUTH);
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
                JOptionPane.showMessageDialog(this, "Błąd podczas ładowania obrazu", "Błąd", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void applyGrayscaleInWorker() {
        if (image != null) {
            new SwingWorker<BufferedImage, Void>() {
                @Override
                protected BufferedImage doInBackground() {
                    BufferedImage grayscaleImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);
                    for (int y = 0; y < image.getHeight(); y++) {
                        for (int x = 0; x < image.getWidth(); x++) {
                            int pixel = image.getRGB(x, y);
                            int r = (pixel >> 16) & 0xff;
                            int g = (pixel >> 8) & 0xff;
                            int b = pixel & 0xff;
                            int gray = (r + g + b) / 3;
                            int newPixel = (gray << 16) | (gray << 8) | gray;
                            grayscaleImage.setRGB(x, y, newPixel);
                        }
                    }
                    return grayscaleImage;
                }

                @Override
                protected void done() {
                    try {
                        image = get();
                        imageLabel.setIcon(new ImageIcon(image));
                        repaint();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }.execute();
        }
    }

    private void adjustSaturationInWorker() {
        if (image != null) {
            float saturation = saturationSlider.getValue() / 100f;
            new SwingWorker<BufferedImage, Void>() {
                @Override
                protected BufferedImage doInBackground() {
                    BufferedImage adjustedImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);
                    for (int y = 0; y < image.getHeight(); y++) {
                        for (int x = 0; x < image.getWidth(); x++) {
                            int pixel = image.getRGB(x, y);
                            int r = (pixel >> 16) & 0xff;
                            int g = (pixel >> 8) & 0xff;
                            int b = pixel & 0xff;
                            float[] hsb = Color.RGBtoHSB(r, g, b, null);
                            hsb[1] = Math.min(1.0f, hsb[1] * saturation);
                            Color newColor = Color.getHSBColor(hsb[0], hsb[1], hsb[2]);
                            adjustedImage.setRGB(x, y, newColor.getRGB());
                        }
                    }
                    return adjustedImage;
                }

                @Override
                protected void done() {
                    try {
                        image = get();
                        imageLabel.setIcon(new ImageIcon(image));
                        repaint();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }.execute();
        }
    }

    private void applyColorFilterInWorker() {
        if (image != null) {
            new SwingWorker<BufferedImage, Void>() {
                @Override
                protected BufferedImage doInBackground() {
                    BufferedImage filteredImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);
                    for (int y = 0; y < image.getHeight(); y++) {
                        for (int x = 0; x < image.getWidth(); x++) {
                            int pixel = image.getRGB(x, y);
                            int r = (pixel >> 16) & 0xff;
                            int g = (pixel >> 8) & 0xff;
                            int b = pixel & 0xff;
                            int newPixel = (0) | b;
                            filteredImage.setRGB(x, y, newPixel);
                        }
                    }
                    return filteredImage;
                }

                @Override
                protected void done() {
                    try {
                        image = get();
                        imageLabel.setIcon(new ImageIcon(image));
                        repaint();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }.execute();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            PodZad2 app = new PodZad2();
            app.setVisible(true);
        });
    }
}
