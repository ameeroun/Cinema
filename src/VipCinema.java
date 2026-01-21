
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.*;

public class VipCinema extends Application {

    ListView<String> listView = new ListView<>();
    private static final String ADD_USER = "INSERT INTO user (User_ID, Username, Password) VALUES(?,?,?)";
    private static final String UPDATE_USER = "UPDATE user SET Password=? WHERE Username=?";
    private static final String DELETE_USER = "DELETE FROM user WHERE Username=?";

    public void start(Stage primaryStage) {
        
        
           BorderPane log1 = new BorderPane();
           
           
           //insid the border :
        FlowPane toplog1 = new FlowPane();
        GridPane centerlog1 = new GridPane();
        /////////////////////////////////////////
        
        Label topic = new Label("Cinema");
          topic.setStyle("-fx-font-size: 30px; -fx-text-fill: white;");


        Label us = new Label("User Name:");
        TextField t1 = new TextField();
    us.setStyle("-fx-font-size: 15px; -fx-text-fill: white;");

        Label pass= new Label("Password:");
        PasswordField tpass= new PasswordField();
        pass.setStyle("-fx-font-size: 15px; -fx-text-fill: white;");
        
         
        Button blogin= new Button("Log in");
        blogin.setPrefSize(80, 20);
        
        
        Button breset= new Button("Reset");
        breset.setPrefSize(80, 20);
        
        
          
           
       
        
    toplog1.getChildren().add(topic);
            toplog1.setAlignment(Pos.CENTER);
            toplog1.setStyle("-fx-background-color:#800000");
          toplog1.setPadding(new Insets(20, 20, 20, 20));
        
          log1.setTop(toplog1);

        centerlog1.add(us, 0, 0);
        centerlog1.add(t1, 1, 0);
       centerlog1.add(pass, 0, 1);
        centerlog1.add(tpass, 1, 1);
        centerlog1.add(breset, 0, 2);
        centerlog1.add(blogin, 1, 2);

        centerlog1.setAlignment(Pos.CENTER);
        centerlog1.setVgap(20);
        centerlog1.setHgap(20);
        centerlog1.setStyle("-fx-background-color:#000000;");
        
        log1.setCenter(centerlog1);
      
       ///////////////////////////////////////////////////////////////////////////////////////////
       
       ///////////////////////////////////////             SCENE2                /////////////////////////
       
       /////////////////////////////////////////////////////////////////////////////////////////////
      
        
         BorderPane home = new BorderPane();
       
        
          Button moviesButton = new Button("🎞 View Movies");
          moviesButton.setStyle("-fx-background-color: #800000; -fx-text-fill: white; -fx-font-size: 19px; -fx-font-weight: bold;");
          
 

        Button settingsButton = new Button("⚙ Settings");
settingsButton.setStyle("-fx-background-color: #800000; -fx-text-fill: white; -fx-font-size: 19px; -fx-font-weight: bold;");


Button  AboutusbButton= new Button("About Us");
          AboutusbButton.setStyle("-fx-background-color: #800000; -fx-text-fill: white; -fx-font-size: 19px; -fx-font-weight: bold;");
          
          
 Button ReviewButton= new Button("Review");
          ReviewButton.setStyle("-fx-background-color: #800000; -fx-text-fill: white; -fx-font-size: 19px; -fx-font-weight: bold;");


        Button logoutButton = new Button("🚪 Logout");
          logoutButton.setStyle("-fx-background-color: #800000; -fx-text-fill: white; -fx-font-size: 19px; -fx-font-weight: bold;");
        
               
          
          
      
        
      ///// HBox add all menubox without logout////////////////////
HBox menuBox= new HBox(15);
menuBox.getChildren().addAll(moviesButton, settingsButton,AboutusbButton,ReviewButton);
menuBox.setAlignment(Pos.CENTER);
////////////////////////////////////////

BorderPane topBar = new BorderPane();   ////////////borderpane داخلي////////////////// عشان ينظم بين ال menubox  AND  logout

topBar.setCenter(menuBox);
topBar.setRight(logoutButton); //// loguout ight the border///////
topBar.setPadding(new Insets(20));
topBar.setStyle("-fx-background-color:#800000");



home.setTop(topBar);
home.setStyle("-fx-background-color: #000000");







   ///////Moviesssssssssssssssssssssssssssssssssssssssssssss///////////////////////////
  


        // Movie 1
        VBox card1 = new VBox(10);
        card1.setAlignment(Pos.CENTER);
        card1.setPadding(new Insets(10));
       

        ImageView poster1 = new ImageView(new Image("file:C:/Users/ThinKBooK/OneDrive/Desktop/term pro/Ameer Term/src/images/Fast Tokyo.jpeg"));
        poster1.setFitWidth(200);
        poster1.setFitHeight(270);

        Label name1 = new Label("Fast & Furious Toyko");
        name1.setStyle("-fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold;");

        Label desc1 = new Label("is about Dominic Toretto turning against his team after being blackmailed by a cyber-terrorist named Cipher. His crew must stop her and bring Dom back."+"");
        desc1.setWrapText(true);
        desc1.setMaxWidth(140);
        desc1.setStyle("-fx-text-fill: lightgray; -fx-font-size: 12px;");

        Button book1 = new Button("Book Now");
        book1.setStyle("-fx-background-color: red; -fx-text-fill: white;");
        book1.setOnAction(e -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Booking");
            alert.setHeaderText(null);
            alert.setContentText("You selected: Fast & Furious 8");
            alert.showAndWait();
        });
Label price1 = new Label("Ticket Price: $10");
price1.setStyle("-fx-text-fill: gold; -fx-font-size: 14px; -fx-font-weight: bold;");

