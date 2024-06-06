package chesspuzzle.results;

import lombok.NonNull;
import util.JacksonHelper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a repository for game results stored in a JSON file.
 */
public class JsonGameResultManager implements GameResultRepo {

    private final Path filePath;

    /**
     * Constructs a new instance of the repository.
     *
     * @param filePath the path to the JSON file
     */
    public JsonGameResultManager(@NonNull Path filePath) {
        this.filePath = filePath;
    }

    /**
     * Adds a new game result to the repository.
     *
     * @param result the game result to add
     * @return the list of all game results
     * @throws IOException if an I/O error occurs
     */
    @Override
    public List<GameResult> add(@NonNull GameResult result) throws IOException {
        var results = getAll();
        results.add(result);
        try (var out = Files.newOutputStream(filePath)) {
            JacksonHelper.writeList(out, results);
        }
        return results;
    }

    /**
     * Retrieves all game results from the repository.
     *
     * @return the list of all game results
     * @throws IOException if an I/O error occurs
     */
    @Override
    public List<GameResult> getAll() throws IOException {
        if (!Files.exists(filePath)) {
            return new ArrayList<>();
        }
        try (var in = Files.newInputStream(filePath)) {
            return JacksonHelper.readList(in, GameResult.class);
        }
    }
}
