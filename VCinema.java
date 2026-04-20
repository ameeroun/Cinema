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

public class VipCinema extends Application {

    // ═══════════════════════════════════════════════════════════════
    //  DATA MODELS
    // ═══════════════════════════════════════════════════════════════

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
        String posterUrl  = "";

        Movie(String id, String t, String e, String d, String g,
              String dir, String cast, int p, double r, String... times) {
            this.id = id; title = t; emoji = e; description = d; genre = g;
            director = dir; this.cast = cast; price = p; rating = r;
            showTimes = Arrays.asList(times);
        }

        Movie poster(String url) { this.posterUrl = url; return this; }
    }

    static class Booking {
        String userEmail, movieId, movieTitle, date, time, payMethod;
        int tickets, totalPrice;

        Booking(String ue, String mid, String mt, String date, String time,
                int tickets, int total, String pay) {
            userEmail = ue; movieId = mid; movieTitle = mt;
            this.date = date; this.time = time;
            this.tickets = tickets; totalPrice = total; payMethod = pay;
        }
    }

    static class Review {
        String userEmail, userName, movieId, text;
        long timestamp;

        Review(String ue, String un, String mid, String txt) {
            userEmail = ue; userName = un; movieId = mid; text = txt;
            timestamp = System.currentTimeMillis();
        }
    }

    // ═══════════════════════════════════════════════════════════════
    //  APP STATE
    // ═══════════════════════════════════════════════════════════════

    private List<User>    users    = new ArrayList<>();
    private List<Movie>   movies   = new ArrayList<>();
    private List<Booking> bookings = new ArrayList<>();
    private List<Review>  reviews  = new ArrayList<>();

    private String sessionEmail = "";
    private String sessionRole  = "";

    private Stage primaryStage;
    private double stageW = 1200, stageH = 750;

    private User me() {
        return users.stream().filter(u -> u.email.equals(sessionEmail)).findFirst().orElse(null);
    }
    private String myName() { User u = me(); return u != null ? u.name : ""; }
    private boolean isAdmin() { return "admin".equals(sessionRole); }
    private boolean isStaff() { return "staff".equals(sessionRole) || isAdmin(); }
    private boolean isValidEmail(String e) { return e.matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$"); }
    private Movie movieById(String id) {
        return movies.stream().filter(m -> m.id.equals(id)).findFirst().orElse(null);
    }



    private void seedData() {
        // Users — passwords بسيطة وواضحة
        users.add(new User("Admin",   "ameer@mail.com",  "0000",  "admin"));
       
       
        movies.add(new Movie("m1", "The Dark Knight", "🦇",
            "Batman faces the anarchic Joker who plans to plunge Gotham into chaos.",
            "Action", "Christopher Nolan", "Christian Bale, Heath Ledger", 13, 9.0,
            "16:00", "19:30", "22:00")
            .poster("https://image.tmdb.org/t/p/w500/qJ2tW6WMUDux911r6m7haRef0WH.jpg"));

        movies.add(new Movie("m2", "Inception", "🌀",
            "A thief enters dreams to steal secrets — but his biggest mission is planting an idea.",
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
            "Barbie and Ken leave Barbieland to discover the real world — and themselves.",
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
            "Animation", "Jon Favreau", "Donald Glover, Beyoncé", 10, 7.1,
            "14:00", "16:30", "19:15")
            .poster("https://image.tmdb.org/t/p/w500/2bXbqYdUdNVa8VIWXVfclP2ICtT.jpg"));

        movies.add(new Movie("m14", "Black Panther", "🐾",
            "T'Challa returns to Wakanda to take the throne but faces a challenger from the past.",
            "Action", "Ryan Coogler", "Chadwick Boseman, Michael B. Jordan", 12, 7.3,
            "15:00", "18:00", "21:00")
            .poster("https://image.tmdb.org/t/p/w500/uxzzxijgPIY7slzFvMotPv8wjKA.jpg"));

        movies.add(new Movie("m15", "Dune", "🏜",
            "Paul Atreides leads a rebellion on the desert planet Arrakis to protect its people and resources.",
            "Sci-Fi", "Denis Villeneuve", "Timothée Chalamet, Zendaya", 14, 8.0,
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
            "A poor Korean family schemes to become employed by a wealthy family — with unexpected consequences.",
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

    //  COLORS & STYLES
    // ═══════════════════════════════════════════════════════════════

    static final String C_BG      = "#0a0a0a";
    static final String C_SURFACE = "#111111";
    static final String C_CARD    = "#1a1a1a";
    static final String C_BORDER  = "#2a2a2a";
    static final String C_RED     = "#800000";
    static final String C_RED2    = "#cc0000";
    static final String C_GOLD    = "#f5c518";
    static final String C_GREEN   = "#27ae60";
    static final String C_BLUE    = "#2980b9";
    static final String C_GRAY    = "#aaaaaa";
    static final String C_WHITE   = "#ffffff";
    static final String C_DIM     = "#555555";

   
    static final String S_FIELD =
        "-fx-background-color:#0d0d0d;" +
        "-fx-text-fill:#ffffff;" +
        "-fx-prompt-text-fill:#666666;" +
        "-fx-border-color:#2a2a2a;" +
        "-fx-border-radius:8;" +
        "-fx-background-radius:8;" +
        "-fx-padding:10 14;" +
        "-fx-font-size:13px;" +
        "-fx-pref-width:300px;";

    static final String S_BTN_PRI =
        "-fx-background-color:#800000;-fx-text-fill:white;" +
        "-fx-font-size:13px;-fx-font-weight:bold;-fx-background-radius:8;" +
        "-fx-cursor:hand;-fx-padding:10 24;";

    static final String S_BTN_PRI_H =
        "-fx-background-color:#cc0000;-fx-text-fill:white;" +
        "-fx-font-size:13px;-fx-font-weight:bold;-fx-background-radius:8;" +
        "-fx-cursor:hand;-fx-padding:10 24;";

    static final String S_BTN_SEC =
        "-fx-background-color:#1a1a1a;-fx-text-fill:#aaaaaa;" +
        "-fx-font-size:13px;-fx-background-radius:8;-fx-cursor:hand;" +
        "-fx-padding:10 24;-fx-border-color:#2a2a2a;-fx-border-radius:8;";

    static final String S_BTN_SEC_H =
        "-fx-background-color:#222222;-fx-text-fill:#ffffff;" +
        "-fx-font-size:13px;-fx-background-radius:8;-fx-cursor:hand;" +
        "-fx-padding:10 24;-fx-border-color:#444;-fx-border-radius:8;";

    static final String S_BTN_LINK =
        "-fx-background-color:transparent;-fx-text-fill:#cc0000;" +
        "-fx-font-size:13px;-fx-cursor:hand;-fx-underline:true;-fx-padding:0;";

    static final String S_BTN_DANGER =
        "-fx-background-color:#1a0000;-fx-text-fill:#ff6b6b;" +
        "-fx-font-size:13px;-fx-background-radius:8;-fx-cursor:hand;" +
        "-fx-padding:10 24;-fx-border-color:#c0392b;-fx-border-radius:8;";

    static final String S_NAV_BTN =
        "-fx-background-color:transparent;-fx-text-fill:#ffffff;" +
        "-fx-font-size:13px;-fx-cursor:hand;-fx-padding:8 16;-fx-background-radius:6;";

    static final String S_NAV_BTN_H =
        "-fx-background-color:#1a1a1a;-fx-text-fill:white;" +
        "-fx-font-size:13px;-fx-cursor:hand;-fx-padding:8 16;-fx-background-radius:6;";

    static final String S_NAV_BTN_ACTIVE =
        "-fx-background-color:#800000;-fx-text-fill:white;" +
        "-fx-font-size:13px;-fx-cursor:hand;-fx-padding:8 16;-fx-background-radius:6;-fx-font-weight:bold;";

    // ComboBox CSS — يُحل مشكلة الكتابة الغامقة
    static final String S_COMBO =
        "-fx-background-color:#1a1a1a;" +
        "-fx-color:#1a1a1a;" +
        "-fx-background-radius:8;";

    // ── UI Helpers ────────────────────────────────────────────────

    private void hover(Button b, String n, String h) {
        b.setOnMouseEntered(e -> b.setStyle(h));
        b.setOnMouseExited(e -> b.setStyle(n));
    }

    private Label lbl(String t, String style) {
        Label l = new Label(t);
        l.setStyle(style);
        return l;
    }

    private TextField field(String prompt) {
        TextField tf = new TextField();
        tf.setPromptText(prompt);
        tf.setStyle(S_FIELD);
        return tf;
    }

    private PasswordField pfield(String prompt) {
        PasswordField pf = new PasswordField();
        pf.setPromptText(prompt);
        pf.setStyle(S_FIELD);
        return pf;
    }

    private Separator sep() {
        Separator s = new Separator();
        s.setStyle("-fx-background-color:#2a2a2a;");
        return s;
    }

    private VBox card(double padH, double padV) {
        VBox c = new VBox(12);
        c.setPadding(new Insets(padV, padH, padV, padH));
        c.setStyle("-fx-background-color:#1a1a1a;" +
            "-fx-background-radius:14;-fx-border-color:#2a2a2a;" +
            "-fx-border-radius:14;-fx-border-width:1;");
        return c;
    }

    // ── ComboBox helper مع لون واضح ──────────────────────────────
    private <T> ComboBox<T> makeCombo(ObservableList<T> items, double width) {
        ComboBox<T> cb = new ComboBox<>(items);
        cb.setStyle(S_COMBO);
        cb.setPrefWidth(width);
        // style the cell text
        cb.setCellFactory(lv -> new ListCell<T>() {
            protected void updateItem(T item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setText(null); }
                else {
                    setText(item.toString());
                    setStyle("-fx-text-fill:#ffffff;-fx-background-color:#1a1a1a;-fx-font-size:13px;");
                }
            }
        });
        cb.setButtonCell(new ListCell<T>() {
            protected void updateItem(T item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setText(null); }
                else {
                    setText(item.toString());
                    setStyle("-fx-text-fill:#ffffff;-fx-font-size:13px;");
                }
            }
        });
        return cb;
    }

    // ═══════════════════════════════════════════════════════════════
    //  TOAST NOTIFICATIONS
    // ═══════════════════════════════════════════════════════════════

    private void toast(String icon, String title, String msg, String color) {
        Stage t = new Stage();
        t.initOwner(primaryStage);
        t.initStyle(StageStyle.UNDECORATED);
        t.initModality(Modality.NONE);

        HBox content = new HBox(16);
        content.setPadding(new Insets(20, 28, 20, 28));
        content.setAlignment(Pos.CENTER_LEFT);
        content.setStyle("-fx-background-color:#1a1a1a;" +
            "-fx-background-radius:14;-fx-border-color:" + color + ";" +
            "-fx-border-radius:14;-fx-border-width:2;");
        content.setEffect(new DropShadow(20, Color.web(color, 0.3)));

        Label ico = new Label(icon);
        ico.setStyle("-fx-font-size:28px;");

        VBox txt = new VBox(4);
        Label ttl = new Label(title);
        ttl.setStyle("-fx-font-size:15px;-fx-font-weight:bold;-fx-text-fill:" + color + ";");
        Label sub = new Label(msg);
        sub.setStyle("-fx-font-size:12px;-fx-text-fill:#aaaaaa;");
        sub.setWrapText(true);
        sub.setMaxWidth(280);
        txt.getChildren().addAll(ttl, sub);
        content.getChildren().addAll(ico, txt);

        Scene sc = new Scene(content);
        sc.setFill(Color.TRANSPARENT);
        t.setScene(sc);
        t.setX(primaryStage.getX() + primaryStage.getWidth() - 400);
        t.setY(primaryStage.getY() + 80);
        t.show();

        FadeTransition fade = new FadeTransition(Duration.millis(600), content);
        fade.setDelay(Duration.millis(2200));
        fade.setFromValue(1);
        fade.setToValue(0);
        fade.setOnFinished(e -> t.close());
        fade.play();
    }

    private void toastSuccess(String t, String m) { toast("✅", t, m, C_GREEN); }
    private void toastError(String t, String m)   { toast("❌", t, m, C_RED2); }
    private void toastInfo(String t, String m)     { toast("ℹ️", t, m, C_BLUE); }

    // ═══════════════════════════════════════════════════════════════
    //  START
    // ═══════════════════════════════════════════════════════════════

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

    // ═══════════════════════════════════════════════════════════════
    //  NAVBAR
    // ═══════════════════════════════════════════════════════════════

    private HBox buildNav(String active) {
        HBox nav = new HBox(4);
        nav.setPadding(new Insets(0, 20, 0, 20));
        nav.setAlignment(Pos.CENTER_LEFT);
        nav.setStyle("-fx-background-color:#111111;" +
            "-fx-border-color:#2a2a2a;-fx-border-width:0 0 1 0;");
        nav.setMinHeight(56);

        Label logo = new Label("🎬 VCinema");
        logo.setStyle("-fx-font-size:18px;-fx-font-weight:bold;-fx-text-fill:#cc0000;" +
            "-fx-padding:0 28 0 4;-fx-cursor:hand;");
        logo.setOnMouseClicked(e -> showHome());

        Button btnHome     = navBtn("🏠 Home",     "home",     active);
        Button btnReviews  = navBtn("⭐ Reviews",  "reviews",  active);
        Button btnAbout    = navBtn("ℹ About",     "about",    active);
        Button btnSettings = navBtn("⚙ Settings", "settings", active);

        btnHome.setOnAction(e     -> showHome());
        btnReviews.setOnAction(e  -> showReviews());
        btnAbout.setOnAction(e    -> showAbout());
        btnSettings.setOnAction(e -> showSettings());

        Region sp = new Region();
        HBox.setHgrow(sp, Priority.ALWAYS);

        String roleIcon = isAdmin() ? "👑 " : isStaff() ? "🎫 " : "👤 ";
        Label userLbl = new Label(roleIcon + myName());
        userLbl.setStyle("-fx-text-fill:#aaaaaa;-fx-font-size:12px;-fx-padding:0 10 0 0;");

        Button btnLogout = new Button("🚪 Logout");
        btnLogout.setStyle(S_BTN_DANGER.replace("-fx-padding:10 24;", "-fx-padding:6 14;"));
        btnLogout.setOnAction(e -> { sessionEmail = ""; sessionRole = ""; showLogin(); });

        nav.getChildren().addAll(logo, btnHome, btnReviews, btnAbout, btnSettings);

        if (isStaff()) {
            Button btnAdmin = navBtn("🛡 Admin", "admin", active);
            btnAdmin.setOnAction(e -> showAdmin());
            nav.getChildren().add(btnAdmin);
        }

        nav.getChildren().addAll(sp, userLbl, btnLogout);
        return nav;
    }

    private Button navBtn(String text, String page, String active) {
        Button b = new Button(text);
        if (page.equals(active)) {
            b.setStyle(S_NAV_BTN_ACTIVE);
        } else {
            b.setStyle(S_NAV_BTN);
            hover(b, S_NAV_BTN, S_NAV_BTN_H);
        }
        return b;
    }

    // ═══════════════════════════════════════════════════════════════
    //  AUTH SHELL
    // ═══════════════════════════════════════════════════════════════

    private BorderPane authRoot() {
        BorderPane r = new BorderPane();
        r.setStyle("-fx-background-color:#0a0a0a;");

        HBox top = new HBox();
        top.setAlignment(Pos.CENTER);
        top.setPadding(new Insets(20));
        top.setStyle("-fx-background-color:#111111;" +
            "-fx-border-color:#2a2a2a;-fx-border-width:0 0 1 0;");

        Label logo = new Label("🎬  VCinema");
        logo.setStyle("-fx-font-size:30px;-fx-font-weight:bold;-fx-text-fill:#cc0000;");

        Label tagline = new Label("  Premium Cinema Experience");
        tagline.setStyle("-fx-font-size:14px;-fx-text-fill:#555555;");

        top.getChildren().addAll(logo, tagline);
        r.setTop(top);
        return r;
    }

    // ═══════════════════════════════════════════════════════════════
    //  1. LOGIN
    // ═══════════════════════════════════════════════════════════════

    private void showLogin() {
        BorderPane root = authRoot();

        VBox c = card(50, 36);
        c.setMaxWidth(420);
        c.setAlignment(Pos.CENTER_LEFT);

        Label h  = lbl("Sign In", "-fx-font-size:22px;-fx-font-weight:bold;-fx-text-fill:#ffffff;");
        Label sh = lbl("Welcome back to VCinema", "-fx-font-size:13px;-fx-text-fill:#aaaaaa;");

        Label lEmail = lbl("Email Address", "-fx-text-fill:#aaaaaa;-fx-font-size:12px;");
        TextField tfEmail = field("you@example.com");

        Label lPass = lbl("Password", "-fx-text-fill:#aaaaaa;-fx-font-size:12px;");
        PasswordField pfPass = pfield("Your password");

        Label errLbl = lbl("", "-fx-text-fill:#cc0000;-fx-font-size:12px;");

        Button btnLogin = new Button("Sign In");
        btnLogin.setStyle(S_BTN_PRI);
        btnLogin.setPrefWidth(180);
        hover(btnLogin, S_BTN_PRI, S_BTN_PRI_H);

        Label noAcc = lbl("Don't have an account?", "-fx-text-fill:#aaaaaa;-fx-font-size:13px;");
        Button btnGo = new Button("Sign Up");
        btnGo.setStyle(S_BTN_LINK);
        btnGo.setOnAction(e -> showSignUp());
        HBox row = new HBox(6, noAcc, btnGo);
        row.setAlignment(Pos.CENTER);

        


        c.getChildren().addAll(h, sh, sep(), lEmail, tfEmail, lPass, pfPass,
            errLbl, btnLogin, sep(), row);

        StackPane center = new StackPane(c);
        center.setStyle("-fx-background-color:#0a0a0a;");
        root.setCenter(center);

        Runnable doLogin = () -> {
            String em = tfEmail.getText().trim().toLowerCase();
            String pw = pfPass.getText();
            if (em.isEmpty() || pw.isEmpty()) {
                errLbl.setText("⚠ Fill in all fields");
                return;
            }
            User f = users.stream()
                .filter(u -> u.email.equalsIgnoreCase(em) && u.password.equals(pw))
                .findFirst().orElse(null);
            if (f != null) {
                sessionEmail = f.email;
                sessionRole  = f.role;
                showHome();
                if (f.mustChangePassword) showForceChangePassword();
            } else {
                errLbl.setText("✖ Invalid email or password");
            }
        };

        btnLogin.setOnAction(e -> doLogin.run());
        pfPass.setOnAction(e  -> doLogin.run());

        primaryStage.setScene(new Scene(root, stageW, stageH));
    }

   
    //  2. SIGN UP
   

    private void showSignUp() {
        BorderPane root = authRoot();

        VBox c = card(50, 30);
        c.setMaxWidth(440);
        c.setAlignment(Pos.CENTER_LEFT);

        Label h  = lbl("Create Account", "-fx-font-size:22px;-fx-font-weight:bold;-fx-text-fill:#ffffff;");
        Label sh = lbl("Join VCinema — it's free!", "-fx-font-size:13px;-fx-text-fill:#aaaaaa;");

        Label lName  = lbl("Full Name", "-fx-text-fill:#aaaaaa;-fx-font-size:12px;");
        TextField tfName = field("e.g. John Doe");

        Label lEmail = lbl("Email Address", "-fx-text-fill:#aaaaaa;-fx-font-size:12px;");
        TextField tfEmail = field("you@example.com");

        Label lPass  = lbl("Password", "-fx-text-fill:#aaaaaa;-fx-font-size:12px;");
        PasswordField pfPass = pfield("At least 6 characters");

        Label lConf  = lbl("Confirm Password", "-fx-text-fill:#aaaaaa;-fx-font-size:12px;");
        PasswordField pfConf = pfield("Repeat password");

        // Strength bar
        ProgressBar bar = new ProgressBar(0);
        bar.setPrefWidth(300);
        bar.setStyle("-fx-accent:#800000;");
        Label barLbl = lbl("", "-fx-font-size:11px;-fx-text-fill:#aaaaaa;");

        pfPass.textProperty().addListener((o, ov, nv) -> {
            if (nv.isEmpty()) { bar.setProgress(0); barLbl.setText(""); return; }
            double s = 0.2;
            String col = C_RED;
            if (nv.length() >= 8)  { s = 0.5;  col = "#e67e22"; }
            if (nv.length() >= 10 && nv.matches(".*[A-Z].*")) { s = 0.75; col = C_GOLD; }
            if (nv.length() >= 12 && nv.matches(".*[A-Z].*") && nv.matches(".*[0-9].*")) { s = 1.0; col = C_GREEN; }
            bar.setProgress(s);
            bar.setStyle("-fx-accent:" + col + ";");
            String[] lvl = {"", "Weak", "Fair", "Good", "Strong"};
            barLbl.setText("Strength: " + lvl[Math.min((int)(s * 4), 4)]);
        });

        CheckBox cbTerms = new CheckBox("I agree to the Terms & Conditions");
        cbTerms.setStyle("-fx-text-fill:#aaaaaa;-fx-font-size:12px;");

        Label errLbl = lbl("", "-fx-text-fill:#cc0000;-fx-font-size:12px;");
        errLbl.setWrapText(true);

        Button btnUp = new Button("Create Account");
        btnUp.setStyle(S_BTN_PRI);
        btnUp.setPrefWidth(200);
        hover(btnUp, S_BTN_PRI, S_BTN_PRI_H);

        Label hasAcc = lbl("Already have an account?", "-fx-text-fill:#aaaaaa;-fx-font-size:13px;");
        Button btnBack = new Button("Log In");
        btnBack.setStyle(S_BTN_LINK);
        btnBack.setOnAction(e -> showLogin());
        HBox row = new HBox(6, hasAcc, btnBack);
        row.setAlignment(Pos.CENTER);

        c.getChildren().addAll(h, sh, sep(),
            lName, tfName, lEmail, tfEmail,
            lPass, pfPass, bar, barLbl,
            lConf, pfConf, cbTerms, errLbl, btnUp, row);

        ScrollPane sp = new ScrollPane(new StackPane(c));
        sp.setFitToWidth(true);
        sp.setStyle("-fx-background-color:#0a0a0a;-fx-background:transparent;");
        root.setCenter(sp);

        btnUp.setOnAction(e -> {
            String name  = tfName.getText().trim();
            String email = tfEmail.getText().trim().toLowerCase();
            String pass  = pfPass.getText();
            String conf  = pfConf.getText();
            if (name.isEmpty() || email.isEmpty() || pass.isEmpty() || conf.isEmpty()) {
                errLbl.setText("⚠ Fill in all fields"); return;
            }
            if (name.length() < 2) { errLbl.setText("⚠ Name too short"); return; }
            if (!isValidEmail(email)) { errLbl.setText("✖ Invalid email address"); return; }
            if (users.stream().anyMatch(u -> u.email.equalsIgnoreCase(email))) {
                errLbl.setText("✖ Email already registered"); return;
            }
            if (pass.length() < 6) { errLbl.setText("⚠ Password min 6 characters"); return; }
            if (!pass.equals(conf)) { errLbl.setText("✖ Passwords don't match"); return; }
            if (!cbTerms.isSelected()) { errLbl.setText("⚠ Accept Terms & Conditions"); return; }

            users.add(new User(name, email, pass, "customer"));
            sessionEmail = email;
            sessionRole  = "customer";
            showHome();
            toastSuccess("Welcome, " + name + "!", "Your account has been created.");
        });

        primaryStage.setScene(new Scene(root, stageW, stageH));
    }

   
    //  3. HOME 
  

    private void showHome() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color:#0a0a0a;");

        // Hero
        VBox hero = new VBox(10);
        hero.setAlignment(Pos.CENTER);
        hero.setPadding(new Insets(36, 40, 24, 40));
        hero.setStyle("-fx-background-color:linear-gradient(to bottom, #1a0000, #0a0a0a);");

        Label heroTitle = new Label("Welcome to VCinema");
        heroTitle.setStyle("-fx-font-size:34px;-fx-font-weight:bold;-fx-text-fill:#ffffff;");

        Label heroSub = new Label("Book your next cinematic experience");
        heroSub.setStyle("-fx-font-size:14px;-fx-text-fill:#aaaaaa;");

        hero.getChildren().addAll(heroTitle, heroSub);

        // Filter bar
        HBox filters = new HBox(12);
        filters.setPadding(new Insets(12, 28, 12, 28));
        filters.setAlignment(Pos.CENTER_LEFT);
        filters.setStyle("-fx-background-color:#111111;" +
            "-fx-border-color:#2a2a2a;-fx-border-width:0 0 1 0;");

        
        TextField searchBox = new TextField();
        searchBox.setPromptText("🔍  Search movies...");
        searchBox.setStyle(S_FIELD + "-fx-pref-width:220px;");

        
        List<String> genreList = new ArrayList<>(List.of("All Genres"));
        movies.stream().map(m -> m.genre).distinct().sorted().forEach(genreList::add);
        ComboBox<String> genreCb = makeCombo(FXCollections.observableArrayList(genreList), 150);
        genreCb.setValue("All Genres");

        // Sort combo
        ComboBox<String> sortCb = makeCombo(
            FXCollections.observableArrayList("Default", "Rating ↓", "Price ↑", "Price ↓"), 140);
        sortCb.setValue("Default");

        Label moviesCount = lbl(movies.size() + " movies", "-fx-text-fill:#555555;-fx-font-size:12px;");
        Region sp2 = new Region();
        HBox.setHgrow(sp2, Priority.ALWAYS);

        filters.getChildren().addAll(
            lbl("Showing:", "-fx-text-fill:#aaaaaa;-fx-font-size:13px;"),
            searchBox, genreCb, sortCb, sp2, moviesCount);

        // Movies grid
        FlowPane grid = new FlowPane();
        grid.setHgap(18);
        grid.setVgap(18);
        grid.setPadding(new Insets(24, 28, 28, 28));
        grid.setStyle("-fx-background-color:#0a0a0a;");

        Runnable renderMovies = () -> {
            grid.getChildren().clear();
            String search = searchBox.getText().toLowerCase().trim();
            String genre  = genreCb.getValue();
            String sort   = sortCb.getValue();

            List<Movie> list = movies.stream()
                .filter(m -> search.isEmpty()
                    || m.title.toLowerCase().contains(search)
                    || m.genre.toLowerCase().contains(search))
                .filter(m -> "All Genres".equals(genre) || m.genre.equals(genre))
                .collect(Collectors.toList());

            if ("Rating ↓".equals(sort)) list.sort((a, b) -> Double.compare(b.rating, a.rating));
            else if ("Price ↑".equals(sort)) list.sort(Comparator.comparingInt(m -> m.price));
            else if ("Price ↓".equals(sort)) list.sort((a, b) -> b.price - a.price);

            moviesCount.setText(list.size() + " movie" + (list.size() != 1 ? "s" : ""));
            if (list.isEmpty()) {
                grid.getChildren().add(lbl("No movies found.",
                    "-fx-text-fill:#555555;-fx-font-size:16px;-fx-padding:40;"));
            } else {
                list.forEach(m -> grid.getChildren().add(makeMovieCard(m)));
            }
        };

        renderMovies.run();
        searchBox.setOnKeyReleased(e -> renderMovies.run());
        genreCb.setOnAction(e -> renderMovies.run());
        sortCb.setOnAction(e  -> renderMovies.run());

        VBox top2 = new VBox(hero, filters);
        root.setTop(new VBox(buildNav("home"), top2));

        ScrollPane sp = new ScrollPane(grid);
        sp.setFitToWidth(true);
        sp.setStyle("-fx-background-color:#0a0a0a;-fx-background:transparent;");
        root.setCenter(sp);

        primaryStage.setScene(new Scene(root, stageW, stageH));
    }

    // ─── Poster builder ───────────────────────────────────────────

    private StackPane makePoster(Movie m, double w, double h, double emojiSize) {
        StackPane poster = new StackPane();
        poster.setPrefSize(w, h);
        poster.setStyle("-fx-background-color:#0d0d0d;-fx-background-radius:12 12 0 0;");

        if (!m.posterPath.isEmpty()) {
            try {
                Image img = new Image("file:///" + m.posterPath.replace("\\", "/"),
                    w, h, false, true, true);
                ImageView iv = new ImageView(img);
                iv.setFitWidth(w); iv.setFitHeight(h);
                iv.setPreserveRatio(false);
                poster.getChildren().add(iv);
                return poster;
            } catch (Exception ignored) {}
        }

        if (!m.posterUrl.isEmpty()) {
            try {
                Image img = new Image(m.posterUrl, w, h, false, true, true);
                ImageView iv = new ImageView(img);
                iv.setFitWidth(w); iv.setFitHeight(h);
                iv.setPreserveRatio(false);

                Label loading = new Label(m.emoji);
                loading.setStyle("-fx-font-size:" + emojiSize + "px;");
                img.progressProperty().addListener((o, ov, nv) -> {
                    if (nv.doubleValue() >= 1.0) poster.getChildren().remove(loading);
                });
                img.errorProperty().addListener((o, ov, nv) -> {
                    if (nv) poster.getChildren().remove(iv);
                });
                poster.getChildren().addAll(loading, iv);
                return poster;
            } catch (Exception ignored) {}
        }

        Label emo = new Label(m.emoji);
        emo.setStyle("-fx-font-size:" + emojiSize + "px;");
        poster.getChildren().add(emo);
        return poster;
    }

    // ─── Movie Card 

    private VBox makeMovieCard(Movie m) {
        VBox card = new VBox(8);
        card.setAlignment(Pos.TOP_CENTER);
        card.setPrefWidth(190);
        card.setPadding(new Insets(0, 0, 14, 0));
        card.setStyle("-fx-background-color:#1a1a1a;" +
            "-fx-background-radius:12;-fx-border-color:#2a2a2a;" +
            "-fx-border-radius:12;-fx-cursor:hand;");

        StackPane poster = makePoster(m, 190, 250, 64);

        Label badge = new Label(m.genre);
        badge.setStyle("-fx-background-color:#800000;-fx-text-fill:white;" +
            "-fx-font-size:10px;-fx-padding:3 8;-fx-background-radius:4;");
        StackPane.setAlignment(badge, Pos.TOP_LEFT);
        StackPane.setMargin(badge, new Insets(8, 0, 0, 8));

        Label rBadge = new Label("★ " + m.rating);
        rBadge.setStyle("-fx-background-color:#0a0a0a;-fx-text-fill:#f5c518;" +
            "-fx-font-size:10px;-fx-padding:3 8;-fx-background-radius:4;-fx-font-weight:bold;");
        StackPane.setAlignment(rBadge, Pos.TOP_RIGHT);
        StackPane.setMargin(rBadge, new Insets(8, 8, 0, 0));

        poster.getChildren().addAll(badge, rBadge);

        Label title = new Label(m.title);
        title.setStyle("-fx-text-fill:#ffffff;-fx-font-size:12px;-fx-font-weight:bold;");
        title.setWrapText(true);
        title.setTextAlignment(TextAlignment.CENTER);
        title.setMaxWidth(166);
        title.setPadding(new Insets(0, 12, 0, 12));

        Label price = new Label("From $" + m.price + "/ticket");
        price.setStyle("-fx-text-fill:#f5c518;-fx-font-size:11px;");

        Button bookBtn = new Button("Book Now");
        String bStyle = "-fx-background-color:#800000;-fx-text-fill:white;" +
            "-fx-font-size:11px;-fx-background-radius:6;-fx-cursor:hand;" +
            "-fx-padding:6 18;-fx-font-weight:bold;";
        String bHover = "-fx-background-color:#cc0000;-fx-text-fill:white;" +
            "-fx-font-size:11px;-fx-background-radius:6;-fx-cursor:hand;" +
            "-fx-padding:6 18;-fx-font-weight:bold;";
        bookBtn.setStyle(bStyle);
        hover(bookBtn, bStyle, bHover);

        card.getChildren().addAll(poster, title, price, bookBtn);

        card.setOnMouseEntered(e -> card.setStyle("-fx-background-color:#20202e;" +
            "-fx-background-radius:12;-fx-border-color:#800000;" +
            "-fx-border-radius:12;-fx-cursor:hand;" +
            "-fx-effect:dropshadow(gaussian,rgba(192,57,43,0.4),16,0,0,4);"));
        card.setOnMouseExited(e -> card.setStyle("-fx-background-color:#1a1a1a;" +
            "-fx-background-radius:12;-fx-border-color:#2a2a2a;" +
            "-fx-border-radius:12;-fx-cursor:hand;"));

        card.setOnMouseClicked(e -> showMovieDetail(m));
        bookBtn.setOnAction(e -> { e.consume(); showMovieDetail(m); });

        return card;
    }

    // ─── Movie Detail Popup

    private void showMovieDetail(Movie m) {
        Stage dlg = new Stage();
        dlg.initOwner(primaryStage);
        dlg.initModality(Modality.APPLICATION_MODAL);
        dlg.initStyle(StageStyle.UNDECORATED);
        dlg.setResizable(false);

        VBox root = new VBox(0);
        root.setStyle("-fx-background-color:#1a1a1a;" +
            "-fx-background-radius:16;-fx-border-color:#2a2a2a;" +
            "-fx-border-radius:16;-fx-border-width:1;");
        root.setMaxWidth(520);

        StackPane header = new StackPane();
        header.setPrefHeight(220);

        StackPane posterHeader = makePoster(m, 520, 220, 80);

        Region overlay = new Region();
        overlay.setStyle("-fx-background-color:linear-gradient(to bottom, rgba(0,0,0,0.15), rgba(0,0,0,0.55));" +
            "-fx-background-radius:16 16 0 0;");

        Button closeBtn = new Button("✕");
        closeBtn.setStyle("-fx-background-color:rgba(42,0,0,0.85);-fx-text-fill:#ff6b6b;" +
            "-fx-background-radius:20;-fx-cursor:hand;-fx-font-size:12px;-fx-padding:4 10;");
        closeBtn.setOnAction(e -> dlg.close());
        StackPane.setAlignment(closeBtn, Pos.TOP_RIGHT);
        StackPane.setMargin(closeBtn, new Insets(12));

        Label genreLbl = new Label(m.genre);
        genreLbl.setStyle("-fx-background-color:#800000;-fx-text-fill:white;" +
            "-fx-font-size:11px;-fx-padding:4 10;-fx-background-radius:4;");
        StackPane.setAlignment(genreLbl, Pos.TOP_LEFT);
        StackPane.setMargin(genreLbl, new Insets(12));

        Label posterTitle = new Label(m.title);
        posterTitle.setStyle("-fx-font-size:18px;-fx-font-weight:bold;-fx-text-fill:white;" +
            "-fx-effect:dropshadow(gaussian,black,6,0.8,0,1);");
        StackPane.setAlignment(posterTitle, Pos.BOTTOM_LEFT);
        StackPane.setMargin(posterTitle, new Insets(0, 0, 12, 14));

        header.getChildren().addAll(posterHeader, overlay, closeBtn, genreLbl, posterTitle);

        VBox body = new VBox(12);
        body.setPadding(new Insets(16, 28, 24, 28));

        HBox meta = new HBox(16);
        meta.setAlignment(Pos.CENTER_LEFT);
        Label rat = new Label("★ " + m.rating);
        rat.setStyle("-fx-text-fill:#f5c518;-fx-font-weight:bold;");
        Label pr = new Label("$" + m.price + "/ticket");
        pr.setStyle("-fx-text-fill:#27ae60;");
        Label dir = new Label("🎬 " + m.director);
        dir.setStyle("-fx-text-fill:#aaaaaa;-fx-font-size:12px;");
        meta.getChildren().addAll(rat, pr, dir);

        Label cast = new Label("Cast: " + m.cast);
        cast.setStyle("-fx-text-fill:#aaaaaa;-fx-font-size:12px;");
        cast.setWrapText(true);

        Label descL = new Label(m.description);
        descL.setStyle("-fx-text-fill:#ccccdd;-fx-font-size:13px;");
        descL.setWrapText(true);

        body.getChildren().addAll(meta, cast, sep(), descL, sep());

        LocalDate today = LocalDate.now();
        Label sLabel = new Label("🗓  Select Date & Time");
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
            tb.setStyle("-fx-background-color:#1a1a1a;-fx-text-fill:#aaaaaa;" +
                "-fx-background-radius:8;-fx-cursor:hand;-fx-pref-width:60;-fx-font-size:12px;");
            String ds = d.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            tb.setOnAction(e -> selectedDate[0] = ds);
            if (i == 0) {
                tb.setSelected(true);
                tb.setStyle("-fx-background-color:#800000;-fx-text-fill:white;-fx-background-radius:8;-fx-cursor:hand;-fx-pref-width:60;-fx-font-size:12px;-fx-font-weight:bold;");
            }
            tb.selectedProperty().addListener((o, ov, nv) ->
                tb.setStyle(nv
                    ? "-fx-background-color:#800000;-fx-text-fill:white;-fx-background-radius:8;-fx-cursor:hand;-fx-pref-width:60;-fx-font-size:12px;-fx-font-weight:bold;"
                    : "-fx-background-color:#1a1a1a;-fx-text-fill:#aaaaaa;-fx-background-radius:8;-fx-cursor:hand;-fx-pref-width:60;-fx-font-size:12px;"));
            dateRow.getChildren().add(tb);
        }

        HBox timeRow = new HBox(10);
        timeRow.setAlignment(Pos.CENTER_LEFT);
        ToggleGroup timeGroup = new ToggleGroup();
        String[] selectedTime = {m.showTimes.get(0)};

        for (String t : m.showTimes) {
            ToggleButton tb = new ToggleButton(t);
            tb.setToggleGroup(timeGroup);
            tb.setStyle("-fx-background-color:#1a1a1a;-fx-text-fill:#aaaaaa;" +
                "-fx-background-radius:8;-fx-cursor:hand;-fx-pref-width:72;-fx-font-size:12px;");
            tb.setOnAction(e -> selectedTime[0] = t);
            if (t.equals(m.showTimes.get(0))) {
                tb.setSelected(true);
                tb.setStyle("-fx-background-color:#2980b9;-fx-text-fill:white;-fx-background-radius:8;-fx-cursor:hand;-fx-pref-width:72;-fx-font-size:12px;-fx-font-weight:bold;");
            }
            tb.selectedProperty().addListener((o, ov, nv) ->
                tb.setStyle(nv
                    ? "-fx-background-color:#2980b9;-fx-text-fill:white;-fx-background-radius:8;-fx-cursor:hand;-fx-pref-width:72;-fx-font-size:12px;-fx-font-weight:bold;"
                    : "-fx-background-color:#1a1a1a;-fx-text-fill:#aaaaaa;-fx-background-radius:8;-fx-cursor:hand;-fx-pref-width:72;-fx-font-size:12px;"));
            timeRow.getChildren().add(tb);
        }

        HBox tickRow = new HBox(12);
        tickRow.setAlignment(Pos.CENTER_LEFT);
        Label tLbl = lbl("Tickets:", "-fx-text-fill:#aaaaaa;-fx-font-size:13px;");
        Spinner<Integer> spinner = new Spinner<>(1, 20, 1);
        spinner.setEditable(true);
        spinner.setPrefWidth(90);

        Label totalLbl = lbl("Total:  $" + m.price,
            "-fx-font-size:16px;-fx-font-weight:bold;-fx-text-fill:#f5c518;");
        spinner.valueProperty().addListener((o, ov, nv) ->
            totalLbl.setText("Total:  $" + (nv * m.price)));

        tickRow.getChildren().addAll(tLbl, spinner,
            new Region() {{ HBox.setHgrow(this, Priority.ALWAYS); }},
            totalLbl);

        Button btnBook = new Button("🎟  Book Now");
        btnBook.setStyle(S_BTN_PRI + "-fx-font-size:14px;-fx-pref-width:200;");
        hover(btnBook, S_BTN_PRI + "-fx-font-size:14px;-fx-pref-width:200;",
            S_BTN_PRI_H + "-fx-font-size:14px;-fx-pref-width:200;");
        btnBook.setOnAction(e -> {
            if (timeGroup.getSelectedToggle() == null) {
                toastError("No time selected", "Please choose a showtime first.");
                return;
            }
            dlg.close();
            showPayment(m, selectedDate[0], selectedTime[0], spinner.getValue());
        });

        body.getChildren().addAll(sLabel, dateRow, timeRow, tickRow, btnBook);
        root.getChildren().addAll(header, body);

        Scene sc = new Scene(root, 520, 680);
        sc.setFill(Color.TRANSPARENT);
        dlg.setScene(sc);
        dlg.showAndWait();
    }

    //  4. PAYMENT
    

    private void showPayment(Movie m, String date, String time, int tickets) {
        Stage dlg = new Stage();
        dlg.initOwner(primaryStage);
        dlg.initModality(Modality.APPLICATION_MODAL);
        dlg.setTitle("Payment — VCinema");
        dlg.setResizable(false);

        int total = tickets * m.price;

        VBox root = new VBox(0);
        root.setStyle("-fx-background-color:#1a1a1a;-fx-background-radius:14;");

        HBox hdr = new HBox(12);
        hdr.setPadding(new Insets(18, 24, 18, 24));
        hdr.setAlignment(Pos.CENTER_LEFT);
        hdr.setStyle("-fx-background-color:#111111;" +
            "-fx-border-color:#2a2a2a;-fx-border-width:0 0 1 0;");
        Label hdrLbl = new Label("💳  Checkout");
        hdrLbl.setStyle("-fx-font-size:16px;-fx-font-weight:bold;-fx-text-fill:#ffffff;");
        Region hsp = new Region();
        HBox.setHgrow(hsp, Priority.ALWAYS);
        Label hdrTotal = new Label("$" + total);
        hdrTotal.setStyle("-fx-font-size:20px;-fx-font-weight:bold;-fx-text-fill:#27ae60;");
        hdr.getChildren().addAll(hdrLbl, hsp, hdrTotal);

        VBox summary = new VBox(8);
        summary.setPadding(new Insets(16, 24, 16, 24));
        summary.setStyle("-fx-background-color:#0d0d0d;-fx-border-color:#2a2a2a;-fx-border-width:0 0 1 0;");
        summary.getChildren().addAll(
            infoRow("🎬  Movie",   m.title),
            infoRow("📅  Date",    date),
            infoRow("🕐  Time",    time),
            infoRow("🎟  Tickets", tickets + " × $" + m.price),
            infoRow("💵  Total",   "$" + total)
        );

        ToggleGroup payGroup = new ToggleGroup();
        ToggleButton tbCard = new ToggleButton("💳  Card");
        ToggleButton tbCash = new ToggleButton("💵  Cash");
        for (ToggleButton tb : new ToggleButton[]{tbCard, tbCash}) {
            tb.setToggleGroup(payGroup);
            tb.setStyle("-fx-background-color:#1a1a1a;-fx-text-fill:#aaaaaa;" +
                "-fx-background-radius:8;-fx-cursor:hand;-fx-pref-width:110;-fx-padding:9 0;");
            tb.selectedProperty().addListener((o, ov, nv) ->
                tb.setStyle(nv
                    ? "-fx-background-color:#2980b9;-fx-text-fill:white;-fx-background-radius:8;-fx-cursor:hand;-fx-pref-width:110;-fx-padding:9 0;-fx-font-weight:bold;"
                    : "-fx-background-color:#1a1a1a;-fx-text-fill:#aaaaaa;-fx-background-radius:8;-fx-cursor:hand;-fx-pref-width:110;-fx-padding:9 0;"));
        }
        tbCard.setSelected(true);
        HBox tabs = new HBox(10, tbCard, tbCash);
        tabs.setAlignment(Pos.CENTER);

        VBox cardForm = new VBox(10);
        cardForm.setPadding(new Insets(12, 0, 0, 0));

        TextField tfCard   = field("•••• •••• •••• ••••");
        TextField tfExp    = field("MM / YY");
        PasswordField tfCvv = pfield("•••");
        tfCvv.setStyle(S_FIELD + "-fx-pref-width:120px;");
        TextField tfHolder = field("Full name on card");

        HBox expCvv = new HBox(12,
            new VBox(4, lbl("Expiry", "-fx-text-fill:#aaaaaa;-fx-font-size:12px;"), tfExp),
            new VBox(4, lbl("CVV", "-fx-text-fill:#aaaaaa;-fx-font-size:12px;"), tfCvv));

        cardForm.getChildren().addAll(
            lbl("Card Number", "-fx-text-fill:#aaaaaa;-fx-font-size:12px;"), tfCard,
            expCvv,
            lbl("Cardholder Name", "-fx-text-fill:#aaaaaa;-fx-font-size:12px;"), tfHolder);

        VBox cashInfo = new VBox(12);
        cashInfo.setVisible(false);
        cashInfo.setManaged(false);
        cashInfo.setPadding(new Insets(12, 0, 0, 0));

        VBox cashCard = new VBox(10);
        cashCard.setPadding(new Insets(16));
        cashCard.setAlignment(Pos.CENTER);
        cashCard.setStyle("-fx-background-color:#0a180a;-fx-background-radius:10;" +
            "-fx-border-color:#27ae60;-fx-border-radius:10;-fx-border-width:1;");
        Label cashTitle = new Label("Pay at the Cinema Counter");
        cashTitle.setStyle("-fx-font-size:14px;-fx-font-weight:bold;-fx-text-fill:#27ae60;");
        Label cashSub = new Label("Show your booking confirmation at the box office\nand pay $" + total + " in cash before the show.");
        cashSub.setStyle("-fx-text-fill:#aaaaaa;-fx-font-size:12px;");
        cashSub.setWrapText(true);
        cashCard.getChildren().addAll(new Label("💵") {{
            setStyle("-fx-font-size:32px;");
        }}, cashTitle, cashSub);
        cashInfo.getChildren().add(cashCard);

        tbCard.setOnAction(e -> {
            cardForm.setVisible(true);  cardForm.setManaged(true);
            cashInfo.setVisible(false); cashInfo.setManaged(false);
        });
        tbCash.setOnAction(e -> {
            cashInfo.setVisible(true);  cashInfo.setManaged(true);
            cardForm.setVisible(false); cardForm.setManaged(false);
        });

        Label payErr = lbl("", "-fx-text-fill:#cc0000;-fx-font-size:12px;");
        payErr.setWrapText(true);

        Button btnPay = new Button("✅  Confirm & Pay  $" + total);
        btnPay.setStyle(S_BTN_PRI + "-fx-font-size:14px;-fx-pref-width:280;");
        hover(btnPay, S_BTN_PRI + "-fx-font-size:14px;-fx-pref-width:280;",
            S_BTN_PRI_H + "-fx-font-size:14px;-fx-pref-width:280;");

        Button btnCancel = new Button("Cancel");
        btnCancel.setStyle(S_BTN_SEC);
        hover(btnCancel, S_BTN_SEC, S_BTN_SEC_H);
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
                    payErr.setText("⚠ Please fill in all card details");
                    return;
                }
                if (cn.length() < 12) { payErr.setText("✖ Invalid card number"); return; }
                if (!ex.matches("\\d{1,2}\\s*/\\s*\\d{2}")) {
                    payErr.setText("✖ Invalid expiry (MM/YY)");
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

    private HBox infoRow(String key, String val) {
        HBox r = new HBox();
        Label k = lbl(key, "-fx-text-fill:#aaaaaa;-fx-font-size:12px;-fx-pref-width:120;");
        Label v = lbl(val, "-fx-text-fill:#ffffff;-fx-font-size:12px;-fx-font-weight:bold;");
        r.getChildren().addAll(k, v);
        return r;
    }

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
        root.setStyle("-fx-background-color:#1a1a1a;" +
            "-fx-background-radius:16;-fx-border-color:#27ae60;" +
            "-fx-border-radius:16;-fx-border-width:2;");
        root.setEffect(new DropShadow(30, Color.web(C_GREEN, 0.3)));

        Label icon = new Label("🎉");
        icon.setStyle("-fx-font-size:52px;");
        Label title = new Label("Booking Confirmed!");
        title.setStyle("-fx-font-size:20px;-fx-font-weight:bold;-fx-text-fill:#27ae60;");
        Label subLbl = new Label("Enjoy the show, " + myName() + "!");
        subLbl.setStyle("-fx-font-size:13px;-fx-text-fill:#aaaaaa;");

        VBox info = new VBox(8);
        info.setPadding(new Insets(14, 20, 14, 20));
        info.setStyle("-fx-background-color:#0a0a0a;-fx-background-radius:10;");
        info.getChildren().addAll(
            infoRow("🎬  Movie",   m.title),
            infoRow("📅  Date",    date),
            infoRow("🕐  Time",    time),
            infoRow("🎟  Tickets", tickets + " tickets"),
            infoRow("💵  Total",   "$" + total),
            infoRow("💳  Payment", payMethod),
            infoRow("🔖  Ref",     bookId)
        );

        Button btnDone = new Button("Done  ✓");
        btnDone.setStyle(S_BTN_PRI + "-fx-font-size:14px;-fx-pref-width:160;");
        hover(btnDone, S_BTN_PRI + "-fx-font-size:14px;-fx-pref-width:160;",
            S_BTN_PRI_H + "-fx-font-size:14px;-fx-pref-width:160;");
        btnDone.setOnAction(e -> dlg.close());

        root.getChildren().addAll(icon, title, subLbl, info, btnDone);

        Scene sc = new Scene(root);
        sc.setFill(Color.TRANSPARENT);
        dlg.setScene(sc);
        dlg.showAndWait();
    }

  
    //  5. REVIEWS


    private void showReviews() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color:#0a0a0a;");
        root.setTop(buildNav("reviews"));

        VBox page = new VBox(24);
        page.setPadding(new Insets(30, 80, 40, 80));
        page.setAlignment(Pos.TOP_CENTER);

        Label h  = lbl("⭐  Reviews", "-fx-font-size:26px;-fx-font-weight:bold;-fx-text-fill:#ffffff;");
        Label sh = lbl("Share your experience with the VCinema community",
            "-fx-font-size:13px;-fx-text-fill:#aaaaaa;");

        VBox writeCard = card(28, 22);
        writeCard.setMaxWidth(600);

        Label wHead = lbl("✍  Write a Review",
            "-fx-font-size:15px;-fx-font-weight:bold;-fx-text-fill:#ffffff;");

        HBox nameRow = new HBox(10);
        nameRow.setAlignment(Pos.CENTER_LEFT);
        Label nameLbl = lbl("Posting as:", "-fx-text-fill:#aaaaaa;-fx-font-size:12px;");
        Label nameVal = new Label("👤 " + myName());
        nameVal.setStyle("-fx-background-color:#0d0d0d;-fx-text-fill:#f5c518;" +
            "-fx-padding:7 14;-fx-background-radius:8;-fx-font-weight:bold;-fx-font-size:13px;");
        nameRow.getChildren().addAll(nameLbl, nameVal);

        Label lFB = lbl("Your Review", "-fx-text-fill:#aaaaaa;-fx-font-size:12px;");
        TextArea ta = new TextArea();
        ta.setPromptText("Tell others about your experience...");
        ta.setStyle("-fx-background-color:#0d0d0d;-fx-text-fill:#000000;" +
            "-fx-border-color:#2a2a2a;-fx-border-radius:8;-fx-background-radius:8;" +
            "-fx-font-size:13px;-fx-pref-width:540px;");
        ta.setPrefRowCount(3);
        ta.setWrapText(true);

        Label errR = lbl("", "-fx-text-fill:#cc0000;-fx-font-size:12px;");
        Button btnSubmit = new Button("Submit Review");
        btnSubmit.setStyle(S_BTN_PRI);
        hover(btnSubmit, S_BTN_PRI, S_BTN_PRI_H);

        writeCard.getChildren().addAll(wHead, nameRow, sep(), lFB, ta, errR, btnSubmit);

        VBox revList = new VBox(12);
        revList.setMaxWidth(600);
        Label listHead = lbl("💬  All Reviews",
            "-fx-font-size:17px;-fx-font-weight:bold;-fx-text-fill:#ffffff;");

        Runnable renderRevs = () -> {
            revList.getChildren().clear();
            revList.getChildren().add(listHead);
            if (reviews.isEmpty()) {
                revList.getChildren().add(lbl("No reviews yet — be the first!",
                    "-fx-text-fill:#555555;-fx-font-size:13px;-fx-padding:20;"));
            } else {
                List<Review> sorted = new ArrayList<>(reviews);
                sorted.sort((a, b) -> Long.compare(b.timestamp, a.timestamp));
                for (Review r : sorted) {
                    VBox rc = new VBox(8);
                    rc.setPadding(new Insets(16));
                    rc.setStyle("-fx-background-color:#1a1a1a;" +
                        "-fx-background-radius:10;-fx-border-color:#800000;" +
                        "-fx-border-radius:10;-fx-border-width:0 0 0 3;");

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
            if (fb.isEmpty()) { errR.setText("⚠ Please write your review"); return; }
            reviews.add(new Review(sessionEmail, myName(), "", fb));
            ta.clear();
            errR.setText("");
            renderRevs.run();
            toastSuccess("Review Submitted!", "Thank you for sharing your experience.");
        });

        page.getChildren().addAll(h, sh, writeCard, revList);

        ScrollPane sp = new ScrollPane(page);
        sp.setFitToWidth(true);
        sp.setStyle("-fx-background-color:#0a0a0a;-fx-background:transparent;");
        root.setCenter(sp);

        primaryStage.setScene(new Scene(root, stageW, stageH));
    }

    
    //  6. ABOUT
  
    private void showAbout() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color:#0a0a0a;");
        root.setTop(buildNav("about"));

        VBox page = new VBox(28);
        page.setPadding(new Insets(40, 80, 40, 80));
        page.setAlignment(Pos.TOP_CENTER);

        Label h  = lbl("About VCinema", "-fx-font-size:32px;-fx-font-weight:bold;-fx-text-fill:#ffffff;");
        Label sh = lbl("Your premium cinema booking experience", "-fx-font-size:14px;-fx-text-fill:#aaaaaa;");

        HBox infoCards = new HBox(18,
            aboutCard("🎬", "Our Mission",
                "Delivering exceptional cinema experiences — blockbusters, classics, and everything in between."),
            aboutCard("🌍", "Our Vision",
                "Movies bring people together. We believe great stories can change the world."),
            aboutCard("🤝", "Our Team",
                "Our dedicated team works tirelessly to make every visit enjoyable from start to finish.")
        );
        infoCards.setAlignment(Pos.CENTER);

        VBox contactCard = card(28, 22);
        contactCard.setMaxWidth(480);
        contactCard.getChildren().addAll(
            lbl("📬  Contact Us", "-fx-font-size:15px;-fx-font-weight:bold;-fx-text-fill:#ffffff;"),
            lbl("✉  ameermujeeb@gmail.com", "-fx-text-fill:#ffffff;-fx-font-size:14px;"),
            lbl("📞  +962 777 898 911", "-fx-text-fill:#ffffff;-fx-font-size:14px;"),
            lbl("📍  Amman, Jordan", "-fx-text-fill:#aaaaaa;-fx-font-size:13px;")
        );

        page.getChildren().addAll(h, sh, sep(), infoCards, contactCard);

        ScrollPane sp = new ScrollPane(page);
        sp.setFitToWidth(true);
        sp.setStyle("-fx-background-color:#0a0a0a;-fx-background:transparent;");
        root.setCenter(sp);

        primaryStage.setScene(new Scene(root, stageW, stageH));
    }

    private VBox aboutCard(String icon, String title, String text) {
        VBox c = new VBox(10);
        c.setPadding(new Insets(22));
        c.setPrefWidth(270);
        c.setStyle("-fx-background-color:#1a1a1a;" +
            "-fx-background-radius:12;-fx-border-color:#2a2a2a;-fx-border-radius:12;");
        Label ic  = new Label(icon);
        ic.setStyle("-fx-font-size:26px;");
        Label ttl = lbl(title, "-fx-font-size:14px;-fx-font-weight:bold;-fx-text-fill:#ffffff;");
        Label txt = lbl(text, "-fx-font-size:12px;-fx-text-fill:#aaaaaa;");
        txt.setWrapText(true);
        c.getChildren().addAll(ic, ttl, txt);
        return c;
    }

    
    //  7. SETTINGS

    private void showSettings() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color:#0a0a0a;");
        root.setTop(buildNav("settings"));

        VBox page = new VBox(24);
        page.setPadding(new Insets(30, 80, 40, 80));
        page.setAlignment(Pos.TOP_CENTER);

        Label h = lbl("⚙  Settings", "-fx-font-size:26px;-fx-font-weight:bold;-fx-text-fill:#ffffff;");

        User u = me();

        // Profile card
        VBox profileCard = card(28, 22);
        profileCard.setMaxWidth(540);
        Label pHead = lbl("👤  My Profile", "-fx-font-size:15px;-fx-font-weight:bold;-fx-text-fill:#ffffff;");

        TextField tfName  = field(u.name); tfName.setText(u.name);
        TextField tfPhone = field("+962 ..."); tfPhone.setText(u.phone);

        Label emailDisp = lbl("✉  " + u.email, "-fx-text-fill:#555555;-fx-font-size:12px;-fx-padding:4 0;");
        Label profMsg   = lbl("", "-fx-font-size:12px;-fx-min-height:16;");

        Button btnSaveProf = new Button("Save Profile");
        btnSaveProf.setStyle(S_BTN_PRI);
        hover(btnSaveProf, S_BTN_PRI, S_BTN_PRI_H);
        btnSaveProf.setOnAction(e -> {
            String n = tfName.getText().trim();
            if (n.length() < 2) {
                profMsg.setStyle("-fx-text-fill:#cc0000;-fx-font-size:12px;");
                profMsg.setText("⚠ Name too short");
                return;
            }
            u.name  = n;
            u.phone = tfPhone.getText().trim();
            profMsg.setStyle("-fx-text-fill:#27ae60;-fx-font-size:12px;");
            profMsg.setText("✔ Profile updated");
            toastSuccess("Profile Updated", "Your display name has been saved.");
        });

        profileCard.getChildren().addAll(pHead, sep(),
            lbl("Display Name", "-fx-text-fill:#aaaaaa;-fx-font-size:12px;"), tfName,
            lbl("Phone Number", "-fx-text-fill:#aaaaaa;-fx-font-size:12px;"), tfPhone,
            emailDisp, profMsg, btnSaveProf);

        // Change password card
        VBox passCard = card(28, 22);
        passCard.setMaxWidth(540);
        Label passHead = lbl("🔑  Change Password", "-fx-font-size:15px;-fx-font-weight:bold;-fx-text-fill:#ffffff;");

        PasswordField pfOld  = pfield("Current password");
        PasswordField pfNew  = pfield("New password");
        PasswordField pfConf = pfield("Confirm new password");

        Label passMsg = lbl("", "-fx-font-size:12px;-fx-min-height:16;");

        Button btnPass = new Button("Update Password");
        btnPass.setStyle(S_BTN_PRI);
        hover(btnPass, S_BTN_PRI, S_BTN_PRI_H);
        btnPass.setOnAction(e -> {
            if (!pfOld.getText().equals(u.password)) {
                passMsg.setStyle("-fx-text-fill:#cc0000;-fx-font-size:12px;");
                passMsg.setText("✖ Current password incorrect");
                return;
            }
            if (pfNew.getText().length() < 6) {
                passMsg.setStyle("-fx-text-fill:#cc0000;-fx-font-size:12px;");
                passMsg.setText("⚠ Password min 6 characters");
                return;
            }
            if (!pfNew.getText().equals(pfConf.getText())) {
                passMsg.setStyle("-fx-text-fill:#cc0000;-fx-font-size:12px;");
                passMsg.setText("✖ Passwords don't match");
                return;
            }
            u.password = pfNew.getText();
            pfOld.clear(); pfNew.clear(); pfConf.clear();
            passMsg.setStyle("-fx-text-fill:#27ae60;-fx-font-size:12px;");
            passMsg.setText("✔ Password changed");
            toastSuccess("Password Updated", "Your password has been changed.");
        });

        passCard.getChildren().addAll(passHead, sep(),
            lbl("Current Password", "-fx-text-fill:#aaaaaa;-fx-font-size:12px;"), pfOld,
            lbl("New Password", "-fx-text-fill:#aaaaaa;-fx-font-size:12px;"), pfNew,
            lbl("Confirm New Password", "-fx-text-fill:#aaaaaa;-fx-font-size:12px;"), pfConf,
            passMsg, btnPass);

        // Booking history card
        VBox histCard = card(28, 22);
        histCard.setMaxWidth(540);
        Label histHead = lbl("🎟  My Booking History",
            "-fx-font-size:15px;-fx-font-weight:bold;-fx-text-fill:#ffffff;");
        histCard.getChildren().add(histHead);

        List<Booking> myBk = bookings.stream()
            .filter(b -> b.userEmail.equals(sessionEmail)).collect(Collectors.toList());

        if (myBk.isEmpty()) {
            histCard.getChildren().add(lbl("No bookings yet.",
                "-fx-text-fill:#555555;-fx-font-size:13px;"));
        } else {
            for (Booking b : myBk) {
                VBox bInfo = new VBox(4);
                bInfo.setPadding(new Insets(10));
                bInfo.setStyle("-fx-background-color:#0d0d0d;-fx-background-radius:8;");
                bInfo.getChildren().addAll(
                    lbl("🎬 " + b.movieTitle, "-fx-text-fill:#ffffff;-fx-font-size:13px;-fx-font-weight:bold;"),
                    lbl("📅 " + b.date + "  🕐 " + b.time, "-fx-text-fill:#aaaaaa;-fx-font-size:11px;"),
                    lbl("🎟 " + b.tickets + " tickets  |  💵 $" + b.totalPrice + "  |  " + b.payMethod,
                        "-fx-text-fill:#f5c518;-fx-font-size:11px;")
                );
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


    private void showAdmin() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color:#0a0a0a;");
        root.setTop(buildNav("admin"));

        HBox tabBar = new HBox(8);
        tabBar.setPadding(new Insets(14, 24, 14, 24));
        tabBar.setStyle("-fx-background-color:#111111;" +
            "-fx-border-color:#2a2a2a;-fx-border-width:0 0 1 0;");

        ToggleGroup tg = new ToggleGroup();
     
        ToggleButton tbMovies = adminTab("🎬  Movie Management", tg, false);
        tabBar.getChildren().addAll( tbMovies);

        StackPane content = new StackPane();
        VBox moviesPane = buildMoviesPanel();
        content.getChildren().addAll(moviesPane);
        moviesPane.setVisible(false);
        moviesPane.setManaged(false);

       
        tbMovies.setOnAction(e -> {
            moviesPane.setVisible(true);  moviesPane.setManaged(true);
          
        });

        VBox body = new VBox(tabBar, content);
        VBox.setVgrow(content, Priority.ALWAYS);
        root.setCenter(body);

        primaryStage.setScene(new Scene(root, stageW, stageH));
    }

    private ToggleButton adminTab(String text, ToggleGroup g, boolean sel) {
        ToggleButton tb = new ToggleButton(text);
        tb.setToggleGroup(g);
        tb.setSelected(sel);
        tb.setStyle(sel
            ? "-fx-background-color:#800000;-fx-text-fill:white;-fx-background-radius:8;-fx-cursor:hand;-fx-padding:8 20;-fx-font-weight:bold;"
            : "-fx-background-color:#1a1a1a;-fx-text-fill:#aaaaaa;-fx-background-radius:8;-fx-cursor:hand;-fx-padding:8 20;");
        tb.selectedProperty().addListener((o, ov, nv) ->
            tb.setStyle(nv
                ? "-fx-background-color:#800000;-fx-text-fill:white;-fx-background-radius:8;-fx-cursor:hand;-fx-padding:8 20;-fx-font-weight:bold;"
                : "-fx-background-color:#1a1a1a;-fx-text-fill:#aaaaaa;-fx-background-radius:8;-fx-cursor:hand;-fx-padding:8 20;"));
        return tb;
    }


    // ─── Movies Management Panel ──────────────────────────────────

    private VBox buildMoviesPanel() {
        HBox layout = new HBox(0);

        ObservableList<Movie> moviesObs = FXCollections.observableArrayList(movies);

        TableView<Movie> table = new TableView<>(moviesObs);
        table.setStyle("-fx-background-color:#111111;");
        table.setPrefWidth(440);

        TableColumn<Movie, String> cEmo   = col("",      m -> m.emoji,       36);
        TableColumn<Movie, String> cTitle = col("Title", m -> m.title,       190);
        TableColumn<Movie, String> cGenre = col("Genre", m -> m.genre,        90);
        TableColumn<Movie, String> cPri   = col("$",     m -> "$" + m.price,  50);
        TableColumn<Movie, String> cRat   = col("★",     m -> "" + m.rating,  50);
        table.getColumns().addAll(cEmo, cTitle, cGenre, cPri, cRat);

        Label tHead = lbl("🎬  All Movies (" + movies.size() + ")",
            "-fx-font-size:15px;-fx-font-weight:bold;-fx-text-fill:#ffffff;-fx-padding:0 0 10 0;");

        VBox tableBox = new VBox(10, tHead, table);
        tableBox.setPadding(new Insets(28));
        tableBox.setStyle("-fx-background-color:#0d0d0d;" +
            "-fx-border-color:#2a2a2a;-fx-border-width:0 1 0 0;");
        VBox.setVgrow(table, Priority.ALWAYS);

        VBox forms = new VBox(22);
        forms.setPadding(new Insets(28));
        forms.setStyle("-fx-background-color:#0a0a0a;");

        // ── Add movie form ──────────────────────────────────────
        VBox addCard = card(24, 18);
        Label aHead = lbl("➕  Add New Movie",
            "-fx-font-size:14px;-fx-font-weight:bold;-fx-text-fill:#ffffff;");

        TextField tfTitle  = field("Movie title");
        TextField tfGenre  = field("Genre");
        TextField tfDir    = field("Director");
        TextField tfCast   = field("Lead cast");
        TextField tfDesc   = field("Short description");
        TextField tfPrice  = field("Price (e.g. 12)");
        tfPrice.setStyle(S_FIELD + "-fx-pref-width:120px;");
        TextField tfRating = field("Rating (e.g. 8.5)");
        tfRating.setStyle(S_FIELD + "-fx-pref-width:120px;");
        TextField tfTimes  = field("Show times: 16:00,19:30,22:00");

        // Poster picker
        String[] chosenPosterPath = {""};
        ImageView previewIV = new ImageView();
        previewIV.setFitWidth(80);
        previewIV.setFitHeight(110);
        previewIV.setPreserveRatio(false);

        StackPane previewBox = new StackPane();
        previewBox.setPrefSize(80, 110);
        previewBox.setStyle("-fx-background-color:#0d0d0d;-fx-background-radius:6;" +
            "-fx-border-color:#2a2a2a;-fx-border-radius:6;");
        Label previewPlaceholder = new Label("🖼");
        previewPlaceholder.setStyle("-fx-font-size:28px;");
        previewBox.getChildren().addAll(previewPlaceholder, previewIV);

        Button btnBrowse = new Button("📁  Choose Poster Image");
        btnBrowse.setStyle(S_BTN_SEC + "-fx-pref-width:200;");
        hover(btnBrowse, S_BTN_SEC + "-fx-pref-width:200;", S_BTN_SEC_H + "-fx-pref-width:200;");

        Label posterLbl = lbl("No image selected", "-fx-text-fill:#555555;-fx-font-size:11px;");

        btnBrowse.setOnAction(ev -> {
            FileChooser fc = new FileChooser();
            fc.setTitle("Choose Movie Poster");
            fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files",
                "*.jpg", "*.jpeg", "*.png", "*.gif", "*.webp", "*.bmp"));
            File chosen = fc.showOpenDialog(primaryStage);
            if (chosen != null) {
                chosenPosterPath[0] = chosen.getAbsolutePath();
                posterLbl.setText(chosen.getName());
                posterLbl.setStyle("-fx-text-fill:#27ae60;-fx-font-size:11px;");
                try {
                    Image img = new Image(chosen.toURI().toString(), 80, 110, false, true);
                    previewIV.setImage(img);
                    previewBox.getChildren().remove(previewPlaceholder);
                } catch (Exception ignored) {}
            }
        });

        HBox posterRow = new HBox(12, previewBox,
            new VBox(8,
                lbl("Poster Image", "-fx-text-fill:#aaaaaa;-fx-font-size:12px;"),
                btnBrowse, posterLbl,
                lbl("JPG / PNG / WEBP", "-fx-text-fill:#555555;-fx-font-size:10px;")));
        posterRow.setAlignment(Pos.CENTER_LEFT);

        Label addMsg = lbl("", "-fx-font-size:12px;-fx-min-height:16;");
        addMsg.setWrapText(true);

        Button btnAdd = new Button("Add Movie");
        btnAdd.setStyle(S_BTN_PRI);
        hover(btnAdd, S_BTN_PRI, S_BTN_PRI_H);

        btnAdd.setOnAction(ev -> {
            String t  = tfTitle.getText().trim();
            String g  = tfGenre.getText().trim();
            String di = tfDir.getText().trim();
            String ca = tfCast.getText().trim();
            String ds = tfDesc.getText().trim();
            String pr = tfPrice.getText().trim();
            String ra = tfRating.getText().trim();
            String tm = tfTimes.getText().trim();

            if (t.isEmpty() || g.isEmpty() || di.isEmpty() || ca.isEmpty()
                    || ds.isEmpty() || pr.isEmpty() || ra.isEmpty() || tm.isEmpty()) {
                addMsg.setStyle("-fx-text-fill:#cc0000;-fx-font-size:12px;");
                addMsg.setText("⚠ Fill in all fields");
                return;
            }

            int price; double rating;
            try { price = Integer.parseInt(pr); }
            catch (Exception e) {
                addMsg.setStyle("-fx-text-fill:#cc0000;-fx-font-size:12px;");
                addMsg.setText("✖ Price must be a number");
                return;
            }
            try { rating = Double.parseDouble(ra); }
            catch (Exception e) {
                addMsg.setStyle("-fx-text-fill:#cc0000;-fx-font-size:12px;");
                addMsg.setText("✖ Rating must be a number (e.g. 8.5)");
                return;
            }

            String[] timesArr = Arrays.stream(tm.split(","))
                .map(String::trim).filter(s -> !s.isEmpty()).toArray(String[]::new);

            String id = "m" + (movies.size() + 1);
            Movie newM = new Movie(id, t, "🎬", ds, g, di, ca, price, rating, timesArr);
            newM.posterPath = chosenPosterPath[0];
            movies.add(newM);
            moviesObs.setAll(movies);
            tHead.setText("🎬  All Movies (" + movies.size() + ")");
            addMsg.setStyle("-fx-text-fill:#27ae60;-fx-font-size:12px;");
            addMsg.setText("✔ Movie added: " + t);

            for (TextField f : new TextField[]{tfTitle, tfGenre, tfDir, tfCast, tfDesc, tfPrice, tfRating, tfTimes})
                f.clear();
            chosenPosterPath[0] = "";
            previewIV.setImage(null);
            if (!previewBox.getChildren().contains(previewPlaceholder))
                previewBox.getChildren().add(0, previewPlaceholder);
            posterLbl.setText("No image selected");
            posterLbl.setStyle("-fx-text-fill:#555555;-fx-font-size:11px;");
            toastSuccess("Movie Added", "\"" + t + "\" is now in the catalog.");
        });

        GridPane fieldGrid = new GridPane();
        fieldGrid.setHgap(10);
        fieldGrid.setVgap(8);
        fieldGrid.addRow(0, lbl("Title",      "-fx-text-fill:#aaaaaa;-fx-font-size:12px;"), tfTitle);
        fieldGrid.addRow(1, lbl("Genre",      "-fx-text-fill:#aaaaaa;-fx-font-size:12px;"), tfGenre);
        fieldGrid.addRow(2, lbl("Director",   "-fx-text-fill:#aaaaaa;-fx-font-size:12px;"), tfDir);
        fieldGrid.addRow(3, lbl("Cast",       "-fx-text-fill:#aaaaaa;-fx-font-size:12px;"), tfCast);
        fieldGrid.addRow(4, lbl("Description","-fx-text-fill:#aaaaaa;-fx-font-size:12px;"), tfDesc);
        fieldGrid.addRow(5, lbl("Price ($)",  "-fx-text-fill:#aaaaaa;-fx-font-size:12px;"), tfPrice);
        fieldGrid.addRow(6, lbl("Rating",     "-fx-text-fill:#aaaaaa;-fx-font-size:12px;"), tfRating);
        fieldGrid.addRow(7, lbl("Showtimes",  "-fx-text-fill:#aaaaaa;-fx-font-size:12px;"), tfTimes);

        addCard.getChildren().addAll(aHead, sep(), posterRow, sep(), fieldGrid, addMsg, btnAdd);

        // ── Delete movie 
        VBox delCard = card(24, 18);
        Label dHead = lbl("🗑  Remove Movie",
            "-fx-font-size:14px;-fx-font-weight:bold;-fx-text-fill:#ff6b6b;");

        ComboBox<String> delCb = makeCombo(
            FXCollections.observableArrayList(
                movies.stream().map(m -> m.title).collect(Collectors.toList())), 280);

        Label delMsg = lbl("", "-fx-font-size:12px;-fx-min-height:16;");

        Button btnDel = new Button("🗑  Remove Movie");
        btnDel.setStyle(S_BTN_DANGER);
        hover(btnDel, S_BTN_DANGER,
            "-fx-background-color:#200000;-fx-text-fill:#ff8888;-fx-font-size:13px;" +
            "-fx-background-radius:8;-fx-cursor:hand;-fx-padding:10 24;" +
            "-fx-border-color:#cc0000;-fx-border-radius:8;");

        btnDel.setOnAction(ev -> {
            String sel = delCb.getValue();
            if (sel == null || sel.isEmpty()) {
                delMsg.setStyle("-fx-text-fill:#cc0000;-fx-font-size:12px;");
                delMsg.setText("⚠ Select a movie");
                return;
            }
            Movie target = movies.stream().filter(m -> m.title.equals(sel)).findFirst().orElse(null);
            if (target == null) {
                delMsg.setStyle("-fx-text-fill:#cc0000;-fx-font-size:12px;");
                delMsg.setText("✖ Not found");
                return;
            }
            movies.remove(target);
            moviesObs.setAll(movies);
            tHead.setText("🎬  All Movies (" + movies.size() + ")");
            delCb.setItems(FXCollections.observableArrayList(
                movies.stream().map(m -> m.title).collect(Collectors.toList())));
            delCb.setValue(null);
            delMsg.setStyle("-fx-text-fill:#27ae60;-fx-font-size:12px;");
            delMsg.setText("✔ Removed: " + sel);
            toastInfo("Movie Removed", "\"" + sel + "\" removed from catalog.");
        });

        delCard.getChildren().addAll(dHead, sep(),
            lbl("Movie Title", "-fx-text-fill:#aaaaaa;-fx-font-size:12px;"), delCb, delMsg, btnDel);

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

    // ─── Force Change Password 

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
        root.setStyle("-fx-background-color:#1a1a1a;" +
            "-fx-background-radius:16;-fx-border-color:#800000;" +
            "-fx-border-radius:16;-fx-border-width:2;");

        Label icon  = new Label("🔐");
        icon.setStyle("-fx-font-size:36px;");
        Label title = new Label("Set Your New Password");
        title.setStyle("-fx-font-size:18px;-fx-font-weight:bold;-fx-text-fill:#ffffff;");
        Label sub   = new Label("You're using a temporary password.\nPlease set a personal password to continue.");
        sub.setStyle("-fx-font-size:12px;-fx-text-fill:#aaaaaa;");
        sub.setWrapText(true);

        PasswordField pfNew  = pfield("At least 6 characters");
        PasswordField pfConf = pfield("Repeat password");

        Label errLbl = lbl("", "-fx-text-fill:#cc0000;-fx-font-size:12px;");

        Button btnSave = new Button("Set Password & Continue");
        btnSave.setStyle(S_BTN_PRI + "-fx-pref-width:240;");
        hover(btnSave, S_BTN_PRI + "-fx-pref-width:240;", S_BTN_PRI_H + "-fx-pref-width:240;");
        btnSave.setOnAction(e -> {
            String np = pfNew.getText();
            String nc = pfConf.getText();
            if (np.isEmpty() || nc.isEmpty()) { errLbl.setText("⚠ Fill in both fields"); return; }
            if (np.length() < 6) { errLbl.setText("⚠ Password min 6 characters"); return; }
            if (!np.equals(nc))  { errLbl.setText("✖ Passwords don't match"); return; }
            User u = me();
            if (u != null) {
                u.password = np;
                u.mustChangePassword = false;
            }
            dlg.close();
            toastSuccess("Password Set", "Welcome, " + myName() + "!");
        });

        root.getChildren().addAll(icon, title, sub, sep(),
            lbl("New Password", "-fx-text-fill:#aaaaaa;-fx-font-size:12px;"), pfNew,
            lbl("Confirm Password", "-fx-text-fill:#aaaaaa;-fx-font-size:12px;"), pfConf,
            errLbl, btnSave);

        Scene sc = new Scene(root);
        sc.setFill(Color.TRANSPARENT);
        dlg.setScene(sc);
        dlg.showAndWait();
    }

    // ─── Table column helper──────────────────────────────────────

    private <T> TableColumn<T, String> col(String name,
            java.util.function.Function<T, String> fn, double w) {
        TableColumn<T, String> c = new TableColumn<>(name);
        c.setCellValueFactory(d -> new SimpleStringProperty(fn.apply(d.getValue())));
        c.setPrefWidth(w);
        return c;
    }

  


    public static void main(String[] args) { launch(args); }
}
