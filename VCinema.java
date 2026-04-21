
import javafx.application.Application;
import javafx.animation.*;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.*;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.scene.shape.*;
import javafx.scene.text.*;
import javafx.scene.effect.*;
import javafx.stage.*;
import javafx.util.Duration;
import java.io.File;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.*;

public class VCinema extends Application {

    // data models
    static class User {

        String name, email, password, role, phone;
        boolean mustChangePassword = false;

        User(String name, String email, String password, String role) {
            this.name = name;
            this.email = email;
            this.password = password;
            this.role = role;
            this.phone = "";
        }
    }

    static class Movie {

        String id, title, emoji, description, genre, director, cast;
        int price;
        double rating;
        List<String> showTimes;
        String posterPath = "";
        String posterUrl = "";

        Movie(String id, String t, String e, String d, String g,
                String dir, String cast, int p, double r, String... times) {
            this.id = id;
            title = t;
            emoji = e;
            description = d;
            genre = g;
            director = dir;
            this.cast = cast;
            price = p;
            rating = r;
            showTimes = Arrays.asList(times);
        }

        Movie poster(String url) {
            this.posterUrl = url;
            return this;
        }
    }

    static class Booking {

        String userEmail, movieId, movieTitle, date, time, payMethod;
        int tickets, totalPrice;

        Booking(String ue, String mid, String mt, String date, String time,
                int tickets, int total, String pay) {
            userEmail = ue;
            movieId = mid;
            movieTitle = mt;
            this.date = date;
            this.time = time;
            this.tickets = tickets;
            totalPrice = total;
            payMethod = pay;
        }
    }

    static class Review {

        String userEmail, userName, movieId, text;
        long timestamp;

        Review(String ue, String un, String mid, String txt) {
            userEmail = ue;
            userName = un;
            movieId = mid;
            text = txt;
            timestamp = System.currentTimeMillis();
        }
    }

    private List<User> users = new ArrayList<>();
    private List<Movie> movies = new ArrayList<>();
    private List<Booking> bookings = new ArrayList<>();
    private List<Review> reviews = new ArrayList<>();

    private String sessionEmail = "";
    private String sessionRole = "";

    private Stage primaryStage;
    private double stageW = 1200, stageH = 750;

    private User me() {
        return users.stream().filter(u -> u.email.equals(sessionEmail)).findFirst().orElse(null);
    }

    private String myName() {
        User u = me();
        return u != null ? u.name : "";
    }

    private boolean isAdmin() {
        return "admin".equals(sessionRole);
    }

    private boolean isStaff() {
        return "staff".equals(sessionRole) || isAdmin();
    }

private boolean isValidEmail(String email) {
    // check that @ exists
    int atIndex = email.indexOf("@");
    if (atIndex <= 0) return false;

    // check that there is something after @
    String afterAt = email.substring(atIndex + 1);
    if (afterAt.isEmpty()) return false;

    // check that there is a dot after @
    int dotIndex = afterAt.lastIndexOf(".");
    if (dotIndex <= 0) return false;

    // check that there is something after the dot
    String afterDot = afterAt.substring(dotIndex + 1);
    if (afterDot.isEmpty()) return false;

    // check no spaces anywhere
    if (email.contains(" ")) return false;

    return true;
}

    private Movie movieById(String id) {
        return movies.stream().filter(m -> m.id.equals(id)).findFirst().orElse(null);
    }

    private void seedData() {
        users.add(new User("Admin", "ameer@mail.com", "0000", "admin"));

        movies.add(new Movie("m1", "The Dark Knight", "🦇",
                "Batman faces the anarchic Joker who plans to plunge Gotham into chaos.",
                "Action", "Christopher Nolan", "Christian Bale, Heath Ledger", 13, 9.0,
                "16:00", "19:30", "22:00")
                .poster("https://image.tmdb.org/t/p/w500/qJ2tW6WMUDux911r6m7haRef0WH.jpg"));

        movies.add(new Movie("m2", "Inception", "🌀",
                "A thief enters dreams to steal secrets - but his biggest mission is planting an idea.",
                "Sci-Fi", "Christopher Nolan", "Leonardo DiCaprio, Joseph Gordon-Levitt", 12, 8.8,
                "17:00", "20:15", "23:00")
                .poster("https://image.tmdb.org/t/p/w500/ljsZTbVsrQSqZgWeep2B1QiDKuh.jpg"));

        movies.add(new Movie("m3", "Interstellar", "🚀",
                "A crew of astronauts travel through a wormhole to find humanity a new home.",
                "Sci-Fi", "Christopher Nolan", "Matthew McConaughey, Anne Hathaway", 15, 8.6,
                "16:30", "20:00", "22:45")
                .poster("https://image.tmdb.org/t/p/w500/gEU2QniE6E77NI6lCU6MxlNBvIx.jpg"));

        movies.add(new Movie("m4", "Avatar", "🌿",
                "A marine on Pandora becomes torn between following orders and protecting an alien civilization.",
                "Adventure", "James Cameron", "Sam Worthington, Zoe Saldana", 11, 7.8,
                "15:00", "18:30", "21:30")
                .poster("https://image.tmdb.org/t/p/w500/jRXYjXNq0Cs2TcJjLkki24MLp7u.jpg"));

        movies.add(new Movie("m5", "Joker", "🃏",
                "Failed comedian Arthur Fleck descends into madness and becomes an icon of chaos.",
                "Drama", "Todd Phillips", "Joaquin Phoenix", 10, 8.4,
                "17:30", "20:30", "23:15")
                .poster("https://image.tmdb.org/t/p/w500/udDclJoHjfjb8Ekgsd4FDteOkCU.jpg"));

        movies.add(new Movie("m6", "Oppenheimer", "☢",
                "The story of the physicist who led the Manhattan Project and changed the world forever.",
                "Drama", "Christopher Nolan", "Cillian Murphy, Emily Blunt", 14, 8.5,
                "16:00", "19:45", "22:30")
                .poster("https://image.tmdb.org/t/p/w500/8Gxv8gSFCU0XGDykEGv7zR1n2ua.jpg"));

        movies.add(new Movie("m7", "Spider-Man: No Way Home", "🕷",
                "Peter Parker asks Doctor Strange for help after his identity is revealed to the world.",
                "Action", "Jon Watts", "Tom Holland, Zendaya", 11, 8.2,
                "14:30", "17:15", "20:00", "22:45")
                .poster("https://image.tmdb.org/t/p/w500/1g0dhYtq4irTY1GPXvft6k4YLjm.jpg"));

        movies.add(new Movie("m8", "Barbie", "💗",
                "Barbie and Ken leave Barbieland to discover the real world - and themselves.",
                "Comedy", "Greta Gerwig", "Margot Robbie, Ryan Gosling", 10, 7.0,
                "15:30", "18:00", "20:30")
                .poster("https://image.tmdb.org/t/p/w500/iuFNMS8vlbStZg2tKpMDeltaWx9.jpg"));

        movies.add(new Movie("m9", "Titanic", "🚢",
                "Two souls from different worlds fall in love aboard the doomed RMS Titanic.",
                "Romance", "James Cameron", "Leonardo DiCaprio, Kate Winslet", 10, 7.8,
                "16:00", "19:30", "22:00")
                .poster("https://image.tmdb.org/t/p/w500/9xjZS2rlVxm8SFx8kPC3aIGCOYQ.jpg"));

        movies.add(new Movie("m10", "John Wick", "🔫",
                "A legendary retired assassin hunts the men who killed his dog and took everything from him.",
                "Action", "Chad Stahelski", "Keanu Reeves", 13, 7.4,
                "18:00", "21:00", "23:30")
                .poster("https://image.tmdb.org/t/p/w500/fZPSd91yGE9fCcCe6OoQr6E3Bev.jpg"));

        movies.add(new Movie("m11", "Frozen II", "❄",
                "Elsa travels into the unknown to discover the mysterious origin of her powers.",
                "Animation", "Chris Buck", "Idina Menzel, Kristen Bell", 9, 6.8,
                "14:00", "16:30", "19:00")
                .poster("https://image.tmdb.org/t/p/w500/xTHVFGS0GRFnNs4de0nOwTL3HvL.jpg"));

        movies.add(new Movie("m12", "Thor", "⚡",
                "The powerful but arrogant god Thor is cast to Earth and must prove himself worthy.",
                "Action", "Kenneth Branagh", "Chris Hemsworth, Natalie Portman", 12, 7.0,
                "17:00", "20:00", "22:30")
                .poster("https://image.tmdb.org/t/p/w500/prSfAi1xGrhLQNxVSUFh61xAqzd.jpg"));

        movies.add(new Movie("m13", "The Lion King", "🦁",
                "Young Simba flees his kingdom after his father's murder, only to return and reclaim his throne.",
                "Animation", "Jon Favreau", "Donald Glover, Beyonce", 10, 7.1,
                "14:00", "16:30", "19:15")
                .poster("https://image.tmdb.org/t/p/w500/2bXbqYdUdNVa8VIWXVfclP2ICtT.jpg"));

        movies.add(new Movie("m14", "Black Panther", "🐾",
                "T'Challa returns to Wakanda to take the throne but faces a challenger from the past.",
                "Action", "Ryan Coogler", "Chadwick Boseman, Michael B. Jordan", 12, 7.3,
                "15:00", "18:00", "21:00")
                .poster("https://image.tmdb.org/t/p/w500/uxzzxijgPIY7slzFvMotPv8wjKA.jpg"));

        movies.add(new Movie("m15", "Dune", "🏜",
                "Paul Atreides leads a rebellion on the desert planet Arrakis to protect its people and resources.",
                "Sci-Fi", "Denis Villeneuve", "Timothee Chalamet, Zendaya", 14, 8.0,
                "16:00", "19:30", "22:15")
                .poster("https://image.tmdb.org/t/p/w500/d5NXSklpcKoA8A9fBMPyIMHxeGe.jpg"));

        movies.add(new Movie("m16", "The Matrix", "💊",
                "A hacker discovers that reality is a simulation and joins a rebellion against the machines.",
                "Sci-Fi", "The Wachowskis", "Keanu Reeves, Laurence Fishburne", 12, 8.7,
                "17:30", "20:30", "23:00")
                .poster("https://image.tmdb.org/t/p/w500/f89U3ADr1oiB1s9GkdPOEpXUk5H.jpg"));

        movies.add(new Movie("m17", "Guardians of the Galaxy", "🌌",
                "A ragtag team of cosmic misfits must band together to stop a powerful villain.",
                "Action", "James Gunn", "Chris Pratt, Zoe Saldana", 11, 8.0,
                "15:30", "18:15", "21:00")
                .poster("https://image.tmdb.org/t/p/w500/r7vmZjiyZw9rpJMQJdXpjgiCOk9.jpg"));

        movies.add(new Movie("m18", "Top Gun: Maverick", "✈",
                "After 30 years, Maverick returns to train a new squad of Top Gun graduates for a daring mission.",
                "Action", "Joseph Kosinski", "Tom Cruise, Miles Teller", 13, 8.3,
                "16:30", "19:30", "22:00")
                .poster("https://image.tmdb.org/t/p/w500/62HCnUTziyWcpDaBO2i1DX17ljH.jpg"));

        movies.add(new Movie("m19", "Everything Everywhere All at Once", "🥢",
                "A laundromat owner discovers she can access the multiverse to save her family and the world.",
                "Comedy", "Daniels", "Michelle Yeoh, Ke Huy Quan", 11, 7.8,
                "17:00", "19:45", "22:30")
                .poster("https://image.tmdb.org/t/p/w500/w3LxiVYdWWRvEVdn5RYq6jIqkb1.jpg"));

        movies.add(new Movie("m20", "The Shawshank Redemption", "🔒",
                "Two imprisoned men bond over years and find solace and eventual redemption through kindness.",
                "Drama", "Frank Darabont", "Tim Robbins, Morgan Freeman", 11, 9.3,
                "18:00", "21:00")
                .poster("https://image.tmdb.org/t/p/w500/q6y0Go1tsGEsmtFryDOJo3dEmqu.jpg"));

        movies.add(new Movie("m21", "Pulp Fiction", "💼",
                "The lives of two mob hitmen, a boxer, and a gangster's wife intertwine in dark, comic ways.",
                "Crime", "Quentin Tarantino", "John Travolta, Uma Thurman", 12, 8.9,
                "19:00", "22:00")
                .poster("https://image.tmdb.org/t/p/w500/d5iIlFn5s0ImszYzBPb8JPIfbXD.jpg"));

        movies.add(new Movie("m22", "Parasite", "🏚",
                "A poor Korean family schemes to become employed by a wealthy family - with unexpected consequences.",
                "Thriller", "Bong Joon-ho", "Song Kang-ho, Lee Sun-kyun", 12, 8.5,
                "17:30", "20:15", "22:45")
                .poster("https://image.tmdb.org/t/p/w500/7IiTTgloJzvGI1TAYymCfbfl3vT.jpg"));

        movies.add(new Movie("m23", "Avengers: Endgame", "🛡",
                "The Avengers travel through time to undo Thanos's snap and restore the universe.",
                "Action", "Russo Brothers", "Robert Downey Jr., Chris Evans", 14, 8.4,
                "14:00", "17:30", "21:00")
                .poster("https://image.tmdb.org/t/p/w500/or06FN3Dka5tukK1e9sl16pB3iy.jpg"));

        movies.add(new Movie("m24", "Fight Club", "🥊",
                "An insomniac and a soap salesman create an underground fight club that evolves into something far darker.",
                "Drama", "David Fincher", "Brad Pitt, Edward Norton", 13, 8.8,
                "20:00", "22:30")
                .poster("https://image.tmdb.org/t/p/w500/pB8BM7pdSp6B6Ih7QZ4DrQ3PmJK.jpg"));
    }

