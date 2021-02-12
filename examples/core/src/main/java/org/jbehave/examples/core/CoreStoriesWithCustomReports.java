package org.jbehave.examples.core;

import org.jbehave.core.configuration.Configuration;
import org.jbehave.core.configuration.Keywords;
import org.jbehave.core.i18n.LocalizedKeywords;
import org.jbehave.core.io.StoryFinder;
import org.jbehave.core.junit.JBehaveJUnit4Runner;
import org.jbehave.core.parsers.RegexStoryParser;
import org.jbehave.core.reporters.*;
import org.junit.runner.RunWith;

import java.io.File;
import java.util.List;
import java.util.Locale;
import java.util.Properties;

import static org.jbehave.core.io.CodeLocations.codeLocationFromClass;

@RunWith(JBehaveJUnit4Runner.class)
public class CoreStoriesWithCustomReports extends CoreStories {

    @Override
    public Configuration configuration() {
        Configuration configuration = super.configuration();
        Properties viewResources = new Properties();
        viewResources.put("reports", "ftl/custom-reports.ftl");
        configuration.useViewGenerator(new FreemarkerViewGenerator(this.getClass()));
        Keywords keywords = new LocalizedKeywords(new Locale("en"), "i18n/custom", "i18n/keywords", this.getClass().getClassLoader());
        StoryReporterBuilder reporterBuilder = configuration.storyReporterBuilder()
                .withKeywords(keywords)
                .withViewResources(viewResources)
                .withFormats(CustomHtmlOutput.FORMAT);
        return configuration
                .useKeywords(keywords)
                .useStoryParser(new RegexStoryParser(keywords))
                .useStoryReporterBuilder(reporterBuilder);
    }

    @Override
    public List<String> storyPaths() {
        String filter = System.getProperty("story.filter", "**/*.story");
        return new StoryFinder().findPaths(codeLocationFromClass(this.getClass()), filter, "**/failing/*.story,**/given/*.story,**/pending/*.story");
    }

    public static class CustomHtmlOutput extends HtmlTemplateOutput {

        public CustomHtmlOutput(File file, Keywords keywords) {
            super(file, keywords, new FreemarkerProcessor(CustomHtmlOutput.class), "ftl/custom-html-output.ftl");
        }

        public static final Format FORMAT = new Format("HTML") {
            @Override
            public StoryReporter createStoryReporter(FilePrintStreamFactory factory,
                    StoryReporterBuilder storyReporterBuilder) {
                factory.useConfiguration(storyReporterBuilder.fileConfiguration("html"));
                return new CustomHtmlOutput(factory.getOutputFile(), storyReporterBuilder.keywords());
            }
        };

    }
}
