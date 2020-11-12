package sample;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.paint.Color;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class Controller {
    public TableView tableViewReceivedPackages;
    public TableColumn receivedColumnSend, receivedColumnFromPort,
            receivedColumnASCII, receivedColumnHEX;
    public Canvas canvas;
    private GraphicsContext graphicsContext;

    private ObservableList<UdpPackage> receivedPackages = FXCollections.observableArrayList();
    private UdpPackageReceiver receiver;
    private Drone drone;
    @FXML
    private TextField txtIPAddress;

    public void initialize() throws UnknownHostException {

        graphicsContext = canvas.getGraphicsContext2D();

        InitializeRecivedTableView();
        InitializeDrone();

        //add udp server/reeciver
        receiver = new UdpPackageReceiver(receivedPackages, 8000);
        new Thread(receiver).start();

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


    }


    public void actionClearLogs(ActionEvent actionEvent) {
        receivedPackages.clear();
    }
}