    @Override
    public void start(Stage stage) {
        this.primaryStage = stage;
        seedData();
        stage.setTitle("VCinema");
        stage.setWidth(stageW);
        stage.setHeight(stageH);
        stage.setMinWidth(900);
        stage.setMinHeight(600);
        showLogin();
        stage.show();
    }

    // build the top navigation bar
    private HBox buildNav(String active) {
        HBox nav = new HBox(4);
        nav.setPadding(new Insets(0, 20, 0, 20));
        nav.setAlignment(Pos.CENTER_LEFT);
        nav.setStyle("-fx-background-color:#111111;-fx-border-color:#2a2a2a;-fx-border-width:0 0 1 0;");
        nav.setMinHeight(56);

        Label logo = new Label("🎬 VCinema");
        logo.setStyle("-fx-font-size:18px;-fx-font-weight:bold;-fx-text-fill:#cc0000;-fx-padding:0 28 0 4;-fx-cursor:hand;");
        logo.setOnMouseClicked(e -> showHome());

        Button btnHome = new Button("🏠 Home");
        Button btnReviews = new Button("⭐ Reviews");
        Button btnAbout = new Button("ℹ About");
        Button btnSettings = new Button("⚙ Settings");

        // style active vs normal buttons
        String normalStyle = "-fx-background-color:transparent;-fx-text-fill:#ffffff;-fx-font-size:13px;-fx-cursor:hand;-fx-padding:8 16;-fx-background-radius:6;";
        String activeStyle = "-fx-background-color:#800000;-fx-text-fill:white;-fx-font-size:13px;-fx-cursor:hand;-fx-padding:8 16;-fx-background-radius:6;-fx-font-weight:bold;";
        String hoverStyle = "-fx-background-color:#1a1a1a;-fx-text-fill:white;-fx-font-size:13px;-fx-cursor:hand;-fx-padding:8 16;-fx-background-radius:6;";

        for (Button b : new Button[]{btnHome, btnReviews, btnAbout, btnSettings}) {
            b.setStyle(normalStyle);
            b.setOnMouseEntered(e -> {
                if (!b.getStyle().contains("#800000")) {
                    b.setStyle(hoverStyle);
                }
            });
            b.setOnMouseExited(e -> {
                if (!b.getStyle().contains("#800000")) {
                    b.setStyle(normalStyle);
                }
            });
        }

        // set active button
        if (active.equals("home")) {
            btnHome.setStyle(activeStyle);
        } else if (active.equals("reviews")) {
            btnReviews.setStyle(activeStyle);
        } else if (active.equals("about")) {
            btnAbout.setStyle(activeStyle);
        } else if (active.equals("settings")) {
            btnSettings.setStyle(activeStyle);
        }

        btnHome.setOnAction(e -> showHome());
        btnReviews.setOnAction(e -> showReviews());
        btnAbout.setOnAction(e -> showAbout());
        btnSettings.setOnAction(e -> showSettings());

        Region sp = new Region();
        HBox.setHgrow(sp, Priority.ALWAYS);

        String roleIcon = isAdmin() ? "👑 " : isStaff() ? "🎫 " : "👤 ";
        Label userLbl = new Label(roleIcon + myName());
        userLbl.setStyle("-fx-text-fill:#aaaaaa;-fx-font-size:12px;-fx-padding:0 10 0 0;");

        Button btnLogout = new Button("🚪 Logout");
        btnLogout.setStyle("-fx-background-color:#1a0000;-fx-text-fill:#ff6b6b;-fx-font-size:13px;-fx-background-radius:8;-fx-cursor:hand;-fx-padding:6 14;-fx-border-color:#c0392b;-fx-border-radius:8;");
        btnLogout.setOnAction(e -> {
            sessionEmail = "";
            sessionRole = "";
            showLogin();
        });

        nav.getChildren().addAll(logo, btnHome, btnReviews, btnAbout, btnSettings);

        if (isStaff()) {
            Button btnAdmin = new Button("🛡 Admin");
            if (active.equals("admin")) {
                btnAdmin.setStyle(activeStyle);
            } else {
                btnAdmin.setStyle(normalStyle);
                btnAdmin.setOnMouseEntered(e -> btnAdmin.setStyle(hoverStyle));
                btnAdmin.setOnMouseExited(e -> btnAdmin.setStyle(normalStyle));
            }
            btnAdmin.setOnAction(e -> showAdmin());
            nav.getChildren().add(btnAdmin);
        }

        nav.getChildren().addAll(sp, userLbl, btnLogout);
        return nav;
    }

    // show a toast notification that disappears automatically
    private void showToast(String icon, String title, String message, String color) {
        Stage toast = new Stage();
        toast.initOwner(primaryStage);
        toast.initStyle(StageStyle.UNDECORATED);
        toast.initModality(Modality.NONE);

        HBox box = new HBox(14);
        box.setPadding(new Insets(18, 24, 18, 24));
        box.setAlignment(Pos.CENTER_LEFT);
        box.setStyle(
                "-fx-background-color:#1a1a1a;"
                + "-fx-background-radius:12;"
                + "-fx-border-color:" + color + ";"
                + "-fx-border-radius:12;"
                + "-fx-border-width:2;"
        );

        Label ico = new Label(icon);
        ico.setStyle("-fx-font-size:26px;");

        VBox text = new VBox(4);
        Label lblTitle = new Label(title);
        lblTitle.setStyle("-fx-font-size:14px;-fx-font-weight:bold;-fx-text-fill:" + color + ";");
        Label lblMsg = new Label(message);
        lblMsg.setStyle("-fx-font-size:12px;-fx-text-fill:#aaaaaa;");
        lblMsg.setWrapText(true);
        lblMsg.setMaxWidth(260);
        text.getChildren().addAll(lblTitle, lblMsg);

        box.getChildren().addAll(ico, text);

        Scene sc = new Scene(box);
        sc.setFill(Color.TRANSPARENT);
        toast.setScene(sc);

        // position it in the top right corner of the main window
        toast.setX(primaryStage.getX() + primaryStage.getWidth() - 380);
        toast.setY(primaryStage.getY() + 70);
        toast.show();

        // fade out after 2.5 seconds then close
        FadeTransition fade = new FadeTransition(Duration.millis(600), box);
        fade.setDelay(Duration.millis(2500));
        fade.setFromValue(1.0);
        fade.setToValue(0.0);
        fade.setOnFinished(e -> toast.close());
        fade.play();
    }

    private void showAlert(String title, String message) {
        showToast("✅", title, message, "#27ae60");
    }

    private void showError(String title, String message) {
        showToast("❌", title, message, "#cc0000");
    }

    // login screen
    private void showLogin() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color:#0a0a0a;");

        // top logo area
        HBox top = new HBox();
        top.setAlignment(Pos.CENTER);
        top.setPadding(new Insets(20));
        top.setStyle("-fx-background-color:#111111;-fx-border-color:#2a2a2a;-fx-border-width:0 0 1 0;");
        Label logo = new Label("🎬  VCinema");
        logo.setStyle("-fx-font-size:30px;-fx-font-weight:bold;-fx-text-fill:#cc0000;");
        Label tagline = new Label("  Premium Cinema Experience");
        tagline.setStyle("-fx-font-size:14px;-fx-text-fill:#555555;");
        top.getChildren().addAll(logo, tagline);
        root.setTop(top);

        // login card
        VBox card = new VBox(12);
        card.setPadding(new Insets(36, 50, 36, 50));
        card.setMaxWidth(420);
        card.setAlignment(Pos.CENTER);
        card.setStyle("-fx-background-color:#1a1a1a;-fx-background-radius:14;-fx-border-color:#2a2a2a;-fx-border-radius:14;-fx-border-width:1;");

        Label title = new Label("Sign In");
        title.setStyle("-fx-font-size:22px;-fx-font-weight:bold;-fx-text-fill:#ffffff;");
        Label subtitle = new Label("Welcome back to VCinema");
        subtitle.setStyle("-fx-font-size:13px;-fx-text-fill:#aaaaaa;");

        Separator sep1 = new Separator();
        sep1.setStyle("-fx-background-color:#2a2a2a;");

        Label lEmail = new Label("Email Address");
        lEmail.setStyle("-fx-text-fill:#aaaaaa;-fx-font-size:12px;");
        TextField tfEmail = new TextField();
        tfEmail.setPromptText("you@example.com");
        tfEmail.setStyle("-fx-background-color:#0d0d0d;-fx-text-fill:#ffffff;-fx-prompt-text-fill:#666666;-fx-border-color:#2a2a2a;-fx-border-radius:8;-fx-background-radius:8;-fx-padding:10 14;-fx-font-size:13px;-fx-pref-width:300px;");

        Label lPass = new Label("Password");
        lPass.setStyle("-fx-text-fill:#aaaaaa;-fx-font-size:12px;");
        PasswordField pfPass = new PasswordField();
        pfPass.setPromptText("Your password");
        pfPass.setStyle("-fx-background-color:#0d0d0d;-fx-text-fill:#ffffff;-fx-prompt-text-fill:#666666;-fx-border-color:#2a2a2a;-fx-border-radius:8;-fx-background-radius:8;-fx-padding:10 14;-fx-font-size:13px;-fx-pref-width:300px;");

        Label errLbl = new Label("");
        errLbl.setStyle("-fx-text-fill:#cc0000;-fx-font-size:12px;");

        Button btnLogin = new Button("Sign In");
        btnLogin.setStyle("-fx-background-color:#800000;-fx-text-fill:white;-fx-font-size:13px;-fx-font-weight:bold;-fx-background-radius:8;-fx-cursor:hand;-fx-padding:10 24;");
        btnLogin.setPrefWidth(180);
        btnLogin.setOnMouseEntered(e -> btnLogin.setStyle("-fx-background-color:#cc0000;-fx-text-fill:white;-fx-font-size:13px;-fx-font-weight:bold;-fx-background-radius:8;-fx-cursor:hand;-fx-padding:10 24;"));
        btnLogin.setOnMouseExited(e -> btnLogin.setStyle("-fx-background-color:#800000;-fx-text-fill:white;-fx-font-size:13px;-fx-font-weight:bold;-fx-background-radius:8;-fx-cursor:hand;-fx-padding:10 24;"));

        Separator sep2 = new Separator();
        sep2.setStyle("-fx-background-color:#2a2a2a;");

        Label noAcc = new Label("Don't have an account?");
        noAcc.setStyle("-fx-text-fill:#aaaaaa;-fx-font-size:13px;");
        Button btnGo = new Button("Sign Up");
        btnGo.setStyle("-fx-background-color:transparent;-fx-text-fill:#cc0000;-fx-font-size:13px;-fx-cursor:hand;-fx-underline:true;-fx-padding:0;");
        btnGo.setOnAction(e -> showSignUp());
        HBox signupRow = new HBox(6, noAcc, btnGo);
        signupRow.setAlignment(Pos.CENTER);

        card.getChildren().addAll(title, subtitle, sep1, lEmail, tfEmail, lPass, pfPass, errLbl, btnLogin, sep2, signupRow);

        StackPane center = new StackPane(card);
        center.setStyle("-fx-background-color:#0a0a0a;");
        root.setCenter(center);

        Runnable doLogin = () -> {
            String em = tfEmail.getText().trim().toLowerCase();
            String pw = pfPass.getText();
            if (em.isEmpty() || pw.isEmpty()) {
                errLbl.setText("Please fill in all fields");
                return;
            }
            User found = null;
            for (User u : users) {
                if (u.email.equalsIgnoreCase(em) && u.password.equals(pw)) {
                    found = u;
                    break;
                }
            }
            if (found != null) {
                sessionEmail = found.email;
                sessionRole = found.role;
                showHome();
                if (found.mustChangePassword) {
                    showForceChangePassword();
                }
            } else {
                errLbl.setText("Invalid email or password");
            }
        };

        btnLogin.setOnAction(e -> doLogin.run());
        pfPass.setOnAction(e -> doLogin.run());

