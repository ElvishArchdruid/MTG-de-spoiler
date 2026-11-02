public class App {
    public static void main(String[] args) throws Exception {
        System.out.println(SFClient.getInstance().getCard("Lightning Bolt").getName());
        System.out.println(SFClient.getInstance().getCard("lightning bolt").getName());
    }
}
