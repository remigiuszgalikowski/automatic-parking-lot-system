import net.sf.javaanpr.imageanalysis.CarSnapshot;
import net.sf.javaanpr.intelligence.Intelligence;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

import net.sf.javaanpr.jar.Main;

public class PlateRecognizer {

    //Intelligence intel = new Intelligence();
    FileAdapter fileAdapter;

    public PlateRecognizer() throws ParserConfigurationException, IOException, SAXException {
        Main.systemLogic = new Intelligence();
        fileAdapter = new FileAdapter();
    }

    public String recognize(String path) throws IOException {
        CarSnapshot carSnap = fileAdapter.adapt(path);
        String numberPlate = Main.systemLogic.recognize(carSnap);
        return numberPlate;
    }
    public String recognize(BufferedImage img) {
        CarSnapshot carSnap = fileAdapter.adapt(img);
        String numberPlate = Main.systemLogic.recognize(carSnap);
        return numberPlate;
    }
    public String recognize(InputStream is) throws IOException {
        CarSnapshot carSnap = fileAdapter.adapt(is);
        String numberPlate = Main.systemLogic.recognize(carSnap);
        return numberPlate;
    }
}
