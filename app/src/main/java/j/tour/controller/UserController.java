package j.tour.controller;

public class UserController {
    public boolean validateUser(String username, String password) {
        return !username.trim().isEmpty(); // Menerima semua user non-kosong
    }
}