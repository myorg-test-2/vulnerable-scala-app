# Vulnerable Scala/Play Framework Application

**⚠️ WARNING: This application is intentionally vulnerable and should NEVER be deployed to production!**

Intentionally vulnerable Scala/Akka HTTP application for testing security remediation tools, SAST/SCA scanners, and security training.

## 🎯 Purpose

- **200+ open source dependency vulnerabilities** requiring complex remediation
- **18 code-level security vulnerabilities** covering OWASP Top 10
- Realistic vulnerable code patterns found in real-world Scala applications

## 📊 Vulnerability Summary

### Dependency Vulnerabilities (SCA)
- **60+ vulnerable SBT dependencies** from 2019
- Play Framework 2.7.3, Akka 2.5.23, Jackson 2.9.9 with known vulnerabilities
- Expected **200+ total vulnerabilities** including transitive dependencies

**Key Vulnerable Dependencies:**
- `play: 2.7.3` (security issues)
- `akka-http: 10.1.8` (vulnerabilities)
- `jackson-databind: 2.9.9` (deserialization RCE)
- `aws-java-sdk: 1.11.327` (100+ transitive vulns)
- `slick: 3.3.1`, `json4s: 3.6.6`, `circe: 0.11.1`
- And 50+ more...

### Code Vulnerabilities (SAST) - 18 Vulnerabilities

1. SQL Injection (CWE-89) - Main.scala:78
2. Command Injection (CWE-78) - Main.scala:90
3. Path Traversal (CWE-22) - Main.scala:102
4. XSS (CWE-79) - Main.scala:119
5. SSRF (CWE-918) - Main.scala:129
6. Code Injection (CWE-94) - Main.scala:144
7. Mass Assignment (CWE-915) - Main.scala:153-164
8. IDOR (CWE-639) - Main.scala:172
9. Missing Authentication (CWE-306) - Main.scala:183
10. Sensitive Data Exposure (CWE-200) - Main.scala:194-204
11. Open Redirect (CWE-601) - Main.scala:211
12. Weak Cryptography - MD5 (CWE-327) - Main.scala:223-227
13. Insecure Randomness (CWE-330) - Main.scala:235
14. Hardcoded Credentials (CWE-798) - Main.scala:12-16, 244-257
15. Information Exposure via Errors (CWE-209) - Main.scala:264
16. Missing Rate Limiting (CWE-770) - Main.scala:274
17. Debug Mode Enabled - Main.scala:73
18. Exposed Secrets - .env file

## 🚀 Setup

### Prerequisites
- Scala 2.12.8+
- SBT 1.2+

### Installation

```bash
git clone https://github.com/YOUR_USERNAME/vulnerable-scala-app.git
cd vulnerable-scala-app

# Compile (expect warnings)
sbt compile

# Run
sbt run
```

Access at: `http://localhost:8080`

## 🔍 Testing Vulnerabilities

```bash
snyk test
# Expected: 200+ vulnerabilities
```

## 📚 Endpoints

- POST /api/login - SQL Injection
- GET /api/exec?cmd=ls - Command Injection
- GET /api/files?filename=test.txt - Path Traversal
- GET /api/search?query=test - XSS
- GET /api/proxy?url=http://example.com - SSRF
- POST /api/register - Mass Assignment
- GET /api/users/{id} - IDOR
- DELETE /api/admin/users/{id} - Missing Auth
- GET /api/debug - Sensitive Data Exposure
- And 7 more...

## 🛡️ Remediation

### Simple Upgrades
```bash
sbt dependencyUpdates
```

### Major Framework Upgrade
- Play 2.7 → 2.8 (Breaking changes)
- Akka 2.5 → 2.6 (API changes)

## ⚠️ Security Notice

DO NOT deploy to production or expose to internet. Educational use only.

MIT License - Testing purposes only.
