package local.computingMedia.cannings.simulatedAnnealing;

import local.computingMedia.cannings.Canning;

@FunctionalInterface
public interface RandomNeighborGenerator {
    Canning generate(Canning canning);
}