        primaryStage.setScene(new Scene(root, stageW, stageH));
    }

    // sign up screen
    private void showSignUp() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color:#0a0a0a;");

        HBox top = new HBox();
        top.setAlignment(Pos.CENTER);
        top.setPadding(new Insets(20));
        top.setStyle("-fx-background-color:#111111;-fx-border-color:#2a2a2a;-fx-border-width:0 0 1 0;");
        Label logo = new Label("🎬  VCinema");
        logo.setStyle("-fx-font-size:30px;-fx-font-weight:bold;-fx-text-fill:#cc0000;");
        top.getChildren().add(logo);
        root.setTop(top);

        VBox card = new VBox(12);
        card.setPadding(new Insets(30, 50, 30, 50));
        card.setMaxWidth(440);
        card.setAlignment(Pos.CENTER);
        card.setStyle("-fx-background-color:#1a1a1a;-fx-background-radius:14;-fx-border-color:#2a2a2a;-fx-border-radius:14;-fx-border-width:1;");

        Label title = new Label("Create Account");
        title.setStyle("-fx-font-size:22px;-fx-font-weight:bold;-fx-text-fill:#ffffff;");
        Label subtitle = new Label("Join VCinema - it's free!");
        subtitle.setStyle("-fx-font-size:13px;-fx-text-fill:#aaaaaa;");

        String fieldStyle = "-fx-background-color:#0d0d0d;-fx-text-fill:#ffffff;-fx-prompt-text-fill:#666666;-fx-border-color:#2a2a2a;-fx-border-radius:8;-fx-background-radius:8;-fx-padding:10 14;-fx-font-size:13px;-fx-pref-width:300px;";

        Label lName = new Label("Full Name");
        lName.setStyle("-fx-text-fill:#aaaaaa;-fx-font-size:12px;");
        TextField tfName = new TextField();
        tfName.setPromptText("e.g. John Doe");
        tfName.setStyle(fieldStyle);

        Label lEmail = new Label("Email Address");
        lEmail.setStyle("-fx-text-fill:#aaaaaa;-fx-font-size:12px;");
        TextField tfEmail = new TextField();
        tfEmail.setPromptText("you@example.com");
        tfEmail.setStyle(fieldStyle);

        Label lPass = new Label("Password");
        lPass.setStyle("-fx-text-fill:#aaaaaa;-fx-font-size:12px;");
        PasswordField pfPass = new PasswordField();
        pfPass.setPromptText("At least 6 characters");
        pfPass.setStyle(fieldStyle);

        // password strength bar
        ProgressBar strengthBar = new ProgressBar(0);
        strengthBar.setPrefWidth(300);
        strengthBar.setStyle("-fx-accent:#800000;");
        Label strengthLabel = new Label("");
        strengthLabel.setStyle("-fx-font-size:11px;-fx-text-fill:#aaaaaa;");

        pfPass.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal.isEmpty()) {
                strengthBar.setProgress(0);
                strengthLabel.setText("");
                return;
            }
            double strength = 0.2;
            String color = "#800000";
            if (newVal.length() >= 8) {
                strength = 0.5;
                color = "#e67e22";
            }
            if (newVal.length() >= 10 && newVal.matches(".*[A-Z].*")) {
                strength = 0.75;
                color = "#f5c518";
            }
            if (newVal.length() >= 12 && newVal.matches(".*[A-Z].*") && newVal.matches(".*[0-9].*")) {
                strength = 1.0;
                color = "#27ae60";
            }
            strengthBar.setProgress(strength);
            strengthBar.setStyle("-fx-accent:" + color + ";");
            String[] levels = {"", "Weak", "Fair", "Good", "Strong"};
            strengthLabel.setText("Strength: " + levels[Math.min((int) (strength * 4), 4)]);
        });

        Label lConf = new Label("Confirm Password");
        lConf.setStyle("-fx-text-fill:#aaaaaa;-fx-font-size:12px;");
        PasswordField pfConf = new PasswordField();
        pfConf.setPromptText("Repeat password");
        pfConf.setStyle(fieldStyle);

        CheckBox cbTerms = new CheckBox("I agree to the Terms & Conditions");
        cbTerms.setStyle("-fx-text-fill:#aaaaaa;-fx-font-size:12px;");

        Label errLbl = new Label("");
        errLbl.setStyle("-fx-text-fill:#cc0000;-fx-font-size:12px;");
        errLbl.setWrapText(true);

        Button btnUp = new Button("Create Account");
        btnUp.setStyle("-fx-background-color:#800000;-fx-text-fill:white;-fx-font-size:13px;-fx-font-weight:bold;-fx-background-radius:8;-fx-cursor:hand;-fx-padding:10 24;");
        btnUp.setPrefWidth(200);
        btnUp.setOnMouseEntered(e -> btnUp.setStyle("-fx-background-color:#cc0000;-fx-text-fill:white;-fx-font-size:13px;-fx-font-weight:bold;-fx-background-radius:8;-fx-cursor:hand;-fx-padding:10 24;"));
        btnUp.setOnMouseExited(e -> btnUp.setStyle("-fx-background-color:#800000;-fx-text-fill:white;-fx-font-size:13px;-fx-font-weight:bold;-fx-background-radius:8;-fx-cursor:hand;-fx-padding:10 24;"));

        Label hasAcc = new Label("Already have an account?");
        hasAcc.setStyle("-fx-text-fill:#aaaaaa;-fx-font-size:13px;");
        Button btnBack = new Button("Log In");
        btnBack.setStyle("-fx-background-color:transparent;-fx-text-fill:#cc0000;-fx-font-size:13px;-fx-cursor:hand;-fx-underline:true;-fx-padding:0;");
        btnBack.setOnAction(e -> showLogin());
        HBox loginRow = new HBox(6, hasAcc, btnBack);
        loginRow.setAlignment(Pos.CENTER);

        Separator sep = new Separator();
        sep.setStyle("-fx-background-color:#2a2a2a;");

        card.getChildren().addAll(title, subtitle, sep, lName, tfName, lEmail, tfEmail,
                lPass, pfPass, strengthBar, strengthLabel, lConf, pfConf, cbTerms, errLbl, btnUp, loginRow);

        ScrollPane scrollPane = new ScrollPane(new StackPane(card));
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color:#0a0a0a;-fx-background:transparent;");
        root.setCenter(scrollPane);

        btnUp.setOnAction(e -> {
            String name = tfName.getText().trim();
            String email = tfEmail.getText().trim().toLowerCase();
            String pass = pfPass.getText();
            String conf = pfConf.getText();

            if (name.isEmpty() || email.isEmpty() || pass.isEmpty() || conf.isEmpty()) {
                errLbl.setText("Please fill in all fields");
                return;
            }
            if (name.length() < 2) {
                errLbl.setText("Name is too short");
                return;
            }
            if (!isValidEmail(email)) {
                errLbl.setText("Invalid email address");
                return;
            }

            boolean emailExists = false;
            for (User u : users) {
                if (u.email.equalsIgnoreCase(email)) {
                    emailExists = true;
                    break;
                }
            }
            if (emailExists) {
                errLbl.setText("Email already registered");
                return;
            }
            if (pass.length() < 6) {
                errLbl.setText("Password must be at least 6 characters");
                return;
            }
            if (!pass.equals(conf)) {
                errLbl.setText("Passwords don't match");
                return;
            }
            if (!cbTerms.isSelected()) {
                errLbl.setText("Please accept the Terms & Conditions");
                return;
            }

            users.add(new User(name, email, pass, "customer"));
            sessionEmail = email;
            sessionRole = "customer";
            showHome();
            showAlert("Welcome!", "Account created successfully. Welcome to VCinema, " + name + "!");
        });

        primaryStage.setScene(new Scene(root, stageW, stageH));
    }

    // home screen with movie grid
    private void showHome() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color:#0a0a0a;");

        VBox hero = new VBox(10);
        hero.setAlignment(Pos.CENTER);
        hero.setPadding(new Insets(36, 40, 24, 40));
        hero.setStyle("-fx-background-color:linear-gradient(to bottom, #1a0000, #0a0a0a);");
        Label heroTitle = new Label("Welcome to VCinema");
        heroTitle.setStyle("-fx-font-size:34px;-fx-font-weight:bold;-fx-text-fill:#ffffff;");
        Label heroSub = new Label("Book your next cinematic experience");
        heroSub.setStyle("-fx-font-size:14px;-fx-text-fill:#aaaaaa;");
        hero.getChildren().addAll(heroTitle, heroSub);

        // filter bar
        HBox filters = new HBox(12);
        filters.setPadding(new Insets(12, 28, 12, 28));
        filters.setAlignment(Pos.CENTER_LEFT);
        filters.setStyle("-fx-background-color:#111111;-fx-border-color:#2a2a2a;-fx-border-width:0 0 1 0;");

        TextField searchBox = new TextField();
        searchBox.setPromptText("Search movies...");
        searchBox.setStyle("-fx-background-color:#0d0d0d;-fx-text-fill:#ffffff;-fx-prompt-text-fill:#666666;-fx-border-color:#2a2a2a;-fx-border-radius:8;-fx-background-radius:8;-fx-padding:10 14;-fx-font-size:13px;-fx-pref-width:220px;");

        // get unique genres for combo box
        List<String> genreList = new ArrayList<>();
        genreList.add("All Genres");
        for (Movie m : movies) {
            if (!genreList.contains(m.genre)) {
                genreList.add(m.genre);
            }
        }
        Collections.sort(genreList.subList(1, genreList.size()));

        ComboBox<String> genreCb = new ComboBox<>(FXCollections.observableArrayList(genreList));
        genreCb.setValue("All Genres");
        genreCb.setStyle("-fx-background-color:#ffffff;-fx-background-radius:8;");

        ComboBox<String> sortCb = new ComboBox<>(FXCollections.observableArrayList("Default", "Rating high to low", "Price low to high", "Price high to low"));
        sortCb.setValue("Default");
        sortCb.setStyle("-fx-background-color:#ffffff;-fx-background-radius:8;");

        Label countLabel = new Label(movies.size() + " movies");
        countLabel.setStyle("-fx-text-fill:#555555;-fx-font-size:12px;");
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label showingLabel = new Label("Showing:");
        showingLabel.setStyle("-fx-text-fill:#aaaaaa;-fx-font-size:13px;");
        filters.getChildren().addAll(showingLabel, searchBox, genreCb, sortCb, spacer, countLabel);

        // movie grid
        FlowPane grid = new FlowPane();
        grid.setHgap(18);
        grid.setVgap(18);
        grid.setPadding(new Insets(24, 28, 28, 28));
        grid.setStyle("-fx-background-color:#0a0a0a;");

        Runnable renderMovies = () -> {
            grid.getChildren().clear();
            String search = searchBox.getText().toLowerCase().trim();
            String genre = genreCb.getValue();
            String sort = sortCb.getValue();

            List<Movie> list = new ArrayList<>();
            for (Movie m : movies) {
                boolean matchSearch = search.isEmpty() || m.title.toLowerCase().contains(search) || m.genre.toLowerCase().contains(search);
                boolean matchGenre = "All Genres".equals(genre) || m.genre.equals(genre);
                if (matchSearch && matchGenre) {
                    list.add(m);
                }
            }

            if ("Rating high to low".equals(sort)) {
                list.sort((a, b) -> Double.compare(b.rating, a.rating));
            } else if ("Price low to high".equals(sort)) {
                list.sort(Comparator.comparingInt(m -> m.price));
            } else if ("Price high to low".equals(sort)) {
                list.sort((a, b) -> b.price - a.price);
            }

            countLabel.setText(list.size() + " movie" + (list.size() != 1 ? "s" : ""));

            if (list.isEmpty()) {
                Label noResult = new Label("No movies found.");
                noResult.setStyle("-fx-text-fill:#555555;-fx-font-size:16px;-fx-padding:40;");
                grid.getChildren().add(noResult);
            } else {
                for (Movie m : list) {
                    grid.getChildren().add(makeMovieCard(m));
                }
            }
        };

        renderMovies.run();
        searchBox.setOnKeyReleased(e -> renderMovies.run());
        genreCb.setOnAction(e -> renderMovies.run());
        sortCb.setOnAction(e -> renderMovies.run());

        VBox topSection = new VBox(hero, filters);
        root.setTop(new VBox(buildNav("home"), topSection));

        ScrollPane sp = new ScrollPane(grid);
        sp.setFitToWidth(true);
        sp.setStyle("-fx-background-color:#0a0a0a;-fx-background:transparent;");
        root.setCenter(sp);

        primaryStage.setScene(new Scene(root, stageW, stageH));
    }

    // build the poster image for a movie card
    private StackPane makePoster(Movie m, double w, double h, double emojiSize) {
        StackPane poster = new StackPane();
        poster.setPrefSize(w, h);
        poster.setStyle("-fx-background-color:#0d0d0d;-fx-background-radius:12 12 0 0;");

        if (!m.posterPath.isEmpty()) {
            try {
                Image img = new Image("file:///" + m.posterPath.replace("\\", "/"), w, h, false, true, true);
                ImageView iv = new ImageView(img);
                iv.setFitWidth(w);
                iv.setFitHeight(h);
                iv.setPreserveRatio(false);
                poster.getChildren().add(iv);
                return poster;
            } catch (Exception ex) {
            }
        }

        if (!m.posterUrl.isEmpty()) {
            try {
                Image img = new Image(m.posterUrl, w, h, false, true, true);
                ImageView iv = new ImageView(img);
                iv.setFitWidth(w);
                iv.setFitHeight(h);
                iv.setPreserveRatio(false);

                Label loading = new Label(m.emoji);
                loading.setStyle("-fx-font-size:" + emojiSize + "px;");
                img.progressProperty().addListener((obs, ov, nv) -> {
                    if (nv.doubleValue() >= 1.0) {
                        poster.getChildren().remove(loading);
                    }
                });
                img.errorProperty().addListener((obs, ov, nv) -> {
                    if (nv) {
                        poster.getChildren().remove(iv);
                    }
                });
                poster.getChildren().addAll(loading, iv);
                return poster;
            } catch (Exception ex) {
            }
        }

        Label emo = new Label(m.emoji);
        emo.setStyle("-fx-font-size:" + emojiSize + "px;");
        poster.getChildren().add(emo);
        return poster;
    }

    // build a single movie card for the grid
    private VBox makeMovieCard(Movie m) {
        VBox card = new VBox(10);
        card.setAlignment(Pos.TOP_CENTER);
        card.setPrefWidth(220);
        card.setPadding(new Insets(0, 0, 16, 0));
        card.setStyle("-fx-background-color:#1a1a1a;-fx-background-radius:12;-fx-border-color:#2a2a2a;-fx-border-radius:12;-fx-cursor:hand;");

        StackPane poster = makePoster(m, 220, 300, 64);

        // genre badge top left
        Label badge = new Label(m.genre);
        badge.setStyle("-fx-background-color:#800000;-fx-text-fill:white;-fx-font-size:10px;-fx-padding:3 8;-fx-background-radius:4;");
        StackPane.setAlignment(badge, Pos.TOP_LEFT);
        StackPane.setMargin(badge, new Insets(8, 0, 0, 8));

        // rating badge top right
        Label rBadge = new Label("\u2605 " + m.rating);
        rBadge.setStyle("-fx-background-color:#0a0a0a;-fx-text-fill:#f5c518;-fx-font-size:10px;-fx-padding:3 8;-fx-background-radius:4;-fx-font-weight:bold;");
        StackPane.setAlignment(rBadge, Pos.TOP_RIGHT);
        StackPane.setMargin(rBadge, new Insets(8, 8, 0, 0));

        // price badge bottom right on the poster
        Label priceBadge = new Label("$" + m.price);
        priceBadge.setStyle("-fx-background-color:#0a0a0a;-fx-text-fill:#27ae60;-fx-font-size:11px;-fx-padding:3 8;-fx-background-radius:4;-fx-font-weight:bold;");
        StackPane.setAlignment(priceBadge, Pos.BOTTOM_RIGHT);
        StackPane.setMargin(priceBadge, new Insets(0, 8, 8, 0));

        poster.getChildren().addAll(badge, rBadge, priceBadge);

        Label titleLabel = new Label(m.title);
        titleLabel.setStyle("-fx-text-fill:#ffffff;-fx-font-size:13px;-fx-font-weight:bold;");
        titleLabel.setWrapText(true);
        titleLabel.setTextAlignment(TextAlignment.CENTER);
        titleLabel.setMaxWidth(196);
        titleLabel.setPadding(new Insets(0, 12, 0, 12));

        Button bookBtn = new Button("Book Now");
        bookBtn.setStyle("-fx-background-color:#800000;-fx-text-fill:white;-fx-font-size:11px;-fx-background-radius:6;-fx-cursor:hand;-fx-padding:7 22;-fx-font-weight:bold;");
        bookBtn.setOnMouseEntered(e -> bookBtn.setStyle("-fx-background-color:#cc0000;-fx-text-fill:white;-fx-font-size:11px;-fx-background-radius:6;-fx-cursor:hand;-fx-padding:7 22;-fx-font-weight:bold;"));
        bookBtn.setOnMouseExited(e -> bookBtn.setStyle("-fx-background-color:#800000;-fx-text-fill:white;-fx-font-size:11px;-fx-background-radius:6;-fx-cursor:hand;-fx-padding:7 22;-fx-font-weight:bold;"));

        card.getChildren().addAll(poster, titleLabel, bookBtn);

        card.setOnMouseEntered(e -> card.setStyle("-fx-background-color:#20202e;-fx-background-radius:12;-fx-border-color:#800000;-fx-border-radius:12;-fx-cursor:hand;-fx-effect:dropshadow(gaussian,rgba(192,57,43,0.4),16,0,0,4);"));
        card.setOnMouseExited(e -> card.setStyle("-fx-background-color:#1a1a1a;-fx-background-radius:12;-fx-border-color:#2a2a2a;-fx-border-radius:12;-fx-cursor:hand;"));

        card.setOnMouseClicked(e -> showMovieDetail(m));
        bookBtn.setOnAction(e -> {
            e.consume();
            showMovieDetail(m);
        });

        return card;
    }

    // movie detail popup
    private void showMovieDetail(Movie m) {
        Stage dlg = new Stage();
        dlg.initOwner(primaryStage);
        dlg.initModality(Modality.APPLICATION_MODAL);
        dlg.initStyle(StageStyle.UNDECORATED);
        dlg.setResizable(false);

        VBox root = new VBox(0);
        root.setStyle("-fx-background-color:#1a1a1a;-fx-background-radius:16;-fx-border-color:#2a2a2a;-fx-border-radius:16;-fx-border-width:1;");
        root.setMaxWidth(520);

        // header with poster
        StackPane header = new StackPane();
        header.setPrefHeight(220);

        StackPane posterHeader = makePoster(m, 520, 220, 80);

        Region overlay = new Region();
        overlay.setStyle("-fx-background-color:linear-gradient(to bottom, rgba(0,0,0,0.15), rgba(0,0,0,0.55));-fx-background-radius:16 16 0 0;");

        Button closeBtn = new Button("X");
        closeBtn.setStyle("-fx-background-color:rgba(42,0,0,0.85);-fx-text-fill:#ff6b6b;-fx-background-radius:20;-fx-cursor:hand;-fx-font-size:12px;-fx-padding:4 10;");
        closeBtn.setOnAction(e -> dlg.close());
        StackPane.setAlignment(closeBtn, Pos.TOP_RIGHT);
        StackPane.setMargin(closeBtn, new Insets(12));

        Label genreLbl = new Label(m.genre);
        genreLbl.setStyle("-fx-background-color:#800000;-fx-text-fill:white;-fx-font-size:11px;-fx-padding:4 10;-fx-background-radius:4;");
        StackPane.setAlignment(genreLbl, Pos.TOP_LEFT);
        StackPane.setMargin(genreLbl, new Insets(12));

        Label posterTitle = new Label(m.title);
        posterTitle.setStyle("-fx-font-size:18px;-fx-font-weight:bold;-fx-text-fill:white;-fx-effect:dropshadow(gaussian,black,6,0.8,0,1);");
        StackPane.setAlignment(posterTitle, Pos.BOTTOM_LEFT);
        StackPane.setMargin(posterTitle, new Insets(0, 0, 12, 14));

        header.getChildren().addAll(posterHeader, overlay, closeBtn, genreLbl, posterTitle);

        VBox body = new VBox(12);
        body.setPadding(new Insets(16, 28, 24, 28));

        HBox meta = new HBox(16);
        meta.setAlignment(Pos.CENTER_LEFT);
        Label ratLabel = new Label("★ " + m.rating);
        ratLabel.setStyle("-fx-text-fill:#f5c518;-fx-font-weight:bold;");
        Label priceInfo = new Label("$" + m.price + "/ticket");
        priceInfo.setStyle("-fx-text-fill:#27ae60;");
        Label dirLabel = new Label("Director: " + m.director);
        dirLabel.setStyle("-fx-text-fill:#aaaaaa;-fx-font-size:12px;");
        meta.getChildren().addAll(ratLabel, priceInfo, dirLabel);

        Label castLabel = new Label("Cast: " + m.cast);
        castLabel.setStyle("-fx-text-fill:#aaaaaa;-fx-font-size:12px;");
        castLabel.setWrapText(true);

        Separator s1 = new Separator();
        s1.setStyle("-fx-background-color:#2a2a2a;");

        Label descLabel = new Label(m.description);
        descLabel.setStyle("-fx-text-fill:#ccccdd;-fx-font-size:13px;");
        descLabel.setWrapText(true);

        Separator s2 = new Separator();
        s2.setStyle("-fx-background-color:#2a2a2a;");

        body.getChildren().addAll(meta, castLabel, s1, descLabel, s2);

        // date and time selection
        LocalDate today = LocalDate.now();
        Label sLabel = new Label("Select Date & Time");
        sLabel.setStyle("-fx-font-size:14px;-fx-font-weight:bold;-fx-text-fill:#ffffff;");

        HBox dateRow = new HBox(10);
        dateRow.setAlignment(Pos.CENTER_LEFT);
        ToggleGroup dateGroup = new ToggleGroup();
        String[] selectedDate = {today.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))};

        for (int i = 0; i < 3; i++) {
            LocalDate d = today.plusDays(i);
            ToggleButton tb = new ToggleButton();
            tb.setToggleGroup(dateGroup);
            String dayName = d.getDayOfWeek().name().substring(0, 3);
            tb.setText(dayName + "\n" + d.getDayOfMonth());
            tb.setStyle("-fx-background-color:#1a1a1a;-fx-text-fill:#aaaaaa;-fx-background-radius:8;-fx-cursor:hand;-fx-pref-width:60;-fx-font-size:12px;");
            String ds = d.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            tb.setOnAction(e -> selectedDate[0] = ds);
            if (i == 0) {
                tb.setSelected(true);
                tb.setStyle("-fx-background-color:#800000;-fx-text-fill:white;-fx-background-radius:8;-fx-cursor:hand;-fx-pref-width:60;-fx-font-size:12px;-fx-font-weight:bold;");
            }
            tb.selectedProperty().addListener((obs, ov, nv) -> {
                if (nv) {
                    tb.setStyle("-fx-background-color:#800000;-fx-text-fill:white;-fx-background-radius:8;-fx-cursor:hand;-fx-pref-width:60;-fx-font-size:12px;-fx-font-weight:bold;");
                } else {
                    tb.setStyle("-fx-background-color:#1a1a1a;-fx-text-fill:#aaaaaa;-fx-background-radius:8;-fx-cursor:hand;-fx-pref-width:60;-fx-font-size:12px;");
                }
            });
            dateRow.getChildren().add(tb);
        }

        HBox timeRow = new HBox(10);
        timeRow.setAlignment(Pos.CENTER_LEFT);
        ToggleGroup timeGroup = new ToggleGroup();
        String[] selectedTime = {m.showTimes.get(0)};

        for (String t : m.showTimes) {
            ToggleButton tb = new ToggleButton(t);
            tb.setToggleGroup(timeGroup);
            tb.setStyle("-fx-background-color:#1a1a1a;-fx-text-fill:#aaaaaa;-fx-background-radius:8;-fx-cursor:hand;-fx-pref-width:72;-fx-font-size:12px;");
            tb.setOnAction(e -> selectedTime[0] = t);
            if (t.equals(m.showTimes.get(0))) {
                tb.setSelected(true);
                tb.setStyle("-fx-background-color:#2980b9;-fx-text-fill:white;-fx-background-radius:8;-fx-cursor:hand;-fx-pref-width:72;-fx-font-size:12px;-fx-font-weight:bold;");
            }
            tb.selectedProperty().addListener((obs, ov, nv) -> {
                if (nv) {
                    tb.setStyle("-fx-background-color:#2980b9;-fx-text-fill:white;-fx-background-radius:8;-fx-cursor:hand;-fx-pref-width:72;-fx-font-size:12px;-fx-font-weight:bold;");
                } else {
                    tb.setStyle("-fx-background-color:#1a1a1a;-fx-text-fill:#aaaaaa;-fx-background-radius:8;-fx-cursor:hand;-fx-pref-width:72;-fx-font-size:12px;");
                }
            });
            timeRow.getChildren().add(tb);
        }

        HBox ticketRow = new HBox(12);
        ticketRow.setAlignment(Pos.CENTER_LEFT);
        Label tLbl = new Label("Tickets:");
        tLbl.setStyle("-fx-text-fill:#aaaaaa;-fx-font-size:13px;");
        Spinner<Integer> spinner = new Spinner<>(1, 20, 1);
        spinner.setEditable(true);
        spinner.setPrefWidth(90);

        Label totalLbl = new Label("Total:  $" + m.price);
        totalLbl.setStyle("-fx-font-size:16px;-fx-font-weight:bold;-fx-text-fill:#f5c518;");
        spinner.valueProperty().addListener((obs, ov, nv) -> totalLbl.setText("Total:  $" + (nv * m.price)));

        Region spacer2 = new Region();
        HBox.setHgrow(spacer2, Priority.ALWAYS);
        ticketRow.getChildren().addAll(tLbl, spinner, spacer2, totalLbl);

        Button btnBook = new Button("Book Now");
        btnBook.setStyle("-fx-background-color:#800000;-fx-text-fill:white;-fx-font-size:14px;-fx-font-weight:bold;-fx-background-radius:8;-fx-cursor:hand;-fx-padding:10 24;-fx-pref-width:200;");
        btnBook.setOnMouseEntered(e -> btnBook.setStyle("-fx-background-color:#cc0000;-fx-text-fill:white;-fx-font-size:14px;-fx-font-weight:bold;-fx-background-radius:8;-fx-cursor:hand;-fx-padding:10 24;-fx-pref-width:200;"));
        btnBook.setOnMouseExited(e -> btnBook.setStyle("-fx-background-color:#800000;-fx-text-fill:white;-fx-font-size:14px;-fx-font-weight:bold;-fx-background-radius:8;-fx-cursor:hand;-fx-padding:10 24;-fx-pref-width:200;"));
        btnBook.setOnAction(e -> {
            if (timeGroup.getSelectedToggle() == null) {
                showError("Error", "Please choose a showtime first.");
                return;
            }
            dlg.close();
            showPayment(m, selectedDate[0], selectedTime[0], spinner.getValue());
        });

        body.getChildren().addAll(sLabel, dateRow, timeRow, ticketRow, btnBook);
        root.getChildren().addAll(header, body);

        Scene sc = new Scene(root, 520, 680);
        sc.setFill(Color.TRANSPARENT);
        dlg.setScene(sc);
        dlg.showAndWait();
    }

    // payment screen
    private void showPayment(Movie m, String date, String time, int tickets) {
        Stage dlg = new Stage();
        dlg.initOwner(primaryStage);
        dlg.initModality(Modality.APPLICATION_MODAL);
        dlg.setTitle("Payment - VCinema");
        dlg.setResizable(false);

        int total = tickets * m.price;

        VBox root = new VBox(0);
        root.setStyle("-fx-background-color:#1a1a1a;-fx-background-radius:14;");

        // header
        HBox hdr = new HBox(12);
        hdr.setPadding(new Insets(18, 24, 18, 24));
        hdr.setAlignment(Pos.CENTER_LEFT);
        hdr.setStyle("-fx-background-color:#111111;-fx-border-color:#2a2a2a;-fx-border-width:0 0 1 0;");
        Label hdrLbl = new Label("Checkout");
        hdrLbl.setStyle("-fx-font-size:16px;-fx-font-weight:bold;-fx-text-fill:#ffffff;");
        Region hsp = new Region();
        HBox.setHgrow(hsp, Priority.ALWAYS);
        Label hdrTotal = new Label("$" + total);
        hdrTotal.setStyle("-fx-font-size:20px;-fx-font-weight:bold;-fx-text-fill:#27ae60;");
        hdr.getChildren().addAll(hdrLbl, hsp, hdrTotal);

        // booking summary
        VBox summary = new VBox(8);
        summary.setPadding(new Insets(16, 24, 16, 24));
        summary.setStyle("-fx-background-color:#0d0d0d;-fx-border-color:#2a2a2a;-fx-border-width:0 0 1 0;");

        String[] keys = {"Movie", "Date", "Time", "Tickets", "Total"};
        String[] vals = {m.title, date, time, tickets + " x $" + m.price, "$" + total};
        for (int i = 0; i < keys.length; i++) {
            HBox row = new HBox();
            Label k = new Label(keys[i]);
            k.setStyle("-fx-text-fill:#aaaaaa;-fx-font-size:12px;-fx-pref-width:120;");
            Label v = new Label(vals[i]);
            v.setStyle("-fx-text-fill:#ffffff;-fx-font-size:12px;-fx-font-weight:bold;");
            row.getChildren().addAll(k, v);
            summary.getChildren().add(row);
        }

        // payment method toggle
        ToggleGroup payGroup = new ToggleGroup();
        ToggleButton tbCard = new ToggleButton("Card");
        ToggleButton tbCash = new ToggleButton("Cash");
        for (ToggleButton tb : new ToggleButton[]{tbCard, tbCash}) {
            tb.setToggleGroup(payGroup);
            tb.setStyle("-fx-background-color:#1a1a1a;-fx-text-fill:#aaaaaa;-fx-background-radius:8;-fx-cursor:hand;-fx-pref-width:110;-fx-padding:9 0;");
            tb.selectedProperty().addListener((obs, ov, nv) -> {
                if (nv) {
                    tb.setStyle("-fx-background-color:#2980b9;-fx-text-fill:white;-fx-background-radius:8;-fx-cursor:hand;-fx-pref-width:110;-fx-padding:9 0;-fx-font-weight:bold;");
                } else {
                    tb.setStyle("-fx-background-color:#1a1a1a;-fx-text-fill:#aaaaaa;-fx-background-radius:8;-fx-cursor:hand;-fx-pref-width:110;-fx-padding:9 0;");
                }
            });
        }
        tbCard.setSelected(true);
        HBox tabs = new HBox(10, tbCard, tbCash);
        tabs.setAlignment(Pos.CENTER);

        // card form
        String fs = "-fx-background-color:#0d0d0d;-fx-text-fill:#ffffff;-fx-prompt-text-fill:#666666;-fx-border-color:#2a2a2a;-fx-border-radius:8;-fx-background-radius:8;-fx-padding:10 14;-fx-font-size:13px;-fx-pref-width:300px;";

        VBox cardForm = new VBox(10);
        cardForm.setPadding(new Insets(12, 0, 0, 0));

        Label lCard = new Label("Card Number");
        lCard.setStyle("-fx-text-fill:#aaaaaa;-fx-font-size:12px;");
        TextField tfCard = new TextField();
        tfCard.setPromptText("1234 5678 9012 3456");
        tfCard.setStyle(fs);

        Label lExp = new Label("Expiry");
        lExp.setStyle("-fx-text-fill:#aaaaaa;-fx-font-size:12px;");
        TextField tfExp = new TextField();
        tfExp.setPromptText("MM / YY");
        tfExp.setStyle(fs + "-fx-pref-width:140px;");

        Label lCvv = new Label("CVV");
        lCvv.setStyle("-fx-text-fill:#aaaaaa;-fx-font-size:12px;");
        PasswordField tfCvv = new PasswordField();
        tfCvv.setPromptText("123");
        tfCvv.setStyle(fs + "-fx-pref-width:120px;");

        HBox expCvvRow = new HBox(12, new VBox(4, lExp, tfExp), new VBox(4, lCvv, tfCvv));

        Label lHolder = new Label("Cardholder Name");
        lHolder.setStyle("-fx-text-fill:#aaaaaa;-fx-font-size:12px;");
        TextField tfHolder = new TextField();
        tfHolder.setPromptText("Full name on card");
        tfHolder.setStyle(fs);

        cardForm.getChildren().addAll(lCard, tfCard, expCvvRow, lHolder, tfHolder);

        // cash option info
        VBox cashInfo = new VBox(12);
        cashInfo.setVisible(false);
        cashInfo.setManaged(false);
        cashInfo.setPadding(new Insets(12, 0, 0, 0));

        VBox cashCard = new VBox(10);
        cashCard.setPadding(new Insets(16));
        cashCard.setAlignment(Pos.CENTER);
        cashCard.setStyle("-fx-background-color:#0a180a;-fx-background-radius:10;-fx-border-color:#27ae60;-fx-border-radius:10;-fx-border-width:1;");
        Label cashIcon = new Label("💵");
        cashIcon.setStyle("-fx-font-size:32px;");
        Label cashTitle = new Label("Pay at the Cinema Counter");
        cashTitle.setStyle("-fx-font-size:14px;-fx-font-weight:bold;-fx-text-fill:#27ae60;");
        Label cashSub = new Label("Show your booking at the box office and pay $" + total + " in cash.");
        cashSub.setStyle("-fx-text-fill:#aaaaaa;-fx-font-size:12px;");
        cashSub.setWrapText(true);
        cashCard.getChildren().addAll(cashIcon, cashTitle, cashSub);
        cashInfo.getChildren().add(cashCard);

        tbCard.setOnAction(e -> {
            cardForm.setVisible(true);
            cardForm.setManaged(true);
            cashInfo.setVisible(false);
            cashInfo.setManaged(false);
        });
        tbCash.setOnAction(e -> {
            cashInfo.setVisible(true);
            cashInfo.setManaged(true);
            cardForm.setVisible(false);
            cardForm.setManaged(false);
        });

        Label payErr = new Label("");
        payErr.setStyle("-fx-text-fill:#cc0000;-fx-font-size:12px;");
        payErr.setWrapText(true);

        Button btnPay = new Button("Confirm & Pay  $" + total);
        btnPay.setStyle("-fx-background-color:#800000;-fx-text-fill:white;-fx-font-size:14px;-fx-font-weight:bold;-fx-background-radius:8;-fx-cursor:hand;-fx-padding:10 24;-fx-pref-width:280;");
        btnPay.setOnMouseEntered(e -> btnPay.setStyle("-fx-background-color:#cc0000;-fx-text-fill:white;-fx-font-size:14px;-fx-font-weight:bold;-fx-background-radius:8;-fx-cursor:hand;-fx-padding:10 24;-fx-pref-width:280;"));
        btnPay.setOnMouseExited(e -> btnPay.setStyle("-fx-background-color:#800000;-fx-text-fill:white;-fx-font-size:14px;-fx-font-weight:bold;-fx-background-radius:8;-fx-cursor:hand;-fx-padding:10 24;-fx-pref-width:280;"));

        Button btnCancel = new Button("Cancel");
        btnCancel.setStyle("-fx-background-color:#1a1a1a;-fx-text-fill:#aaaaaa;-fx-font-size:13px;-fx-background-radius:8;-fx-cursor:hand;-fx-padding:10 24;-fx-border-color:#2a2a2a;-fx-border-radius:8;");
        btnCancel.setOnAction(e -> dlg.close());

        HBox btnRow = new HBox(12, btnCancel, btnPay);
        btnRow.setAlignment(Pos.CENTER);

        VBox body = new VBox(14);
        body.setPadding(new Insets(20, 24, 24, 24));
        body.getChildren().addAll(tabs, cardForm, cashInfo, payErr, btnRow);

        root.getChildren().addAll(hdr, summary, body);

        btnPay.setOnAction(e -> {
            String payMethod;
            if (tbCard.isSelected()) {
                String cn = tfCard.getText().replaceAll("\\s", "");
                String ex = tfExp.getText().trim();
                String cv = tfCvv.getText().trim();
                String hn = tfHolder.getText().trim();
                if (cn.isEmpty() || ex.isEmpty() || cv.isEmpty() || hn.isEmpty()) {
                    payErr.setText("Please fill in all card details");
                    return;
                }
                if (cn.length() < 12) {
                    payErr.setText("Invalid card number");
                    return;
                }
                if (!ex.matches("\\d{1,2}\\s*/\\s*\\d{2}")) {
                    payErr.setText("Invalid expiry format (use MM/YY)");
                    return;
                }
                payMethod = "Card";
            } else {
                payMethod = "Cash";
            }

            String bookId = "BK-" + (1000 + bookings.size());
            bookings.add(new Booking(sessionEmail, m.id, m.title, date, time, tickets, total, payMethod));
            dlg.close();
            showBookingConfirmation(m, date, time, tickets, total, payMethod, bookId);
        });

        dlg.setScene(new Scene(root, 480, 620));
        dlg.showAndWait();
    }

    // booking confirmation popup
    private void showBookingConfirmation(Movie m, String date, String time,
            int tickets, int total, String payMethod, String bookId) {
        Stage dlg = new Stage();
        dlg.initOwner(primaryStage);
        dlg.initModality(Modality.APPLICATION_MODAL);
        dlg.initStyle(StageStyle.UNDECORATED);

        VBox root = new VBox(16);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(36, 40, 36, 40));
        root.setMaxWidth(400);
        root.setStyle("-fx-background-color:#1a1a1a;-fx-background-radius:16;-fx-border-color:#27ae60;-fx-border-radius:16;-fx-border-width:2;");
        root.setEffect(new DropShadow(30, Color.web("#27ae60", 0.3)));

        Label icon = new Label("🎉");
        icon.setStyle("-fx-font-size:52px;");
        Label titleLabel = new Label("Booking Confirmed!");
        titleLabel.setStyle("-fx-font-size:20px;-fx-font-weight:bold;-fx-text-fill:#27ae60;");
        Label subLabel = new Label("Enjoy the show, " + myName() + "!");
        subLabel.setStyle("-fx-font-size:13px;-fx-text-fill:#aaaaaa;");

        VBox info = new VBox(8);
        info.setPadding(new Insets(14, 20, 14, 20));
        info.setStyle("-fx-background-color:#0a0a0a;-fx-background-radius:10;");

        String[] infoKeys = {"Movie", "Date", "Time", "Tickets", "Total", "Payment", "Ref"};
        String[] infoVals = {m.title, date, time, tickets + " tickets", "$" + total, payMethod, bookId};
        for (int i = 0; i < infoKeys.length; i++) {
            HBox row = new HBox();
            Label k = new Label(infoKeys[i]);
            k.setStyle("-fx-text-fill:#aaaaaa;-fx-font-size:12px;-fx-pref-width:120;");
            Label v = new Label(infoVals[i]);
            v.setStyle("-fx-text-fill:#ffffff;-fx-font-size:12px;-fx-font-weight:bold;");
            row.getChildren().addAll(k, v);
            info.getChildren().add(row);
        }

        Button btnDone = new Button("Done");
        btnDone.setStyle("-fx-background-color:#800000;-fx-text-fill:white;-fx-font-size:14px;-fx-font-weight:bold;-fx-background-radius:8;-fx-cursor:hand;-fx-padding:10 24;-fx-pref-width:160;");
        btnDone.setOnMouseEntered(e -> btnDone.setStyle("-fx-background-color:#cc0000;-fx-text-fill:white;-fx-font-size:14px;-fx-font-weight:bold;-fx-background-radius:8;-fx-cursor:hand;-fx-padding:10 24;-fx-pref-width:160;"));
        btnDone.setOnMouseExited(e -> btnDone.setStyle("-fx-background-color:#800000;-fx-text-fill:white;-fx-font-size:14px;-fx-font-weight:bold;-fx-background-radius:8;-fx-cursor:hand;-fx-padding:10 24;-fx-pref-width:160;"));
        btnDone.setOnAction(e -> dlg.close());

        Separator sep = new Separator();
        sep.setStyle("-fx-background-color:#2a2a2a;");
        root.getChildren().addAll(icon, titleLabel, subLabel, sep, info, btnDone);

        Scene sc = new Scene(root);
        sc.setFill(Color.TRANSPARENT);
        dlg.setScene(sc);
        dlg.showAndWait();
    }

    // reviews page
    private void showReviews() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color:#0a0a0a;");
        root.setTop(buildNav("reviews"));

        VBox page = new VBox(24);
        page.setPadding(new Insets(30, 80, 40, 80));
        page.setAlignment(Pos.TOP_CENTER);

        Label h = new Label("Reviews");
        h.setStyle("-fx-font-size:26px;-fx-font-weight:bold;-fx-text-fill:#ffffff;");
        Label sh = new Label("Share your experience with the VCinema community");
        sh.setStyle("-fx-font-size:13px;-fx-text-fill:#aaaaaa;");

        // write review card
        VBox writeCard = new VBox(12);
        writeCard.setPadding(new Insets(22, 28, 22, 28));
        writeCard.setMaxWidth(600);
        writeCard.setStyle("-fx-background-color:#1a1a1a;-fx-background-radius:14;-fx-border-color:#2a2a2a;-fx-border-radius:14;-fx-border-width:1;");

        Label wHead = new Label("Write a Review");
        wHead.setStyle("-fx-font-size:15px;-fx-font-weight:bold;-fx-text-fill:#ffffff;");

        HBox nameRow = new HBox(10);
        nameRow.setAlignment(Pos.CENTER_LEFT);
        Label nameLbl = new Label("Posting as:");
        nameLbl.setStyle("-fx-text-fill:#aaaaaa;-fx-font-size:12px;");
        Label nameVal = new Label("👤 " + myName());
        nameVal.setStyle("-fx-background-color:#0d0d0d;-fx-text-fill:#f5c518;-fx-padding:7 14;-fx-background-radius:8;-fx-font-weight:bold;-fx-font-size:13px;");
        nameRow.getChildren().addAll(nameLbl, nameVal);

        Separator sep = new Separator();
        sep.setStyle("-fx-background-color:#2a2a2a;");

        Label lFB = new Label("Your Review");
        lFB.setStyle("-fx-text-fill:#aaaaaa;-fx-font-size:12px;");
        TextArea ta = new TextArea();
        ta.setPromptText("Tell others about your experience...");
        ta.setStyle("-fx-background-color:#0d0d0d;-fx-text-fill:#000000;-fx-border-color:#2a2a2a;-fx-border-radius:8;-fx-background-radius:8;-fx-font-size:13px;-fx-pref-width:540px;");
        ta.setPrefRowCount(3);
        ta.setWrapText(true);

        Label errR = new Label("");
        errR.setStyle("-fx-text-fill:#cc0000;-fx-font-size:12px;");

        Button btnSubmit = new Button("Submit Review");
        btnSubmit.setStyle("-fx-background-color:#800000;-fx-text-fill:white;-fx-font-size:13px;-fx-font-weight:bold;-fx-background-radius:8;-fx-cursor:hand;-fx-padding:10 24;");
        btnSubmit.setOnMouseEntered(e -> btnSubmit.setStyle("-fx-background-color:#cc0000;-fx-text-fill:white;-fx-font-size:13px;-fx-font-weight:bold;-fx-background-radius:8;-fx-cursor:hand;-fx-padding:10 24;"));
        btnSubmit.setOnMouseExited(e -> btnSubmit.setStyle("-fx-background-color:#800000;-fx-text-fill:white;-fx-font-size:13px;-fx-font-weight:bold;-fx-background-radius:8;-fx-cursor:hand;-fx-padding:10 24;"));

        writeCard.getChildren().addAll(wHead, nameRow, sep, lFB, ta, errR, btnSubmit);

        // reviews list
        VBox revList = new VBox(12);
        revList.setMaxWidth(600);

        Label listHead = new Label("All Reviews");
        listHead.setStyle("-fx-font-size:17px;-fx-font-weight:bold;-fx-text-fill:#ffffff;");

        Runnable renderRevs = () -> {
            revList.getChildren().clear();
            revList.getChildren().add(listHead);
            if (reviews.isEmpty()) {
                Label empty = new Label("No reviews yet - be the first!");
                empty.setStyle("-fx-text-fill:#555555;-fx-font-size:13px;-fx-padding:20;");
                revList.getChildren().add(empty);
            } else {
                List<Review> sorted = new ArrayList<>(reviews);
                sorted.sort((a, b) -> Long.compare(b.timestamp, a.timestamp));
                for (Review r : sorted) {
                    VBox rc = new VBox(8);
                    rc.setPadding(new Insets(16));
                    rc.setStyle("-fx-background-color:#1a1a1a;-fx-background-radius:10;-fx-border-color:#800000;-fx-border-radius:10;-fx-border-width:0 0 0 3;");
                    Label rUser = new Label("👤 " + r.userName);
                    rUser.setStyle("-fx-text-fill:#cc0000;-fx-font-size:12px;-fx-font-weight:bold;");
                    Label rTxt = new Label(r.text);
                    rTxt.setStyle("-fx-text-fill:#c8c8dc;-fx-font-size:13px;");
                    rTxt.setWrapText(true);
                    rc.getChildren().addAll(rUser, rTxt);
                    revList.getChildren().add(rc);
                }
            }
        };
        renderRevs.run();

        btnSubmit.setOnAction(e -> {
            String fb = ta.getText().trim();
            if (fb.isEmpty()) {
                errR.setText("Please write your review first");
                return;
            }
            reviews.add(new Review(sessionEmail, myName(), "", fb));
            ta.clear();
            errR.setText("");
            renderRevs.run();
            showAlert("Review Submitted", "Thank you for sharing your experience!");
        });

        page.getChildren().addAll(h, sh, writeCard, revList);

        ScrollPane sp = new ScrollPane(page);
        sp.setFitToWidth(true);
        sp.setStyle("-fx-background-color:#0a0a0a;-fx-background:transparent;");
        root.setCenter(sp);

        primaryStage.setScene(new Scene(root, stageW, stageH));
    }

    // about page
    private void showAbout() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color:#0a0a0a;");
        root.setTop(buildNav("about"));

        VBox page = new VBox(28);
        page.setPadding(new Insets(40, 80, 40, 80));
        page.setAlignment(Pos.TOP_CENTER);

        Label h = new Label("About VCinema");
        h.setStyle("-fx-font-size:32px;-fx-font-weight:bold;-fx-text-fill:#ffffff;");
        Label sh = new Label("Your premium cinema booking experience");
        sh.setStyle("-fx-font-size:14px;-fx-text-fill:#aaaaaa;");

        Separator sep = new Separator();
        sep.setStyle("-fx-background-color:#2a2a2a;");

        // three info cards
        String[] icons = {"🎬", "🌍", "🤝"};
        String[] cardTitles = {"Our Mission", "Our Vision", "Our Team"};
        String[] cardTexts = {
            "Delivering exceptional cinema experiences - blockbusters, classics, and everything in between.",
            "Movies bring people together. We believe great stories can change the world.",
            "Our dedicated team works tirelessly to make every visit enjoyable from start to finish."
        };

        HBox infoCards = new HBox(18);
        infoCards.setAlignment(Pos.CENTER);
        for (int i = 0; i < 3; i++) {
            VBox c = new VBox(10);
            c.setPadding(new Insets(22));
            c.setPrefWidth(270);
            c.setStyle("-fx-background-color:#1a1a1a;-fx-background-radius:12;-fx-border-color:#2a2a2a;-fx-border-radius:12;");
            Label ic = new Label(icons[i]);
            ic.setStyle("-fx-font-size:26px;");
            Label ttl = new Label(cardTitles[i]);
            ttl.setStyle("-fx-font-size:14px;-fx-font-weight:bold;-fx-text-fill:#ffffff;");
            Label txt = new Label(cardTexts[i]);
            txt.setStyle("-fx-font-size:12px;-fx-text-fill:#aaaaaa;");
            txt.setWrapText(true);
            c.getChildren().addAll(ic, ttl, txt);
            infoCards.getChildren().add(c);
        }

        VBox contactCard = new VBox(12);
        contactCard.setPadding(new Insets(22, 28, 22, 28));
        contactCard.setMaxWidth(480);
        contactCard.setStyle("-fx-background-color:#1a1a1a;-fx-background-radius:14;-fx-border-color:#2a2a2a;-fx-border-radius:14;-fx-border-width:1;");

        Label contactHead = new Label("Contact Us");
        contactHead.setStyle("-fx-font-size:15px;-fx-font-weight:bold;-fx-text-fill:#ffffff;");
        Label email = new Label("ameermujeeb@gmail.com");
        email.setStyle("-fx-text-fill:#ffffff;-fx-font-size:14px;");
        Label phone = new Label("+962 777 898 911");
        phone.setStyle("-fx-text-fill:#ffffff;-fx-font-size:14px;");
        Label location = new Label("Amman, Jordan");
        location.setStyle("-fx-text-fill:#aaaaaa;-fx-font-size:13px;");
        contactCard.getChildren().addAll(contactHead, email, phone, location);

        page.getChildren().addAll(h, sh, sep, infoCards, contactCard);

        ScrollPane sp = new ScrollPane(page);
        sp.setFitToWidth(true);
        sp.setStyle("-fx-background-color:#0a0a0a;-fx-background:transparent;");
        root.setCenter(sp);

        primaryStage.setScene(new Scene(root, stageW, stageH));
    }

    // settings page
    private void showSettings() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color:#0a0a0a;");
        root.setTop(buildNav("settings"));

        VBox page = new VBox(24);
        page.setPadding(new Insets(30, 80, 40, 80));
        page.setAlignment(Pos.TOP_CENTER);

        Label h = new Label("Settings");
        h.setStyle("-fx-font-size:26px;-fx-font-weight:bold;-fx-text-fill:#ffffff;");

        User u = me();

        String fieldStyle = "-fx-background-color:#0d0d0d;-fx-text-fill:#ffffff;-fx-prompt-text-fill:#666666;-fx-border-color:#2a2a2a;-fx-border-radius:8;-fx-background-radius:8;-fx-padding:10 14;-fx-font-size:13px;-fx-pref-width:300px;";

        // profile card
        VBox profileCard = new VBox(12);
        profileCard.setPadding(new Insets(22, 28, 22, 28));
        profileCard.setMaxWidth(540);
        profileCard.setStyle("-fx-background-color:#1a1a1a;-fx-background-radius:14;-fx-border-color:#2a2a2a;-fx-border-radius:14;-fx-border-width:1;");

        Label pHead = new Label("My Profile");
        pHead.setStyle("-fx-font-size:15px;-fx-font-weight:bold;-fx-text-fill:#ffffff;");
        Separator s1 = new Separator();
        s1.setStyle("-fx-background-color:#2a2a2a;");

        Label lName = new Label("Display Name");
        lName.setStyle("-fx-text-fill:#aaaaaa;-fx-font-size:12px;");
        TextField tfName = new TextField(u.name);
        tfName.setStyle(fieldStyle);

        Label lPhone = new Label("Phone Number");
        lPhone.setStyle("-fx-text-fill:#aaaaaa;-fx-font-size:12px;");
        TextField tfPhone = new TextField(u.phone);
        tfPhone.setStyle(fieldStyle);

        Label emailDisp = new Label("Email: " + u.email);
        emailDisp.setStyle("-fx-text-fill:#555555;-fx-font-size:12px;-fx-padding:4 0;");

        Label profMsg = new Label("");
        profMsg.setStyle("-fx-font-size:12px;-fx-min-height:16;");

        Button btnSaveProf = new Button("Save Profile");
        btnSaveProf.setStyle("-fx-background-color:#800000;-fx-text-fill:white;-fx-font-size:13px;-fx-font-weight:bold;-fx-background-radius:8;-fx-cursor:hand;-fx-padding:10 24;");
        btnSaveProf.setOnMouseEntered(e -> btnSaveProf.setStyle("-fx-background-color:#cc0000;-fx-text-fill:white;-fx-font-size:13px;-fx-font-weight:bold;-fx-background-radius:8;-fx-cursor:hand;-fx-padding:10 24;"));
        btnSaveProf.setOnMouseExited(e -> btnSaveProf.setStyle("-fx-background-color:#800000;-fx-text-fill:white;-fx-font-size:13px;-fx-font-weight:bold;-fx-background-radius:8;-fx-cursor:hand;-fx-padding:10 24;"));
        btnSaveProf.setOnAction(e -> {
            String n = tfName.getText().trim();
            if (n.length() < 2) {
                profMsg.setStyle("-fx-text-fill:#cc0000;-fx-font-size:12px;");
                profMsg.setText("Name is too short");
                return;
            }
            u.name = n;
            u.phone = tfPhone.getText().trim();
            profMsg.setStyle("-fx-text-fill:#27ae60;-fx-font-size:12px;");
            profMsg.setText("Profile updated successfully");
            showAlert("Profile Updated", "Your display name has been saved.");
        });

        profileCard.getChildren().addAll(pHead, s1, lName, tfName, lPhone, tfPhone, emailDisp, profMsg, btnSaveProf);

        // change password card
        VBox passCard = new VBox(12);
        passCard.setPadding(new Insets(22, 28, 22, 28));
        passCard.setMaxWidth(540);
        passCard.setStyle("-fx-background-color:#1a1a1a;-fx-background-radius:14;-fx-border-color:#2a2a2a;-fx-border-radius:14;-fx-border-width:1;");

        Label passHead = new Label("Change Password");
        passHead.setStyle("-fx-font-size:15px;-fx-font-weight:bold;-fx-text-fill:#ffffff;");
        Separator s2 = new Separator();
        s2.setStyle("-fx-background-color:#2a2a2a;");

        Label lOld = new Label("Current Password");
        lOld.setStyle("-fx-text-fill:#aaaaaa;-fx-font-size:12px;");
        PasswordField pfOld = new PasswordField();
        pfOld.setStyle(fieldStyle);

        Label lNew = new Label("New Password");
        lNew.setStyle("-fx-text-fill:#aaaaaa;-fx-font-size:12px;");
        PasswordField pfNew = new PasswordField();
        pfNew.setStyle(fieldStyle);

        Label lConf = new Label("Confirm New Password");
        lConf.setStyle("-fx-text-fill:#aaaaaa;-fx-font-size:12px;");
        PasswordField pfConf = new PasswordField();
        pfConf.setStyle(fieldStyle);

        Label passMsg = new Label("");
        passMsg.setStyle("-fx-font-size:12px;-fx-min-height:16;");

        Button btnPass = new Button("Update Password");
        btnPass.setStyle("-fx-background-color:#800000;-fx-text-fill:white;-fx-font-size:13px;-fx-font-weight:bold;-fx-background-radius:8;-fx-cursor:hand;-fx-padding:10 24;");
        btnPass.setOnMouseEntered(e -> btnPass.setStyle("-fx-background-color:#cc0000;-fx-text-fill:white;-fx-font-size:13px;-fx-font-weight:bold;-fx-background-radius:8;-fx-cursor:hand;-fx-padding:10 24;"));
        btnPass.setOnMouseExited(e -> btnPass.setStyle("-fx-background-color:#800000;-fx-text-fill:white;-fx-font-size:13px;-fx-font-weight:bold;-fx-background-radius:8;-fx-cursor:hand;-fx-padding:10 24;"));
        btnPass.setOnAction(e -> {
            if (!pfOld.getText().equals(u.password)) {
                passMsg.setStyle("-fx-text-fill:#cc0000;-fx-font-size:12px;");
                passMsg.setText("Current password is incorrect");
                return;
            }
            if (pfNew.getText().length() < 6) {
                passMsg.setStyle("-fx-text-fill:#cc0000;-fx-font-size:12px;");
                passMsg.setText("Password must be at least 6 characters");
                return;
            }
            if (!pfNew.getText().equals(pfConf.getText())) {
                passMsg.setStyle("-fx-text-fill:#cc0000;-fx-font-size:12px;");
                passMsg.setText("Passwords don't match");
                return;
            }
            u.password = pfNew.getText();
            pfOld.clear();
            pfNew.clear();
            pfConf.clear();
            passMsg.setStyle("-fx-text-fill:#27ae60;-fx-font-size:12px;");
            passMsg.setText("Password changed successfully");
            showAlert("Password Updated", "Your password has been changed.");
        });

        passCard.getChildren().addAll(passHead, s2, lOld, pfOld, lNew, pfNew, lConf, pfConf, passMsg, btnPass);

        // booking history card
        VBox histCard = new VBox(12);
        histCard.setPadding(new Insets(22, 28, 22, 28));
        histCard.setMaxWidth(540);
        histCard.setStyle("-fx-background-color:#1a1a1a;-fx-background-radius:14;-fx-border-color:#2a2a2a;-fx-border-radius:14;-fx-border-width:1;");

        Label histHead = new Label("My Booking History");
        histHead.setStyle("-fx-font-size:15px;-fx-font-weight:bold;-fx-text-fill:#ffffff;");
        histCard.getChildren().add(histHead);

        List<Booking> myBookings = new ArrayList<>();
        for (Booking b : bookings) {
            if (b.userEmail.equals(sessionEmail)) {
                myBookings.add(b);
            }
        }

        if (myBookings.isEmpty()) {
            Label noBk = new Label("No bookings yet.");
            noBk.setStyle("-fx-text-fill:#555555;-fx-font-size:13px;");
            histCard.getChildren().add(noBk);
        } else {
            for (Booking b : myBookings) {
                VBox bInfo = new VBox(4);
                bInfo.setPadding(new Insets(10));
                bInfo.setStyle("-fx-background-color:#0d0d0d;-fx-background-radius:8;");

                Label bTitle = new Label(b.movieTitle);
                bTitle.setStyle("-fx-text-fill:#ffffff;-fx-font-size:13px;-fx-font-weight:bold;");
                Label bDate = new Label(b.date + "  " + b.time);
                bDate.setStyle("-fx-text-fill:#aaaaaa;-fx-font-size:11px;");
                Label bDetails = new Label(b.tickets + " tickets  |  $" + b.totalPrice + "  |  " + b.payMethod);
                bDetails.setStyle("-fx-text-fill:#f5c518;-fx-font-size:11px;");

                bInfo.getChildren().addAll(bTitle, bDate, bDetails);
                histCard.getChildren().add(bInfo);
            }
        }

        page.getChildren().addAll(h, profileCard, passCard, histCard);

        ScrollPane sp = new ScrollPane(page);
        sp.setFitToWidth(true);
        sp.setStyle("-fx-background-color:#0a0a0a;-fx-background:transparent;");
        root.setCenter(sp);

        primaryStage.setScene(new Scene(root, stageW, stageH));
    }

    // admin panel
    private void showAdmin() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color:#0a0a0a;");
        root.setTop(buildNav("admin"));

        HBox tabBar = new HBox(8);
        tabBar.setPadding(new Insets(14, 24, 14, 24));
        tabBar.setStyle("-fx-background-color:#111111;-fx-border-color:#2a2a2a;-fx-border-width:0 0 1 0;");

        ToggleGroup tg = new ToggleGroup();
        ToggleButton tbMovies = new ToggleButton("Movie Management");
        tbMovies.setToggleGroup(tg);
        tbMovies.setSelected(false);
        tbMovies.setStyle("-fx-background-color:#1a1a1a;-fx-text-fill:#aaaaaa;-fx-background-radius:8;-fx-cursor:hand;-fx-padding:8 20;");
        tbMovies.selectedProperty().addListener((obs, ov, nv) -> {
            if (nv) {
                tbMovies.setStyle("-fx-background-color:#800000;-fx-text-fill:white;-fx-background-radius:8;-fx-cursor:hand;-fx-padding:8 20;-fx-font-weight:bold;");
            } else {
                tbMovies.setStyle("-fx-background-color:#1a1a1a;-fx-text-fill:#aaaaaa;-fx-background-radius:8;-fx-cursor:hand;-fx-padding:8 20;");
            }
        });

        tabBar.getChildren().add(tbMovies);

        StackPane content = new StackPane();
        VBox moviesPane = buildMoviesPanel();
        content.getChildren().add(moviesPane);
        moviesPane.setVisible(false);
        moviesPane.setManaged(false);

        tbMovies.setOnAction(e -> {
            moviesPane.setVisible(true);
            moviesPane.setManaged(true);
        });

        VBox body = new VBox(tabBar, content);
        VBox.setVgrow(content, Priority.ALWAYS);
        root.setCenter(body);

        primaryStage.setScene(new Scene(root, stageW, stageH));
    }

    // admin movies management panel
    private VBox buildMoviesPanel() {
        HBox layout = new HBox(0);

        ObservableList<Movie> moviesObs = FXCollections.observableArrayList(movies);

        TableView<Movie> table = new TableView<>(moviesObs);
        table.setStyle("-fx-background-color:#111111;");
        table.setPrefWidth(440);

        TableColumn<Movie, String> cEmo = new TableColumn<>("");
        cEmo.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().emoji));
        cEmo.setPrefWidth(36);

        TableColumn<Movie, String> cTitle = new TableColumn<>("Title");
        cTitle.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().title));
        cTitle.setPrefWidth(190);

        TableColumn<Movie, String> cGenre = new TableColumn<>("Genre");
        cGenre.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().genre));
        cGenre.setPrefWidth(90);

        TableColumn<Movie, String> cPrice = new TableColumn<>("$");
        cPrice.setCellValueFactory(d -> new SimpleStringProperty("$" + d.getValue().price));
        cPrice.setPrefWidth(50);

        TableColumn<Movie, String> cRat = new TableColumn<>("Rating");
        cRat.setCellValueFactory(d -> new SimpleStringProperty("" + d.getValue().rating));
        cRat.setPrefWidth(50);

        table.getColumns().addAll(cEmo, cTitle, cGenre, cPrice, cRat);

        Label tHead = new Label("All Movies (" + movies.size() + ")");
        tHead.setStyle("-fx-font-size:15px;-fx-font-weight:bold;-fx-text-fill:#ffffff;-fx-padding:0 0 10 0;");

        VBox tableBox = new VBox(10, tHead, table);
        tableBox.setPadding(new Insets(28));
        tableBox.setStyle("-fx-background-color:#0d0d0d;-fx-border-color:#2a2a2a;-fx-border-width:0 1 0 0;");
        VBox.setVgrow(table, Priority.ALWAYS);

        VBox forms = new VBox(22);
        forms.setPadding(new Insets(28));
        forms.setStyle("-fx-background-color:#0a0a0a;");

        String fieldStyle = "-fx-background-color:#0d0d0d;-fx-text-fill:#ffffff;-fx-prompt-text-fill:#666666;-fx-border-color:#2a2a2a;-fx-border-radius:8;-fx-background-radius:8;-fx-padding:10 14;-fx-font-size:13px;-fx-pref-width:300px;";

        // add movie form
        VBox addCard = new VBox(12);
        addCard.setPadding(new Insets(18, 24, 18, 24));
        addCard.setStyle("-fx-background-color:#1a1a1a;-fx-background-radius:14;-fx-border-color:#2a2a2a;-fx-border-radius:14;-fx-border-width:1;");

        Label aHead = new Label("Add New Movie");
        aHead.setStyle("-fx-font-size:14px;-fx-font-weight:bold;-fx-text-fill:#ffffff;");
        Separator s1 = new Separator();
        s1.setStyle("-fx-background-color:#2a2a2a;");

        TextField tfTitle = new TextField();
        tfTitle.setPromptText("Movie title");
        tfTitle.setStyle(fieldStyle);
        TextField tfGenre = new TextField();
        tfGenre.setPromptText("Genre");
        tfGenre.setStyle(fieldStyle);
        TextField tfDir = new TextField();
        tfDir.setPromptText("Director");
        tfDir.setStyle(fieldStyle);
        TextField tfCast = new TextField();
        tfCast.setPromptText("Lead cast");
        tfCast.setStyle(fieldStyle);
        TextField tfDesc = new TextField();
        tfDesc.setPromptText("Short description");
        tfDesc.setStyle(fieldStyle);
        TextField tfPrice = new TextField();
        tfPrice.setPromptText("Price (e.g. 12)");
        tfPrice.setStyle(fieldStyle + "-fx-pref-width:120px;");
        TextField tfRating = new TextField();
        tfRating.setPromptText("Rating (e.g. 8.5)");
        tfRating.setStyle(fieldStyle + "-fx-pref-width:120px;");
        TextField tfTimes = new TextField();
        tfTimes.setPromptText("Show times: 16:00,19:30,22:00");
        tfTimes.setStyle(fieldStyle);

        // poster picker
        String[] chosenPosterPath = {""};
        ImageView previewIV = new ImageView();
        previewIV.setFitWidth(80);
        previewIV.setFitHeight(110);
        previewIV.setPreserveRatio(false);

        StackPane previewBox = new StackPane();
        previewBox.setPrefSize(80, 110);
        previewBox.setStyle("-fx-background-color:#0d0d0d;-fx-background-radius:6;-fx-border-color:#2a2a2a;-fx-border-radius:6;");
        Label previewPlaceholder = new Label("🖼");
        previewPlaceholder.setStyle("-fx-font-size:28px;");
        previewBox.getChildren().addAll(previewPlaceholder, previewIV);

        Button btnBrowse = new Button("Choose Poster Image");
        btnBrowse.setStyle("-fx-background-color:#1a1a1a;-fx-text-fill:#aaaaaa;-fx-font-size:13px;-fx-background-radius:8;-fx-cursor:hand;-fx-padding:10 24;-fx-border-color:#2a2a2a;-fx-border-radius:8;-fx-pref-width:200;");

        Label posterLbl = new Label("No image selected");
        posterLbl.setStyle("-fx-text-fill:#555555;-fx-font-size:11px;");

        btnBrowse.setOnAction(ev -> {
            FileChooser fc = new FileChooser();
            fc.setTitle("Choose Movie Poster");
            fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.jpg", "*.jpeg", "*.png", "*.gif", "*.webp", "*.bmp"));
            File chosen = fc.showOpenDialog(primaryStage);
            if (chosen != null) {
                chosenPosterPath[0] = chosen.getAbsolutePath();
                posterLbl.setText(chosen.getName());
                posterLbl.setStyle("-fx-text-fill:#27ae60;-fx-font-size:11px;");
                try {
                    Image img = new Image(chosen.toURI().toString(), 80, 110, false, true);
                    previewIV.setImage(img);
                    previewBox.getChildren().remove(previewPlaceholder);
                } catch (Exception ignored) {
                }
            }
        });

        HBox posterRow = new HBox(12, previewBox,
                new VBox(8,
                        new Label("Poster Image") {
                    {
                        setStyle("-fx-text-fill:#aaaaaa;-fx-font-size:12px;");
                    }
                },
                        btnBrowse, posterLbl,
                        new Label("JPG / PNG / WEBP") {
                    {
                        setStyle("-fx-text-fill:#555555;-fx-font-size:10px;");
                    }
                }));
        posterRow.setAlignment(Pos.CENTER_LEFT);

        Label addMsg = new Label("");
        addMsg.setStyle("-fx-font-size:12px;-fx-min-height:16;");
        addMsg.setWrapText(true);

        Button btnAdd = new Button("Add Movie");
        btnAdd.setStyle("-fx-background-color:#800000;-fx-text-fill:white;-fx-font-size:13px;-fx-font-weight:bold;-fx-background-radius:8;-fx-cursor:hand;-fx-padding:10 24;");
        btnAdd.setOnMouseEntered(e -> btnAdd.setStyle("-fx-background-color:#cc0000;-fx-text-fill:white;-fx-font-size:13px;-fx-font-weight:bold;-fx-background-radius:8;-fx-cursor:hand;-fx-padding:10 24;"));
        btnAdd.setOnMouseExited(e -> btnAdd.setStyle("-fx-background-color:#800000;-fx-text-fill:white;-fx-font-size:13px;-fx-font-weight:bold;-fx-background-radius:8;-fx-cursor:hand;-fx-padding:10 24;"));

        GridPane fieldGrid = new GridPane();
        fieldGrid.setHgap(10);
        fieldGrid.setVgap(8);

        String[][] gridRows = {
            {"Title", ""}, {"Genre", ""}, {"Director", ""}, {"Cast", ""},
            {"Description", ""}, {"Price ($)", ""}, {"Rating", ""}, {"Showtimes", ""}
        };
        TextField[] fields = {tfTitle, tfGenre, tfDir, tfCast, tfDesc, tfPrice, tfRating, tfTimes};
        for (int i = 0; i < fields.length; i++) {
            Label lbl = new Label(gridRows[i][0]);
            lbl.setStyle("-fx-text-fill:#aaaaaa;-fx-font-size:12px;");
            fieldGrid.addRow(i, lbl, fields[i]);
        }

        btnAdd.setOnAction(ev -> {
            String t = tfTitle.getText().trim();
            String g = tfGenre.getText().trim();
            String di = tfDir.getText().trim();
            String ca = tfCast.getText().trim();
            String ds = tfDesc.getText().trim();
            String pr = tfPrice.getText().trim();
            String ra = tfRating.getText().trim();
            String tm = tfTimes.getText().trim();

            if (t.isEmpty() || g.isEmpty() || di.isEmpty() || ca.isEmpty()
                    || ds.isEmpty() || pr.isEmpty() || ra.isEmpty() || tm.isEmpty()) {
                addMsg.setStyle("-fx-text-fill:#cc0000;-fx-font-size:12px;");
                addMsg.setText("Please fill in all fields");
                return;
            }

            int price;
            double rating;
            try {
                price = Integer.parseInt(pr);
            } catch (Exception e) {
                addMsg.setStyle("-fx-text-fill:#cc0000;-fx-font-size:12px;");
                addMsg.setText("Price must be a number");
                return;
            }
            try {
                rating = Double.parseDouble(ra);
            } catch (Exception e) {
                addMsg.setStyle("-fx-text-fill:#cc0000;-fx-font-size:12px;");
                addMsg.setText("Rating must be a number like 8.5");
                return;
            }

            String[] timesArr = tm.split(",");
            for (int i = 0; i < timesArr.length; i++) {
                timesArr[i] = timesArr[i].trim();
            }

            String id = "m" + (movies.size() + 1);
            Movie newM = new Movie(id, t, "🎬", ds, g, di, ca, price, rating, timesArr);
            newM.posterPath = chosenPosterPath[0];
            movies.add(newM);
            moviesObs.setAll(movies);
            tHead.setText("All Movies (" + movies.size() + ")");
            addMsg.setStyle("-fx-text-fill:#27ae60;-fx-font-size:12px;");
            addMsg.setText("Movie added: " + t);

            for (TextField f : fields) {
                f.clear();
            }
            chosenPosterPath[0] = "";
            previewIV.setImage(null);
            if (!previewBox.getChildren().contains(previewPlaceholder)) {
                previewBox.getChildren().add(0, previewPlaceholder);
            }
            posterLbl.setText("No image selected");
            posterLbl.setStyle("-fx-text-fill:#555555;-fx-font-size:11px;");
            showAlert("Movie Added", "\"" + t + "\" has been added to the catalog.");
        });

        addCard.getChildren().addAll(aHead, s1, posterRow, new Separator() {
            {
                setStyle("-fx-background-color:#2a2a2a;");
            }
        }, fieldGrid, addMsg, btnAdd);

        // delete movie card
        VBox delCard = new VBox(12);
        delCard.setPadding(new Insets(18, 24, 18, 24));
        delCard.setStyle("-fx-background-color:#1a1a1a;-fx-background-radius:14;-fx-border-color:#2a2a2a;-fx-border-radius:14;-fx-border-width:1;");

        Label dHead = new Label("Remove Movie");
        dHead.setStyle("-fx-font-size:14px;-fx-font-weight:bold;-fx-text-fill:#ff6b6b;");
        Separator s2 = new Separator();
        s2.setStyle("-fx-background-color:#2a2a2a;");

        List<String> movieTitles = new ArrayList<>();
        for (Movie mv : movies) {
            movieTitles.add(mv.title);
        }
        ComboBox<String> delCb = new ComboBox<>(FXCollections.observableArrayList(movieTitles));
        delCb.setStyle("-fx-background-color:#ffffff;-fx-background-radius:8;");
        delCb.setPrefWidth(280);

        Label delMsg = new Label("");
        delMsg.setStyle("-fx-font-size:12px;-fx-min-height:16;");

        Button btnDel = new Button("Remove Movie");
        btnDel.setStyle("-fx-background-color:#1a0000;-fx-text-fill:#ff6b6b;-fx-font-size:13px;-fx-background-radius:8;-fx-cursor:hand;-fx-padding:10 24;-fx-border-color:#c0392b;-fx-border-radius:8;");
        btnDel.setOnAction(ev -> {
            String sel = delCb.getValue();
            if (sel == null || sel.isEmpty()) {
                delMsg.setStyle("-fx-text-fill:#cc0000;-fx-font-size:12px;");
                delMsg.setText("Please select a movie");
                return;
            }
            Movie target = null;
            for (Movie mv : movies) {
                if (mv.title.equals(sel)) {
                    target = mv;
                    break;
                }
            }
            if (target == null) {
                delMsg.setStyle("-fx-text-fill:#cc0000;-fx-font-size:12px;");
                delMsg.setText("Movie not found");
                return;
            }
            movies.remove(target);
            moviesObs.setAll(movies);
            tHead.setText("All Movies (" + movies.size() + ")");
            List<String> updated = new ArrayList<>();
            for (Movie mv : movies) {
                updated.add(mv.title);
            }
            delCb.setItems(FXCollections.observableArrayList(updated));
            delCb.setValue(null);
            delMsg.setStyle("-fx-text-fill:#27ae60;-fx-font-size:12px;");
            delMsg.setText("Removed: " + sel);
            showAlert("Movie Removed", "\"" + sel + "\" has been removed from the catalog.");
        });

        Label lMovTitle = new Label("Movie Title");
        lMovTitle.setStyle("-fx-text-fill:#aaaaaa;-fx-font-size:12px;");
        delCard.getChildren().addAll(dHead, s2, lMovTitle, delCb, delMsg, btnDel);

        forms.getChildren().addAll(addCard, delCard);

        ScrollPane sp = new ScrollPane(forms);
        sp.setFitToWidth(true);
        sp.setStyle("-fx-background-color:#0a0a0a;-fx-background:transparent;");
        HBox.setHgrow(sp, Priority.ALWAYS);
        layout.getChildren().addAll(tableBox, sp);

        VBox wrapper = new VBox(layout);
        VBox.setVgrow(layout, Priority.ALWAYS);
        return wrapper;
    }

    // force password change for new staff/admin accounts
    private void showForceChangePassword() {
        Stage dlg = new Stage();
        dlg.initOwner(primaryStage);
        dlg.initModality(Modality.APPLICATION_MODAL);
        dlg.initStyle(StageStyle.UNDECORATED);
        dlg.setResizable(false);

        VBox root = new VBox(16);
        root.setAlignment(Pos.CENTER_LEFT);
        root.setPadding(new Insets(32, 40, 32, 40));
        root.setMaxWidth(420);
        root.setStyle("-fx-background-color:#1a1a1a;-fx-background-radius:16;-fx-border-color:#800000;-fx-border-radius:16;-fx-border-width:2;");

        Label icon = new Label("🔐");
        icon.setStyle("-fx-font-size:36px;");
        Label title = new Label("Set Your New Password");
        title.setStyle("-fx-font-size:18px;-fx-font-weight:bold;-fx-text-fill:#ffffff;");
        Label sub = new Label("You are using a temporary password. Please set a personal password to continue.");
        sub.setStyle("-fx-font-size:12px;-fx-text-fill:#aaaaaa;");
        sub.setWrapText(true);

        Separator sep = new Separator();
        sep.setStyle("-fx-background-color:#2a2a2a;");

        String fieldStyle = "-fx-background-color:#0d0d0d;-fx-text-fill:#ffffff;-fx-prompt-text-fill:#666666;-fx-border-color:#2a2a2a;-fx-border-radius:8;-fx-background-radius:8;-fx-padding:10 14;-fx-font-size:13px;-fx-pref-width:300px;";

        Label lNew = new Label("New Password");
        lNew.setStyle("-fx-text-fill:#aaaaaa;-fx-font-size:12px;");
        PasswordField pfNew = new PasswordField();
        pfNew.setPromptText("At least 6 characters");
        pfNew.setStyle(fieldStyle);

        Label lConf = new Label("Confirm Password");
        lConf.setStyle("-fx-text-fill:#aaaaaa;-fx-font-size:12px;");
        PasswordField pfConf = new PasswordField();
        pfConf.setPromptText("Repeat password");
        pfConf.setStyle(fieldStyle);

        Label errLbl = new Label("");
        errLbl.setStyle("-fx-text-fill:#cc0000;-fx-font-size:12px;");

        Button btnSave = new Button("Set Password & Continue");
        btnSave.setStyle("-fx-background-color:#800000;-fx-text-fill:white;-fx-font-size:13px;-fx-font-weight:bold;-fx-background-radius:8;-fx-cursor:hand;-fx-padding:10 24;-fx-pref-width:240;");
        btnSave.setOnMouseEntered(e -> btnSave.setStyle("-fx-background-color:#cc0000;-fx-text-fill:white;-fx-font-size:13px;-fx-font-weight:bold;-fx-background-radius:8;-fx-cursor:hand;-fx-padding:10 24;-fx-pref-width:240;"));
        btnSave.setOnMouseExited(e -> btnSave.setStyle("-fx-background-color:#800000;-fx-text-fill:white;-fx-font-size:13px;-fx-font-weight:bold;-fx-background-radius:8;-fx-cursor:hand;-fx-padding:10 24;-fx-pref-width:240;"));
        btnSave.setOnAction(e -> {
            String np = pfNew.getText();
            String nc = pfConf.getText();
            if (np.isEmpty() || nc.isEmpty()) {
                errLbl.setText("Please fill in both fields");
                return;
            }
            if (np.length() < 6) {
                errLbl.setText("Password must be at least 6 characters");
                return;
            }
            if (!np.equals(nc)) {
                errLbl.setText("Passwords don't match");
                return;
            }
            User u = me();
            if (u != null) {
                u.password = np;
                u.mustChangePassword = false;
            }
            dlg.close();
            showAlert("Password Set", "Welcome, " + myName() + "!");
        });

        root.getChildren().addAll(icon, title, sub, sep, lNew, pfNew, lConf, pfConf, errLbl, btnSave);

        Scene sc = new Scene(root);
        sc.setFill(Color.TRANSPARENT);
        dlg.setScene(sc);
        dlg.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
