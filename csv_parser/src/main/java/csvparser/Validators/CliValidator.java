package csvparser.Validators;

import java.nio.file.Files;
import java.nio.file.Path;

public final class CliValidator {

    public ValidatorErrors validateArgs(String[] args) {
        ValidatorErrors errors = new ValidatorErrors();

        if (args == null || args.length != 2) {
            errors.addError("Ошибка в аргументах! Используйте так: <Текстовый файл на ввод> <Файл, куда сохранять результат .csv>");
            return errors;
        }

        Path input = Path.of(args[0]);
        Path output = Path.of(args[1]);


        if (!Files.exists(input)) {
            errors.addError("Файл не существует: " + input);
        } else if (!Files.isRegularFile(input)) {
            errors.addError("Указанный путь не является текстовым файлом: " + input);
        } else if (!Files.isReadable(input)) {
            errors.addError("Нет прав на чтение файла: " + input);
        }


        if (Files.exists(output) && Files.isDirectory(output)) {
            errors.addError("Путь вывода указывает на директорию, а не на файл: " + output);
        }

        return errors;
    }
}
