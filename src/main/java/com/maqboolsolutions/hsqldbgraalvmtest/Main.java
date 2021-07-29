package com.maqboolsolutions.hsqldbgraalvmtest;

import com.gluonhq.attach.storage.StorageService;
import com.gluonhq.attach.util.Platform;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.*;

public class Main extends Application {

    String DB_URL = "jdbc:hsqldb:file:" + getFile("HsqlDb Database/hsqldb-database") + ";crypt_key=C3ACDC4DA6A15C33BF2F54804C2EF281;crypt_type=AES;shutdown=true;hsqldb.lock_file=false";
    String DB_USER = "SA";
    String DB_PASSWORD = "";
    String DRIVER = "org.hsqldb.jdbc.JDBCDriver";

    @Override
    public void start(Stage pStage) {
        VBox root = new VBox(10);
        root.setAlignment(Pos.TOP_CENTER);
        root.setPadding(new Insets(10));

        Button btnCreateDb = new Button("Create / Test Database");

        TextArea txtPath = new TextArea();
        VBox.setVgrow(txtPath, Priority.ALWAYS);

        root.getChildren().addAll(btnCreateDb, txtPath);

        Scene scene;
        if (Platform.getCurrent().equals(Platform.ANDROID)) {
            Rectangle2D visualBounds = Screen.getPrimary().getVisualBounds();
            scene = new Scene(root, visualBounds.getWidth(), visualBounds.getHeight());
        } else {
            scene = new Scene(root);
        }

        pStage.setTitle("HSQLDb On GraalVM NativeImage Test!");
        pStage.setScene(scene);
        pStage.show();

        getFile("HsqlDb Database").mkdir();

        try (FileOutputStream outputStream = new FileOutputStream(getFile("HsqlDb Database/temp.txt"))) {
            outputStream.write("Hello Android.".getBytes());

            txtPath.appendText("File Created At:" + "\n" + getFile("HsqlDb Database/temp.txt").getAbsolutePath() + "\n" + "------------" + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }

        btnCreateDb.setOnAction((event) -> {
            Connection con = null;
            Statement stmt;

            try {
                Class.forName(DRIVER);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }

            try {
                con = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }

            txtPath.appendText("Database Created At:" + "\n" + getFile("HsqlDb Database/hsqldb-database").getAbsolutePath() + "\n" + "------------" + "\n");

            PreparedStatement ps;
            try {
                if (con != null) {
                    ps = con.prepareStatement("CREATE TABLE IF NOT EXISTS temp (msg VARCHAR(100))");
                    ps.executeUpdate();
                }
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }

            try {
                for (int i = 0; i < 5; i++) {
                    stmt = con.createStatement();

                    stmt.executeUpdate("INSERT INTO temp VALUES NOW()");
                }
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }

            ResultSet rs;
            try {
                stmt = con.createStatement();

                rs = stmt.executeQuery("SELECT * FROM temp");
                while (rs.next()) {
                    txtPath.appendText("Database Message: " + rs.getString(1) + "\n" + "------------" + "\n");
                }
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }

            try {
                con.close();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        });
    }

    public static void main(String[] args) {
        launch(args);
    }

    public static File getFile(String name) {
        String dir;

        if (Platform.isAndroid()) {
            dir = "Document";
        } else {
            dir = "Documents";
        }

        File path = null;
        try {
            path = StorageService.create()
                    .flatMap(s -> s.getPublicStorage(dir))
                    .orElseThrow(() -> new FileNotFoundException("Could not access: " + dir));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return new File(path, name);
    }

}
