package chesspuzzle.results;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;

public interface GameResultRepo {

    List<GameResult> add(GameResult result) throws IOException;

    List<GameResult> getAll() throws IOException;

    default List<GameResult> getBest(int limit) throws IOException {
        return getAll()
                .stream()
                .filter(GameResult::isSolved)
                .sorted(Comparator.comparingInt(GameResult::getSteps))
                .limit(limit)
                .toList();
    }

    static GameResultRepo getInstance() {
        return JsonGameResultManagerHolder.INSTANCE;
    }

    class JsonGameResultManagerHolder {
        private static final GameResultRepo INSTANCE = new JsonGameResultManager(Path.of("results.json"));
    }
}
