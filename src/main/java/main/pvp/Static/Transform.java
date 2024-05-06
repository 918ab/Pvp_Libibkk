package main.pvp.Static;

import org.bukkit.entity.Player;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class Transform {

    public static void PvpTransform(Integer TransformNum, String txtFilePath, String yamlOutputPath, Player player) {
        List<String> nicknames = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(txtFilePath))) {
            reader.readLine();

            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\t");
                if (parts.length >= 3) {
                    String nickname = parts[TransformNum-1].trim();
                    if (!nicknames.contains(nickname)) {
                        nicknames.add(nickname);
                    }
                }
            }

            Collections.shuffle(nicknames);

            Map<Integer, Map<Integer, String>> pairsMap = new LinkedHashMap<>();
            int numNicknames = nicknames.size();

            for (int i = 0; i < numNicknames; i += 2) {
                String nickname1 = nicknames.get(i);
                String nickname2 = (i + 1 < numNicknames) ? nicknames.get(i + 1) : "";

                Map<Integer, String> pair = new LinkedHashMap<>();
                pair.put(1, nickname1);
                pair.put(2, nickname2);

                pairsMap.put(i / 2 + 1, pair);
            }

            Map<String, Map<Integer, Map<Integer, String>>> yamlData = new LinkedHashMap<>();
            yamlData.put("daejeon", pairsMap);

            writeYamlToFile(yamlData, yamlOutputPath);

            player.sendMessage("§x§F§F§E§2§5§9P§x§F§F§C§5§5§5V§x§F§F§A§7§5§1P §f>> 파일변환 완료");
        } catch (IOException e) {
            System.err.println("Error occurred: " + e.getMessage());
            player.sendMessage("§cError 버킷을 확인해주세요");
            e.printStackTrace();
        }
    }

    private static void writeYamlToFile(Map<String, ?> data, String outputPath) throws IOException {
        DumperOptions options = new DumperOptions();
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);

        Yaml yaml = new Yaml(options);
        try (FileWriter writer = new FileWriter(outputPath)) {
            yaml.dump(data, writer);
        }
    }
}
