package org.jbehave.core.parsers;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jbehave.core.model.StepPattern;
import org.jbehave.core.steps.StepType;

public class RegexStepMatcher implements StepMatcher {

    private final Pattern regexPattern;
    private final String[] parameterNames;
    private final StepPattern stepPattern;
    private Matcher matcher;

    public RegexStepMatcher(StepType stepType, String annotatedPattern, Pattern regexPattern, String[] parameterNames) {
        this.regexPattern = regexPattern;
        this.parameterNames = parameterNames;
        this.stepPattern = new StepPattern(stepType, annotatedPattern, regexPattern.pattern());
    }

    @Override
    public boolean matches(String stepWithoutStartingWord) {
        matcher(stepWithoutStartingWord);
        return matcher.matches();
    }

    @Override
    public boolean find(String stepWithoutStartingWord) {
        matcher(stepWithoutStartingWord);
        return matcher.find();
    }

    @Override
    public String parameter(int matchedPosition) {
        return matcher.group(matchedPosition);
    }

    private void matcher(String patternToMatch) {
        matcher = regexPattern.matcher(patternToMatch);
    }

    @Override
    public String[] parameterNames() {
        return parameterNames;
    }

    @Override
    public StepPattern pattern() {
        return stepPattern;
    }
}
