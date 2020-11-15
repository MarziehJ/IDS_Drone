package sample;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Controller {
    public TableView tableViewReceivedPackages;
    public TableColumn receivedColumnSend, receivedColumnFromPort,
            receivedColumnASCII, receivedColumnHEX;
    public Canvas canvas;

    public LineChart chartLocations;
    private GraphicsContext graphicsContext;

    private ObservableList<UdpPackage> receivedPackages = FXCollections.observableArrayList();
    private ObservableList<XYChart.Series> locationSeries = FXCollections.observableArrayList();
    private ObservableList<XYChart.Data> locationData = FXCollections.observableArrayList();
    private ObservableList<XYChart.Data> HeightData = FXCollections.observableArrayList();
    private UdpPackageReceiver receiver;
    private Drone drone;
    @FXML
    private TextField txtIPAddress;
    private TextField txtCommand;
    public void initialize() throws UnknownHostException {

        graphicsContext = canvas.getGraphicsContext2D();

        InitializeRecivedTableView();
        InitializeDrone();
        InitializeLineChart();

        receiver = new UdpPackageReceiver(receivedPackages, 8000);
        new Thread(receiver).start();

    }

    private void InitializeLineChart() {
        locationSeries.add(new XYChart.Series("Heights", HeightData));
        locationSeries.add(new XYChart.Series("Location", locationData));
        chartLocations.setData(locationSeries);
    }


    private void InitializeDrone() throws UnknownHostException {
        drone = new Drone();
        drone.drawShadowDrone(canvas);
        InetAddress my_localhost = InetAddress.getLocalHost();
        String ipAddress = (my_localhost.getHostAddress()).trim();
        txtIPAddress.setText(ipAddress);

    }

    private void InitializeRecivedTableView() {
        tableViewReceivedPackages.setItems(receivedPackages);

        //set columns content
        receivedColumnSend.setCellValueFactory(
                new PropertyValueFactory<UdpPackage, String>("fromIp")
        );
        receivedColumnFromPort.setCellValueFactory(
                new PropertyValueFactory<UdpPackage, String>("fromPort")
        );
        receivedColumnASCII.setCellValueFactory(
                new PropertyValueFactory<UdpPackage, String>("dataAsString")
        );
        receivedColumnHEX.setCellValueFactory(
                new PropertyValueFactory<UdpPackage, String>("dataAsHex")
        );

        //Set listener to the list for processing receiving messages
        tableViewReceivedPackages.getItems().addListener(new ListChangeListener() {
            @Override
            public void onChanged(Change change) {
                if (receivedPackages.size() > 0)
                    ProcessReceivedCommand(receivedPackages.get(receivedPackages.size() - 1));
            }
        });


    }

    private void ProcessReceivedCommand(UdpPackage receivedPackage) {
        if (receivedPackage.isCommand("init"))
            drone.initDrone(canvas);
        else if (receivedPackage.isCommand("takeoff"))
            drone.takeOff(canvas);
        else if (receivedPackage.isCommand("left"))
            drone.moveLeft(canvas);
        else if (receivedPackage.isCommand("right"))
            drone.moveRight(canvas);
        else if (receivedPackage.isCommand("top"))
            drone.moveTop(canvas);
        else if (receivedPackage.isCommand("down"))
            drone.moveDown(canvas);
        else if (receivedPackage.isCommand("stop"))
            drone.stop(canvas);

        UpdateLineChartData();

    }

    private void UpdateLineChartData() {
        final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
        Date now = new Date();
        HeightData.add(new XYChart.Data(simpleDateFormat.format(now), drone.getLocationInXY().getY()));
        locationData.add(new XYChart.Data(simpleDateFormat.format(now), drone.getLocationInXY().getX()));
    }


    public void actionClearLogs(ActionEvent actionEvent) {
        receivedPackages.clear();
        HeightData.clear();
        locationData.clear();
    }
}