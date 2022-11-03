package io.github.kglowins.shifts

import groovy.util.logging.Slf4j
import spock.lang.Specification
import spock.lang.Unroll

@Slf4j
class SampleSpec extends Specification {

    @Unroll
    def "should"(input, expectedOutput) {
        given:
        int x = input

        expect:
        x * x == expectedOutput

        where:
        input || expectedOutput
        1     || 1
        2     || 4

    }
}
