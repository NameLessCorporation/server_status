package github.nameless;

import github.nameless.app.ConnectWindow;
import github.nameless.app.MainWindow;

public class Main {

    public static void main(String[] args) {
    	//new ConnectWindow("NameLess Server Starus - Connect", 520, 230);
	    new MainWindow("NameLess Server Status - Client", 900, 600, "192.168.0.208", "stdian");
    }
}
