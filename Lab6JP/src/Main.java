// Opracuj aplikację do przetwarzania obrazów w czasie rzeczywistym z efektem "Live". Aplikacja powinna mieć interfejs
// użytkownika z możliwością załadowania obrazu, a następnie zastosowania różnych efektów, takich jak filtry kolorystyczne,
// nasycenie, czy zamiana na odcienie szarości.

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

public class Main extends JFrame {
    private BufferedImage image; // Zmienna do przechowywania obrazu
    private JLabel imageLabel; // Etykieta do wyświetlania obrazu w GUI
    private JPanel controlPanel; // Panel do umieszczania przycisków i suwaków kontrolujących efekty
    private JButton loadButton; // Przycisk do ładowania obrazu
    private JButton grayscaleButton; // Przycisk do zastosowania efektu szarości
    private JButton saturationButton; // Przycisk do regulacji nasycenia
    private JButton colorFilterButton; // Przycisk do nałożenia filtra kolorystycznego
    private JSlider saturationSlider; // Suwak do regulacji nasycenia

    public Main() { // Konstruktor klasy
        setTitle("Aplikacja do przetwarzania obrazów");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Ustawienie akcji po zamknięciu okna - zakończenie programu
        setLocationRelativeTo(null);                      // Ustawienie okna na środku ekranu

        // Tworzymy panel do wyświetlania obrazu
        imageLabel = new JLabel(); // Etykieta, która będzie trzymała obraz
        add(imageLabel, BorderLayout.CENTER); // Dodajemy do głównego okna

        // Tworzymy panel do przycisków
        controlPanel = new JPanel(); // Inicjalizujemy panel kontrolny
        controlPanel.setLayout(new FlowLayout()); // Ustawiamy układ kontrolek na FlowLayout (przyciski w 1 linii)
        controlPanel.setBackground(Color.decode("#DC667C"));

        // Tworzymy przycisk do ładowania obrazu
        loadButton = new JButton("Załaduj obrazek"); // Tworzymy przycisk
        loadButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadImage();
            }
        });
        controlPanel.add(loadButton); // Dodajemy przycisk do panelu kontrolnego

        // Tworzymy przycisk do konwersji na odcienie szarości
        grayscaleButton = new JButton("Czarno-białe"); // Tworzymy przycisk
        grayscaleButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                applyGrayscale();
            }
        });
        controlPanel.add(grayscaleButton);

        // Tworzymy przycisk do zastosowania filtra kolorystycznego
        colorFilterButton = new JButton("Filtr kolorystyczny"); // Tworzymy przycisk
        colorFilterButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                applyColorFilter();
            }
        });
        controlPanel.add(colorFilterButton);

        // Tworzymy przycisk do regulacji nasycenia
        saturationButton = new JButton("Regulacja nasycenia"); // Tworzymy przycisk do regulacji nasycenia
        saturationSlider = new JSlider(0, 200, 100); // Tworzymy suwak o zakresie od 0 do 200, z wartością początkową 100
        saturationSlider.setMajorTickSpacing(50);
        saturationSlider.setMinorTickSpacing(10);
        saturationSlider.setPaintTicks(true); // Włączamy rysowanie na suwaku
        saturationSlider.setPaintLabels(true); // Włączamy etykiety na suwaku
        saturationSlider.addChangeListener(e -> adjustSaturation());
        controlPanel.add(saturationButton);
        controlPanel.add(saturationSlider);

        add(controlPanel, BorderLayout.SOUTH);
    }

    // Metoda do załadowania obrazu
    private void loadImage() {
        JFileChooser fileChooser = new JFileChooser();
        int returnValue = fileChooser.showOpenDialog(this); // Wyświetlamy okno dialogowe do wyboru pliku
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try {
                image = ImageIO.read(file); // Ładujemy obraz do zmiennej image
                ImageIcon imageIcon = new ImageIcon(image); // Tworzymy obiekt ImageIcon z obrazu
                imageLabel.setIcon(imageIcon); // Ustawiamy obraz na etykiecie
                repaint(); // Odświeżamy widok
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Źle załadowany plik", "Błąd", JOptionPane.ERROR_MESSAGE); // Jeśli wystąpił błąd, wyświetlamy komunikat
            }
        }
    }

    // Metoda do konwersji obrazu na odcienie szarości
    private void applyGrayscale() {
        if (image != null) { // Sprawdzamy, czy obraz jest załadowany
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
            repaint(); // Odświeżamy widok
        }
    }

    // Metoda do dostosowania nasycenia obrazu
    private void adjustSaturation() {
        if (image != null) { // Sprawdzamy, czy obraz jest załadowany
            float saturation = saturationSlider.getValue() / 100f; // Pobieramy wartość z suwaka i przekształcamy na zakres 0-2
            for (int y = 0; y < image.getHeight(); y++) {
                for (int x = 0; x < image.getWidth(); x++) {
                    int pixel = image.getRGB(x, y);
                    int r = (pixel >> 16) & 0xff;
                    int g = (pixel >> 8) & 0xff;
                    int b = pixel & 0xff;

                    float[] hsb = Color.RGBtoHSB(r, g, b, null); // Konwertujemy RGB na HSB
                    hsb[1] = Math.min(1.0f, hsb[1] * saturation); // Zmieniamy nasycenie
                    Color newColor = Color.getHSBColor(hsb[0], hsb[1], hsb[2]); // Tworzymy nowy kolor z przekształconymi wartościami HSB

                    image.setRGB(x, y, newColor.getRGB()); // Ustawiamy nowy piksel
                }
            }
            imageLabel.setIcon(new ImageIcon(image)); // Ustawiamy zmodyfikowany obraz na etykiecie
            repaint(); // Odświeżamy widok
        }
    }

    // Metoda do zastosowania filtra kolorystycznego
    private void applyColorFilter() {
        if (image != null) { // Sprawdzamy, czy obraz jest załadowany
            for (int y = 0; y < image.getHeight(); y++) { // Iterujemy po wszystkich wierszach obrazu
                for (int x = 0; x < image.getWidth(); x++) { // Iterujemy po wszystkich kolumnach obrazu
                    int pixel = image.getRGB(x, y); // Pobieramy wartość piksela
                    int b = pixel & 0xff; // Pobieramy składnik niebieski

                    // Filtr niebieski - zostawiamy tylko komponent niebieski
                    int newPixel = (0) | b; // Ustawiamy czerwony i zielony na 0, a niebieski na oryginalny
                    image.setRGB(x, y, newPixel);
                }
            }
            imageLabel.setIcon(new ImageIcon(image)); // Ustawiamy zmodyfikowany obraz na etykiecie
            repaint(); // Odświeżamy widok
        }
    }

    // Metoda główna uruchamiająca aplikację
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Main app = new Main();
            app.setVisible(true);
        });
    }
}
