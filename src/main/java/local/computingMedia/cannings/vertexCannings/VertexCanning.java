package local.computingMedia.cannings.vertexCannings;

import local.computingMedia.cannings.coords.sCoords.VertexCoord;
import local.computingMedia.sLoci.Vertex;
import local.computingMedia.media.Medium;

import java.util.HashMap;

/**
 * A vertex canning is map from the vertices of a medium to a set of 2D coordinates.
 * This map must cover all vertices of the medium, and each vertex must have a unique coordinate.
 */
public interface VertexCanning {
    /**
     * Sets the medium for which this vertex canning will be applied.
     * @param medium The medium to set.
     */
    void setMedium(Medium medium);

    /**
     * Applies the canning algorithm to the medium, generating a mapping of vertices to coordinates.
     * This method should be called after setting the medium, and before retrieving the vertex canning.
     * <p>
     * Since the canning process may involve heavy computation, it is recommended to call this method only once
     * each time the medium is set or modified.
     */
    void can();

    /**
     * Returns the mapping of vertices to coordinates.
     * @return A map where keys are vertices and values are their corresponding coordinates.
     */
    HashMap<Vertex, VertexCoord> getVertexCanning();

    /**
     * @return The width of the canning, i.e. the length of the longest row of coordinates.
     */
    int getWidth();

    /**
     * @return The height of the canning, i.e. the length of the longest column of coordinates.
     */
    int getHeight();

    /**
     * @return The density of the canning, i.e. the ratio of the number of cells occupied by a vertex vs the total number of cells in the canning.
     */
    default double getDensity(){
        return getVertexCanning().size()/(double)(getHeight() * getWidth());
    }
}
