package controllers;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

public class FileOps {

    public static void main(String[] args) throws IOException {
        List<String> listOfFiles = getListOfFiles(System.getProperty("user.home"));
        Path fileList = Paths.get("files.list");
        System.out.println("scanned");
        Files.write(fileList, listOfFiles);
    }

    static List<String> getListOfFiles(String dir) throws IOException {
        return Files.walk(Paths.get(dir)).filter(Files::isRegularFile).map(Path::toString).collect(Collectors.toList());
    }


}
