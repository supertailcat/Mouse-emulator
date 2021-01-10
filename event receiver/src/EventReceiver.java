import java.awt.*;
import java.awt.event.InputEvent;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class EventReceiver {
    public static void main(String[] args) {
        try {
            ServerSocket serverSocket = new ServerSocket(8866);
            System.out.println("服务器已启动");
            while(true) {
                Socket socket = serverSocket.accept();
                System.out.println("手机连接成功");
                ReceiveThread receiveThread = new ReceiveThread(socket);
                Thread thread = new Thread(receiveThread);
                thread.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static class ReceiveThread implements Runnable {
        Socket socket;
        Boolean keepmoving = false;
        public ReceiveThread(Socket socket) {
            this.socket = socket;
        }
        @Override
        public void run() {
            try {
                Point point = null;
                Robot robot = new Robot();
                InputStream inputStream = socket.getInputStream();
                byte[] bytes = new byte[1024];
                int len = -1;
                String str = "";
                while (!socket.isClosed()) {
                    Thread.sleep(1);
                    len = inputStream.read(bytes);
                    if (len == -1)
                        continue;
                    str = new String(bytes, 0, len, "UTF-8");
                    switch (str) {
                        case "shutdown":
                            Runtime.getRuntime().exec("cmd /c Shutdown -s");
                            break;
                        case "lift click":
                            robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
                            robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
                            break;
                        case "right click":
                            robot.mousePress(InputEvent.BUTTON3_DOWN_MASK);
                            robot.mouseRelease(InputEvent.BUTTON3_DOWN_MASK);
                            break;
                        case "wheel click":
                            robot.mousePress(InputEvent.BUTTON2_DOWN_MASK);
                            robot.mouseRelease(InputEvent.BUTTON2_DOWN_MASK);
                        case "wheel up":
                            robot.mouseWheel(-1);
                            break;
                        case "wheel down":
                            robot.mouseWheel(1);
                            break;
                        case "up":
                            point = java.awt.MouseInfo.getPointerInfo().getLocation();
                            robot.mouseMove(point.x, point.y - 100);
                            break;
                        case "down":
                            point = java.awt.MouseInfo.getPointerInfo().getLocation();
                            robot.mouseMove(point.x, point.y + 100);
                            break;
                        case "left":
                            point = java.awt.MouseInfo.getPointerInfo().getLocation();
                            robot.mouseMove(point.x - 100, point.y);
                            break;
                        case "right":
                            point = java.awt.MouseInfo.getPointerInfo().getLocation();
                            robot.mouseMove(point.x + 100, point.y);
                            break;
                        case "keepup":
                            new Thread(new Runnable() {
                                Point point;
                                @Override
                                public void run() {
                                    keepmoving = true;
                                    while(keepmoving) {
                                        point = java.awt.MouseInfo.getPointerInfo().getLocation();
                                        robot.mouseMove(point.x, point.y - 1);
                                        robot.delay(15);
                                    }
                                }
                            }, "MovingThread").start();
                            break;
                        case "keepdown":
                            new Thread(new Runnable() {
                                Point point;
                                @Override
                                public void run() {
                                    keepmoving = true;
                                    while(keepmoving) {
                                        point = java.awt.MouseInfo.getPointerInfo().getLocation();
                                        robot.mouseMove(point.x, point.y + 1);
                                        robot.delay(15);
                                    }
                                }
                            }, "MovingThread").start();
                            break;
                        case "keepleft":
                            new Thread(new Runnable() {
                                Point point;
                                @Override
                                public void run() {
                                    keepmoving = true;
                                    while(keepmoving) {
                                        point = java.awt.MouseInfo.getPointerInfo().getLocation();
                                        robot.mouseMove(point.x - 1, point.y);
                                        robot.delay(15);
                                    }
                                }
                            }, "MovingThread").start();
                            break;
                        case "keepright":
                            new Thread(new Runnable() {
                                Point point;
                                @Override
                                public void run() {
                                    keepmoving = true;
                                    while(keepmoving) {
                                        point = java.awt.MouseInfo.getPointerInfo().getLocation();
                                        robot.mouseMove(point.x + 1, point.y);
                                        robot.delay(15);
                                    }
                                }
                            }, "MovingThread").start();
                            break;
                        case "stop":
                            keepmoving = false;
                            break;
                        case "close":
                            socket.close();
                            break;
                    }
                }
                System.out.println("手机断开连接");
            } catch (AWTException | IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}