       card1.getChildren().addAll(poster1, name1, desc1, price1, book1);


        // Movie 2
        VBox card2 = new VBox(10);
        card2.setAlignment(Pos.CENTER);
        card2.setPadding(new Insets(10));
     

        ImageView poster2 = new ImageView(new Image("file:C:/Users/ThinKBooK/OneDrive/Desktop/term pro/Ameer Term/src/images/Inseption.jpeg"));
        poster2.setFitWidth(200);
        poster2.setFitHeight(270);

        Label name2 = new Label("Inception");
        name2.setStyle("-fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold;");

        Label desc2 = new Label("A thief who enters dreams to steal secrets from the subconscious...");
        desc2.setWrapText(true);
        desc2.setMaxWidth(140);
        desc2.setStyle("-fx-text-fill: lightgray; -fx-font-size: 12px;");

        Button book2 = new Button("Book Now");
        book2.setStyle("-fx-background-color: red; -fx-text-fill: white;");
        book2.setOnAction(e -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Booking");
            alert.setHeaderText(null);
            alert.setContentText("You selected: Inception");
            alert.showAndWait();
        });

      Label price2 = new Label("Ticket Price: $12");
price2.setStyle("-fx-text-fill: gold; -fx-font-size: 14px; -fx-font-weight: bold;");

card2.getChildren().addAll(poster2, name2,  desc2,price2, book2);

        
        // Movie 3
        VBox card3 = new VBox(10);
        card3.setAlignment(Pos.CENTER);
        card3.setPadding(new Insets(10));
        

        ImageView poster3 = new ImageView(new Image("file:C:/Users/ThinKBooK/OneDrive/Desktop/term pro/Ameer Term/src/images/Interstellar.jpeg"));
         poster2.setFitWidth(200);                        
        poster2.setFitHeight(270);

        Label name3 = new Label("Interstellar");
        name3.setStyle("-fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold;");

        Label desc3 = new Label("A team of explorers travel through a wormhole to save humanity...");
        desc3.setWrapText(true);
        desc3.setMaxWidth(140);
        desc3.setStyle("-fx-text-fill: lightgray; -fx-font-size: 12px;");

        Button book3 = new Button("Book Now");
        book3.setStyle("-fx-background-color: red; -fx-text-fill: white;");
        book3.setOnAction(e -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Booking");
            alert.setHeaderText(null);
            alert.setContentText("You selected: Interstellar");
            alert.showAndWait();
        });
        
        
      Label price3 = new Label("Ticket Price: $15");
price3.setStyle("-fx-text-fill: gold; -fx-font-size: 14px; -fx-font-weight: bold;");


Button Back4= new Button("Back");
        Back4.setPrefSize(80, 20);
        
        
card3.getChildren().addAll(poster3, name3, desc3,price3, book3);





VBox card4 = new VBox(10);
card4.setAlignment(Pos.CENTER);
card4.setPadding(new Insets(10));

ImageView poster4 = new ImageView(new Image("file:C:/Users/ThinKBooK/OneDrive/Desktop/term pro/Ameer Term/src/images/Avatar.jpeg"));
poster4.setFitWidth(200);
poster4.setFitHeight(270);

Label name4 = new Label("Avatar");
name4.setStyle("-fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold;");

Label desc4 = new Label("A marine on an alien planet becomes torn between following orders and protecting his new home.");
desc4.setWrapText(true);
desc4.setMaxWidth(140);
desc4.setStyle("-fx-text-fill: lightgray; -fx-font-size: 12px;");

Label price4 = new Label("Ticket Price: $11");
price4.setStyle("-fx-text-fill: gold; -fx-font-size: 14px; -fx-font-weight: bold;");

Button book4 = new Button("Book Now");
book4.setStyle("-fx-background-color: red; -fx-text-fill: white;");
book4.setOnAction(e -> handleBooking("Avatar", 11));

card4.getChildren().addAll(poster4, name4, desc4, price4, book4);




VBox card5 = new VBox(10);
card5.setAlignment(Pos.CENTER);
card5.setPadding(new Insets(10));

