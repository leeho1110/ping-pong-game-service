package quest.prography.lh.common.domain;

import java.util.regex.Pattern;
import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class Email {

    private final String REGEX = "^[A-Za-z0-9._+-]+@[A-Za-z0-9.-]+.[A-Za-z]{2,6}(_suspended)?$";
    private final Pattern PATTERN = Pattern.compile(REGEX);

    @Column(name = "email")
    private String value;

    protected Email() {
    }

    public Email(String value) {
        if (!validate(value)) {
            throw new IllegalArgumentException(String.format("Invalid email address(%s).", value));
        }
        this.value = value;
    }

    public String value() {
        return value;
    }

    private boolean validate(String value){
        return PATTERN.matcher(value).find();
    }
}
