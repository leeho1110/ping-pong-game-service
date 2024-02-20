package quest.prography.lh.user.domain;

import java.util.Arrays;
import java.util.function.Predicate;

public enum UserStatus {
    ACTIVE(threshold -> threshold <= 30),
    WAIT(threshold -> threshold <= 60),
    NON_ACTIVE(threshold -> threshold > 60);

    private final Predicate<Long> isMatchedByRule;

    UserStatus(Predicate<Long> isMatchedByRule) {
        this.isMatchedByRule = isMatchedByRule;
    }

    public static UserStatus findStatusByRule(long fakerIdValue) {
        return Arrays.stream(values())
                .filter(status -> status.isMatchedByRule.test(fakerIdValue))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(String.format("Unsupported fakerId(%d).", fakerIdValue)));
    }

    public boolean isActive() {
        return this == ACTIVE;
    }

}
