package j.tour.controller.database;

import j.tour.model.Destination;
import j.tour.view.MainDashboardView.SelectedDayTrip;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class TextDatabaseManager {
    private static final String USER_FILE = "users.txt";
    private static final String ITINERARY_FILE = "itinerary.txt";

    static {
        try {
            File userFile = new File(USER_FILE);
            if (!userFile.exists()) userFile.createNewFile();

            File itineraryFile = new File(ITINERARY_FILE);
            if (!itineraryFile.exists()) itineraryFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean registerUser(String email, String password) {
        if (isEmailRegistered(email)) {
            return false; 
        }
        try (FileWriter fw = new FileWriter(USER_FILE, true);
             BufferedWriter bw = new BufferedWriter(fw);
             PrintWriter out = new PrintWriter(bw)) {
            out.println(email + "|" + password);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    private static boolean isEmailRegistered(String email) {
        try (BufferedReader br = new BufferedReader(new FileReader(USER_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split("\\|");
                if (data.length > 0 && data[0].equalsIgnoreCase(email)) {
                    return true;
                }
            }
        } catch (IOException e) {
            // Abaikan jika file kosong
        }
        return false;
    }

    public static boolean loginUser(String email, String password) {
        try (BufferedReader br = new BufferedReader(new FileReader(USER_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split("\\|");
                if (data.length >= 2) {
                    if (data[0].trim().equalsIgnoreCase(email) && data[1].trim().equals(password)) {
                        return true;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void saveItinerary(String email, List<SelectedDayTrip> list) {
        List<String> remainingRecords = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(ITINERARY_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split("\\|");
                if (data.length > 0 && !data[0].equalsIgnoreCase(email)) {
                    remainingRecords.add(line);
                }
            }
        } catch (IOException e) {
            // Abaikan jika file kosong
        }

        try (PrintWriter out = new PrintWriter(new FileWriter(ITINERARY_FILE))) {
            for (String record : remainingRecords) {
                out.println(record);
            }
            for (SelectedDayTrip trip : list) {
                out.println(email + "|" + trip.getDestination().getDestinationId() + "|" + trip.getAssignedDay());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<SelectedDayTrip> loadItinerary(String email, List<Destination> masterDestinations) {
        List<SelectedDayTrip> userList = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(ITINERARY_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split("\\|");
                if (data.length >= 3 && data[0].equalsIgnoreCase(email)) {
                    String destId = data[1].trim();
                    int assignedDay = Integer.parseInt(data[2].trim());

                    Destination matched = masterDestinations.stream()
                            .filter(d -> d.getDestinationId().equalsIgnoreCase(destId))
                            .findFirst()
                            .orElse(null);

                    if (matched != null) {
                        userList.add(new SelectedDayTrip(matched, assignedDay));
                    }
                }
            }
        } catch (IOException e) {
            // Abaikan jika file kosong
        }
        return userList;
    }

    // =========================================================================
    // FITUR BARU: MEMPERBARUI KATA SANDI PENGGUNA
    // =========================================================================
    public static boolean updateUserPassword(String email, String newPassword) {
        List<String> updatedUsers = new ArrayList<>();
        boolean found = false;

        try (BufferedReader br = new BufferedReader(new FileReader(USER_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split("\\|");
                if (data.length >= 2 && data[0].trim().equalsIgnoreCase(email.trim())) {
                    // Ganti password lama dengan password baru
                    updatedUsers.add(data[0].trim() + "|" + newPassword.trim());
                    found = true;
                } else {
                    updatedUsers.add(line);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        // Tulis kembali seluruh data user yang telah diperbarui ke users.txt
        if (found) {
            try (PrintWriter out = new PrintWriter(new FileWriter(USER_FILE))) {
                for (String userRecord : updatedUsers) {
                    out.println(userRecord);
                }
                return true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    // =========================================================================
    // FITUR BARU: MENGHAPUS AKUN & DATA ITINERARY SECARA PERMANEN
    // =========================================================================
    public static boolean deleteUserAccount(String email) {
        List<String> remainingUsers = new ArrayList<>();
        boolean userRemoved = false;

        // 1. Ambil semua baris user kecuali akun yang ingin dihapus
        try (BufferedReader br = new BufferedReader(new FileReader(USER_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split("\\|");
                if (data.length > 0 && data[0].trim().equalsIgnoreCase(email.trim())) {
                    userRemoved = true; // Tandai akun ditemukan dan dihapus
                } else {
                    remainingUsers.add(line);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        if (userRemoved) {
            // 2. Tulis ulang file users.txt tanpa akun yang dihapus
            try (PrintWriter out = new PrintWriter(new FileWriter(USER_FILE))) {
                for (String userRecord : remainingUsers) {
                    out.println(userRecord);
                }
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }

            // 3. Bersihkan seluruh data itinerary milik user ini agar tidak menjadi sampah data
            List<String> remainingItineraries = new ArrayList<>();
            try (BufferedReader br = new BufferedReader(new FileReader(ITINERARY_FILE))) {
                String line;
                while ((line = br.readLine()) != null) {
                    String[] data = line.split("\\|");
                    if (data.length > 0 && !data[0].trim().equalsIgnoreCase(email.trim())) {
                        remainingItineraries.add(line);
                    }
                }
            } catch (IOException e) {
                // Abaikan jika itinerary kosong
            }

            try (PrintWriter out = new PrintWriter(new FileWriter(ITINERARY_FILE))) {
                for (String itineraryRecord : remainingItineraries) {
                    out.println(itineraryRecord);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            return true;
        }
        return false;
    }
}