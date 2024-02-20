package quest.prography.lh.game.event;

import java.time.Instant;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;
import quest.prography.lh.game.domain.event.GameStartEvent;
import quest.prography.lh.player.domain.repository.PlayerRepository;
import quest.prography.lh.room.domain.Room;
import quest.prography.lh.room.domain.repository.RoomRepository;

@Component
public class GameStartEventListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(GameStartEventListener.class);
    private static final int GAME_RUNNING_TIME = 60;

    private final TaskScheduler taskScheduler;
    private final RoomRepository roomRepository;
    private final PlayerRepository playerRepository;

    public GameStartEventListener(TaskScheduler taskScheduler, RoomRepository roomRepository, PlayerRepository playerRepository) {
        this.taskScheduler = taskScheduler;
        this.roomRepository = roomRepository;
        this.playerRepository = playerRepository;
    }

    @TransactionalEventListener
    public void handleGameStartEvent(GameStartEvent event) {
        LOGGER.info("GameStartEvent received: {}", event);
        scheduleGameFinishAfter(event, GAME_RUNNING_TIME);
    }

    private void scheduleGameFinishAfter(GameStartEvent event, int progressTime) {
        taskScheduler.schedule(() -> finishGameCallBack(event.roomId()), Instant.now().plusSeconds(progressTime));
    }

    private void finishGameCallBack(long gameId) {
        Optional<Room> maybeExistGame = roomRepository.findById(gameId);
        maybeExistGame.ifPresent(game -> {
            LOGGER.info("Start Game finish: {}", game);
            game.finish();
            updateGameStatus(game);
            quitAllPlayer(game);
            LOGGER.info("End Game finish: {}", game);
        });
    }

    private void quitAllPlayer(Room game) {
        playerRepository.deleteAllByRoomId(game.id());
    }

    private void updateGameStatus(Room game) {
        roomRepository.save(game);
    }
}
