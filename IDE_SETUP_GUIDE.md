# IDE Setup Guide - Resolving MapStruct Warnings

## IntelliJ IDEA Configuration

### 1. Enable Annotation Processing
1. Go to **File** → **Settings** (or **IntelliJ IDEA** → **Preferences** on macOS)
2. Navigate to **Build, Execution, Deployment** → **Compiler** → **Annotation Processors**
3. Check **Enable annotation processing**
4. Set **Obtain processors from project classpath**
5. Set **Generated sources directory** to: `target/generated-sources/annotations`
6. Click **Apply** and **OK**

### 2. Mark Generated Sources Directory
1. In the **Project** view, navigate to `target/generated-sources/annotations`
2. Right-click on the `annotations` folder
3. Select **Mark Directory as** → **Generated Sources Root**

### 3. Refresh Project
1. Go to **File** → **Reload All from Disk**
2. Or press **Ctrl+Shift+A** (or **Cmd+Shift+A** on macOS) and type "Reload All from Disk"

### 4. Invalidate Caches (if needed)
1. Go to **File** → **Invalidate Caches and Restart**
2. Select **Invalidate and Restart**

## Eclipse Configuration

### 1. Enable Annotation Processing
1. Right-click on your project
2. Select **Properties**
3. Go to **Java Compiler** → **Annotation Processing**
4. Check **Enable annotation processing**
5. Set **Generated source directory** to: `target/generated-sources/annotations`
6. Click **Apply and Close**

### 2. Add Generated Sources to Build Path
1. Right-click on your project
2. Select **Properties**
3. Go to **Java Build Path** → **Source**
4. Click **Add Folder**
5. Select `target/generated-sources/annotations`
6. Click **Apply and Close**

## VS Code Configuration

### 1. Java Extension Settings
1. Open **Settings** (Ctrl+,)
2. Search for "java.compile.nullAnalysis.mode"
3. Set it to "automatic"

### 2. Add to .vscode/settings.json
```json
{
    "java.compile.nullAnalysis.mode": "automatic",
    "java.configuration.updateBuildConfiguration": "automatic"
}
```

## Verification Steps

After applying the above configurations:

1. **Clean and rebuild**:
   ```bash
   mvn clean compile
   ```

2. **Check if warnings are resolved**:
   - The 272 problems should disappear
   - MapStruct generated classes should be recognized
   - No red underlines on mapper interfaces

3. **Test that everything still works**:
   ```bash
   mvn test
   ```

## Common Issues and Solutions

### Issue: "Cannot resolve symbol" for generated mapper classes
**Solution**: Ensure annotation processing is enabled and generated sources directory is marked correctly.

### Issue: IDE still shows warnings after configuration
**Solution**: 
1. Restart the IDE
2. Invalidate caches
3. Re-import the project

### Issue: Maven builds work but IDE shows errors
**Solution**: This confirms it's an IDE configuration issue. Follow the IDE-specific setup above.

## Project Structure Verification

Your project should have this structure for generated sources:
```
target/
└── generated-sources/
    └── annotations/
        └── org/
            └── driver/
                └── driverapp/
                    └── mapper/
                        ├── AddressMapperImpl.java
                        ├── CustomerMapperImpl.java
                        ├── NotificationMapperImpl.java
                        └── ... (other generated mappers)
```

## Note

The 272 problems you're seeing are **IDE warnings**, not compilation errors. Your code compiles and runs correctly. These warnings appear because the IDE doesn't recognize the MapStruct-generated implementation classes. Following the above configuration steps should resolve all warnings.
