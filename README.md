# TextCrypt
Encrypts text file using encryption key. Sample application.properties file is embedded inside the jar.
It is also possible to list available algorithms.

Usage:

```
java -jar TextCrypt.jar
    -c,--configuration_file <path-to-configuration-file>   Configuration file path
    -d,--decrypt                                           Decrypt input
    -e,--encrypt                                           Encrypt input
    -f,--input-file <path-to-input-file>                   Input file path
    -h,--help                                              Print this help
    -o,--output-file <path-to-output-file>                 Output file path

java -cp TextCrypt.jar konrad.test.kotlin.InstalledAlgorithmsKt
```