ImageView poster5 = new ImageView(new Image("file:C:/Users/ThinKBooK/OneDrive/Desktop/term pro/Ameer Term/src/images/DarkKnight.jpeg"));
poster5.setFitWidth(200);
poster5.setFitHeight(270);

Label name5 = new Label("The Dark Knight");
name5.setStyle("-fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold;");

Label desc5 = new Label("Batman faces his greatest psychological and physical tests as he battles the Joker.");
desc5.setWrapText(true);
desc5.setMaxWidth(140);
desc5.setStyle("-fx-text-fill: lightgray; -fx-font-size: 12px;");

Label price5 = new Label("Ticket Price: $13");
price5.setStyle("-fx-text-fill: gold; -fx-font-size: 14px; -fx-font-weight: bold;");

Button book5 = new Button("Book Now");
book5.setStyle("-fx-background-color: red; -fx-text-fill: white;");
book5.setOnAction(e -> handleBooking("The Dark Knight", 13));

card5.getChildren().addAll(poster5, name5, desc5, price5, book5);


VBox card6 = new VBox(10);
card6.setAlignment(Pos.CENTER);
card6.setPadding(new Insets(10));

ImageView poster6 = new ImageView(new Image("file:C:/Users/ThinKBooK/OneDrive/Desktop/term pro/Ameer term/src/images/Titanic.jpeg"));
poster6.setFitWidth(200);                           
poster6.setFitHeight(270);

Label name6 = new Label("Titanic");
name6.setStyle("-fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold;");

Label desc6 = new Label("A young couple falls in love aboard the ill-fated RMS Titanic.");
desc6.setWrapText(true);
desc6.setMaxWidth(140);
desc6.setStyle("-fx-text-fill: lightgray; -fx-font-size: 12px;");

Label price6 = new Label("Ticket Price: $10");
price6.setStyle("-fx-text-fill: gold; -fx-font-size: 14px; -fx-font-weight: bold;");

Button book6 = new Button("Book Now");
book6.setStyle("-fx-background-color: red; -fx-text-fill: white;");
book6.setOnAction(e -> handleBooking("Titanic", 10));

card6.getChildren().addAll(poster6, name6, desc6, price6, book6);






// Card 7 - Joker
VBox card7 = new VBox(10);
card7.setAlignment(Pos.CENTER);
card7.setPadding(new Insets(10));
ImageView poster7 = new ImageView(new Image("file:C:/Users/ThinKBooK/OneDrive/Desktop/term pro/Ameer term/src/images/Joker.jpeg"));

poster7.setFitWidth(200);
poster7.setFitHeight(270);
Label name7 = new Label("Joker");
name7.setStyle("-fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold;");
Label desc7 = new Label("Arthur Fleck becomes the infamous criminal known as Joker.");
desc7.setWrapText(true);
desc7.setMaxWidth(140);
desc7.setStyle("-fx-text-fill: lightgray; -fx-font-size: 12px;");
Label price7 = new Label("Ticket Price: $10");
price7.setStyle("-fx-text-fill: gold; -fx-font-size: 14px; -fx-font-weight: bold;");
Button book7 = new Button("Book Now");
book7.setStyle("-fx-background-color: red; -fx-text-fill: white;");
book7.setOnAction(e -> handleBooking("Joker", 10));
card7.getChildren().addAll(poster7, name7, desc7, price7, book7);

// Card 8 - Spider-Man: No Way Home
VBox card8 = new VBox(10);
card8.setAlignment(Pos.CENTER);
card8.setPadding(new Insets(10));
ImageView poster8 = new ImageView(new Image("file:C:/Users/ThinKBooK/OneDrive/Desktop/term pro/Ameer term/src/images/SpiderMan No Way Home.jpeg"));

poster8.setFitWidth(200);
poster8.setFitHeight(270);
Label name8 = new Label("Spider-Man: No Way Home");
name8.setStyle("-fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold;");
Label desc8 = new Label("Peter Parker faces multiverse chaos and seeks help from Doctor Strange.");
desc8.setWrapText(true);
desc8.setMaxWidth(140);
desc8.setStyle("-fx-text-fill: lightgray; -fx-font-size: 12px;");
Label price8 = new Label("Ticket Price: $11");
price8.setStyle("-fx-text-fill: gold; -fx-font-size: 14px; -fx-font-weight: bold;");
Button book8 = new Button("Book Now");
book8.setStyle("-fx-background-color: red; -fx-text-fill: white;");
book8.setOnAction(e -> handleBooking("Spider-Man: No Way Home", 11));
card8.getChildren().addAll(poster8, name8, desc8, price8, book8);

// Card 9 - Oppenheimer
VBox card9 = new VBox(10);
card9.setAlignment(Pos.CENTER);
card9.setPadding(new Insets(10));
ImageView poster9 = new ImageView(new Image("file:C:/Users/ThinKBooK/OneDrive/Desktop/term pro/Ameer term/src/images/OppenHeimer.jpeg"));

