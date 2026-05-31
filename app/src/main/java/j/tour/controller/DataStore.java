package j.tour.controller.database;

import j.tour.model.Destination;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class DataStore {
    private static DataStore instance;
    private final List<Destination> destinations = new ArrayList<>();
    private static final String DEST_FILE = "destinasi.txt";

    private DataStore() {
        loadDestinationsFromFile();
    }

    public static synchronized DataStore getInstance() {
        if (instance == null) {
            instance = new DataStore();
        }
        return instance;
    }

    public List<Destination> getAllDestinations() {
        return destinations;
    }

    public void saveDestinationsToFile() {
        try (PrintWriter out = new PrintWriter(new FileWriter(DEST_FILE))) {
            for (Destination dest : destinations) {
                out.println(dest.getDestinationId() + "|" +
                            dest.getName() + "|" +
                            dest.getCategory() + "|" +
                            dest.getLocation() + "|" +
                            dest.getDescription().replace("\n", " "));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadDestinationsFromFile() {
        destinations.clear();
        File file = new File(DEST_FILE);
        
        if (!file.exists()) {
            destinations.add(new Destination("DEST001", "Candi Prambanan", "Budaya", "Sleman", "Candi Hindu megah bersejarah."));
            destinations.add(new Destination("DEST002", "Pantai Parangtritis", "Alam", "Bantul", "Pantai selatan dengan keindahan sunset."));
            destinations.add(new Destination("DEST003", "Keraton Yogyakarta", "Budaya", "Kota Jogja", "Istana resmi Kesultanan Ngayogyakarta."));
            saveDestinationsToFile();
            return;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split("\\|");
                if (data.length >= 5) {
                    destinations.add(new Destination(
                        data[0].trim(), data[1].trim(), data[2].trim(), data[3].trim(), data[4].trim()
                    ));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}