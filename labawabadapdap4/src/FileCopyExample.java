import java.io.IOException;
import java.nio.file.*;
import java.util.concurrent.*;
public class FileCopyExample {
    public static void copyFile(String source, String destination)
            throws IOException {
        Path sourcePath = Paths.get(source);
        Path destinationPath = Paths.get(destination);
        Files.copy(sourcePath, destinationPath,
                StandardCopyOption.REPLACE_EXISTING);

        System.out.println("Файл скопирован: " + sourcePath);
    }
    public static void main(String[] args) {
        String file1 = "file1.txt";
        String file2 = "file2.txt";

        String copy1 = "copy1.txt";
        String copy2 = "copy2.txt";

        try {
            System.out.println("=== Копирование одного файла ===");
            copyFile(file1, copy1);
            System.out.println("\n=== Последовательное копирование ===");
            long startSequential = System.nanoTime();

            copyFile(file1, copy1);
            copyFile(file2, copy2);

            long endSequential = System.nanoTime();
            double sequentialTime = (endSequential - startSequential) / 1_000_000.0;

            System.out.println("Время последовательного копирования: "
                    + sequentialTime + " мс");
            System.out.println("\n=== Параллельное копирование ===");

            ExecutorService executor =
                    Executors.newFixedThreadPool(2);

            long startParallel = System.nanoTime();
            Future<?> task1 = executor.submit(() -> {
                try {
                    copyFile(file1, copy1);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
            Future<?> task2 = executor.submit(() -> {
                try {
                    copyFile(file2, copy2);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
            task1.get();
            task2.get();
            long endParallel = System.nanoTime();
            executor.shutdown();
            double parallelTime = (endParallel - startParallel) / 1_000_000.0;
            System.out.println("Время параллельного копирования: "
                    + parallelTime + " мс");
        } catch (Exception e) {
            System.out.println("Ошибка: " + e.getMessage());
        }
    }
}