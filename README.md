# Hybrid pnpm JavaScript + Java Backend - Veracode Vulnerable Methods Test

This project demonstrates **Vulnerable Methods detection** by combining:
- **Frontend**: pnpm JavaScript with vulnerable npm dependencies
- **Backend**: Java with vulnerable methods that Veracode can detect

## Project Structure

```
.
├── package.json                 # pnpm frontend config
├── frontend/
│   └── src/index.js            # Vulnerable JavaScript methods
├── backend/
│   ├── pom.xml                 # Maven Java config
│   └── src/main/java/
│       └── com/veracode/test/
│           └── VulnerableBackend.java  # Vulnerable Java methods
└── pnpm-lock.yaml              # pnpm lock file
```

## Frontend - pnpm JavaScript

**Vulnerable npm Libraries:**
- `axios@0.18.0` - Proxy Bypass
- `lodash@4.17.11` - Prototype Pollution
- `minimist@1.2.0` - Prototype Pollution
- `express@4.16.2` - Multiple vulnerabilities

**JavaScript Vulnerable Methods:**
1. `makeRequestWithAxios()` - HTTP request with vulnerable library
2. `mergeConfigFrontend()` - Object merging with prototype pollution
3. `parseArgsFrontend()` - CLI argument parsing with vulnerable library

## Backend - Java

**Vulnerable Libraries:**
- `commons-collections 3.1` - Remote Code Execution (gadget chain)
- `struts2-core 2.3.15` - Remote Code Execution
- `log4j-core 2.8` - JNDI Injection (CVE-2021-44228)
- `jackson-databind 2.6.0` - Arbitrary Code Execution
- `spring-core 4.1.0` - Expression Language Injection

**Java Vulnerable Methods (8 total):**
1. `deserializeUntrustedData()` - Object deserialization with gadget chain ⚠️ **RCE**
2. `logUserInput()` - Log4j JNDI injection vulnerability
3. `parseJsonUntrusted()` - Jackson unsafe deserialization
4. `processExpression()` - Expression Language injection pattern
5. `executeCommand()` - Direct command execution
6. `buildSqlQuery()` - SQL injection via string concatenation
7. `readFile()` - Path traversal vulnerability
8. `useWeakCrypto()` - Weak MD5 cryptography

## Building Backend

```bash
cd backend
mvn clean compile
mvn package
```

## Installing Frontend Dependencies

```bash
# With pnpm
pnpm install

# Or with npm
npm install
```

## Veracode SCA Scanning

```bash
# Scan entire project (both frontend and backend)
srcclr scan . --json output.json

# Or with upload to Veracode
srcclr scan . --update-advisor
```

## Expected Veracode Results

**Frontend (pnpm JavaScript):**
- ✅ ~10 vulnerabilities in npm libraries
- ✅ Library-level vulnerability detection
- ❌ No vulnerable methods (JavaScript limitation)

**Backend (Java):**
- ✅ 30+ vulnerabilities from Java libraries
- ✅ **8 Vulnerable Methods detected**
- ✅ Call graph showing which methods use vulnerable code

**Combined Scan:**
- Total vulnerabilities across both stacks
- Java methods marked as "Vulnerable Methods" 
- JavaScript libraries marked as vulnerable dependencies

## Vulnerability Details

### Critical Vulnerabilities

**CVE-2015-7501** - Apache Commons Collections
- **Method**: `deserializeUntrustedData()`
- **Risk**: Remote Code Execution via gadget chain
- **CVSS**: 9.8 (Critical)

**CVE-2021-44228** - Apache Log4j
- **Method**: `logUserInput()`
- **Risk**: Remote Code Execution via JNDI injection
- **CVSS**: 10.0 (Critical)

**CVE-2017-7525** - Jackson Databind
- **Method**: `parseJsonUntrusted()`
- **Risk**: Arbitrary Code Execution
- **CVSS**: 9.8 (Critical)

## Recommendations

1. **Update Dependencies**:
   ```
   commons-collections → 3.2.2+
   struts2-core → 2.5.26+
   log4j-core → 2.17.0+
   jackson-databind → 2.13.4.2+
   ```

2. **Immediate Fixes**:
   - Never deserialize untrusted data
   - Upgrade Log4j immediately
   - Disable Jackson default typing
   - Validate and sanitize all user inputs
   - Use parameterized queries (prepared statements)
   - Use secure cryptography (SHA-256, bcrypt)

## Testing with Veracode

This project is designed for testing Veracode's vulnerability detection across multiple languages:
- **pnpm/JavaScript**: Library vulnerability detection
- **Java/Maven**: Vulnerable methods detection
- **Combined**: Full-stack security analysis

## License

MIT