poster9.setFitWidth(200);
poster9.setFitHeight(270);
Label name9 = new Label("Oppenheimer");
name9.setStyle("-fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold;");
Label desc9 = new Label("The story of the father of the atomic bomb, Robert Oppenheimer.");
desc9.setWrapText(true);
desc9.setMaxWidth(140);
desc9.setStyle("-fx-text-fill: lightgray; -fx-font-size: 12px;");
Label price9 = new Label("Ticket Price: $14");
price9.setStyle("-fx-text-fill: gold; -fx-font-size: 14px; -fx-font-weight: bold;");
Button book9 = new Button("Book Now");
book9.setStyle("-fx-background-color: red; -fx-text-fill: white;");
book9.setOnAction(e -> handleBooking("Oppenheimer", 14));
card9.getChildren().addAll(poster9, name9, desc9, price9, book9);

// Card 10 -Barbie
VBox card10 = new VBox(10);
card10.setAlignment(Pos.CENTER);
card10.setPadding(new Insets(10));
ImageView poster10 = new ImageView(new Image("file:C:/Users/ThinKBooK/OneDrive/Desktop/term pro/Ameer term/src/images/Barbie.jpeg"));

poster10.setFitWidth(200);
poster10.setFitHeight(270);
Label name10 = new Label("Barbie");
name10.setStyle("-fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold;");
Label desc10 = new Label("Barbie and Ken leave Barbie Land to explore the real world.");
desc10.setWrapText(true);
desc10.setMaxWidth(140);
desc10.setStyle("-fx-text-fill: lightgray; -fx-font-size: 12px;");
Label price10 = new Label("Ticket Price: $10");
price10.setStyle("-fx-text-fill: gold; -fx-font-size: 14px; -fx-font-weight: bold;");
Button book10 = new Button("Book Now");
book10.setStyle("-fx-background-color: red; -fx-text-fill: white;");
book10.setOnAction(e -> handleBooking("Barbie", 10));
card10.getChildren().addAll(poster10, name10, desc10, price10, book10);

// Card 11 - Home Alone 2
VBox card11 = new VBox(10);
card11.setAlignment(Pos.CENTER);
card11.setPadding(new Insets(10));
ImageView poster11 = new ImageView(new Image("file:C:/Users/ThinKBooK/OneDrive/Desktop/term pro/Ameer term/src/images/Home Alone 2.jpeg"));

poster11.setFitWidth(200);
poster11.setFitHeight(270);
Label name11 = new Label("Home Alone 2");
name11.setStyle("-fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold;");
Label desc11 = new Label("Kevin ends up in New York City and runs into the same burglars from last year.");
desc11.setWrapText(true);
desc11.setMaxWidth(140);
desc11.setStyle("-fx-text-fill: lightgray; -fx-font-size: 12px;");
Label price11 = new Label("Ticket Price: $9");
price11.setStyle("-fx-text-fill: gold; -fx-font-size: 14px; -fx-font-weight: bold;");
Button book11 = new Button("Book Now");
book11.setStyle("-fx-background-color: red; -fx-text-fill: white;");
book11.setOnAction(e -> handleBooking("Home Alone 2", 9));
card11.getChildren().addAll(poster11, name11, desc11, price11, book11);

// Card 12 - Frozen II
VBox card12 = new VBox(10);
card12.setAlignment(Pos.CENTER);
card12.setPadding(new Insets(10));
ImageView poster12 = new ImageView(new Image("file:C:/Users/ThinKBooK/OneDrive/Desktop/term pro/Ameer term/src/images/Frozen 2.jpeg"));

poster12.setFitWidth(200);
poster12.setFitHeight(270);
Label name12 = new Label("Frozen II");
name12.setStyle("-fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold;");
Label desc12 = new Label("Elsa journeys into the unknown to discover the source of her powers.");
desc12.setWrapText(true);
desc12.setMaxWidth(140);
desc12.setStyle("-fx-text-fill: lightgray; -fx-font-size: 12px;");
Label price12 = new Label("Ticket Price: $9");
price12.setStyle("-fx-text-fill: gold; -fx-font-size: 14px; -fx-font-weight: bold;");
Button book12 = new Button("Book Now");
book12.setStyle("-fx-background-color: red; -fx-text-fill: white;");
book12.setOnAction(e -> handleBooking("Frozen II", 9));
card12.getChildren().addAll(poster12, name12, desc12, price12, book12);

// Card 13 - Thor
VBox card13 = new VBox(10);
card13.setAlignment(Pos.CENTER);
card13.setPadding(new Insets(10));
ImageView poster13 = new ImageView(new Image("file:C:/Users/ThinKBooK/OneDrive/Desktop/term pro/Ameer term/src/images/Thor.jpeg"));

