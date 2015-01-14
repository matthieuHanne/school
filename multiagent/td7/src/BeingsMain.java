import sim.display.Console;

/**
 * Created with IntelliJ IDEA.
 * User: home
 * Date: 06/05/2014
 * Time: 11:27
 * To change this template use File | Settings | File Templates.
 */
public class BeingsMain {
    public static void main(String[] args) {
        runUI();
    }
    public static void runUI() {
        Beings model = new Beings(System.currentTimeMillis());
        BeingsWithUI gui = new BeingsWithUI(model);
        Console console = new Console(gui);
        console.setVisible(true);
    }
}
