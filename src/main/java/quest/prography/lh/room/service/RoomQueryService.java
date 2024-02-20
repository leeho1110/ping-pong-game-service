package quest.prography.lh.room.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import quest.prography.lh.room.domain.exception.RoomNotFoundExceptionGame;
import quest.prography.lh.room.domain.repository.RoomRepository;
import quest.prography.lh.room.service.output.RoomFindOutput;

@Transactional
@Service
public class RoomQueryService {

    private final RoomRepository roomRepository;

    public RoomQueryService(RoomRepository roomRepository) {
        this.roomRepository = roomRepository;
    }

    public Page<RoomFindOutput> findAllOfRoomBy(Pageable pageable) {
        return roomRepository.findAll(pageable).map(RoomFindOutput::from);
    }

    public RoomFindOutput findRoomBy(long roomId) {
        return RoomFindOutput.from(roomRepository.findById(roomId).orElseThrow(RoomNotFoundExceptionGame::new));
    }
}
