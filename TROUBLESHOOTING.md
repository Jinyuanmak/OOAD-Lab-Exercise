# Troubleshooting Guide

## Common Issues and Solutions

### Issue 1: CMD Window Closes Immediately

**Symptom:** When you double-click the batch file, a CMD window appears briefly and then closes.

**Solutions:**

1. **Try the simple launcher:**
   - Double-click `run-simple.bat` instead
   - This keeps the window open so you can see any error messages

2. **Run from Command Prompt:**
   - Open Command Prompt (cmd.exe)
   - Navigate to the project folder
   - Run: `run-seminar-app.bat`
   - You'll see all output and error messages

3. **Check Java installation:**
   ```cmd
   java -version
   ```
   - Should show Java 17 or higher
   - If not found, install Java from [Oracle](https://www.oracle.com/java/technologies/downloads/)

---

### Issue 2: NullPointerException on Startup

**Symptom:** Error message shows `NullPointerException` with `userService is null`

**Solution:** This has been fixed in the latest version. Make sure you have the updated code:
- The fix moves sample data loading to after service initialization
- Recompile: `apache-maven-3.9.12\bin\mvn.cmd clean compile`

---

### Issue 3: Application Doesn't Start (No GUI Window)

**Symptom:** Compilation succeeds but no GUI window appears

**Possible Causes & Solutions:**

1. **Java version too old:**
   ```cmd
   java -version
   ```
   - Must be Java 17 or higher
   - Update Java if needed

2. **Display/Graphics issue:**
   - Try running from terminal to see error messages
   - Check if Java Swing is supported on your system

3. **Port or resource conflict:**
   - Close other Java applications
   - Restart your computer

---

### Issue 4: Compilation Fails

**Symptom:** Error during compilation phase

**Solutions:**

1. **Clean and rebuild:**
   ```cmd
   apache-maven-3.9.12\bin\mvn.cmd clean compile
   ```

2. **Check internet connection:**
   - Maven may need to download dependencies
   - Ensure you have internet access

3. **Delete target folder:**
   - Manually delete the `target` folder
   - Run compilation again

---

### Issue 5: "Cannot find main class" Error

**Symptom:** Error says it cannot find `com.fci.seminar.ui.SeminarApp`

**Solutions:**

1. **Compile first:**
   ```cmd
   apache-maven-3.9.12\bin\mvn.cmd compile
   ```

2. **Check class files exist:**
   - Look in `target\classes\com\fci\seminar\ui\`
   - Should contain `SeminarApp.class`

3. **Use the direct launcher:**
   - Try `run-direct.bat` which compiles first

---

### Issue 6: GUI Window Opens But Crashes

**Symptom:** Window appears briefly then closes with error

**Solutions:**

1. **Check the error message in CMD window**
   - Use `run-simple.bat` to keep window open
   - Read the error message

2. **Delete data file:**
   - Delete `seminar_data.ser` if it exists
   - Restart application
   - This forces a fresh start

3. **Check for corrupted data:**
   - Backup `seminar_data.ser`
   - Delete it and restart

---

### Issue 7: Sample Data Doesn't Load

**Symptom:** No prompt to load sample data, or empty system

**Solutions:**

1. **Delete existing data file:**
   ```cmd
   del seminar_data.ser
   ```
   - Restart application
   - You'll see the sample data prompt

2. **Click "Yes" on the prompt:**
   - Make sure you click "Yes" when asked
   - If you clicked "No", delete `seminar_data.ser` and restart

---

### Issue 8: Login Fails

**Symptom:** Cannot log in with provided credentials

**Solutions:**

1. **Check you loaded sample data:**
   - Sample accounts only exist if you loaded sample data
   - Delete `seminar_data.ser` and restart to load sample data

2. **Verify credentials:**
   - Username: `admin` (lowercase)
   - Password: `admin123`
   - Role: Select "Coordinator" from dropdown

3. **Check role selection:**
   - Make sure you select the correct role from dropdown
   - Username/password must match the role

---

## Step-by-Step Debugging

If you're still having issues, follow these steps:

### Step 1: Verify Java

```cmd
java -version
```

Expected output: `java version "17.x.x"` or higher

### Step 2: Clean Build

```cmd
apache-maven-3.9.12\bin\mvn.cmd clean compile
```

Expected: `BUILD SUCCESS`

### Step 3: Run Tests

```cmd
apache-maven-3.9.12\bin\mvn.cmd test
```

Expected: All 42 tests passing

### Step 4: Try Direct Launch

```cmd
run-direct.bat
```

Watch for any error messages in the CMD window

### Step 5: Check for GUI Window

- A window titled "FCI Seminar Management System" should appear
- If not, check for error messages in CMD

---

## Getting More Help

### View Detailed Logs

Run with verbose output:
```cmd
apache-maven-3.9.12\bin\mvn.cmd exec:java -Dexec.mainClass=com.fci.seminar.ui.SeminarApp -X
```

### Check System Information

```cmd
java -version
echo %JAVA_HOME%
apache-maven-3.9.12\bin\mvn.cmd -version
```

### Common Error Messages

| Error Message | Solution |
|---------------|----------|
| "Java is not recognized" | Install Java or add to PATH |
| "BUILD FAILURE" | Check Maven output for specific error |
| "NullPointerException" | Update to latest code (fixed) |
| "Cannot find symbol" | Clean and recompile |
| "Class not found" | Compile first: `mvn compile` |

---

## Alternative Running Methods

If batch files don't work, try these alternatives:

### Method 1: Command Prompt

```cmd
cd path\to\project
apache-maven-3.9.12\bin\mvn.cmd compile
apache-maven-3.9.12\bin\mvn.cmd exec:java -Dexec.mainClass=com.fci.seminar.ui.SeminarApp
```

### Method 2: Direct Java

```cmd
apache-maven-3.9.12\bin\mvn.cmd compile
java -cp target\classes com.fci.seminar.ui.SeminarApp
```

### Method 3: Build JAR

```cmd
apache-maven-3.9.12\bin\mvn.cmd package
java -jar target\seminar-management-system-1.0-SNAPSHOT.jar
```

---

## Still Having Issues?

1. Check the [README.md](README.md) for system requirements
2. Review the [QUICK_START_GUIDE.md](QUICK_START_GUIDE.md) for detailed instructions
3. Ensure all prerequisites are installed:
   - Java 17 or higher
   - Sufficient disk space (50MB)
   - Sufficient RAM (512MB minimum)

---

## Contact Information

For persistent issues:
1. Check the error message carefully
2. Try all alternative running methods above
3. Verify system requirements
4. Review specification documents in `.kiro/specs/`

---

*Last Updated: January 9, 2026*
