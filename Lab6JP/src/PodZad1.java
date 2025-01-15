// Zaimplementuj wczytywanie obrazu z pliku, ale uruchom to w tle za pomocą SwingWorker. Upewnij się, że użytkownik
// może nadal korzystać z interfejsu użytkownika podczas ładowania obrazu.

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

public class PodZad1 extends JFrame {
    private BufferedImage image; // Zmienna do przechowywania obrazu
    private JLabel imageLabel; // Etykieta do wyświetlania obrazu w GUI
    private JPanel controlPanel; // Panel do umieszczania przycisków i suwaków kontrolujących efekty
    private JButton loadButton; // Przycisk do ładowania obrazu
    private JButton grayscaleButton; // Przycisk do zastosowania efektu szarości
    private JButton colorFilterButton; // Przycisk do nałożenia filtra kolorystycznego
    private JSlider saturationSlider; // Suwak do regulacji nasycenia

    public PodZad1() {
        setTitle("Aplikacja do przetwarzania obrazów");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Ustawienie akcji po zamknięciu okna - zakończenie programu
        setLocationRelativeTo(null);                      // Ustawienie okna na środku ekranu

        // Tworzymy panel do wyświetlania obrazu
        imageLabel = new JLabel(); // Tworzymy etykietę, która będzie trzymała obraz
        add(imageLabel, BorderLayout.CENTER); // Dodajemy etykietę do głównego okna w centrum

        // Tworzymy panel do przycisków
        controlPanel = new JPanel();
        controlPanel.setLayout(new FlowLayout());
        controlPanel.setBackground(Color.decode("#DC667C"));

        // Tworzymy przycisk do ładowania obrazu
        loadButton = new JButton("Załaduj obrazek"); // Tworzymy przycisk
        loadButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadImage();
            }
        });
        controlPanel.add(loadButton);

        // Tworzymy przycisk do konwersji na odcienie szarości
        grayscaleButton = new JButton("Czarno-białe"); // Tworzymy przycisk
        grayscaleButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                applyGrayscale();
            }
        });
        controlPanel.add(grayscaleButton); // Dodajemy przycisk do panelu kontrolnego

        // Tworzymy przycisk do zastosowania filtra kolorystycznego
        colorFilterButton = new JButton("Filtr kolorystyczny");
        colorFilterButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                applyColorFilter();
            }
        });
        controlPanel.add(colorFilterButton);

        add(controlPanel, BorderLayout.SOUTH);

        // Tworzymy przycisk do regulacji nasycenia
        saturationSlider = new JSlider(0, 200, 100); // Tworzymy suwak o zakresie od 0 do 200, z wartością początkową 100
        saturationSlider.setMajorTickSpacing(50);
        saturationSlider.setMinorTickSpacing(10);
        saturationSlider.setPaintTicks(true); // Włączamy rysowanie na suwaku
        saturationSlider.setPaintLabels(true); // Włączamy etykiety na suwaku
        saturationSlider.addChangeListener(e -> adjustSaturation());
        controlPanel.add(saturationSlider);
    }

    // Metoda do załadowania obrazu
    private void loadImage() {
        JFileChooser fileChooser = new JFileChooser();
        int returnValue = fileChooser.showOpenDialog(this); // Wyświetlamy okno dialogowe do wyboru pliku

        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile(); // Pobieramy wybrany plik

            // Tworzymy SwingWorker, który wczyta obraz w tle
            SwingWorker<BufferedImage, Void> worker = new SwingWorker<>() {
                @Override
                protected BufferedImage doInBackground() throws Exception {
                    return ImageIO.read(file); // Wczytanie obrazu z pliku
                }

                @Override
                protected void done() {
                    try {
                        image = get(); // Pobieramy wynik operacji w tle
                        ImageIcon imageIcon = new ImageIcon(image); // Tworzymy ikonę z obrazu
                        imageLabel.setIcon(imageIcon);
                        repaint(); // Odświeżamy widok
                    } catch (Exception e) {
                        JOptionPane.showMessageDialog(PodZad1.this, "Błąd podczas ładowania obrazu.", "Błąd", JOptionPane.ERROR_MESSAGE);
                    }
                }
            };

            // Uruchamiamy SwingWorker
            worker.execute();
        }
    }


    // Metoda do konwersji obrazu na odcienie szarości
    private void applyGrayscale() {
        if (image != null) {
            // Tworzymy nowy obraz, który będzie przechowywał wynik przetwarzania
            BufferedImage grayscaleImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);

            // Iterujemy po wszystkich pikselach obrazu
            for (int y = 0; y < image.getHeight(); y++) {
                for (int x = 0; x < image.getWidth(); x++) {
                    int pixel = image.getRGB(x, y);
                    int r = (pixel >> 16) & 0xff;
                    int g = (pixel >> 8) & 0xff;
                    int b = pixel & 0xff;

                    // Obliczamy wartość szarości
                    int gray = (r + g + b) / 3;

                    // Tworzymy nowy piksel w odcieniach szarości
                    int newPixel = (gray << 16) | (gray << 8) | gray;

                    // Ustawiamy przetworzony piksel na nowym obrazie
                    grayscaleImage.setRGB(x, y, newPixel);
                }
            }

            // Ustawiamy nowy obraz w etykiecie
            imageLabel.setIcon(new ImageIcon(grayscaleImage));
            image = grayscaleImage; // Zastępujemy oryginalny obraz nowym obrazem w odcieniach szarości
            repaint();
        }
    }

    // Metoda do dostosowania nasycenia obrazu
    private void adjustSaturation() {
        if (image != null) {
            float saturation = saturationSlider.getValue() / 100f; // Pobieramy wartość z suwaka i przekształcamy na zakres 0-2
            for (int y = 0; y < image.getHeight(); y++) {
                for (int x = 0; x < image.getWidth(); x++) {
                    int pixel = image.getRGB(x, y);
                    int r = (pixel >> 16) & 0xff;
                    int g = (pixel >> 8) & 0xff;
                    int b = pixel & 0xff;

                    float[] hsb = Color.RGBtoHSB(r, g, b, null);
                    hsb[1] = Math.min(1.0f, hsb[1] * saturation); // Zmieniamy nasycenie
                    Color newColor = Color.getHSBColor(hsb[0], hsb[1], hsb[2]); // Tworzymy nowy kolor z przekształconymi wartościami HSB

                    image.setRGB(x, y, newColor.getRGB());
                }
            }
            imageLabel.setIcon(new ImageIcon(image)); // Ustawiamy zmodyfikowany obraz na etykiecie
            repaint();
        }
    }

    // Metoda do zastosowania filtra kolorystycznego
    private void applyColorFilter() {
        if (image != null) { // Sprawdzamy, czy obraz jest załadowany
            for (int y = 0; y < image.getHeight(); y++) {
                for (int x = 0; x < image.getWidth(); x++) {
                    int pixel = image.getRGB(x, y);
                    int b = pixel & 0xff;

                    // Filtr niebieski - zostawiamy tylko komponent niebieski
                    int newPixel = (0) | b; // Ustawiamy czerwony i zielony na 0, a niebieski na oryginalny
                    image.setRGB(x, y, newPixel); // Ustawiamy nowy piksel
                }
            }
            imageLabel.setIcon(new ImageIcon(image)); // Ustawiamy zmodyfikowany obraz na etykiecie
            repaint();
        }
    }

    // Metoda główna uruchamiająca aplikację
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            PodZad1 app = new PodZad1();
            app.setVisible(true);
        });
    }
}
