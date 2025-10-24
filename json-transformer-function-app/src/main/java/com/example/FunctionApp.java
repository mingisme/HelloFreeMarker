package com.example;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.gson.Gson;
import freemarker.template.*;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class FunctionApp {
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
            Map<String, Object> jsonData = gson.fromJson(jsonContent, Map.class);

            // Configure FreeMarker
            Configuration cfg = new Configuration(Configuration.VERSION_2_3_32);
            File templateFile = new File(templatePath).getAbsoluteFile();
            File templateDir = templateFile.getParentFile();
            if (templateDir == null) {
                templateDir = new File(".").getAbsoluteFile();
            }
            cfg.setDirectoryForTemplateLoading(templateDir);
            cfg.setDefaultEncoding("UTF-8");
            cfg.setNumberFormat("computer");

            // Create data model with custom functions
            Map<String, Object> dataModel = new HashMap<>();
            dataModel.put("data", jsonData);
            dataModel.put("lookup", new CacheLookupFunction());
            dataModel.put("convert", new UnitConversionFunction());
            dataModel.put("format", new FormatFunction());

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

    // Cache-based lookup function
    static class CacheLookupFunction implements TemplateMethodModelEx {
        private final LoadingCache<String, String> cache;

        public CacheLookupFunction() {
            this.cache = CacheBuilder.newBuilder()
                .maximumSize(100)
                .expireAfterWrite(10, TimeUnit.MINUTES)
                .build(new CacheLoader<String, String>() {
                    @Override
                    public String load(String key) {
                        // Simulate lookup logic
                        return lookupValue(key);
                    }
                });
        }

        private String lookupValue(String key) {
            // Example: device ID to name mapping
            Map<String, String> deviceNames = new HashMap<>();
            deviceNames.put("iot-sensor-001", "Temperature Sensor A");
            deviceNames.put("iot-sensor-002", "Humidity Sensor B");
            deviceNames.put("iot-sensor-003", "Pressure Sensor C");
            return deviceNames.getOrDefault(key, "Unknown Device");
        }

        @Override
        public Object exec(List arguments) throws TemplateModelException {
            if (arguments.size() != 1) {
                throw new TemplateModelException("lookup requires 1 argument: key");
            }
            
            String key = arguments.get(0).toString();
            try {
                return cache.get(key);
            } catch (Exception e) {
                throw new TemplateModelException("Lookup error: " + e.getMessage(), e);
            }
        }
    }

    // Unit conversion function
    static class UnitConversionFunction implements TemplateMethodModelEx {
        @Override
        public Object exec(List arguments) throws TemplateModelException {
            if (arguments.size() != 3) {
                throw new TemplateModelException("convert requires 3 arguments: value, fromUnit, toUnit");
            }
            
            double value = Double.parseDouble(arguments.get(0).toString());
            String fromUnit = arguments.get(1).toString();
            String toUnit = arguments.get(2).toString();
            
            // Temperature conversion
            if (fromUnit.equals("celsius") && toUnit.equals("fahrenheit")) {
                return value * 9.0 / 5.0 + 32;
            } else if (fromUnit.equals("fahrenheit") && toUnit.equals("celsius")) {
                return (value - 32) * 5.0 / 9.0;
            }
            
            return value;
        }
    }

    // Formatting function
    static class FormatFunction implements TemplateMethodModelEx {
        @Override
        public Object exec(List arguments) throws TemplateModelException {
            if (arguments.size() != 2) {
                throw new TemplateModelException("format requires 2 arguments: value, format");
            }
            
            String value = arguments.get(0).toString();
            String format = arguments.get(1).toString();
            
            switch (format) {
                case "uppercase":
                    return value.toUpperCase();
                case "lowercase":
                    return value.toLowerCase();
                case "capitalize":
                    return value.substring(0, 1).toUpperCase() + value.substring(1).toLowerCase();
                default:
                    return value;
            }
        }
    }
}
