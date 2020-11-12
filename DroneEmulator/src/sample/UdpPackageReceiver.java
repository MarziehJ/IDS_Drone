package sample;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.List;

public class UdpPackageReceiver implements Runnable {

    boolean running = false;
    DatagramSocket socket;

    int port;

    List udpPackages;

    public UdpPackageReceiver(List udpPackages, int port) {
        this.running = true;
        this.udpPackages = udpPackages;
        this.port = port;
        try {
            socket = new DatagramSocket(port);
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    public void shutDown() {
        running = false;
    }

    @Override
    public void run() {
        while (running) {
            byte[] buf = new byte[256];
            DatagramPacket packet = new DatagramPacket(buf, buf.length);
            try {
                socket.receive(packet);
                System.out.println("package arrived!");
                UdpPackage udpPackage = new UdpPackage("name", packet.getData(),
                        packet.getAddress(),
                        socket.getLocalAddress(),
                        packet.getPort(),
                        socket.getLocalPort());
                udpPackages.add(udpPackage);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
