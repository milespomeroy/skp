package com.milespomeroy.skp

import spock.lang.Shared
import spock.lang.Specification


class AppSpec extends Specification {
    @Shared ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    @Shared ByteArrayOutputStream errContent = new ByteArrayOutputStream();

    def setupSpec() {
        System.setOut(new PrintStream(outContent));
        System.setErr(new PrintStream(errContent));
    }

    def cleanup() {
        outContent.reset()
        errContent.reset()
    }

    def cleanupSpec() {
        System.setOut(null);
        System.setErr(null);
    }

    def "should require a file"() {
        when: "app is run without arguments"
        App.main()

        then:
        errContent.toString() == "Please provide a data file for processing.\n"
    }

    def "should require an existing file"() {
        when: "app is run with a filename that doesn't exist"
        String[] args = ["nonexistant_file.txt"]
        App.main(args)

        then:
        errContent.toString() == "File was not found: nonexistant_file.txt\n"
    }

//    def "should require a properly formatted tab delimited file"() {
//        when:
//        String[] args = ["pom.xml"]
//        App.main(args)
//
//        then:
//        errContent.toString() == "Error reading pom.xml. Is it tab delimited hit data?\n"
//    }
}
