package main.socket;

import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
	public static SocketHandler socketHandler = new SocketHandler();
    public static ExecutorService threadPool;
    public static ArrayList<Client> clients = new ArrayList<Client>();

    ServerSocket serverSocket;
    
    public void startServer(int port) {
        // ������Ǯ ����
        threadPool = Executors.newFixedThreadPool(
                Runtime.getRuntime().availableProcessors()
        );

        // ���� ���� ���� �� ���ε�
        try {
            serverSocket = new ServerSocket();
            serverSocket.bind(new InetSocketAddress(port));
        } catch (Exception e) {
            e.printStackTrace();
            if (!serverSocket.isClosed()) {
                stopServer();
            }
            return;
        }

        // ���� �۾� ����
        Runnable thread = new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        Socket socket = serverSocket.accept();
                        clients.add(new Client(socket));
                        System.out.println("[Ŭ���̾�Ʈ ����] "
                                + socket.getRemoteSocketAddress()
                                + ": " + Thread.currentThread().getName());
                    } catch (Exception e) {
                        if (!serverSocket.isClosed()) {
                            stopServer();
                        }
                        break;
                    }
                }
            }
        };
        threadPool = Executors.newCachedThreadPool();
        threadPool.submit(thread);
    }
    

    public void stopServer() {
        try {
            Iterator<Client> iterator = clients.iterator();
            while (iterator.hasNext()) {
                Client client = iterator.next();
                client.socket.close();
                iterator.remove();
            }
            // ���� ���� �ݱ�
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
            // ������Ǯ ����
            if (threadPool != null && !threadPool.isShutdown()) {
                threadPool.shutdown();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
}
