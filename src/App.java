public class App {
    public static void main(String[] args) throws Exception {
        Database.getInstance().importTaggings();
        //new AppGUI();
    }
}
