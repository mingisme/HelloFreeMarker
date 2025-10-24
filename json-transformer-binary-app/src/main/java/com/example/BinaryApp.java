package com.example;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class BinaryApp {
    public static void main(String[] args) {
        if (args.length != 3) {
            System.err.println("Usage: java -jar app.jar <template.ftl> <source.bin> <target.json>");
            System.exit(1);
        }

        String templatePath = args[0];
        String sourceBinaryPath = args[1];
        String targetJsonPath = args[2];

        try {
            // Read binary file
            byte[] binaryData = Files.readAllBytes(Paths.get(sourceBinaryPath));

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

            // Create data model with binary extraction functions
            Map<String, Object> dataModel = new HashMap<>();
            dataModel.put("binary", binaryData);
            dataModel.put("readInt", new ReadIntFunction(binaryData));
            dataModel.put("readLong", new ReadLongFunction(binaryData));
            dataModel.put("readFloat", new ReadFloatFunction(binaryData));
            dataModel.put("readByte", new ReadByteFunction(binaryData));
            dataModel.put("readBit", new ReadBitFunction(binaryData));
            dataModel.put("readString", new ReadStringFunction(binaryData));
            dataModel.put("readShort", new ReadShortFunction(binaryData));
            dataModel.put("readDouble", new ReadDoubleFunction(binaryData));
            dataModel.put("readUnsignedInt", new ReadUnsignedIntFunction(binaryData));
            dataModel.put("readIntBE", new ReadIntBEFunction(binaryData));
            dataModel.put("readLongBE", new ReadLongBEFunction(binaryData));
            dataModel.put("readFloatBE", new ReadFloatBEFunction(binaryData));
            dataModel.put("readBytes", new ReadBytesFunction(binaryData));
            dataModel.put("readHex", new ReadHexFunction(binaryData));
            dataModel.put("readBoolean", new ReadBooleanFunction(binaryData));

            // Load and process template
            Template template = cfg.getTemplate(templateFile.getName());
            StringWriter writer = new StringWriter();
            template.process(dataModel, writer);

            // Write output
            Files.write(Paths.get(targetJsonPath), writer.toString().getBytes());
            
            System.out.println("Successfully transformed " + sourceBinaryPath + " to " + targetJsonPath);

        } catch (IOException | TemplateException e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }

    // Read int32 from binary at offset
    static class ReadIntFunction implements freemarker.template.TemplateMethodModelEx {
        private final byte[] data;
        
        public ReadIntFunction(byte[] data) {
            this.data = data;
        }
        
        @Override
        public Object exec(java.util.List arguments) throws freemarker.template.TemplateModelException {
            if (arguments.size() != 1) {
                throw new freemarker.template.TemplateModelException("readInt requires 1 argument: offset");
            }
            int offset = Integer.parseInt(arguments.get(0).toString());
            return ByteBuffer.wrap(data, offset, 4).order(ByteOrder.LITTLE_ENDIAN).getInt();
        }
    }
    
    // Read int64 from binary at offset
    static class ReadLongFunction implements freemarker.template.TemplateMethodModelEx {
        private final byte[] data;
        
        public ReadLongFunction(byte[] data) {
            this.data = data;
        }
        
        @Override
        public Object exec(java.util.List arguments) throws freemarker.template.TemplateModelException {
            if (arguments.size() != 1) {
                throw new freemarker.template.TemplateModelException("readLong requires 1 argument: offset");
            }
            int offset = Integer.parseInt(arguments.get(0).toString());
            return ByteBuffer.wrap(data, offset, 8).order(ByteOrder.LITTLE_ENDIAN).getLong();
        }
    }
    
    // Read float32 from binary at offset
    static class ReadFloatFunction implements freemarker.template.TemplateMethodModelEx {
        private final byte[] data;
        
        public ReadFloatFunction(byte[] data) {
            this.data = data;
        }
        
        @Override
        public Object exec(java.util.List arguments) throws freemarker.template.TemplateModelException {
            if (arguments.size() != 1) {
                throw new freemarker.template.TemplateModelException("readFloat requires 1 argument: offset");
            }
            int offset = Integer.parseInt(arguments.get(0).toString());
            return ByteBuffer.wrap(data, offset, 4).order(ByteOrder.LITTLE_ENDIAN).getFloat();
        }
    }
    
    // Read unsigned byte from binary at offset
    static class ReadByteFunction implements freemarker.template.TemplateMethodModelEx {
        private final byte[] data;
        
        public ReadByteFunction(byte[] data) {
            this.data = data;
        }
        
        @Override
        public Object exec(java.util.List arguments) throws freemarker.template.TemplateModelException {
            if (arguments.size() != 1) {
                throw new freemarker.template.TemplateModelException("readByte requires 1 argument: offset");
            }
            int offset = Integer.parseInt(arguments.get(0).toString());
            return data[offset] & 0xFF;
        }
    }
    
    // Read bit from byte at offset
    static class ReadBitFunction implements freemarker.template.TemplateMethodModelEx {
        private final byte[] data;
        
        public ReadBitFunction(byte[] data) {
            this.data = data;
        }
        
        @Override
        public Object exec(java.util.List arguments) throws freemarker.template.TemplateModelException {
            if (arguments.size() != 2) {
                throw new freemarker.template.TemplateModelException("readBit requires 2 arguments: offset, bitPosition");
            }
            int offset = Integer.parseInt(arguments.get(0).toString());
            int bitPos = Integer.parseInt(arguments.get(1).toString());
            return ((data[offset] & 0xFF) & (1 << bitPos)) != 0;
        }
    }
    
    // Read string from binary at offset
    static class ReadStringFunction implements freemarker.template.TemplateMethodModelEx {
        private final byte[] data;
        
        public ReadStringFunction(byte[] data) {
            this.data = data;
        }
        
        @Override
        public Object exec(java.util.List arguments) throws freemarker.template.TemplateModelException {
            if (arguments.size() < 2 || arguments.size() > 3) {
                throw new freemarker.template.TemplateModelException("readString requires 2-3 arguments: offset, length, [encoding]");
            }
            int offset = Integer.parseInt(arguments.get(0).toString());
            int length = Integer.parseInt(arguments.get(1).toString());
            String encoding = arguments.size() == 3 ? arguments.get(2).toString() : "UTF-8";
            
            try {
                return new String(data, offset, length, encoding).trim();
            } catch (Exception e) {
                throw new freemarker.template.TemplateModelException("Error reading string: " + e.getMessage(), e);
            }
        }
    }
    
    // Read int16 from binary at offset
    static class ReadShortFunction implements freemarker.template.TemplateMethodModelEx {
        private final byte[] data;
        
        public ReadShortFunction(byte[] data) {
            this.data = data;
        }
        
        @Override
        public Object exec(java.util.List arguments) throws freemarker.template.TemplateModelException {
            if (arguments.size() != 1) {
                throw new freemarker.template.TemplateModelException("readShort requires 1 argument: offset");
            }
            int offset = Integer.parseInt(arguments.get(0).toString());
            return ByteBuffer.wrap(data, offset, 2).order(ByteOrder.LITTLE_ENDIAN).getShort();
        }
    }
    
    // Read float64 from binary at offset
    static class ReadDoubleFunction implements freemarker.template.TemplateMethodModelEx {
        private final byte[] data;
        
        public ReadDoubleFunction(byte[] data) {
            this.data = data;
        }
        
        @Override
        public Object exec(java.util.List arguments) throws freemarker.template.TemplateModelException {
            if (arguments.size() != 1) {
                throw new freemarker.template.TemplateModelException("readDouble requires 1 argument: offset");
            }
            int offset = Integer.parseInt(arguments.get(0).toString());
            return ByteBuffer.wrap(data, offset, 8).order(ByteOrder.LITTLE_ENDIAN).getDouble();
        }
    }
    
    // Read unsigned int32 from binary at offset
    static class ReadUnsignedIntFunction implements freemarker.template.TemplateMethodModelEx {
        private final byte[] data;
        
        public ReadUnsignedIntFunction(byte[] data) {
            this.data = data;
        }
        
        @Override
        public Object exec(java.util.List arguments) throws freemarker.template.TemplateModelException {
            if (arguments.size() != 1) {
                throw new freemarker.template.TemplateModelException("readUnsignedInt requires 1 argument: offset");
            }
            int offset = Integer.parseInt(arguments.get(0).toString());
            return ByteBuffer.wrap(data, offset, 4).order(ByteOrder.LITTLE_ENDIAN).getInt() & 0xFFFFFFFFL;
        }
    }
    
    // Read int32 big-endian from binary at offset
    static class ReadIntBEFunction implements freemarker.template.TemplateMethodModelEx {
        private final byte[] data;
        
        public ReadIntBEFunction(byte[] data) {
            this.data = data;
        }
        
        @Override
        public Object exec(java.util.List arguments) throws freemarker.template.TemplateModelException {
            if (arguments.size() != 1) {
                throw new freemarker.template.TemplateModelException("readIntBE requires 1 argument: offset");
            }
            int offset = Integer.parseInt(arguments.get(0).toString());
            return ByteBuffer.wrap(data, offset, 4).order(ByteOrder.BIG_ENDIAN).getInt();
        }
    }
    
    // Read int64 big-endian from binary at offset
    static class ReadLongBEFunction implements freemarker.template.TemplateMethodModelEx {
        private final byte[] data;
        
        public ReadLongBEFunction(byte[] data) {
            this.data = data;
        }
        
        @Override
        public Object exec(java.util.List arguments) throws freemarker.template.TemplateModelException {
            if (arguments.size() != 1) {
                throw new freemarker.template.TemplateModelException("readLongBE requires 1 argument: offset");
            }
            int offset = Integer.parseInt(arguments.get(0).toString());
            return ByteBuffer.wrap(data, offset, 8).order(ByteOrder.BIG_ENDIAN).getLong();
        }
    }
    
    // Read float32 big-endian from binary at offset
    static class ReadFloatBEFunction implements freemarker.template.TemplateMethodModelEx {
        private final byte[] data;
        
        public ReadFloatBEFunction(byte[] data) {
            this.data = data;
        }
        
        @Override
        public Object exec(java.util.List arguments) throws freemarker.template.TemplateModelException {
            if (arguments.size() != 1) {
                throw new freemarker.template.TemplateModelException("readFloatBE requires 1 argument: offset");
            }
            int offset = Integer.parseInt(arguments.get(0).toString());
            return ByteBuffer.wrap(data, offset, 4).order(ByteOrder.BIG_ENDIAN).getFloat();
        }
    }
    
    // Read raw bytes as comma-separated integers
    static class ReadBytesFunction implements freemarker.template.TemplateMethodModelEx {
        private final byte[] data;
        
        public ReadBytesFunction(byte[] data) {
            this.data = data;
        }
        
        @Override
        public Object exec(java.util.List arguments) throws freemarker.template.TemplateModelException {
            if (arguments.size() != 2) {
                throw new freemarker.template.TemplateModelException("readBytes requires 2 arguments: offset, length");
            }
            int offset = Integer.parseInt(arguments.get(0).toString());
            int length = Integer.parseInt(arguments.get(1).toString());
            
            StringBuilder sb = new StringBuilder("[");
            for (int i = 0; i < length; i++) {
                if (i > 0) sb.append(", ");
                sb.append(data[offset + i] & 0xFF);
            }
            sb.append("]");
            return sb.toString();
        }
    }
    
    // Read bytes as hex string
    static class ReadHexFunction implements freemarker.template.TemplateMethodModelEx {
        private final byte[] data;
        
        public ReadHexFunction(byte[] data) {
            this.data = data;
        }
        
        @Override
        public Object exec(java.util.List arguments) throws freemarker.template.TemplateModelException {
            if (arguments.size() != 2) {
                throw new freemarker.template.TemplateModelException("readHex requires 2 arguments: offset, length");
            }
            int offset = Integer.parseInt(arguments.get(0).toString());
            int length = Integer.parseInt(arguments.get(1).toString());
            
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < length; i++) {
                sb.append(String.format("%02X", data[offset + i] & 0xFF));
            }
            return sb.toString();
        }
    }
    
    // Read boolean from byte
    static class ReadBooleanFunction implements freemarker.template.TemplateMethodModelEx {
        private final byte[] data;
        
        public ReadBooleanFunction(byte[] data) {
            this.data = data;
        }
        
        @Override
        public Object exec(java.util.List arguments) throws freemarker.template.TemplateModelException {
            if (arguments.size() != 1) {
                throw new freemarker.template.TemplateModelException("readBoolean requires 1 argument: offset");
            }
            int offset = Integer.parseInt(arguments.get(0).toString());
            return data[offset] != 0;
        }
    }
}
