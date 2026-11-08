public class App {
    public static void main(String[] args) throws Exception {
        SFClient.getInstance().readData();
        new AppGUI();
    }
}