poster13.setFitWidth(200);
poster13.setFitHeight(270);
Label name13 = new Label("Thor");
name13.setStyle("-fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold;");
Label desc13 = new Label("The powerful but arrogant god Thor is cast out of Asgard to live among humans.");
desc13.setWrapText(true);
desc13.setMaxWidth(140);
desc13.setStyle("-fx-text-fill: lightgray; -fx-font-size: 12px;");
Label price13 = new Label("Ticket Price: $12");
price13.setStyle("-fx-text-fill: gold; -fx-font-size: 14px; -fx-font-weight: bold;");
Button book13 = new Button("Book Now");
book13.setStyle("-fx-background-color: red; -fx-text-fill: white;");
book13.setOnAction(e -> handleBooking("Thor", 12));
card13.getChildren().addAll(poster13, name13, desc13, price13, book13);


// Card 14 - John Wick
VBox card14 = new VBox(10);
card14.setAlignment(Pos.CENTER);
card14.setPadding(new Insets(10));
ImageView poster14 = new ImageView(new Image("file:C:/Users/ThinKBooK/OneDrive/Desktop/term pro/Ameer term/src/images/JohnWick.jpeg"));

poster14.setFitWidth(200);
poster14.setFitHeight(270);
Label name14 = new Label("John Wick");
name14.setStyle("-fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold;");
Label desc14 = new Label("Legendary assassin comes out of retirement for revenge.");
desc14.setWrapText(true);
desc14.setMaxWidth(140);
desc14.setStyle("-fx-text-fill: lightgray; -fx-font-size: 12px;");
Label price14 = new Label("Ticket Price: $13");
price14.setStyle("-fx-text-fill: gold; -fx-font-size: 14px; -fx-font-weight: bold;");
Button book14 = new Button("Book Now");
book14.setStyle("-fx-background-color: red; -fx-text-fill: white;");
book14.setOnAction(e -> handleBooking("John Wick", 13));
card14.getChildren().addAll(poster14, name14, desc14, price14, book14);

// Card 15 - The Kissing Both
VBox card15 = new VBox(10);
card15.setAlignment(Pos.CENTER);
card15.setPadding(new Insets(10));
ImageView poster15 = new ImageView(new Image("file:C:/Users/ThinKBooK/OneDrive/Desktop/term pro/Ameer term/src/images/The Kissing Booth.jpeg"));

poster15.setFitWidth(200);
poster15.setFitHeight(270);
Label name15 = new Label("The Kissing Booth");
name15.setStyle("-fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold;");
Label desc15 = new Label("A high school student finds herself face-to-face with her secret crush at a kissing booth.");
desc15.setWrapText(true);
desc15.setMaxWidth(140);
desc15.setStyle("-fx-text-fill: lightgray; -fx-font-size: 12px;");
Label price15 = new Label("Ticket Price: $10");
price15.setStyle("-fx-text-fill: gold; -fx-font-size: 14px; -fx-font-weight: bold;");
Button book15 = new Button("Book Now");
book15.setStyle("-fx-background-color: red; -fx-text-fill: white;");
book15.setOnAction(e -> handleBooking("The Kissing Booth", 10));
card15.getChildren().addAll(poster15, name15, desc15, price15, book15);




/////نجمعهم كلهمGrid pane ب 
GridPane movies = new GridPane();
movies.setAlignment(Pos.CENTER);
movies.setPadding(new Insets(30));
movies.setHgap(30);
movies.setVgap(30);
movies.setStyle("-fx-background-color: #1a1a1a;");


movies.add(card1, 0, 0); 
movies.add(card2, 1, 0); 
movies.add(card3, 2, 0); 
movies.add(card4, 3, 0); 
movies.add(card5, 4, 0); 



movies.add(card6, 0, 1); 
movies.add(card7, 1, 1); 
movies.add(card8, 2, 1); 
movies.add(card9, 3, 1); 
movies.add(card10, 4, 1); 




movies.add(card11, 0, 2); 
movies.add(card12, 1, 2); 
movies.add(card13, 2, 2); 
movies.add(card14, 3, 2); 
movies.add(card15, 4, 2);








ScrollPane scrollPane = new ScrollPane();
scrollPane.setContent(movies);
scrollPane.setFitToWidth(true);
scrollPane.setStyle("-fx-background-color: #1a1a1a;");


        
        
        BorderPane  movieslayout =new  BorderPane();
        
       movieslayout.setCenter(scrollPane);
        movieslayout.setRight(listView);
     movieslayout.setBottom(Back4);
     Back4.setStyle("-fx-background-color: #800000; -fx-text-fill: white;");

        
        
        
