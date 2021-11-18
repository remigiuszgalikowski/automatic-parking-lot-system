import net.sf.javaanpr.imageanalysis.CarSnapshot;
import net.sf.javaanpr.intelligence.Intelligence;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;

public class PlateRecognizer {

    Intelligence intel = new Intelligence();

    public PlateRecognizer() throws ParserConfigurationException, IOException, SAXException {
    }

    public String recognize(CarSnapshot carSnap) {
        String numberPlate = intel.recognize(carSnap);
        return numberPlate;
    }
}
