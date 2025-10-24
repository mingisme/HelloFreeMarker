package com.example;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import com.google.gson.Gson;
import com.google.gson.JsonElement;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;

public class App {
    public static void main(String[] args) {
        if (args.length != 3) {
            System.err.println("Usage: java -jar app.jar <template.ftl> <source.json> <target.json>");
            System.exit(1);
        }

        String templatePath = args[0];
        String sourceJsonPath = args[1];
        String targetJsonPath = args[2];

        try {
            // Read source JSON
            String jsonContent = new String(Files.readAllBytes(Paths.get(sourceJsonPath)));
            Gson gson = new Gson();
            JsonElement jsonElement = gson.fromJson(jsonContent, JsonElement.class);
            Map<String, Object> dataModel = gson.fromJson(jsonElement, Map.class);

            // Configure FreeMarker
            Configuration cfg = new Configuration(Configuration.VERSION_2_3_32);
            File templateFile = new File(templatePath).getAbsoluteFile();
            File templateDir = templateFile.getParentFile();
            if (templateDir == null) {
                templateDir = new File(".").getAbsoluteFile();
            }
            cfg.setDirectoryForTemplateLoading(templateDir);
            cfg.setDefaultEncoding("UTF-8");

            // Load template
            Template template = cfg.getTemplate(templateFile.getName());

            // Process template
            StringWriter writer = new StringWriter();
            template.process(dataModel, writer);

            // Write output
            Files.write(Paths.get(targetJsonPath), writer.toString().getBytes());
            
            System.out.println("Successfully transformed " + sourceJsonPath + " to " + targetJsonPath);

        } catch (IOException | TemplateException e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}