/////settings//////////////
         GridPane settings = new GridPane();

        Label Updatetext = new Label("UBDATE YOUR Password : ");
        Updatetext.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: white;");
        
        Label usernamelabel = new Label("USER NAME: ");
        usernamelabel.setStyle("-fx-font-size: 15px; -fx-text-fill: white;");
        
        Label newpassword = new Label("NEW PASSWORD: ");
  newpassword.setStyle("-fx-font-size: 15px; -fx-text-fill: white;");

        TextField usernametext1 = new TextField();
        TextField newpasswordtext = new TextField();
      

     
        Button delete = new Button("Delete");
         delete.setPrefSize(80, 20);
        
        Button Update= new Button("Update");
         Update.setPrefSize(80, 20);
        
        
        Button Back2= new Button("Back");
        Back2.setPrefSize(80, 20);
        
  
    

        settings.add(Updatetext, 0, 0);
        settings.add(usernamelabel, 0, 1);
        settings.add(usernametext1, 1, 1);
        settings.add(newpassword, 0, 2);
        settings.add(newpasswordtext, 1, 2);
        settings.add(delete, 0, 3);
        settings.add(Update, 0,4);
        settings.add(Back2, 0,5);
        
        settings.setStyle("-fx-background-color:#211f1f;");

        
        settings.setAlignment(Pos.CENTER);
        settings.setVgap(10);
        
        
        
          ///About US//////////////////////////////////////////////////////////////////////////////
  GridPane aboutus = new GridPane();
        Label abt = new Label("About Us");
       Label abt2 = new Label("Welcome to cinema!\n\n"
                + "At Our Application, We are passionate about delivering an exceptional movie-going experience. \n" + "Founded in [Year], our mission is to provide a comfortable and enjoyable environment where film lovers can immerse themselves in the magic of cinema.\n\n"
                + "We aim to be the premier destination for movie enthusiasts by offering the latest blockbusters, timeless classics, and everything in between.\n" + " We believe that movies are more than just entertainment; they are an experience that brings people together, evokes emotions, and tells powerful stories.\n\n"
                + "Our friendly and dedicated team is here to ensure your visit is enjoyable from start to finish.\n" + "If you have any questions or need assistance, don't hesitate to reach out to any of our staff members.\n\n"
                + "If you have any questions or feedback, feel free to contact us at:\n"
                
                + "-Email : ameermujeeb@gmail.com\n"
                + "-phoneNo : 0777898911");
        abt.setStyle("-fx-font-weight : normal;");
        abt2.setStyle("-fx-text-fill: white;");
        abt.setStyle("-fx-font-size: 30px; -fx-text-fill: white;");
        aboutus.setAlignment(Pos.CENTER);
        
         Button Back3= new Button("Back");
        Back3.setPrefSize(80, 20);
        
        abt.setAlignment(Pos.CENTER);
        aboutus.add(abt, 0, 0);
        aboutus.add(abt2, 0, 1);
        aboutus.add(Back3, 1, 2);
        aboutus.setStyle("-fx-background-color:#000000");

       
        
 //reviwe
        
        GridPane reviwe= new GridPane();
        
        
        Label plz = new Label("Please enter your review\n");
        plz.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: white;");
        
        Label username1 = new Label("Username: ");   
        TextField usernametext = new TextField();
        username1.setStyle("-fx-font-size: 15px; -fx-text-fill: white;");
        
        
        
        Label feedback1 = new Label("Feedback: ");
        TextArea feedbacktext = new TextArea();
        feedback1.setStyle("-fx-font-size: 15px; -fx-text-fill: white;");
        

        
        
        
       
        
        Button submit= new Button("Submit");
        submit.setPrefSize(80, 20);
        
        Button Back= new Button("Back");
        Back.setPrefSize(80, 20);
        
        
        reviwe.setVgap(10);
        reviwe.setAlignment(Pos.CENTER);
        reviwe.add(plz, 0, 0);
        reviwe.add(username1, 0, 1);
        reviwe.add(usernametext, 1, 1);
        reviwe.add(feedback1, 0, 2);
        reviwe.add(feedbacktext, 1, 2);
        reviwe.add(submit, 1, 3);
        reviwe.add(Back,1,4);
        reviwe.setStyle("-fx-background-color:#211f1f;");
        
    
        
        
        
  
    

    

 
        
        
        
        
        
       

       
        /////Scene////////////////////////////////////////////////////////////////////////////////////////////
        Scene s1 = new Scene(log1, 850, 650);
        Scene s2 = new Scene(home);
        Scene s3 = new Scene(reviwe);
        Scene s4 = new Scene(settings);
        Scene s5= new Scene(aboutus);
        Scene s6= new Scene(movieslayout);
        
     
  
         primaryStage.setHeight(650);
        primaryStage.setWidth(850);
        primaryStage.setScene(s1);
        primaryStage.setTitle("Log in");
        primaryStage.show();
        
        

        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
          //////////////// LogIn//////////////////////////////////////////////////////////////////////////////
        blogin.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (t1.getText().isEmpty() || tpass.getText().isEmpty()) {
                    Alert msg = new Alert(Alert.AlertType.ERROR, "Please fill in the text fields");
                    msg.show();
                } else {
                    try {
                        Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/ameer oun", "root", "");
                        
                        Statement st = con.createStatement();
                        
                        ResultSet result = st.executeQuery("SELECT * FROM user");
                        String usernm = null;
                        String userPassword = null;
                        boolean found = false;
                        while (result.next()) {
                            usernm = result.getString("Username");
                            userPassword = result.getString("Password");
                            if (usernm.equals(t1.getText()) && userPassword.equals(tpass.getText())) {
                                found = true;
                           
                                break;
                            }
                        }
                        if (found) {
                            primaryStage.setScene(s2);
                        } else {
                            Alert msg = new Alert(Alert.AlertType.ERROR, "Invalid username or password");
                            msg.show();
                        }
                    } catch (Exception e) {
                        Alert msg = new Alert(Alert.AlertType.ERROR, "Error: " + e);
                        msg.show();
                    }
                }
            }
        });
        

        
        
        /////////////Reset Button////////////////////////////////////
        breset.setOnAction(e -> {
         
            t1.setText("");
            tpass.setText("");
        });
        
        
        
        
        
        
        
        //////Movies////////////////////
       
        
        
        moviesButton.setOnMouseEntered(e->{
        moviesButton.setStyle("-fx-background-color: #d9534f; -fx-text-fill: white; -fx-font-size: 19px; -fx-font-weight: bold;");
    });
        moviesButton.setOnMouseExited(e->{
              moviesButton.setStyle("-fx-background-color:  #800000; -fx-text-fill: white; -fx-font-size: 19px; -fx-font-weight: bold;");
        });
        
        moviesButton.setOnAction(e->{
        primaryStage.setScene(s6);
        });
        
            Back4.setOnAction(e->{
        primaryStage.setScene(s2);
    });
    

            book1.setOnAction(e->handleBooking("Fast & Furious Tokyo", 10));
   book2.setOnAction(e -> handleBooking("Inception", 12));
