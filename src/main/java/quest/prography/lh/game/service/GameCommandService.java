package quest.prography.lh.game.service;

import java.util.List;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import quest.prography.lh.game.domain.event.GameStartEvent;
import quest.prography.lh.game.external.FakeApiRetrieveUserResponse;
import quest.prography.lh.game.external.FakeApiSpecParser;
import quest.prography.lh.game.external.RandomFakeUserRetrieveClient;
import quest.prography.lh.game.service.input.GameInitiateInput;
import quest.prography.lh.game.service.input.GameStartInput;
import quest.prography.lh.room.domain.Room;
import quest.prography.lh.room.domain.exception.RoomNotFoundExceptionGame;
import quest.prography.lh.room.domain.repository.RoomRepository;
import quest.prography.lh.user.domain.User;
import quest.prography.lh.user.domain.exception.UserNotFoundExceptionGame;
import quest.prography.lh.user.domain.repository.UserRepository;
import quest.prography.lh.player.domain.Player;
import quest.prography.lh.player.domain.repository.PlayerRepository;

@Transactional
@Service
public class GameCommandService {

    private static final String KOREA_LOCALE = "ko_KR";

    private final RandomFakeUserRetrieveClient randomFakeUserRetrieveClient;
    private final FakeApiSpecParser fakeApiUserSpecParser;
    private final UserRepository userRepository;
    private final RoomRepository roomRepository;
    private final PlayerRepository playerRepository;
    private final ApplicationEventPublisher eventPublisher;

    public GameCommandService(
            RandomFakeUserRetrieveClient randomFakeUserRetrieveClient,
            FakeApiSpecParser fakeApiUserSpecParser,
            UserRepository userRepository,
            RoomRepository roomRepository,
            PlayerRepository playerRepository,
            ApplicationEventPublisher eventPublisher) {
        this.randomFakeUserRetrieveClient = randomFakeUserRetrieveClient;
        this.fakeApiUserSpecParser = fakeApiUserSpecParser;
        this.userRepository = userRepository;
        this.roomRepository = roomRepository;
        this.playerRepository = playerRepository;
        this.eventPublisher = eventPublisher;
    }

    public void initiateUsers(final GameInitiateInput input) {
        var externalApiResponse = retrieveRandomFakedUsers(input);
        deleteAllOfDataRelatedToGame();
        userRepository.saveAll(convertToUserEntity(externalApiResponse));
    }

    public void startGame(final GameStartInput gameStartInput) {
        Room roomWannaStart = getRoomBy(gameStartInput.roomId());
        User maybeHost = getUserBy(gameStartInput.userId());

        roomWannaStart.validateUserIsHost(maybeHost.id());
        roomWannaStart.validateStatusAbleToStart();

        List<Player> players = getAllPlayerByRoom(roomWannaStart.id());
        roomWannaStart.fillPlayer(players);
        roomWannaStart.validatePlayerCountIsAbleToStart();

        roomWannaStart.startWithFinishAction((gameRoomId) -> eventPublisher.publishEvent(new GameStartEvent(gameRoomId)));
    }

    private void deleteAllOfDataRelatedToGame() {
        playerRepository.deleteAll();
        roomRepository.deleteAll();
        userRepository.deleteAll();
    }

    /**
     * <p>Faker API의 파라미터를 변경해보며 테스트한 결과, status, code에 대한 예외 케이스가 확인되지 않는다.</p>
     * <p>만약 외부 API 응답이 정상값 범주에서 벗어나는 케이스가 확인된다면 관련 예외 처리가 필요하다.</p>
     *
     * @author 이호
     */
    private FakeApiRetrieveUserResponse retrieveRandomFakedUsers(GameInitiateInput input) {
        return randomFakeUserRetrieveClient.retrieveRandomFakedUsers(input.seed(),
                input.quantity(), KOREA_LOCALE);
    }

    private Room getRoomBy(long roomId) {
        return roomRepository.findById(roomId).orElseThrow(RoomNotFoundExceptionGame::new);
    }

    private User getUserBy(long userId) {
        return userRepository.findById(userId).orElseThrow(UserNotFoundExceptionGame::new);
    }

    private List<User> convertToUserEntity(FakeApiRetrieveUserResponse response) {
        return fakeApiUserSpecParser.parse(response.data());
    }

    private List<Player> getAllPlayerByRoom(long roomId) {
        return playerRepository.findAllByRoomId(roomId);
    }
}
