package quest.prography.lh.user.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import quest.prography.lh.user.domain.repository.UserRepository;
import quest.prography.lh.user.service.output.UserFindOutput;

@Service
public class UserQueryService {

    private final UserRepository userRepository;

    public UserQueryService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Page<UserFindOutput> findUser(Pageable pageable) {
        return userRepository.findAll(pageable).map(UserFindOutput::from);
    }
}