book3.setOnAction(e -> handleBooking("Interstellar", 15));

        
        
        
      
        
        ///Settings
        
        
        settingsButton.setOnMouseEntered(e->{
            settingsButton.setStyle("-fx-background-color: #d9534f; -fx-text-fill: white; -fx-font-size: 19px; -fx-font-weight: bold;");
        });
         settingsButton.setOnMouseExited(e->{
             settingsButton.setStyle("-fx-background-color: #800000; -fx-text-fill: white; -fx-font-size: 19px; -fx-font-weight: bold;");
        });
        
        settingsButton.setOnAction(e->{
          primaryStage.setScene(s4);
        });
        
        //////setting update//////////////////////
    
    Update.setOnAction(new EventHandler<ActionEvent>() {
    @Override
    public void handle(ActionEvent event) {
        if (usernametext1.getText().isEmpty() || newpasswordtext.getText().isEmpty()) {
            Alert msg = new Alert(Alert.AlertType.ERROR, "Please fill in the text fields");
            msg.show();
        } else {
            try {
                Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/ameer oun", "root", "");

                // Check if user exists
                PreparedStatement checkUser = con.prepareStatement("SELECT * FROM user WHERE username = ?");
                checkUser.setString(1, usernametext1.getText());
                ResultSet rs = checkUser.executeQuery();

                if (rs.next()) {
                    // User exists, update password
                    PreparedStatement st = con.prepareStatement(UPDATE_USER);
                    st.setString(1, newpasswordtext.getText());
                    st.setString(2, usernametext1.getText());
                    st.executeUpdate();

                    Alert msg = new Alert(Alert.AlertType.INFORMATION, "Password is updated");
                    msg.show();
                } else {
                    Alert msg = new Alert(Alert.AlertType.ERROR, "User not found");
                    msg.show();
                }

            } catch (Exception e) {
                Alert msg = new Alert(Alert.AlertType.ERROR, "Error: " + e.getMessage());
                msg.show();
            }
        }
    }
});

     
delete.setOnAction(new EventHandler<ActionEvent>() {
    @Override
    public void handle(ActionEvent event) {
        if (usernametext1.getText().isEmpty()) {
            Alert msg = new Alert(Alert.AlertType.ERROR, "Please fill in the text fields");
            msg.show();
        } else {
            try {
                Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/ameer oun", "root", "");

                // Check if user exists
                PreparedStatement checkUser = con.prepareStatement("SELECT * FROM user WHERE username = ?");
                checkUser.setString(1, usernametext1.getText());
                ResultSet rs = checkUser.executeQuery();

                if (rs.next()) {
                    // User exists, delete
                    PreparedStatement st = con.prepareStatement(DELETE_USER);
                    st.setString(1, usernametext1.getText());
                    st.executeUpdate();
                    primaryStage.setScene(s1);

                    Alert msg = new Alert(Alert.AlertType.INFORMATION, "One user deleted");
                    msg.show();
                } else {
                    Alert msg = new Alert(Alert.AlertType.ERROR, "User not found");
                    msg.show();
                }

            } catch (Exception e) {
                Alert msg = new Alert(Alert.AlertType.ERROR, "Error: " + e.getMessage());
                msg.show();
            }
        }
    }
});

        
      
      
  
    Back2.setOnAction(e->{
        primaryStage.setScene(s2);
    });
    
    
    
    
    
    
    
    
    
        ////Review//////////////
        
         ReviewButton.setOnMouseEntered(e->{
            ReviewButton.setStyle("-fx-background-color: #d9534f; -fx-text-fill: white; -fx-font-size: 19px; -fx-font-weight: bold;");
        });
         ReviewButton.setOnMouseExited(e->{
              ReviewButton.setStyle("-fx-background-color: #800000; -fx-text-fill: white; -fx-font-size: 19px; -fx-font-weight: bold;");
        });
        
    ReviewButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
            primaryStage.setScene(s3);
                primaryStage.setTitle("review ");
          

            }
        });
    
    
    
  
    
    
    
    submit.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (usernametext.getText().isEmpty() || feedbacktext.getText().isEmpty()) {
                    Alert msg = new Alert(Alert.AlertType.ERROR, "Please fill in both the username and feedback fields");
                    msg.show();     
                } else {
                    try {
                        // 1-Connection
                        Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/ameer oun", "root", "");
                        // 2-PreparedStatement
                      PreparedStatement st = con.prepareStatement(
                        "INSERT INTO feedback (Username, Feedback) VALUES (?, ?)"
                        );
                      
                         st.setString(1, usernametext.getText());
                         st.setString(2, feedbacktext.getText());
                        

                      

                        // 3-Execute
                        st.execute();

                        Alert msg = new Alert(Alert.AlertType.INFORMATION, "Feedback submitted successfully");
                        msg.show();
                    } catch (Exception e) {
                        Alert msg = new Alert(Alert.AlertType.ERROR, "Error: " + e.getMessage());
                        msg.show();
                    }
                }
            }
        });

    Back.setOnAction(e->{
        primaryStage.setScene(s2);
    });
    
    
    
    
    
    
    
        
       
        
        ///About us
        
         AboutusbButton.setOnMouseEntered(e->{
            AboutusbButton.setStyle("-fx-background-color: #d9534f; -fx-text-fill: white; -fx-font-size: 19px; -fx-font-weight: bold;");
        });
         AboutusbButton.setOnMouseExited(e->{
              AboutusbButton.setStyle("-fx-background-color: #800000; -fx-text-fill: white; -fx-font-size: 19px; -fx-font-weight: bold;");
        });
        
        AboutusbButton.setOnAction(e->{
           primaryStage.setScene(s5);
        });
        
            Back3.setOnAction(e->{
        primaryStage.setScene(s2);
    });
    
       
            
            
            
        //Logout
                
         logoutButton.setOnMouseEntered(e->{
            logoutButton.setStyle("-fx-background-color: #d9534f; -fx-text-fill: white; -fx-font-size: 19px; -fx-font-weight: bold;");
        });
         logoutButton.setOnMouseExited(e->{
              logoutButton.setStyle("-fx-background-color: #800000; -fx-text-fill: white; -fx-font-size: 19px; -fx-font-weight: bold;");
        });
        
        logoutButton.setOnAction(e->{
            primaryStage.setScene(s1);
        });
        
        
        
        }
    
    
    
   

    public static void main(String[] args) {
        launch(args);
    }
    
    
    
    
        
   public void handleBooking(String movieName, int ticketPrice) {
    TextInputDialog dialog = new TextInputDialog();
    dialog.setTitle("Book Tickets");
    dialog.setHeaderText("Booking for: " + movieName);
    dialog.setContentText("Enter number of tickets:");

    dialog.showAndWait().ifPresent(input -> {
        try {
            int tickets = Integer.parseInt(input);
            if (tickets <= 0) throw new NumberFormatException();

            int totalPrice = tickets * ticketPrice;

           
            try {
                Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/ameer oun", "root", "");
                PreparedStatement st = con.prepareStatement(
                    "INSERT INTO tickets ( movie_name, ticket_count, price_per_ticket, total_price) VALUES ( ?, ?, ?, ?)"
                );

           
                st.setString(1, movieName);
                st.setInt(2, tickets);
                st.setInt(3, ticketPrice);
                st.setInt(4, totalPrice);

                st.execute();
            } catch (Exception ex) {
                Alert dbError = new Alert(Alert.AlertType.ERROR, "Database Error: " + ex.getMessage());
                dbError.show();
                return;
            }
                                                                                                            
         
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Booking Confirmed");
            alert.setHeaderText(null);
            alert.setContentText(
                "Movie: " + movieName + "\n"
              + "Tickets: " + tickets + "\n"
              + "Ticket Price: $" + ticketPrice + "\n"
              + "Total Price: $" + totalPrice
            );
            alert.showAndWait();
            listView.getItems().add("Movie: " + movieName + " | Tickets: " + tickets + " | Total: $" + totalPrice);

        } catch (NumberFormatException e) {
            Alert error = new Alert(Alert.AlertType.ERROR, "Please enter a valid number of tickets.");
            error.showAndWait();
        }
    });
}


   
}
