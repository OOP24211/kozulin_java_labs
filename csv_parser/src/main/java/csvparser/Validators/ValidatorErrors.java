package csvparser.Validators;
import java.util.ArrayList;
import java.util.List;
public class ValidatorErrors {
    private final List<String> getErrors = new ArrayList<>();

    public void addError(String message) {
        getErrors.add(message);
    }

    public boolean hasErrors() {
        return !getErrors.isEmpty();
    }

    public List<String> errors() {
        return List.copyOf(getErrors);
    }

}
