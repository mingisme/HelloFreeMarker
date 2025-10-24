package com.example;

import com.google.gson.Gson;
import com.jayway.jsonpath.JsonPath;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateMethodModelEx;
import freemarker.template.TemplateModelException;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JsonPathApp {
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

            // Configure FreeMarker
            Configuration cfg = new Configuration(Configuration.VERSION_2_3_32);
            File templateFile = new File(templatePath).getAbsoluteFile();
            File templateDir = templateFile.getParentFile();
            if (templateDir == null) {
                templateDir = new File(".").getAbsoluteFile();
            }
            cfg.setDirectoryForTemplateLoading(templateDir);
            cfg.setDefaultEncoding("UTF-8");

            // Create data model with JSONPath function
            Gson gson = new Gson();
            Map<String, Object> dataModel = new HashMap<>();
            dataModel.put("jsonPath", new JsonPathFunction(jsonContent, gson));

            // Load and process template
            Template template = cfg.getTemplate(templateFile.getName());
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

    // Custom FreeMarker function for JSONPath queries
    static class JsonPathFunction implements TemplateMethodModelEx {
        private final String jsonContent;
        private final Gson gson;

        public JsonPathFunction(String jsonContent, Gson gson) {
            this.jsonContent = jsonContent;
            this.gson = gson;
        }

        @Override
        public Object exec(List arguments) throws TemplateModelException {
            if (arguments.size() != 1) {
                throw new TemplateModelException("jsonPath requires exactly 1 argument: the JSONPath expression");
            }
            
            String path = arguments.get(0).toString();
            try {
                Object result = JsonPath.read(jsonContent, path);
                
                // Convert collections and maps to JSON strings for FreeMarker
                if (result instanceof List || result instanceof Map) {
                    return gson.toJson(result);
                }
                
                return result;
            } catch (Exception e) {
                throw new TemplateModelException("JSONPath error: " + e.getMessage(), e);
            }
        }
    }
}
