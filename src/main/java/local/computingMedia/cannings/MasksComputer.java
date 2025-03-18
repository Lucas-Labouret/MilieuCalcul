package local.computingMedia.cannings;

import local.computingMedia.cannings.coords.tCoords.EvCoord;
import local.computingMedia.cannings.coords.tCoords.VeCoord;
import local.computingMedia.tLoci.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class MasksComputer {
    private final Canning canning;

    public MasksComputer(Canning canning) {
        this.canning = canning;
    }

    public ArrayList<Integer> computeEvVe() {
        canning.can();
        ArrayList<HashSet<EvCoord>> masks = new ArrayList<>(canning.getHeight());
        for (int i = 0; i < canning.getHeight(); i++) masks.add(new HashSet<>());

        HashMap<Ev, Ve> communication = canning.getEvVeCommunication();
        HashMap<Ev, EvCoord> evCoords = canning.getEvCanning();
        HashMap<Ve, VeCoord> veCoords = canning.getVeCanning();

        for (Ev ev : communication.keySet()) {
            Ve ve = communication.get(ev);
            VeCoord veCoord = veCoords.get(ve);
            EvCoord evCoord = evCoords.get(ev);

            int maskLine = veCoord.vertex().Y();
            masks.get(maskLine).add(new EvCoord(
                    evCoord.side(),
                    evCoord.edge().theta() - veCoord.theta(),
                    evCoord.edge().vertex().X() - veCoord.vertex().X(),
                    evCoord.edge().vertex().Y() - veCoord.vertex().Y()
            ));
        }

        ArrayList<Integer> results = new ArrayList<>();
        for (int i = 0; i < canning.getHeight(); i++) results.add(masks.get(i).size());

        return results;
    }
}
